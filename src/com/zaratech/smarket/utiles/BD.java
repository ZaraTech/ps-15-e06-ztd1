package com.zaratech.smarket.utiles;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;

/**
 * Clase que simula una base de datos. Proporciona los metodos para conectar y
 * desconectar a la BD.
 * Consta de dos tablas, una de Productos y otra de Marcas.
 * @author Marta
 *
 */
public class BD {

	/**
	 * Contadores para simular secuenciacion de identificadores 
	 */
	private static int contadorProductos = 0;
	private static int contadorMarcas = 0;
	
	/** 
	 * Tabla de productos 
	 */
    private List<Producto> tabla_productos = new ArrayList<Producto>();
    
    /**
     * Tabla de marcas 
     */
    private List<Marca> tabla_marcas = new ArrayList<Marca>();
        
    /**
     * Metodo que conecta con la base de datos (en este caso se rellena una 
     * base de datos simulada).
     */
    public void open(Context ctx){
    	
    	/* Obtener imagen */ 
    	
    	Resources res = ctx.getResources();
    	int id = R.drawable.smarket; 
    	Bitmap imagen = BitmapFactory.decodeResource(res, id);
    	
    	/* Rellena tabla de marcas */
    	
    	for(int i=1; i<5; i++){
    		Marca m = new Marca("Marca " + i);
    		m.setId(i);
    		contadorMarcas++;
    		
    		tabla_marcas.add(m);
    	}
    	
    	/* Rellena tabla de productos*/
    	
    	for(int i=1; i<22; i++){
    		
    		int marca = (int)(Math.random()*tabla_marcas.size());
    		int tipo = (int)(Math.random()*2.0);
    		double precio = Math.random()*300+50;
    		Producto p = new Producto("Producto " + i, tabla_marcas.get(marca), tipo, precio);
    		
    		p.setId(i);
    		p.setImagen(imagen);
    		
    		if((int)(Math.random()*2.0) == 0){
    			p.setOferta();
    			p.setPrecioOferta(p.getPrecio() * 0.75);
    		}
    		
    		contadorProductos++;
    		
    		tabla_productos.add(p);
    	}
    	
    }
    
    /**
     * Cierra la conexion con la base de datos (en este caso en el que se
     * trabaja con una base de datos virtual no hace nada).
     */
    public void close() {
    	//
    }

    /**
     * Devuelve una lista con todos los Productos
     * @return Lista de Productos
     */
	public List<Producto> getTabla_productos() {
		return tabla_productos;
	}

	/**
     * Devuelve una lista con todas las Marcas
     * @return Lista de Marcas
     */
	public List<Marca> getTabla_marcas() {
		return tabla_marcas;
	}

	/**
	 * Metodo que incrementa del contador para simular los indices de la tabla 
	 * de Productos.
	 */
	public void incrementarContadorProductos() {
		contadorProductos++;
	}

	/**
	 * Metodo que incrementa del contador para simular los indices de la tabla 
	 * de Marcas.
	 */
	public void incrementarContadorMarcas() {
		contadorMarcas++;
	}

	/**
	 * Devuelve el contador de la tabla de Productos.
	 * @return contador de la tabla de Productos.
	 */
	public int getContadorProductos() {
		return contadorProductos;
	}

	/**
	 * Devuelve el contador de la tabla de Marcas.
	 * @return contador de la tabla de Marcas.
	 */
	public int getContadorMarcas() {
		return contadorMarcas;
	}
}
