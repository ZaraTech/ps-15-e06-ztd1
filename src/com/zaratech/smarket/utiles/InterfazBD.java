package com.zaratech.smarket.utiles;

import java.util.List;

import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;

/**
 * Interfaz que abstrae la funcionalidad ofrecida por la base de datos.
 * 
 * @author Juan
 */
public interface InterfazBD {
	
	// PRODUCTOS
	
	/**
	 * Devuelve todos los Productos almacenados en la BD
	 * @return Lista de Productos
	 */
	public List<Producto> obtenerProductos();
	
	/**
	 * Devuelve el Producto con el identificador [id] almacenado en la BD
	 * @param id Identificador del Producto
	 * @return Producto con identificador igual a [id]
	 */
	public Producto obtenerProducto(int id);
	
	/**
	 * Almacena un Producto en la BD. <br/>
	 * <b> El identificador se crea al almacenar, por lo que no hace falta
	 * especificarlo antes. </b>
	 * @param producto Producto que se desea almacenar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean crearProducto(Producto producto);
	
	/**
	 * Actualiza un Producto almacenado en la BD. <br/>
	 * <b> Se utiliza el identificador [id] como clave para actualizar. </b>
	 * @param producto Producto que se desea actualizar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean actualizarProducto(Producto producto);
	
	/**
	 * Elimina un Producto de la BD. <br/>
	 * <b> Se utiliza el identificador [id] como clave para eliminar. </b>
	 * @param id Identificador del Producto
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean borrarProducto(int id);
	
	
	// MARCAS	
	
	/**
	 * Devuelve todas las Marcas almacenadas en la BD
	 * @return Lista de Marcas
	 */
	public List<Marca> obtenerMarcas();
	
	/**
	 * Devuelve la Marca con el mismo identificador [id] almacenado en la BD
	 * @param id Identificador de la Marca
	 * @return Marca con identificador igual a [id]
	 */
	public Marca obtenerMarca(int id);
	
	/**
	 * Almacena la Marca en la BD. <br/>
	 * <b> El id se crea al almacenar, por lo que no hace falta especificarlo antes. </b>
	 * @param marca Marca que se desea almacenar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean crearMarca(Marca marca);
	
	/**
	 * Actualiza una Marca almacenada en la BD. <br/>
	 * <b> Se utiliza el id como clave para actualizar. </b>
	 * @param marca Marca que se desea actualizar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean actualizarMarca(Marca marca);
	
	/**
	 * Elimina una Marca de la BD. <br/>
	 * <b> Se utiliza el id como clave para eliminar. </b>
	 * @param id Identificador de la Marca
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean borrarMarca(int id);
	
}
