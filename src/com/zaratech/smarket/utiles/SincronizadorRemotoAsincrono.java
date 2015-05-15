package com.zaratech.smarket.utiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaratech.smarket.componentes.Conexion;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Sincroniza una BD remota con una BD local.
 * Si la BD remota no esta completa crea las tablas que faltan.
 * 
 * @author Juan
 */
public class SincronizadorRemotoAsincrono extends AsyncTask<Integer, Integer, Boolean> {

	public static final int OP_CREAR = 0;
	public static final int OP_SINCRONIZAR = 1;

	private AdaptadorBD bdLocal = null;
	private Conexion datosConexion = null;

	public SincronizadorRemotoAsincrono(AdaptadorBD bdLocal, Conexion datosConexion){
		this.bdLocal = bdLocal;
		this.datosConexion = datosConexion;
	}



	/**
	 * Crea las tablas que falten
	 * @return Resultado de la operacion. True si se ha creado.
	 */
	private boolean crearBD(){

		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection conexion = DriverManager.getConnection(
					datosConexion.generarConexionMySQL(),
					datosConexion.getUsuario(), datosConexion.getPass());

			Statement consulta = conexion.createStatement();

			// crear producto

			String sql = AdaptadorBD.DB_CREAR_PRODUCTOS;

			sql = sql.replace("autoincrement", "auto_increment");
			sql = sql.replace("table", "table if not exists");

			consulta.executeUpdate(sql);

			Log.d("smarket", "CREANDO PRODUCTOS");


			// crear marca

			sql = AdaptadorBD.DB_CREAR_MARCAS;

			sql = sql.replace("autoincrement", "auto_increment");
			sql = sql.replace("table", "table if not exists");

			consulta.executeUpdate(sql);

			Log.d("smarket", "CREANDO MARCAS");

			// crear log

			sql = "create table if not exists logs ("
					+ "id integer primary key auto_increment,"
					+ "op text not null,"
					+ "id_componente integer not null"
					+ ");";

			consulta.executeUpdate(sql);

			Log.d("smarket", "CREANDO LOGS");

			conexion.close();

			return true;

		} catch (ClassNotFoundException e) {
			
			Log.d("smarket", "ERROR CREANDO 1");
			return false;

		} catch (SQLException e) {
			
			Log.d("smarket", "ERROR CREANDO 2");
			return false;
		}

	}

	private boolean sincronizar(){

		// comprobar log

		// aplicar cambios

		return true;
	}

	/**
	 * Comprueba si la BD esta completa
	 * @return True si esta completa
	 */
	private boolean isBdCreada(){

		// comprobar que existan las 3 tablas

		boolean productos = false;
		boolean marcas = false;
		boolean logs = false;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection conexion = DriverManager.getConnection(
					datosConexion.generarConexionMySQL(),
					datosConexion.getUsuario(), datosConexion.getPass());

			Statement consulta = conexion.createStatement();

			// Productos

			String sql = "show tables like '" + AdaptadorBD.DB_TABLA_PRODUCTOS + "'";

			ResultSet resultado = consulta.executeQuery(sql);

			if(resultado.next() 
					&& resultado.getString(1).equals(AdaptadorBD.DB_TABLA_PRODUCTOS)){

				Log.d("smarket", "PRODUCTOS YA CREADO");
				productos = true;
			}


			// Marcas

			sql = "show tables like '" + AdaptadorBD.DB_TABLA_MARCAS + "'";

			resultado = consulta.executeQuery(sql);

			if(resultado.next() 
					&& resultado.getString(1).equals(AdaptadorBD.DB_TABLA_MARCAS)){
				Log.d("smarket", "MARCAS YA CREADO");
				marcas = true;
			}


			// Logs

			sql = "show tables like 'logs'";

			resultado = consulta.executeQuery(sql);

			if(resultado.next() && resultado.getString(1).equals("logs")){
				Log.d("smarket", "LOGS YA CREADO");
				logs = true;
			}

			conexion.close();

			return productos & marcas & logs;

		} catch (ClassNotFoundException e) {
			return false;

		} catch (SQLException e) {
			return false;
		}
	}




	/**
	 * Ejecuta en segundo plano
	 */
	@Override
	protected Boolean doInBackground(Integer... params) {
		int op = params[0];

		if(op == OP_CREAR){

			if(!isBdCreada()){
				Log.d("smarket", "CREANDO BD");
				return crearBD();

			}else{
				Log.d("smarket", "BD YA CREADA");
				return false;
			}

		} else if (op == OP_SINCRONIZAR){
			sincronizar();

		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {

		super.onPostExecute(result);
	}

}
