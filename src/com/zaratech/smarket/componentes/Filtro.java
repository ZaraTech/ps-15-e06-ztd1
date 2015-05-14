package com.zaratech.smarket.componentes;

import android.util.Log;

import com.zaratech.smarket.utiles.AdaptadorBD;

/**
 * Objeto que modela un filtro
 * 
 * @author Cristian Romám Morte
 */
public class Filtro {

	private String busqueda = "";

	private int filtros = 0;

	/**
	 * Añade un filtro de nombre de producto
	 * 
	 * @param nombre
	 *            Nombre del producto
	 */
	public void addFiltroNombre(String nombre) {

		// Previene inyecciones SQL
		nombre = nombre.replaceAll("'", "");
		nombre = nombre.replaceAll("0x27", "");
		nombre = nombre.replaceAll("%", "");

		if (filtros != 0) {
			busqueda += " and";
		}
		busqueda += " " + AdaptadorBD.KEY_NOMBRE + " like %" + nombre + "%";
		filtros++;
	}

	/**
	 * Añade un filtro de SO de producto
	 * 
	 * @param idSistemaOperativo
	 *            ID del Sistema operativo
	 */
	public void addFiltroSO(int idSistemaOperativo) {
		if (filtros != 0) {
			busqueda += " and";
		}
		busqueda += " " + AdaptadorBD.KEY_SIST_OP + " = " + idSistemaOperativo;
		filtros++;
	}

	/**
	 * Añade un filtro de la marcade producto
	 * 
	 * @param idMarca
	 *            ID de la marca
	 */
	public void addFiltroMarca(int idMarca) {
		if (filtros != 0) {
			busqueda += " and";
		}
		busqueda += " " + AdaptadorBD.KEY_ID_MARCA + " = " + idMarca;
		filtros++;
	}

	/**
	 * Añade un filtro del rango de pulgadas
	 * 
	 * @param pulgadasDesde
	 *            Valor minimo de las pulgadas
	 * @param pulgadasHasta
	 *            Valor maximo de las pulgadas
	 */
	public void addFiltroPulgadas(float pulgadasDesde, float pulgadasHasta) {
		if (filtros != 0) {
			busqueda += " and";
		}
		busqueda += " " + AdaptadorBD.KEY_DIMENSION + " <= " + pulgadasHasta
				+ " and " + AdaptadorBD.KEY_DIMENSION + " >= " + pulgadasDesde;
		filtros++;
	}

	/**
	 * Añade un filtro del rango de precio
	 * 
	 * @param precioDesde
	 *            Valor minimo del precio
	 * @param precioHasta
	 *            Valor maximo del precio
	 */
	public void addFiltroPrecio(float precioDesde, float precioHasta) {
		if (filtros != 0) {
			busqueda += " and";
		}
		busqueda += "(" + AdaptadorBD.KEY_EN_OFERTA + " = 1 and "
				+ AdaptadorBD.KEY_PRECIO_OFERTA + " <= " + precioHasta
				+ " and " + AdaptadorBD.KEY_PRECIO_OFERTA + " >= "
				+ precioDesde + ") or ( " + AdaptadorBD.KEY_EN_OFERTA
				+ " = 0 and " + AdaptadorBD.KEY_PRECIO + " <= " + precioHasta
				+ " and " + AdaptadorBD.KEY_PRECIO + " >= " + precioDesde + ")";
		filtros++;
	}

	/**
	 * Añade un filtro de los productos en ofertas
	 * 
	 * @param oferta
	 *            true si se desea obtener solo las ofertas false en caso
	 *            contrario
	 */
	public void addFiltroOferta(boolean oferta) {
		if (filtros != 0) {
			busqueda += " and";
		}
		busqueda += " " + AdaptadorBD.KEY_EN_OFERTA + " = " + (oferta ? 1 : 0);
		filtros++;
	}

	public String getBusqueda() {
		Log.d("BUUUSQUEDA", busqueda);
		return busqueda;
	}

	public void addFiltroTipo(int tipo) {
		if (filtros != 0) {
			busqueda += " and";
		}
		busqueda += " " + AdaptadorBD.KEY_TIPO + " = " + tipo;
		filtros++;

	}
}
