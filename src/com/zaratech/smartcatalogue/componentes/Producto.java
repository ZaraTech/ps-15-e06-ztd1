package com.zaratech.smartcatalogue.componentes;

import android.graphics.Bitmap;


public class Producto {

	/**
	 * NOMBRE del producto
	 */
	private String nombre;
	
	/**
	 * Identificador del TIPO de producto: 
	 * 0 - Smartphone
	 * 1 - Tablet
	 * 
	 */
	private int tipo;
	
	/**
	 * Identificador de la MARCA del producto
	 */
	private int marca;
	
	/**
	 * TAMAÑO DE PANTALLA en diagonal y en pulgadas, del producto
	 */
	private double tamañoPantalla;
	
	/**
	 * Identificador del SISTEMA OPERATIVO del producto:
	 * 0 - Android
	 * 1 - Windows Phone
	 * 2 - iOS
	 */
	private int sistemaOperativo;
	
	/**
	 * PRECIO del producto (cuando NO está en oferta)
	 */
	private double precio;
	
	/**
	 * DESCRIPCIÓN del producto
	 */
	private String descripcion;
	
	/**
	 * IMÁGEN del producto 
	 */
	private Bitmap imagen;
	
	/**
	 * Determina si el producto está en OFERTA o no:
	 * false - NO está en oferta
	 * true - SI está en oferta
	 */
	private boolean enOferta;
	
	/**
	 * PRECIO del producto (cuando está en oferta)
	 */
	private double precioOferta;
	
	/* 
	 * 
	 * Ejemplo de uso de imagenes en bitmaps, con imageviews (xml)
	 * ImageView image = (ImageView) findViewById(R.id.test_image);
	 * Bitmap bMap = BitmapFactory.decodeFile("/sdcard/test2.png");
	 * image.setImageBitmap(bMap);
	 * 
     */
	
	
	
	public Producto(String nombre, int marca, int tipo, double precio) {
		
		this.nombre = nombre;
		this.tipo = tipo;
		this.marca = marca;
		this.tamañoPantalla = 0.0;
		this.sistemaOperativo = 0;
		this.precio = precio;
		this.descripcion = "";
		this.imagen = null;
		this.enOferta = false;
		this.precioOferta = 0.0;
	}
	
	
	
	/* SETTERS & GETTERS */
	
	
	// NOMBRE
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	// TIPO
	
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	
	// MARCA

	public int getMarca() {
		return marca;
	}

	public void setMarca(int marca) {
		this.marca = marca;
	}
	
	
	// TAMAÑO DE PANTALLA
	
	public double getTamañoPantalla() {
		return tamañoPantalla;
	}

	public void setTamañoPantalla(double tamañoPantalla) {
		this.tamañoPantalla = tamañoPantalla;
	}
	
	
	// SISTEMA OPERATIVO

	public int getSistemaOperativo() {
		return sistemaOperativo;
	}

	public void setSistemaOperativo(int sistemaOperativo) {
		this.sistemaOperativo = sistemaOperativo;
	}

	
	// PRECIO
	
	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}
	
	
	// DESCRIPCION
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	// IMAGEN
	
	public Bitmap getImagen() {
		return imagen;
	}

	public void setImagen(Bitmap imagen) {
		this.imagen = imagen;
	}
	
	
	// OFERTAS
	
	public double getPrecioOferta() {
		return precioOferta;
	}

	public void setPrecioOferta(double precioOferta) {
		this.precioOferta = precioOferta;
	}
	
	public boolean isOferta() {
		return enOferta;
	}

	public void setOferta() {
		this.enOferta = true;
	}
	
	public void unsetOferta() {
		this.enOferta = false;
	}
	
	

}
