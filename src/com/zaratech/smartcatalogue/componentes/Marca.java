package com.zaratech.smartcatalogue.componentes;

public class Marca {

	/**
	 * IDENTIFICADOR de la marca. Corresponde al identificador en la BD (se
	 * asigna cuando se recupera de la BD)
	 */
	private int id;

	/**
	 * NOMBRE de la marca
	 */
	private String nombre;

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

}
