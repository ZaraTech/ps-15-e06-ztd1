package com.zaratech.smartcatalogue.aplicacion;

import java.util.ArrayList;

import com.zaratech.smartcatalogue.R;
import com.zaratech.smartcatalogue.componentes.Producto;
import com.zaratech.smartcatalogue.utiles.AdaptadorProductos;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class ListaProductos extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_productos);

		
		/* ------------- EJEMPLO DE LLENADO DE LISTA ------------- */
		
		ListView lista = (ListView) findViewById(R.id.lista_productos);
		

		Resources res = getBaseContext().getResources();
		int id = R.drawable.smarket; 
		Bitmap imagen = BitmapFactory.decodeResource(res, id);

		ArrayList<Producto> valores = new ArrayList<Producto>();
		
		for (int i = 0; i < 19; ++i) {
			Producto p = new Producto("Producto " + i, 0, 0, 11.11);
			p.setImagen(imagen);
			valores.add(p);
		}
		
		AdaptadorProductos adaptador = new AdaptadorProductos(this, valores);

		lista.setAdapter(adaptador);
		
		/* ------------------- FIN DE EJEMPLO -------------------- */
	}

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
	            
	        case R.id.menu_opcion_info:
	        	startActivity(new Intent(this, InfoProducto.class));
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
