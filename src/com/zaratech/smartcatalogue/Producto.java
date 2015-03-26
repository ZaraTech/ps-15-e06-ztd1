package com.zaratech.smartcatalogue;

public class Producto {

	private String nombre;
	
	private String marca;
	
	private double precio;
	
	private String descripcion;
	
	
	public Producto(String nombre, String marca, double precio) {
		
		this.nombre = nombre;
		this.marca = marca;
		this.precio = precio;
		this.descripcion = "";
	}
	public Producto(String nombre, String marca, double precio, String descripcion) {
		
		this.nombre = nombre;
		this.marca = marca;
		this.precio = precio;
		this.descripcion = descripcion;
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
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
