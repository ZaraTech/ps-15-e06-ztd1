package com.zaratech.smartcatalogue.utiles;

import java.util.ArrayList;
import java.util.List;
import com.zaratech.smartcatalogue.componentes.Marca;
import com.zaratech.smartcatalogue.componentes.Producto;

/**
 * Clase que se utiliza como adaptador para una base de datos.
 * 
 * @author Marta 
 * @author Juan
 */
public class AdaptadorBD implements InterfazBD {

	/**
	 * Contadores para simular secuenciacion de identificadores 
	 */
	private static int contadorProductos = 0;
	private static int contadorMarcas = 0;
	
	/** 
	 * Tabla de productos 
	 */
    List<Producto> tabla_productos = new ArrayList<Producto>();
    
    /**
     * Tabla de marcas 
     */
    List<Marca> tabla_marcas = new ArrayList<Marca>();
    

    /**
     * Metodo que conecta con la base de datos (en este caso se rellena una 
     * base de datos simulada).
     */
    public void open(){
    	
    	/* Rellena tabla de marcas */
    	
    	for(int i=1; i<5; i++){
    		Marca m = new Marca("Marca " + i);
    		m.setId(i);
    		contadorMarcas++;
    		
    		tabla_marcas.add(m);
    	}
    	
    	/* Rellena tabla de productos*/
    	
    	for(int i=1; i<22; i++){
    		
    		int marca = (int)(Math.random()*tabla_marcas.size() + 1);
    		int tipo = (int)(Math.random()*2.0);
    		double precio = Math.random()*300+50;
    		Producto p = new Producto("Producto " + i, marca, tipo, precio);
    		p.setId(i);
    		contadorProductos++;
    		
    		tabla_productos.add(p);
    	}
    }
    
    /**
     * Cierra la conexion con la base de datos (en este caso en el que se
     * trabaja con una base de datos virtual no hace nada).
     */
    public void close() {
    	//
    }
    
	/**
	 * Devuelve el nombre del Sistema Operativo asociado
	 * @param idSistemaOperativo Identificador del sistema operativo
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
	 * @param idTipo Identificador del tipo de Producto
	 * @return Nombre del tipo de producto asociado al identificador
	 */
	public static String obtenerTipo(int idTipo) {

		if (idTipo == 0) {
			return "SmartPhone";

		} else {
			return "Tablet";
			
		}
	}

	public List<Producto> obtenerProductos() {

		return tabla_productos;

	}

	public Producto obtenerProducto(int id) {
		
		for(Producto p:tabla_productos){
			
			if(p.getId() == id){
				return p;
			}
		}

		return null;
	}

	public Producto obtenerProducto(String nombre) {
		
		for(Producto p: tabla_productos){
			if(p.getNombre().compareTo(nombre) == 0){
				return p;
			}
		}
		
		return null;
	}

	public boolean crearProducto(Producto producto) {
		
		boolean exito = tabla_productos.add(producto);
		
		if(exito){
			contadorProductos++;
			producto.setId(contadorProductos);
		}
		
		return exito;
	}

	public boolean actualizarProducto(Producto producto) {
		
		for(Producto p: tabla_productos){
			
			if(p.getId() == producto.getId()){
				
				p.setNombre(producto.getNombre());
				p.setTipo(producto.getTipo());
				p.setMarca(producto.getMarca());
				p.setDimensionPantalla(producto.getDimensionPantalla());
				p.setSistemaOperativo(producto.getSistemaOperativo());
				p.setPrecio(producto.getPrecio());
				p.setDescripcion(producto.getDescripcion());
				p.setImagen(producto.getImagen());
				
				if(producto.isOferta()){
					p.setOferta();
					p.setPrecioOferta(producto.getPrecioOferta());
				}
				
				return true;
			}
		}
		
		return false;
	}

	public boolean borrarProducto(int id) {

		for(Producto p: tabla_productos){
			
			if(p.getId() == id){
				return tabla_productos.remove(p);
			}
		}
		
		return false;
	}

	public List<Marca> obtenerMarcas(){
		
		return tabla_marcas;
	}
	
	public Marca obtenerMarca(int id) {
		
		for(Marca m: tabla_marcas){

			if(m.getId() == id){
				return m;
			}
		}
		
		return null;
	}

	public Marca obtenerMarca(String nombre) {
		
		for(Marca m: tabla_marcas){
			
			if(m.getNombre().compareTo(nombre) == 0){
				return m;
			}
		}
		
		return null;
	}

	public boolean crearMarca(Marca marca) {
		
		boolean exito = tabla_marcas.add(marca);
		
		if(exito){
			contadorMarcas++;
			marca.setId(contadorMarcas);
		}
		
		return exito;
	}

	public boolean actualizarMarca(Marca marca) {
		
		
		for(Marca m: tabla_marcas){
			
			if(m.getId() == marca.getId()){				
				m.setNombre(marca.getNombre());
				return true;
			}
		}
		
		return false;
	}

	public boolean borrarMarca(int id) {
		
		for(Marca m: tabla_marcas){
			
			if(m.getId() == id){
				return tabla_marcas.remove(m);
			}
		}
		
		return false;
	}
}
