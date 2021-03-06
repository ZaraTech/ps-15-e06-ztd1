package com.zaratech.smarket.componentes;

import com.zaratech.smarket.utiles.AdaptadorBD;

/**
 * Objeto que modela el orden
 * 
 * @author Cristian Romám Morte
 */
public class Orden {

	private String orden = "";

	public Orden() {

	}

	/**
	 * Declara un orden
	 * 
	 * @param variable
	 *            variable que desea ordenar
	 * @param direcion
	 *            orden en el que se desea ordenar
	 */
	public Orden(int variable, int direcion) {
		setOrden(variable, direcion);
	}

	/**
	 * Declara un orden
	 * 
	 * @param variable
	 *            variable que desea ordenar
	 * @param direcion
	 *            orden en el que se desea ordenar
	 */
	public void setOrden(int variable, int direcion) {
		String dir = "";
		String ord = "";


		if (variable == AdaptadorBD.DB_ORDENACION_NOMBRE) {
			ord = "LOWER(" + AdaptadorBD.KEY_NOMBRE + ")";
		} else {
			ord = "CASE " + AdaptadorBD.KEY_EN_OFERTA + " WHEN 1 THEN "
					+ AdaptadorBD.KEY_PRECIO_OFERTA + " ELSE "
					+ AdaptadorBD.KEY_PRECIO + " END";
		}
		
		if (direcion == AdaptadorBD.DB_ORDENACION_ASC) {
			dir = "ASC";
		} else {
			dir = "DESC";
		}
		
		orden = ord + " " + dir;
	}

	/**
	 * Obtiene la intrucion de ordenacion
	 * 
	 * @return Instrucion de ordenacion
	 */
	public String getOrden() {
		return orden;
	}
}
