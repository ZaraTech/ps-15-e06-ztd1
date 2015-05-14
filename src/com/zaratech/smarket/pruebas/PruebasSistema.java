package com.zaratech.smarket.pruebas;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;


/**
 * Clase que permite gestionar pruebas de sistema de la aplicacion.
 * @author Marta
 *
 */
public class PruebasSistema{

	/** Numero de productos que se crean en la prueba */
	private static final int MAX_PRODUCTOS = 50; //1000
	
	/** Numero de marcas que se crean en la prueba */
	private static final int MAX_MARCAS = 10; // 100
	
	/** Nombre asignado a los productos de prueba */
	private static final String NOMBRE_PRODUCTO = "Prueba_Producto ";

	/** Nombre asignado a las marcas de prueba */
	private static final String NOMBRE_MARCA = "Prueba_Marca ";
	
	/**
	 * Prueba de volumen que permite comprobar el funcionamiento del sistema
	 * con una gran carga de datos para comprobar su robustez.
	 * 
	 * @param bd Base de datos
	 * @param ctx Contexto (activity desde donde se ejecuta)
	 */
	public static boolean testVolumen(AdaptadorBD bd, Context ctx){	
		
		// imagen por defecto para los productos
		Resources res = ctx.getResources();
		int id = R.drawable.smarket; 
		Bitmap imagen = BitmapFactory.decodeResource(res, id);		
		
		try{
			
			// crea marcas
			for (int i = 0; i < MAX_MARCAS; i++) {

				Marca m = new Marca(NOMBRE_MARCA + i);
				bd.crearMarca(m);

			}
			android.util.Log.d("Test Volumen", 
					"Se han creado " + MAX_MARCAS + " marcas.");	
			
			List<Marca> marcas = bd.obtenerMarcas();
			
			// crea productos
			for (int i = 0; i < MAX_PRODUCTOS; i++){
				int marca = (int)(Math.random()*(double)marcas.size());
				int tipo = (int)(Math.random()*2.0);
				double precio = Math.random()*650.0;
				String descripcion = "El mejor del mercado.";
				Producto p = new Producto(NOMBRE_PRODUCTO + i, marcas.get(marca), tipo, precio);

				p.setImagen(imagen);
				p.setDescripcion(descripcion);
				p.setDimensionPantalla(3.7);

				if((int)(Math.random()*2.0) == 0){
					p.setOferta();
					p.setPrecioOferta(p.getPrecio() * 0.75);
				}

				bd.crearProducto(p);
			}

			android.util.Log.d("Test Volumen", 
					"Se han creado " + MAX_PRODUCTOS + " productos.");
			return true;
			
		} catch (Exception e){
			
			return false;
		}			
	}
	
	/**
	 * Elimina los datos creados por la prueba de volumen
	 * 
	 * @param bd Base de datos
	 * @return
	 */
	public static boolean deshacerVolumen(AdaptadorBD bd){
		
		// elimina los productos y marcas creados en la prueba de volumen
		try{
			List<Producto> listaProd = bd.buscarProducto(NOMBRE_PRODUCTO);
			List<Marca> listaMarcas = bd.buscarMarca(NOMBRE_MARCA);
			
			if(listaProd.size() > 0 && listaMarcas.size() > 0){
				for (Producto p: listaProd){
					bd.borrarProducto(p.getId());
				}
				
				for (Marca m: listaMarcas){
					bd.borrarMarca(m.getId());
				}
				
				return true;
				
			} else {
				return false;
			}		
		
		} catch (Exception e){
		
			return false;
		}
		
	}
}
