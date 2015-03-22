package com.zaratech.smartcatalogue;

import java.util.ArrayList;

import com.zaratech.smartcatalogue.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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
}
