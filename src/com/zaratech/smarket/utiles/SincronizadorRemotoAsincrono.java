package com.zaratech.smarket.utiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaratech.smarket.componentes.Conexion;
import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Sincroniza una BD remota con una BD local. Si la BD remota no esta completa
 * crea las tablas que faltan.
 * 
 * @author Juan
 */
public class SincronizadorRemotoAsincrono extends
		AsyncTask<Integer, Integer, Boolean> {

	public static final int OP_CREAR = 0;
	public static final int OP_PULL = 1;
	public static final int OP_PUSH = 2;
	public static final int OP_TMP = 3;

	public static final int COMPONENTE_PRODUCTO = 0;
	public static final int COMPONENTE_MARCA = 1;

	public static final String DB_CREAR_LOGS = "create table if not exists logs "
			+ "("
			+ "id integer primary key auto_increment,"
			+ "op integer not null," 
			+ "id_componente integer not null" 
			+ ");";

	public static final int LOG_OP_CREAR_PRODUCTO = 0;
	public static final int LOG_OP_CREAR_MARCA = 1;
	public static final int LOG_OP_BORRAR_PRODUCTO = 2;
	public static final int LOG_OP_BORRAR_MARCA = 3;
	public static final int LOG_OP_ACTUALIZAR_PRODUCTO = 4;
	public static final int LOG_OP_ACTUALIZAR_MARCA = 5;

	public static final int LOG_OP_CREAR = 0;
	public static final int LOG_OP_ACTUALIZAR = 1;

	private AdaptadorBD bdLocal = null;
	private Conexion datosConexion = null;

	private int operacion;

	public SincronizadorRemotoAsincrono(AdaptadorBD bdLocal,
			Conexion datosConexion) {
		this.bdLocal = bdLocal;
		this.datosConexion = datosConexion;
		this.operacion = -1;
	}

	/**
	 * Crea las tablas que falten
	 * 
	 * @return Resultado de la operacion. True si se ha creado.
	 */
	private boolean crearBD() {

		Log.d("smarket", "CREANDO BD");

		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection conexion = DriverManager.getConnection(
					datosConexion.generarConexionMySQL(),
					datosConexion.getUsuario(), datosConexion.getPass());

			Statement consulta = conexion.createStatement();

			// crear producto

			String sql = AdaptadorBD.DB_CREAR_PRODUCTOS;

			sql = sql.replace("autoincrement", "auto_increment");

			consulta.executeUpdate(sql);

			Log.d("smarket", "CREANDO PRODUCTOS");

			// crear marca

			sql = AdaptadorBD.DB_CREAR_MARCAS;

			sql = sql.replace("autoincrement", "auto_increment");

			consulta.executeUpdate(sql);

			Log.d("smarket", "CREANDO MARCAS");

			// crear log remoto

			sql = DB_CREAR_LOGS;

			consulta.executeUpdate(sql);

			Log.d("smarket", "CREANDO LOGS REMOTOS");

			// crear log local

			bdLocal.crearLogs();

			Log.d("smarket", "CREANDO LOGS LOCALES");

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

	/**
	 * Obtiene cambios de BD remota, y los aplica en BD local
	 */
	private boolean pull() {

		Log.d("smarket", "PULL");

		int idUltimoLog = -1;

		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection conexion = DriverManager.getConnection(
					datosConexion.generarConexionMySQL(),
					datosConexion.getUsuario(), datosConexion.getPass());

			Statement consulta = conexion.createStatement();

			// comprobar log remoto

			int ultimoLogLocal = bdLocal.getUltimoLog();

			String sql1 = "select * from logs where id > " + ultimoLogLocal
					+ " order by id asc";

			ResultSet resultado = consulta.executeQuery(sql1);

			while (resultado.next()) {

				Log.d("smarket", "PULL COMPONENTE");

				// por cada log
				idUltimoLog = resultado.getInt(1);
				int op = resultado.getInt(2);
				int idComponente = resultado.getInt(3);

				Statement consulta2 = conexion.createStatement();

				// CREAR PRODUCTO
				if (op == LOG_OP_CREAR_PRODUCTO) {

					String sql2 = "select * from "
							+ AdaptadorBD.DB_TABLA_PRODUCTOS + " where "
							+ AdaptadorBD.KEY_ID + " = " + idComponente;

					ResultSet resultado2 = consulta2.executeQuery(sql2);

					if (resultado2.next()) {
						insertarProducto(resultado2, LOG_OP_CREAR);
					}
					resultado2.close();

					// CREAR MARCA
				} else if (op == LOG_OP_CREAR_MARCA) {

					String sql2 = "select * from "
							+ AdaptadorBD.DB_TABLA_MARCAS + " where "
							+ AdaptadorBD.KEY_ID + " = " + idComponente;

					ResultSet resultado2 = consulta2.executeQuery(sql2);

					if (resultado2.next()) {
						insertarMarca(resultado2, LOG_OP_CREAR);
					}
					resultado2.close();

					// BORRAR PRODUCTO
				} else if (op == LOG_OP_BORRAR_PRODUCTO) {

					bdLocal.borrarProducto(idComponente);

					// BORRAR MARCA
				} else if (op == LOG_OP_BORRAR_MARCA) {

					bdLocal.borrarMarca(idComponente);

					// ACTUALIZAR PRODUCTO
				} else if (op == LOG_OP_ACTUALIZAR_PRODUCTO) {

					String sql2 = "select * from "
							+ AdaptadorBD.DB_TABLA_PRODUCTOS + " where "
							+ AdaptadorBD.KEY_ID + " = " + idComponente;

					ResultSet resultado2 = consulta2.executeQuery(sql2);

					if (resultado2.next()) {
						insertarProducto(resultado2, LOG_OP_ACTUALIZAR);
					}
					resultado2.close();

					// ACTUALIZAR MARCA
				} else if (op == LOG_OP_ACTUALIZAR_MARCA) {

					String sql2 = "select * from "
							+ AdaptadorBD.DB_TABLA_MARCAS + " where "
							+ AdaptadorBD.KEY_ID + " = " + idComponente;

					ResultSet resultado2 = consulta2.executeQuery(sql2);

					if (resultado2.next()) {
						insertarMarca(resultado2, LOG_OP_ACTUALIZAR);
					}
					resultado2.close();
				}

			}

			resultado.close();

			// actualizar log local
			if (idUltimoLog > ultimoLogLocal) {
				bdLocal.setUltimoLog(idUltimoLog);
			}

			conexion.close();

			return true;

		} catch (ClassNotFoundException e) {

			Log.d("smarket", "ERROR PULL 1");
			return false;

		} catch (SQLException e) {

			Log.d("smarket", "ERROR PULL 2");
			Log.d("smarket", e.getMessage());
			return false;
		}
	}

	/**
	 * Inserta Producto en BD local
	 */
	private void insertarProducto(ResultSet res, int op) throws SQLException {

		Producto p = new Producto();

		// ID
		p.setId(res.getInt(1));

		// NOMBRE
		p.setNombre(res.getString(2));

		// TIPO
		p.setTipo(res.getInt(3));

		// MARCA
		Marca m = new Marca();
		m.setId(res.getInt(4));
		p.setMarca(m);

		// DIMENSION DE PANTALLA
		p.setDimensionPantalla(res.getDouble(5));

		// SISTEMA OPERATIVO
		p.setSistemaOperativo(res.getInt(6));

		// PRECIO
		p.setPrecio(res.getDouble(7));

		// DESCRIPCION
		p.setDescripcion(res.getString(8));

		// IMAGEN
		Bitmap imagen = BitmapFactory.decodeStream(
				res.getBlob(9).getBinaryStream());
		p.setImagen(imagen);

		// EN OFERTA
		int oferta = res.getInt(10);
		if (oferta == 1) {
			p.setOferta();

		} else {
			p.unsetOferta();
		}

		// PRECIO EN OFERTA
		p.setPrecioOferta(res.getDouble(11));

		if (op == LOG_OP_CREAR) {

			bdLocal.borrarProducto(p.getId());
			bdLocal.crearProducto(p);

		} else {

			bdLocal.actualizarProducto(p);
		}

	}

	/**
	 * Inserta Marca en BD local
	 */
	private void insertarMarca(ResultSet res, int op) throws SQLException {

		Marca m = new Marca();

		// ID
		m.setId(res.getInt(1));

		// NOMBRE
		m.setNombre(res.getString(2));

		if (op == LOG_OP_CREAR) {

			bdLocal.borrarMarca(m.getId());
			bdLocal.crearMarca(m);

		} else {
			bdLocal.actualizarMarca(m);
		}

	}

	/**
	 * Aplica cambios en BD local a BD remota
	 */
	private boolean push(int op, int id) {

		//TODO
		
		// LOCK TABLES productos READ, marcas READ, LOGS READ;
		// UNLOCK TABLES;

		// subir cambios

		// escribir log remoto

		return true;
	}

	/**
	 * Despues de un tiempo [segundos], obtiene cambios de BD remota, y los
	 * aplica en BD local
	 */
	private boolean tmp(int segundos) {

		try {

			while (segundos > 0) {
				Thread.sleep(1000);
				segundos--;
			}

			if (bdLocal.isBdCreada() && bdLocal.isBdAbierta()) {
				pull();
			}

			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * Ejecuta en segundo plano
	 */
	@Override
	protected Boolean doInBackground(Integer... params) {
		operacion = params[0];

		if (operacion == OP_CREAR) {
			return crearBD();

		} else if (operacion == OP_PULL) {

			if (bdLocal.isBdCreada() && bdLocal.isBdAbierta()) {
				return pull();
			} else {
				return false;
			}

		} else if (operacion == OP_PUSH) {
			int componente = params[1];
			int id = params[2];

			return push(componente, id);

		} else if (operacion == OP_TMP) {
			int segundos = params[1];
			return tmp(segundos);

		} else {

			return false;
		}

	}

	@Override
	protected void onPostExecute(Boolean result) {

		if (operacion == OP_CREAR) {
			bdLocal.setBdCreada();
		}

		if (operacion == OP_PULL || operacion == OP_TMP) {
			bdLocal.recargarListado();

			if (bdLocal.isSincronizacionRemotaPeriodica()) {
				bdLocal.initSincronizacionRemotaPeriodica();
			}

		}

		super.onPostExecute(result);
	}

}