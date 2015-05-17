package com.zaratech.smarket.utiles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zaratech.smarket.R;
import com.zaratech.smarket.aplicacion.ListaProductos;
import com.zaratech.smarket.componentes.Conexion;
import com.zaratech.smarket.componentes.Filtro;
import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Orden;
import com.zaratech.smarket.componentes.Producto;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase que se utiliza como adaptador para interactuar con una base de datos.
 * 
 * @author Marta
 * @author Juan
 */
public class AdaptadorBD implements InterfazBD {

	/**
	 * Base de datos
	 */

	private DatabaseHelper bdHelper;
	private SQLiteDatabase bd;
	private final Context context;

	////////////////////////////////////////////////
	/******** Sincronizacion Remota ***************/

	private static SincronizadorRemoto sincronizador = null;
	private static boolean bdCreada = false;
	private static boolean bdAbierta = false;
	private static boolean sincronizacionPeriodica = false;
	
	public static final int ORIGEN_APP = 0;
	public static final int ORIGEN_SINCRONIZADOR = 1;


	public static final String DB_TABLA_LOGS = "logs";
	public static final String DB_CREAR_LOGS = "create table if not exists "+ DB_TABLA_LOGS
			+ " ("
			+ "id integer primary key"
			+ ");";

	public void setSincronizacionRemota(){

		if (sincronizador == null){

			EditorConfiguracion configuracion = new EditorConfiguracion(context);
			Conexion conexion = new Conexion();

			conexion.setUsuario(configuracion.obtenerUsuarioBD());
			conexion.setPass(configuracion.obtenerPasswordBD());
			conexion.setDireccion(configuracion.obtenerDireccionBD());
			conexion.setPuerto(String.valueOf(configuracion.obtenerPuertoBD()));
			conexion.setBd(configuracion.obtenerNombreBD());

			sincronizador = new SincronizadorRemoto(this, conexion);

		} else if(!isBdCreada()){

			sincronizador.crear();
		}
	}

	public void unSetSincronizacionRemota(){
		sincronizador = null;
	}

	public boolean isSincronizacionRemota(){
		return sincronizador != null;
	}

	public void setSincronizacionRemotaPeriodica(){
		sincronizacionPeriodica = true;
	}

	public void unSetSincronizacionRemotaPeriodica(){
		sincronizacionPeriodica = false;
	}

	public boolean isSincronizacionRemotaPeriodica(){
		return sincronizacionPeriodica;
	}

	public void setBdCreada(){
		bdCreada = true;
		pullRemoto();
	}

	public boolean isBdCreada(){
		return bdCreada;
	}

	public boolean isBdAbierta(){
		return bdAbierta;
	}

	public void initSincronizacionRemotaPeriodica() {

		if (isSincronizacionRemota() && isSincronizacionRemotaPeriodica()) {
			EditorConfiguracion configuracion = new EditorConfiguracion(context);
			sincronizador.temporizador(configuracion.obtenerIntervaloSinc());
		}
	}

	public void pullRemoto(){
		if(isSincronizacionRemota() && isBdAbierta()){
			sincronizador.pull();
		}
	}

	public void recargarListado(){
		if(isSincronizacionRemota() && isBdAbierta()){
			((ListaProductos) context).cargarListado();
		}
	}

	/***********************************************/
	/////////////////////////////////////////////////

	private static final String DB_NOMBRE = "datos_smarket";
	public static final String DB_TABLA_PRODUCTOS = "productos";
	public static final String DB_TABLA_MARCAS = "marcas";

	private static final int DB_VERSION = 1;

	/**
	 * Identificador de Producto / Marca
	 */
	public static final String KEY_ID = "id";

	/**
	 * Nombre de Producto / Marca
	 */
	public static final String KEY_NOMBRE = "nombre";

	/**
	 * Solo para Producto Identificador del tipo
	 */
	public static final String KEY_TIPO = "tipo";

	/**
	 * Solo para Producto Identificador de la Marca asociada
	 */
	public static final String KEY_ID_MARCA = "id_marca";
	public static final String KEY_DIMENSION = "dimension_pantalla";
	public static final String KEY_SIST_OP = "sistema_operativo";
	public static final String KEY_PRECIO = "precio";
	public static final String KEY_DESCRIPCION = "descripcion";
	public static final String KEY_IMAGEN = "imagen";
	public static final String KEY_EN_OFERTA = "en_oferta";
	public static final String KEY_PRECIO_OFERTA = "precio_oferta";

	public static final String DB_CREAR_PRODUCTOS = "create table if not exists "
			+ DB_TABLA_PRODUCTOS
			+ " ("
			+ KEY_ID
			+ " integer primary key autoincrement,"
			+ KEY_NOMBRE
			+ " text not null,"
			+ KEY_TIPO
			+ " integer not null,"
			+ KEY_ID_MARCA
			+ " integer not null default -1,"
			+ KEY_DIMENSION
			+ " real not null,"
			+ KEY_SIST_OP
			+ " integer not null,"
			+ KEY_PRECIO
			+ " real not null,"
			+ KEY_DESCRIPCION
			+ " text not null,"
			+ KEY_IMAGEN
			+ " blob not null,"
			+ KEY_EN_OFERTA
			+ " integer not null default 0,"
			+ KEY_PRECIO_OFERTA 
			+ " real not null default 0.0" 
			+ ");";

	public static final String DB_CREAR_MARCAS = "create table if not exists "
			+ DB_TABLA_MARCAS 
			+ " (" 
			+ KEY_ID
			+ " integer primary key autoincrement," 
			+ KEY_NOMBRE
			+ " text not null" + ");";

	// Operaciones
	private static final int DB_INSERTAR = 0;
	private static final int DB_ACTUALIZAR = 1;

	public static final int DB_ORDENACION_NO = -1;
	public static final int DB_ORDENACION_PRECIO = 0;
	public static final int DB_ORDENACION_NOMBRE = 1;

	public static final int DB_ORDENACION_ASC = 0;
	public static final int DB_ORDENACION_DESC = 1;

	public static final int DB_BUSQUEDA_PRECIO = 0;
	public static final int DB_BUSQUEDA_PULGADAS = 1;
	public static final int DB_BUSQUEDA_SO = 2;
	public static final int DB_BUSQUEDA_TIPO = 3;
	public static final int DB_BUSQUEDA_OFERTA = 4;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NOMBRE, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase bd) {

			bd.execSQL(DB_CREAR_PRODUCTOS);
			bd.execSQL(DB_CREAR_MARCAS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase bd, int oldVersion, int newVersion) {
			bd.execSQL("DROP TABLE IF EXISTS " + DB_TABLA_PRODUCTOS);
			bd.execSQL("DROP TABLE IF EXISTS " + DB_TABLA_MARCAS);
			onCreate(bd);
		}
	}

	/**
	 * Constructor
	 */
	public AdaptadorBD(Context ctx) {

		this.context = ctx;
	}

	public void open() throws SQLException {

		bdHelper = new DatabaseHelper(context);
		bd = bdHelper.getWritableDatabase();
		bdAbierta = true;
	}

	public void close() {
		bd.close();
		bdAbierta = false;
	}

	public void crearLogs(){

		if(sincronizador != null){

			bd.execSQL(DB_CREAR_LOGS);

			if(getUltimoLog() == -1){
				ContentValues valores = new ContentValues();
				valores.put(KEY_ID, 0);
				bd.insert(DB_TABLA_LOGS, null, valores);
			}

		}
	}

	public int getUltimoLog(){

		if(sincronizador != null){

			int idLog = -1;

			Cursor resultado = bd.query(DB_TABLA_LOGS, null, null, null, null, null, null);

			if (resultado != null) {

				resultado.moveToFirst();

				if (!resultado.isAfterLast()) {

					idLog = resultado.getInt(0);
				}
			}

			return idLog;

		} else {

			return -2;
		}
	}

	public int setUltimoLog(int idLog){

		if(sincronizador != null){

			ContentValues valores = new ContentValues();
			valores.put(KEY_ID, idLog);
			return bd.update(DB_TABLA_LOGS, valores, null, null);

		} else {

			return -1;
		}
	}

	public void poblarBD() {
		/* Obtener imagen */

		Resources res = context.getResources();
		int id = R.drawable.smarket;
		Bitmap imagen = BitmapFactory.decodeResource(res, id);

		/* Rellena tabla de marcas */

		for (int i = 1; i < 5; i++) {

			Marca m = new Marca("Marca " + i);

			crearMarca(m);
		}

		/* Rellena tabla de productos */

		List<Marca> marcas = obtenerMarcas();

		for (int i = 1; i < 22; i++) {

			int marca = (int) (Math.random() * (double) marcas.size());
			int tipo = (int) (Math.random() * 2.0);
			double precio = Math.random() * 650.0;
			String descripcion = "El mejor del mercado.";
			Producto p = new Producto("Producto " + i, marcas.get(marca), tipo,
					precio);

			p.setImagen(imagen);
			p.setDescripcion(descripcion);
			p.setDimensionPantalla(3.7);

			if ((int) (Math.random() * 2.0) == 0) {
				p.setOferta();
				p.setPrecioOferta(p.getPrecio() * 0.75);
			}

			crearProducto(p);
		}
	}

	/**
	 * Devuelve el nombre del Sistema Operativo asociado
	 * 
	 * @param idSistemaOperativo
	 *            Identificador del sistema operativo
	 * @return Nombre del sistema operativo asociado al identificador
	 */
	public static String obtenerSistemaOperativo(int idSistemaOperativo) {

		if (idSistemaOperativo == 0) {
			return "Android";

		} else if (idSistemaOperativo == 1) {
			return "Windows Phone";

		} else {
			return "iOS";
		}
	}

	/**
	 * Devuelve el nombre del Tipo de Producto asociado
	 * 
	 * @param idTipo
	 *            Identificador del tipo de Producto
	 * @return Nombre del tipo de producto asociado al identificador
	 */
	public static String obtenerTipo(int idTipo) {

		if (idTipo == 0) {
			return "SmartPhone";

		} else {
			return "Tablet";
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRODUCTOS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Devuelve todos los Productos almacenados en la BD
	 * 
	 * @return Lista de Productos
	 */
	public List<Producto> obtenerProductos() {

		return obtenerProductos(DB_ORDENACION_NO, DB_ORDENACION_DESC);
	}

	/**
	 * Devuelve todos los Productos almacenados en la BD
	 * 
	 * @param ordenacion
	 *            Tipo de ordenacion (Precio o Nombre) <br/>
	 *            <b> odenacion = [ DB_ORDENACION_PRECIO | DB_ORDENACION_NOMBRE
	 *            ] </b>
	 * @param direccion
	 *            Sentido de la ordenacion <br/>
	 *            <b> direccion = [ DB_ORDENACION_ASC | DB_ORDENACION_DESC ] </b>
	 * @return Lista de Productos
	 */
	public List<Producto> obtenerProductos(int ordenacion, int direccion) {

		String sentido = "";

		if(direccion == DB_ORDENACION_ASC){
			sentido = " ASC";
		} else {
			sentido = " DESC";
		}

		// Ordenacion

		String orden = KEY_EN_OFERTA + sentido;

		if (ordenacion == DB_ORDENACION_PRECIO) {

			orden += ", " + KEY_PRECIO + sentido;

		} else if (ordenacion == DB_ORDENACION_NOMBRE) {

			orden += ", " + KEY_NOMBRE + sentido;

		}

		// Consulta

		Cursor resultado = bd.query(DB_TABLA_PRODUCTOS, null, null, null, null,
				null, orden);

		List<Producto> productos = new ArrayList<Producto>();

		if (resultado != null) {

			resultado.moveToFirst();

			while (!resultado.isAfterLast()) {

				Producto p = new Producto();

				bd2producto(resultado, p);

				productos.add(p);

				resultado.moveToNext();
			}
		}

		return productos;
	}

	/**
	 * Devuelve todos los nombres de los Productos almacenados en la BD. <br/>
	 * Ordenados alfabeticamente por nombre
	 * 
	 * @return Lista de nombres de Productos o NULL si no hay resultados
	 */
	public String[] obtenerNombreProductos() {

		String[] columnas = new String[] { KEY_NOMBRE };

		Cursor resultado = bd.query(DB_TABLA_PRODUCTOS, columnas, null, null,
				null, null, KEY_NOMBRE + " DESC");

		String[] nombres = null;

		if (resultado != null) {

			nombres = new String[resultado.getCount()];

			resultado.moveToFirst();

			int i = 0;

			while (!resultado.isAfterLast()) {

				nombres[i] = resultado.getString(resultado
						.getColumnIndex(KEY_NOMBRE));

				i++;
				resultado.moveToNext();
			}
		}

		return nombres;
	}

	/**
	 * Devuelve el Producto con el identificador [id] almacenado en la BD
	 * 
	 * @param id
	 *            Identificador del Producto
	 * @return Producto con identificador igual a [id]
	 */
	public Producto obtenerProducto(int id) {

		Producto producto = new Producto();

		Cursor resultado = bd.query(DB_TABLA_PRODUCTOS, null, KEY_ID + " = "
				+ id, null, null, null, null);

		if (resultado != null) {

			resultado.moveToFirst();

			if (!resultado.isAfterLast()) {

				bd2producto(resultado, producto);
			}
		}

		return producto;
	}

	/**
	 * Almacena un Producto en la BD. <br/>
	 * <b> El identificador se crea al almacenar, por lo que no hace falta
	 * especificarlo antes. </b>
	 * 
	 * @param producto
	 *            Producto que se desea almacenar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean crearProducto(Producto producto) {

		return crearProducto(producto, ORIGEN_APP);

	}

	public boolean crearProducto(Producto producto, int origen) {
		
		// Actualizar BD local por si acaso
		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.pull();
		}

		boolean resultado = producto2bd(producto, DB_INSERTAR);

		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.push(
					SincronizadorRemotoAsincrono.LOG_OP_CREAR_PRODUCTO,
					producto.getId());
		}

		return resultado;
	}

	/**
	 * Actualiza un Producto almacenado en la BD. <br/>
	 * <b> Se utiliza el identificador [id] como clave para actualizar. </b>
	 * 
	 * @param producto
	 *            Producto que se desea actualizar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean actualizarProducto(Producto producto) {

		return actualizarProducto(producto, ORIGEN_APP);
	}
	
	public boolean actualizarProducto(Producto producto, int origen) {
		
		// Actualizar BD local por si acaso
		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.pull();
		}

		boolean resultado = producto2bd(producto, DB_ACTUALIZAR);

		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.push(
					SincronizadorRemotoAsincrono.LOG_OP_ACTUALIZAR_PRODUCTO,
					producto.getId());
		}

		return resultado;
	}

	/**
	 * Elimina un Producto de la BD. <br/>
	 * <b> Se utiliza el identificador [id] como clave para eliminar. </b>
	 * 
	 * @param id
	 *            Identificador del Producto
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean borrarProducto(int id) {

		return borrarProducto(id, ORIGEN_APP);
	}
	
	public boolean borrarProducto(int id, int origen) {
		
		// Actualizar BD local por si acaso
		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.pull();
		}

		boolean resultado = bd.delete(DB_TABLA_PRODUCTOS, KEY_ID + "=" + id, null) > 0;

		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.push(
					SincronizadorRemotoAsincrono.LOG_OP_BORRAR_PRODUCTO, id);
		}

		return resultado;
	}

	/**
	 * Devuelve todos los Productos que contengan [cadena] en su nombre <br/>
	 * 
	 * @param cadena
	 *            Palabra que se debe encontrar en el nombre del Producto
	 * @return Lista de Productos filtrados
	 */
	public List<Producto> buscarProducto(String cadena) {

		// Previene inyecciones SQL
		cadena = cadena.replaceAll("'", "");
		cadena = cadena.replaceAll("0x27", "");
		cadena = cadena.replaceAll("%", "");

		// Consulta
		Cursor resultado = bd.query(DB_TABLA_PRODUCTOS, null,

				KEY_NOMBRE + " LIKE '%" + cadena + "%'",

				null, null, null, KEY_NOMBRE + " DESC");

		List<Producto> productos = new ArrayList<Producto>();

		if (resultado != null) {

			resultado.moveToFirst();

			while (!resultado.isAfterLast()) {

				Producto p = new Producto();

				bd2producto(resultado, p);

				productos.add(p);

				resultado.moveToNext();
			}
		}

		return productos;
	}

	/**
	 * Devuelve todos los Productos aplicando un filtro y un orden
	 * 
	 * @param filtro
	 *            Filtro a aplicar
	 * @param orden
	 *            Orden a aplicar
	 * @return Lista de Productos filtrados y ordenados
	 */
	public List<Producto> FiltrarYOrdenarProducto(Filtro filtro, Orden orden) {
		// Consulta
		Cursor resultado = bd.query(DB_TABLA_PRODUCTOS, null,
				filtro.getBusqueda(), null, null, null, orden.getOrden());

		List<Producto> productos = new ArrayList<Producto>();

		if (resultado != null) {

			resultado.moveToFirst();

			while (!resultado.isAfterLast()) {

				Producto p = new Producto();

				bd2producto(resultado, p);

				productos.add(p);

				resultado.moveToNext();
			}
		}

		return productos;
	}

	/**
	 * Devuelve todos los Productos aplicando un filtro y un orden
	 * 
	 * @param filtro
	 *            Filtro a aplicar
	 * @return Lista de Productos filtrados y ordenados
	 */
	public List<Producto> FiltrarProducto(Filtro filtro) {
		// Consulta
		Cursor resultado = bd.query(DB_TABLA_PRODUCTOS, null,
				filtro.getBusqueda(), null, null, null, null);

		List<Producto> productos = new ArrayList<Producto>();

		if (resultado != null) {

			resultado.moveToFirst();

			while (!resultado.isAfterLast()) {

				Producto p = new Producto();

				bd2producto(resultado, p);

				productos.add(p);

				resultado.moveToNext();
			}
		}

		return productos;
	}

	/**
	 * Devuelve todos los Productos aplicando un orden
	 * 
	 * @param orden
	 *            Orden a aplicar
	 * @return Lista de Productos filtrados y ordenados
	 */
	public List<Producto> OrdenarProducto(Orden orden) {

		// Consulta
		Cursor resultado = bd.query(DB_TABLA_PRODUCTOS, null,
				null, null, null, null, orden.getOrden());

		List<Producto> productos = new ArrayList<Producto>();

		if (resultado != null) {

			resultado.moveToFirst();

			while (!resultado.isAfterLast()) {

				Producto p = new Producto();

				bd2producto(resultado, p);

				productos.add(p);

				resultado.moveToNext();
			}
		}

		return productos;
	}

	/**
	 * Rellena los atributos de un producto a partir de los valores obtenidos de
	 * la BD
	 */
	private void bd2producto(Cursor res, Producto p) {

		// ID
		p.setId(res.getInt(res.getColumnIndex(KEY_ID)));

		// NOMBRE
		p.setNombre(res.getString(res.getColumnIndex(KEY_NOMBRE)));

		// TIPO
		p.setTipo(res.getInt(res.getColumnIndex(KEY_TIPO)));

		// MARCA
		int idMarca = res.getInt(res.getColumnIndex(KEY_ID_MARCA));
		p.setMarca(obtenerMarca(idMarca));

		// DIMENSION DE PANTALLA
		p.setDimensionPantalla(res.getDouble(res.getColumnIndex(KEY_DIMENSION)));

		// SISTEMA OPERATIVO
		p.setSistemaOperativo(res.getInt(res.getColumnIndex(KEY_SIST_OP)));

		// PRECIO
		p.setPrecio(res.getDouble(res.getColumnIndex(KEY_PRECIO)));

		// DESCRIPCION
		p.setDescripcion(res.getString(res.getColumnIndex(KEY_DESCRIPCION)));

		// IMAGEN
		byte[] imagenArray = res.getBlob(res.getColumnIndex(KEY_IMAGEN));
		ByteArrayInputStream imagenStream = new ByteArrayInputStream(
				imagenArray);
		Bitmap imagen = BitmapFactory.decodeStream(imagenStream);
		p.setImagen(imagen);

		// EN OFERTA
		int oferta = res.getInt(res.getColumnIndex(KEY_EN_OFERTA));
		if (oferta == 1) {
			p.setOferta();

		} else {
			p.unsetOferta();
		}

		// PRECIO EN OFERTA
		p.setPrecioOferta(res.getDouble(res.getColumnIndex(KEY_PRECIO_OFERTA)));
	}

	/**
	 * Rellena los valores de la BD a partir de los atributos de un producto
	 */
	private boolean producto2bd(Producto producto, int operacion) {

		ContentValues valores = new ContentValues();

		//ID (si tiene)
		if(producto.getId() > 0){
			valores.put(KEY_ID, producto.getId());
		}

		// NOMBRE
		valores.put(KEY_NOMBRE, producto.getNombre());

		// TIPO
		valores.put(KEY_TIPO, producto.getTipo());

		// MARCA
		valores.put(KEY_ID_MARCA, producto.getMarca().getId());

		// DIMENSION PANTALLA
		valores.put(KEY_DIMENSION, producto.getDimensionPantalla());

		// SISTEMA OPERATIVO
		valores.put(KEY_SIST_OP, producto.getSistemaOperativo());

		// PRECIO
		valores.put(KEY_PRECIO, producto.getPrecio());

		// DESCRIPCION
		valores.put(KEY_DESCRIPCION, producto.getDescripcion());

		// IMAGEN
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Bitmap imagen = producto.getImagen();
		imagen.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] imagenArray = stream.toByteArray();
		valores.put(KEY_IMAGEN, imagenArray);

		// EN OFERTA
		if (producto.isOferta()) {
			valores.put(KEY_EN_OFERTA, 1);
		} else {
			valores.put(KEY_EN_OFERTA, 0);
		}

		// PRECIO EN OFERTA
		valores.put(KEY_PRECIO_OFERTA, producto.getPrecioOferta());

		// METER A BD
		if (operacion == DB_INSERTAR) {

			// Inserta y actualiza el id del producto
			int id = (int) bd.insert(DB_TABLA_PRODUCTOS, null, valores);
			producto.setId(id);

			// Si no ha habido errores devuelve true
			return id != -1;

		} else if (operacion == DB_ACTUALIZAR && producto.getId() > 0) {
			return bd.update(DB_TABLA_PRODUCTOS, valores, KEY_ID + "="
					+ producto.getId(), null) > 0;

		} else {
			return false;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// MARCAS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Devuelve todas las Marcas almacenadas en la BD
	 * 
	 * @return Lista de Marcas
	 */
	public List<Marca> obtenerMarcas() {

		List<Marca> marcas = new ArrayList<Marca>();

		Cursor resultado = bd.query(DB_TABLA_MARCAS, null, null, null, null,
				null, KEY_NOMBRE + " DESC");

		if (resultado != null) {

			resultado.moveToFirst();

			while (!resultado.isAfterLast()) {

				Marca marca = new Marca();

				bd2marca(resultado, marca);

				marcas.add(marca);

				resultado.moveToNext();
			}
		}

		return marcas;
	}

	/**
	 * Devuelve todos los nombres de las Marcas almacenadas en la BD. <br/>
	 * Ordenadas alfabeticamente por nombre
	 * 
	 * @return Lista de nombres de Marcas o NULL si no hay resultados
	 */
	public String[] obtenerNombreMarcas() {

		String[] columnas = new String[] { KEY_NOMBRE };

		Cursor resultado = bd.query(DB_TABLA_MARCAS, columnas, null, null,
				null, null, KEY_NOMBRE + " DESC");

		String[] nombres = null;

		if (resultado != null) {

			nombres = new String[resultado.getCount()];

			resultado.moveToFirst();

			int i = 0;

			while (!resultado.isAfterLast()) {

				nombres[i] = resultado.getString(resultado
						.getColumnIndex(KEY_NOMBRE));

				i++;
				resultado.moveToNext();
			}
		}

		return nombres;
	}

	/**
	 * Devuelve la Marca con el mismo identificador [id] almacenado en la BD
	 * 
	 * @param id
	 *            Identificador de la Marca
	 * @return Marca con identificador igual a [id]
	 */
	public Marca obtenerMarca(int id) {

		Marca marca = new Marca();

		Cursor resultado = bd.query(DB_TABLA_MARCAS, null, KEY_ID + " = " + id,
				null, null, null, null);

		if (resultado != null) {

			resultado.moveToFirst();

			if (!resultado.isAfterLast()) {

				bd2marca(resultado, marca);
			}
		}

		return marca;
	}

	/**
	 * Almacena la Marca en la BD. <br/>
	 * <b> El id se crea al almacenar, por lo que no hace falta especificarlo
	 * antes. </b>
	 * 
	 * @param marca
	 *            Marca que se desea almacenar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean crearMarca(Marca marca) {

		return crearMarca(marca, ORIGEN_APP);
	}
	
	public boolean crearMarca(Marca marca, int origen) {
		
		// Actualizar BD local por si acaso
		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.pull();
		}

		boolean resultado = marca2bd(marca, DB_INSERTAR);

		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.push(
					SincronizadorRemotoAsincrono.LOG_OP_CREAR_MARCA, marca.getId());
		}

		return resultado;
	}

	/**
	 * Actualiza una Marca almacenada en la BD. <br/>
	 * <b> Se utiliza el id como clave para actualizar. </b>
	 * 
	 * @param marca
	 *            Marca que se desea actualizar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean actualizarMarca(Marca marca) {

		return actualizarMarca(marca, ORIGEN_APP);
	}
	
	public boolean actualizarMarca(Marca marca, int origen) {
		
		// Actualizar BD local por si acaso
		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.pull();
		}

		boolean resultado = marca2bd(marca, DB_ACTUALIZAR);

		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.push(
					SincronizadorRemotoAsincrono.LOG_OP_ACTUALIZAR_MARCA, marca.getId());
		}

		return resultado;
	}

	/**
	 * Elimina una Marca de la BD. <br/>
	 * <b> Se utiliza el id como clave para eliminar. </b>
	 * 
	 * @param id
	 *            Identificador de la Marca
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean borrarMarca(int id) {

		return borrarMarca(id, ORIGEN_APP);
	}
	
	public boolean borrarMarca(int id, int origen) {
		
		// Actualizar BD local por si acaso
		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.pull();
		}

		boolean borrado = bd.delete(DB_TABLA_MARCAS, KEY_ID + "=" + id, null) > 0;

		if (borrado) {
			ContentValues valores = new ContentValues();
			valores.put(KEY_ID_MARCA, "-1");

			bd.update(DB_TABLA_PRODUCTOS, valores, KEY_ID_MARCA + " = " + id,
					null);
		}

		if (origen == ORIGEN_APP && isSincronizacionRemota()) {
			sincronizador.push(
					SincronizadorRemotoAsincrono.LOG_OP_BORRAR_MARCA, id);
		}

		return borrado;
	}
	
	public boolean isMarcaUsada(int id){
		
		String sql = "SELECT COUNT(*) FROM " + DB_TABLA_PRODUCTOS + " WHERE "
				+ KEY_ID_MARCA + " = " + id;
		
		Cursor resultado = bd.rawQuery(sql, null);
		
		if (resultado != null) {

			resultado.moveToFirst();

			return resultado.getInt(0) > 0;
		}
		
		//evitar que se borre por error
		return true;
	}

	/**
	 * Devuelve todos las Marcas que contengan [cadena] en su nombre <br/>
	 * 
	 * @param cadena
	 *            Palabra que se debe encontrar en el nombre de la Marca
	 * @return Lista de Marcas filtradas
	 */
	public List<Marca> buscarMarca(String cadena) {

		List<Marca> marcas = new ArrayList<Marca>();

		// Previene inyecciones SQL
		cadena = cadena.replaceAll("'", "");
		cadena = cadena.replaceAll("0x27", "");
		cadena = cadena.replaceAll("%", "");

		// Consulta
		Cursor resultado = bd.query(DB_TABLA_MARCAS, null,

				KEY_NOMBRE + " LIKE '%" + cadena + "%'",

				null, null, null, KEY_NOMBRE + " DESC");

		if (resultado != null) {

			resultado.moveToFirst();

			while (!resultado.isAfterLast()) {

				Marca marca = new Marca();

				bd2marca(resultado, marca);

				marcas.add(marca);

				resultado.moveToNext();
			}
		}

		return marcas;
	}

	/**
	 * Rellena los atributos de una marca a partir de los valores obtenidos de
	 * la BD
	 */
	private void bd2marca(Cursor res, Marca marca) {

		// ID
		marca.setId(res.getInt(res.getColumnIndex(KEY_ID)));

		// NOMBRE
		marca.setNombre(res.getString(res.getColumnIndex(KEY_NOMBRE)));
	}

	/**
	 * Rellena los valores de la BD a partir de los atributos de una marca
	 */
	private boolean marca2bd(Marca marca, int operacion) {

		ContentValues valores = new ContentValues();


		// NOMBRE
		valores.put(KEY_NOMBRE, marca.getNombre());

		// INSERTAR
		if (operacion == DB_INSERTAR) {

			//ID (si tiene)
			if(marca.getId() > 0){
				valores.put(KEY_ID, marca.getId());
			}

			// Inserta y actualiza el id de la marca
			int id = (int) bd.insert(DB_TABLA_MARCAS, null, valores);
			marca.setId(id);

			// Si no ha habido errores devuelve true
			return id != -1;

			// ACTUALIZAR
		} else if (operacion == DB_ACTUALIZAR && marca.getId() > 0) {

			return bd.update(DB_TABLA_MARCAS, valores,
					KEY_ID + "=" + marca.getId(), null) > 0;

		} else {

			return false;
		}
	}
}
