package com.zaratech.smarket.componentes;

import java.io.Serializable;

/**
 * Clase que gestiona objetos Marca.
 * 
 * @author Juan
 */
public class Marca implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * IDENTIFICADOR de la marca. Corresponde al identificador en la BD (se
	 * asigna cuando se recupera de la BD)
	 */
	private int id;

	/**
	 * NOMBRE de la marca
	 */
	private String nombre;

	
	/**
	 * Crea un objeto de tipo Marca
	 * @param nombre es el nombre asociado a la Marca
	 */
	public Marca(String nombre) {

		this.id = 0;
		this.nombre = nombre;
	}
	
	
	
	/* SETTERS & GETTERS */

	
	// IDENTIFICADOR
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	// NOMBRE
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	// TO STRING
	
	@Override
	public String toString() {
		return "Marca [id=" + id + ", nombre=" + nombre + "]";
	}

}
