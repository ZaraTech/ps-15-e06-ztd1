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
 * Clase que permite gestionar pruebas sobre insercion, modificacion, busqueda
 * y borrado de productos en la base de datos de la aplicacion.
 * 
 * @author Marta
 *
 */
public class PruebasProducto {
	
	/** Nombre que se asocia a los productos de prueba */
	private static final String NOMBRE_PRODUCTO = "Producto_Prueba";

	/**
	 * Prueba de insercion de producto en la base de datos. El producto se
	 * inserta con nombre Producto_Prueba y se rellena con datos (marca,
	 * precio...) aleatorios.
	 * 
	 * @param bd Base de datos
	 */
	public static boolean testInsertarProducto(AdaptadorBD bd, Context ctx) {

		// Crea producto de prueba y lo rellena con datos aleatorios
		int tipo = (int) Math.floor(Math.random() * 2);
		String desc = "blablabla";
		double dimensionPantalla = 7.0;
		int sistemaOperativo = (int) Math.floor(Math.random() * 3);
		
		List<Marca> listaMarcas =  bd.obtenerMarcas();
		int idMarca = (int)(Math.random()*(double)listaMarcas.size());
		Marca marca = listaMarcas.get(idMarca);	

		double precio = Math.random() * 1000.0 + 1.0;	

		Producto prodPrueba = new Producto(NOMBRE_PRODUCTO, marca, tipo, precio);
		
		prodPrueba.setDescripcion(desc);
		prodPrueba.setSistemaOperativo(sistemaOperativo);
		prodPrueba.setDimensionPantalla(dimensionPantalla);

		Resources res = ctx.getResources();
		int id = R.drawable.smarket; 
		Bitmap imagen = BitmapFactory.decodeResource(res, id);
		prodPrueba.setImagen(imagen);
		
		// Inserta producto de prueba en la bd
		return bd.crearProducto(prodPrueba);
	}
	
	/**
	 * Prueba de busqueda de producto en la base de datos. Busca el producto
	 * con nombre "TEST_PRODUCTO" (debe ejecutarse despues del metodo 
	 * testInsertarProducto.
	 * 
	 * @param bd Base de datos
	 * @return True si la prueba tiene exito, false en caso contrario.
	 */
	public static boolean testBuscarProducto(AdaptadorBD bd) {	
		
		try{
			
			List<Producto> listaProd = bd.buscarProducto(NOMBRE_PRODUCTO);			
			
			if (listaProd.size() > 0){
				
				Producto prodPrueba = listaProd.get(0);
				return  prodPrueba != null;
				
			} else {
				return false; 
			}
			
		} catch (Exception e){			
			return false;
		}	
	}

	/**
	 * Prueba de borrado de producto en la base de datos.
	 * 
	 * @param bd Base de datos
	 * @return True si la prueba tiene exito, false en caso contrario.
	 */
	public static boolean testBorrarProducto(AdaptadorBD bd) {

		try {
			
			List<Producto> listaProd = bd.buscarProducto(NOMBRE_PRODUCTO);
			
			if (listaProd.size() > 0){
				
				Producto prodPrueba = listaProd.get(0);
				return bd.borrarProducto(prodPrueba.getId());
				
			} else {
				return false;
			}
			
		} catch (Exception e){
			return false;
		}	
	}
	
	/**
	 * Prueba de edicion de un producto existente en la base de datos. La
	 * edicion se realiza sobre el producto de prueba.
	 * 
	 * @param bd Base de datos
	 * @return True si la prueba tiene exito, false en caso contrario.
	 */
	public static boolean testEditarProducto(AdaptadorBD bd){
		
		try{
			
			List<Producto> listaProd = bd.buscarProducto(NOMBRE_PRODUCTO);
			
			if (listaProd.size() > 0){
				
				Producto prodPrueba = listaProd.get(0);
				prodPrueba.setNombre(NOMBRE_PRODUCTO + "_MODIFICADO");
				prodPrueba.setDescripcion("modificado");		
				
				return bd.actualizarProducto(prodPrueba);
				
			} else {				
				return false;
			}
			
		} catch (Exception e){			
			return false;
		}
		
		
	}

}
