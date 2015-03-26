package com.zaratech.smartcatalogue;

import java.util.ArrayList;

import com.zaratech.smartcatalogue.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_productos);

		
		/* ------------- EJEMPLO DE LLENADO DE LISTA ------------- */
		
		ListView lista = (ListView) findViewById(R.id.lista_productos);

		ArrayList<Producto> valores = new ArrayList<Producto>();
		
		for (int i = 0; i < 19; ++i) {
			valores.add(new Producto("Producto " + i, "marca", 11.11));
		}
		
		AdaptadorProductos adaptador = new AdaptadorProductos(this, valores);

		lista.setAdapter(adaptador);
		
		/* ------------------- FIN DE EJEMPLO -------------------- */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menu_add_product:
	        	startActivity(new Intent(this,InsertModificationProduct.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
