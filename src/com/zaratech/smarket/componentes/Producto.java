package com.zaratech.smarket.componentes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Clase que gestiona objetos Producto.
 * 
 * @author Juan
 */
public class Producto implements Parcelable{
	
	/**
	 * IDENTIFICADOR del producto.
	 * Corresponde al identificador en la BD
	 * (se asigna cuando se recupera de la BD)
	 */
	private int id;

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
	 * Marca del producto
	 */
	private Marca marca;
	
	/**
	 * DIMENSION DE PANTALLA en diagonal y en pulgadas, del producto
	 */
	private double dimensionPantalla;
	
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
	
	
	
	/* CONSTANTES */
	
	/**
	 * Tipo de Producto
	 */
	public static final int TIPO_SMARTPHONE = 0;
	public static final int TIPO_TABLET = 1;
	
	/**
	 * Sistema Operativo
	 */
	public static final int SO_ANDROID = 0;
	public static final int SO_WINDOWSPHONE = 1;
	public static final int SO_IOS = 2;
	
	/**
	 * Ofertas
	 */
	public static final boolean OFERTA_NO = false;
	public static final boolean OFERTA_SI = true;
	
	
	
	/* CONSTRUCTORES */
	
	/**
	 * Crea un objeto de tipo Producto totalmente vacio
	 */
	public Producto(){
		
		this.id = -1;
		this.nombre = null;
		this.tipo = 0;
		this.marca = null;
		this.dimensionPantalla = 0.0;
		this.sistemaOperativo = 0;
		this.precio = 0.0;
		this.descripcion = null;
		this.imagen = null;
		this.enOferta = false;
		this.precioOferta = 0.0;
	}
	
	/**
	 * Usado para obtener productos de un Intent
	 * @param entrada Es el objeto que almacena el Producto
	 */
	public Producto(Parcel entrada){
		
		readFromParcel(entrada);
	}
	
	/**
	 * Crea un nuevo Producto
	 * @param nombre es el nombre del Producto
	 * @param marca es la Marca del Producto
	 * @param tipo es el identificador del Tipo del Producto
	 * @param precio es el precio del Producto
	 */
	public Producto(String nombre, Marca marca, int tipo, double precio) {
		
		this.id = 0;
		this.nombre = nombre;
		this.tipo = tipo;
		this.marca = marca;
		this.dimensionPantalla = 0.0;
		this.sistemaOperativo = 0;
		this.precio = precio;
		this.descripcion = "";
		this.imagen = null;
		this.enOferta = false;
		this.precioOferta = 0.0;
	}
	
	
	
	/* MOVILIDAD ENTRE ACTIVITIES */
	
	/**
	 * Requerido por la interfaz
	 */
	public int describeContents() {
		return 0;
	}

	/**
	 * Escribe todos los atributos en un objeto Parcel
	 * Permite enviar Productos en Intents
	 */
	public void writeToParcel(Parcel salida, int flags) {
		
		salida.writeInt(id);
		salida.writeString(nombre);
		salida.writeInt(tipo);
		salida.writeSerializable(marca);
		salida.writeDouble(dimensionPantalla);
		salida.writeInt(sistemaOperativo);
		salida.writeDouble(precio);
		salida.writeString(descripcion);
		
		salida.writeParcelable(imagen, flags);
		
		byte enOferta = (byte) (this.enOferta ? 1 : 0);
		
		salida.writeByte(enOferta);
		salida.writeDouble(precioOferta);	
	}
	
	/**
	 * Lee todos los atributos desde un objeto Parcel.
	 * Permite recibir Productos desde Intents
	 */
	public void readFromParcel(Parcel entrada){
		
		this.id = entrada.readInt();
		this.nombre = entrada.readString();
		this.tipo = entrada.readInt();
		this.marca = (Marca) entrada.readSerializable();
		this.dimensionPantalla = entrada.readDouble();
		this.sistemaOperativo = entrada.readInt();
		this.precio = entrada.readDouble();
		this.descripcion = entrada.readString();
		this.imagen = (Bitmap) entrada.readParcelable(getClass().getClassLoader());
		this.enOferta = entrada.readByte() != 0;
		this.precioOferta = entrada.readDouble();
	}
	
	/**
	 * Permite convertir objetos Parcelables en Productos
	 * de forma transparente.
	 */
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	
	    	new Parcelable.Creator() {
		
	            public Producto createFromParcel(Parcel entrada) {
	                return new Producto(entrada);
	            }
	 
	            public Producto[] newArray(int size) {
	                return new Producto[size];
	            }
	        };
	
	
	
	
	
	
	
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
	
	
	// TIPO
	
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	
	// MARCA

	public Marca getMarca() {
		return marca;
	}

	public void setMarca(Marca marca) {
		this.marca = marca;
	}
	
	
	// TAMAÑO DE PANTALLA
	
	public double getDimensionPantalla() {
		return dimensionPantalla;
	}

	public void setDimensionPantalla(double dimensionPantalla) {
		this.dimensionPantalla = dimensionPantalla;
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

	
	// TO STRING
	
	@Override
	public String toString() {
		return "Producto [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo
				+ ", marca=" + marca + ", dimensionPantalla="
				+ dimensionPantalla + ", sistemaOperativo=" + sistemaOperativo
				+ ", precio=" + precio + ", descripcion=" + descripcion
				+ ", imagen=" + imagen + ", enOferta=" + enOferta
				+ ", precioOferta=" + precioOferta + "]";
	}
	
	
	// CLONAR
	
	/**
	 * Duplica el Producto
	 * @return un nuevo Producto
	 */
	public Producto clonar(){
		
		Producto p = new Producto();
		
		p.setId(id);
		p.setNombre(new String(nombre));
		p.setTipo(tipo);
		
		if(marca != null){
			p.setMarca(marca.clonar());
		}
		
		p.setDimensionPantalla(dimensionPantalla);
		p.setSistemaOperativo(sistemaOperativo);
		p.setPrecio(precio);
		p.setDescripcion(new String(descripcion));
		
		if(imagen != null){
			p.setImagen(imagen.copy(imagen.getConfig(), true));
		}
		
		if(enOferta){
			p.setOferta();
		}
		else{
			p.unsetOferta();
		}
		
		p.setPrecioOferta(this.precioOferta);
		
		return p;
	}

}
