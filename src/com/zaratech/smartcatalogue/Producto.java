package com.zaratech.smartcatalogue;

public class Producto {

	private String nombre;
	
	private String marca;
	
	private double precio;
	
	
	
	public Producto(String nombre, String marca, double precio) {
		
		this.nombre = nombre;
		this.marca = marca;
		this.precio = precio;
	}

	
	
	/* SETTERS & GETTERS */
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}
}
