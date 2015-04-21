package com.zaratech.smarket.utiles;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;

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
	private static BD bd = null;
	
	/**
	 * Constructor 
	 */
	public AdaptadorBD(){
		
		if(bd == null){
			bd = new BD();
			bd.open();
		}
	}
	
	public void close(){
		bd.close();
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

		List<Producto> productos = new ArrayList<Producto>();
		
		for(Producto p: bd.getTabla_productos()){
			Producto nuevoProducto = p.clonar();
			productos.add(nuevoProducto);
		}
		
		return productos;

	}

	public Producto obtenerProducto(int id) {
		
		for(Producto p: bd.getTabla_productos()){
			
			if(p.getId() == id){
				Producto nuevoProducto = p.clonar();			
				return nuevoProducto;
			}
		}

		return null;
	}

	public Producto obtenerProducto(String nombre) {
		
		for(Producto p: bd.getTabla_productos()){
			
			if(p.getNombre().compareTo(nombre) == 0){
				Producto nuevoProducto = p.clonar();			
				return nuevoProducto;
			}
		}
		
		return null;
	}

	public boolean crearProducto(Producto producto) {
		
		producto.setId(bd.getContadorProductos());
		
		boolean exito = bd.getTabla_productos().add(producto);
		
		if(exito){
			bd.incrementarContadorProductos();			
		}
		
		return exito;
	}

	public boolean actualizarProducto(Producto producto) {
		
		for(Producto p: bd.getTabla_productos()){
			
			if(p.getId() == producto.getId()){
								
				p.setNombre(new String(producto.getNombre()));
				p.setTipo(producto.getTipo());
				p.setMarca(producto.getMarca().clonar());
				p.setDimensionPantalla(producto.getDimensionPantalla());
				p.setSistemaOperativo(producto.getSistemaOperativo());
				p.setPrecio(producto.getPrecio());
				p.setDescripcion(new String(producto.getDescripcion()));
				
				Bitmap imagen = producto.getImagen();
				Bitmap nuevaImagen = imagen.copy(imagen.getConfig(), true);
				p.setImagen(nuevaImagen);
				
				if(producto.isOferta()){
					p.setOferta();
				} else {
					p.unsetOferta();
				}
				p.setPrecioOferta(producto.getPrecioOferta());
				
				return true;
			}
		}
		
		return false;
	}

	public boolean borrarProducto(int id) {

		for(Producto p: bd.getTabla_productos()){
			
			if(p.getId() == id){
			
				return bd.getTabla_productos().remove(p);
			}
		}
		
		return false;
	}

	public List<Marca> obtenerMarcas(){
		
		List<Marca> marcas = new ArrayList<Marca>();
		
		for(Marca m: bd.getTabla_marcas()){
			Marca nuevaMarca = m.clonar();
			marcas.add(nuevaMarca);
		}
		
		return marcas;
	}
	
	public Marca obtenerMarca(int id) {
		
		for(Marca m: bd.getTabla_marcas()){
			
			if(m.getId() == id){
				Marca nuevaMarca = m.clonar();			
				return nuevaMarca;
			}
		}

		return null;
	}

	public Marca obtenerMarca(String nombre) {
		
		for(Marca m: bd.getTabla_marcas()){
			
			if(m.getNombre().compareTo(nombre) == 0){
				Marca nuevaMarca = m.clonar();			
				return nuevaMarca;
			}
		}
		
		return null;
	}

	public boolean crearMarca(Marca marca) {
		
		marca.setId(bd.getContadorMarcas());
		
		boolean exito = bd.getTabla_marcas().add(marca);
		
		if(exito){
			bd.incrementarContadorMarcas();			
		}
		
		return exito;
	}

	public boolean actualizarMarca(Marca marca) {
		
		for(Marca m: bd.getTabla_marcas()){
			
			if(m.getId() == marca.getId()){				
				m.setNombre(new String(marca.getNombre()));
				return true;
			}
		}
		
		return false;
	}

	public boolean borrarMarca(int id) {
		
		for(Marca m: bd.getTabla_marcas()){
			
			if(m.getId() == id){
				
				return bd.getTabla_marcas().remove(m);
			}
		}
		
		return false;
	}
}
