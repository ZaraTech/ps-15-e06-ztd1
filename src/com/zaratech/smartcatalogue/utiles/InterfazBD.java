package com.zaratech.smartcatalogue.utiles;

import java.util.List;

import com.zaratech.smartcatalogue.componentes.Marca;
import com.zaratech.smartcatalogue.componentes.Producto;

public interface InterfazBD {
	
	// PRODUCTOS
	
	/**
	 * Devuelve todos los Productos almacenados en la BD
	 * @return Lista de Productos
	 */
	public List<Producto> obtenerProductos();
	
	/**
	 * Devuelve el Producto con el mismo id almacenado en la BD
	 * @param id Identificador del Producto
	 * @return Producto
	 */
	public Producto obtenerProducto(int id);
	
	/**
	 * Devuelve el Producto con el mismo nombre almacenado en la BD
	 * @param nombre Nombre del Producto
	 * @return Producto
	 */
	public Producto obtenerProducto(String nombre);
	
	/**
	 * Almacena un Producto en la BD. <br/>
	 * <b> El id se crea al almacenar, por lo que no hace falta especificarlo antes. </b>
	 * @param producto Producto que se desea almacenar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean crearProducto(Producto producto);
	
	/**
	 * Actualiza un Producto almacenado en la BD. <br/>
	 * <b> Se utiliza el id como clave para actualizar. </b>
	 * @param producto Producto que se desea actualizar
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean actualizarProducto(Producto producto);
	
	/**
	 * Elimina un Producto de la BD. <br/>
	 * <b> Se utiliza el id como clave para eliminar. </b>
	 * @param id Identificador del Producto
	 * @return <b>false</b> si ha habido errores
	 */
	public boolean borrarProducto(int id);
	
	
	// MARCAS	
	
	/**
	 * Devuelve la Marca con el mismo id almacenado en la BD
	 * @param id Identificador de la Marca
	 * @return Marca
	 */
	public Marca obtenerMarca(int id);
	
	/**
	 * Devuelve la Marca con el mismo nombre almacenado en la BD
	 * @param nombre Nombre de la Marca
	 * @return Marca
	 */
	public Marca obtenerMarca(String nombre);
	
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
