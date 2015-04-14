package com.zaratech.smartcatalogue.utiles;

import java.util.List;

import com.zaratech.smartcatalogue.componentes.Marca;
import com.zaratech.smartcatalogue.componentes.Producto;

public class AdaptadorBD implements InterfazBD {

	/**
	 * Devuelve el nombre del Sistema Operativo asociado
	 * @param idSistemaOperativo Identificador del sistema operativo
	 * @return
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
	 * @return
	 */
	public static String obtenerTipo(int idTipo) {

		if (idTipo == 0) {
			return "SmartPhone";

		} else {
			return "Tablet";

		}
	}

	public List<Producto> obtenerProductos() {
		// TODO Auto-generated method stub
		return null;
	}

	public Producto obtenerProducto(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Producto obtenerProducto(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean crearProducto(Producto producto) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean actualizarProducto(Producto producto) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean borrarProducto(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	public Marca obtenerMarca(int id) {
		
		if(id == 0){
			return new Marca("LG");
			
		} else if (id == 1) {
			return new Marca("Sony");
			
		} else {
			return new Marca("Samsung");
			
		}
	}

	public Marca obtenerMarca(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean crearMarca(Marca marca) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean actualizarMarca(Marca marca) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean borrarMarca(int id) {
		// TODO Auto-generated method stub
		return false;
	}
}
