package com.zaratech.smarket.aplicacion;

import java.util.ArrayList;

import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.utiles.AdaptadorProductos;
import com.zaratech.smarket.R;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * Activity que gestiona el listado de productos principal.
 * 
 * @author Juan
 */
public class ListaProductos extends ListActivity {
	
	/**
	 * Guarda la ultima posicion en la que se quedo el listado
	 */
	private int ultimaPosicion;

	/**
	 * Constante que hace referencia a la activity de informacion de Producto.
	 * Se usara para identificar de donde se vuelve (startActivityForResult)
	 */
	private final int ACTIVITY_INFO = 0;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_productos);

		
		/* ------------- EJEMPLO DE LLENADO DE LISTA ------------- */
		

		// Obtener imagen
		Resources res = getBaseContext().getResources();
		int id = R.drawable.smarket; 
		Bitmap imagen = BitmapFactory.decodeResource(res, id);
		
		AdaptadorBD bd = new AdaptadorBD();
		bd.open();
		
		// PRUEBAS: bd
		bd.crearMarca(new Marca("Samsung"));
		Marca marcaSamsung = bd.obtenerMarca("Samsung");
		
		bd.crearProducto(new Producto("Galaxy S6", marcaSamsung, Producto.TIPO_SMARTPHONE, 678.0));

		
		// Rellenar lista
		ArrayList<Producto> productos = (ArrayList<Producto>) bd.obtenerProductos();
		
		
		
		// PRUEBAS: ofertas e imagenes
		for (Producto p : productos) {
			
			
			if((int)(Math.random()*2.0) == 0){
				p.setOferta();
				p.setPrecioOferta(p.getPrecio() * 0.75);
			}
			
			p.setImagen(imagen);
		}
		
		AdaptadorProductos adaptador = new AdaptadorProductos(this, productos);

		this.setListAdapter(adaptador);
		
		bd.close();
	}
	
	
	
	
	@Override
	/**
	 * Se ejecuta al volver de otra Activity
	 */
	protected void onResume() {

		super.onResume();
		
		setSelection(ultimaPosicion);
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int posicion, long id) {
		
		super.onListItemClick(l, v, posicion, id);
		
		ultimaPosicion = posicion;
		
		Intent i = new Intent(this, InfoProducto.class);
		
		// Añade Producto seleccionado
		Producto p = (Producto) this.getListAdapter().getItem(posicion);
		i.putExtra("Producto", p);

		startActivityForResult(i, ACTIVITY_INFO);
	}
	
	
	
	/* MENU SUPERIOR */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.activity_lista_productos, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	    
	        case R.id.menu_opcion_add:
	        	startActivity(new Intent(this, InsertModificationProduct.class));
	            return true;
	            
	        case R.id.menu_opcion_busqueda:
	        	startActivity(new Intent(this, Busqueda.class));
	            return true;
	            
	        case R.id.menu_opcion_envio:
	        	startActivity(new Intent(this, EnvioPedido.class));
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
