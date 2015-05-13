package com.zaratech.smarket.aplicacion;

import java.util.ArrayList;

import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.pruebas.LanzadorPruebas;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.utiles.AdaptadorProductos;
import com.zaratech.smarket.R;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.lang.reflect.Field;

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
	 * Constante que hace referencia a la activity InfoProducto. Se usara para
	 * identificar de donde se vuelve (startActivityForResult)
	 */
	private final int ACTIVITY_INFO = 0;

	/**
	 * Constante que hace referencia a la activity EdicionProducto. Se usara
	 * para identificar de donde se vuelve (startActivityForResult)
	 */
	private final int ACTIVITY_EDICION = 1;

	/**
	 * Constantes que hacen referencias a elementos del menu contextual
	 */
	private final int EDITAR_PRODUCTO = 0;
	private final int ELIMINAR_PRODUCTO = 1;

	/**
	 * Clave que identifica un Producto dentro del campo extras del Intent
	 */
	private final String EXTRA_PRODUCTO = "Producto";

	/**
	 * Conexion con la BD
	 */
	private AdaptadorBD bd;

	/**
	 * Estado de sesion administrador
	 */
	private static boolean admin = false;

	private void cargarListado() {

		// Rellenar lista
		ArrayList<Producto> productos = (ArrayList<Producto>) bd
				.obtenerProductos(AdaptadorBD.DB_ORDENACION_PRECIO);

		AdaptadorProductos adaptador = new AdaptadorProductos(this, productos);

		setListAdapter(adaptador);

		setSelection(ultimaPosicion - 2);
	}

	// INICIO de la aplicacion

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lista_productos);

		// Obtener BD
		bd = new AdaptadorBD(this);
		bd.open();

		// PARA PRUEBAS - BORRAR //////
		if (bd.obtenerMarcas().size() == 0) {
			bd.poblarBD();
		}
		// /////////////////////////////

		// Separador personalizado de elementos de listado
		int[] colors = { 0, 0xFFFFFFFF, 0 };
		getListView().setDivider(
				new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		getListView().setDividerHeight(2);

		// Menu contextual
		registerForContextMenu(getListView());

		// PRUEBAS - BORRAR //////
		admin = true;
		// invalidateOptionsMenu();
		// /////////////////////////////

		// Hack para Barra de Acciones
		// Simula que no hay boton de menu aunque si que lo haya
		// Fuerza mostrar opciones de menu en Barra de Acciones
		try {

			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");

			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}

		} catch (Exception ex) {
			// Ignorar
		}

		/*
		 * Ordenacion
		 */
		Spinner ordenar = (Spinner) findViewById(R.id.listar_ordenar);

		String[] ordenacion = new String[BusquedaProducto.NOMBRES_ORDENACION.length];
		for (int i = 0; i < BusquedaProducto.NOMBRES_ORDENACION.length; i++) {
			ordenacion[i] = getString(BusquedaProducto.NOMBRES_ORDENACION[i]);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, ordenacion);
		ordenar.setAdapter(adapter);

		ordenar.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

			}

			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

	}

	@Override
	protected void onStop() {
		super.onStop();

		bd.close();
	}

	// REANUDAR Activity

	@Override
	protected void onResume() {
		super.onResume();

		bd.open();

		cargarListado();
	}

	// CLICK en Producto del listado

	@Override
	protected void onListItemClick(ListView l, View v, int posicion, long id) {
		super.onListItemClick(l, v, posicion, id);

		ultimaPosicion = posicion;

		Intent i = new Intent(this, InfoProducto.class);

		// Añade Producto seleccionado
		Producto p = (Producto) getListAdapter().getItem(posicion);
		i.putExtra(EXTRA_PRODUCTO, p);

		startActivityForResult(i, ACTIVITY_INFO);
	}

	// VUELTA de activity con RESULTADO

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);

		// INFO
		if (requestCode == ACTIVITY_INFO) {

		}
		// EDICION
		else if (requestCode == ACTIVITY_EDICION) {

		}

	}

	// MENU DE OPCIONES

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (admin) {
			getMenuInflater().inflate(R.menu.activity_lista_productos_admin,
					menu);
		} else {
			getMenuInflater().inflate(R.menu.activity_lista_productos, menu);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		// AÑADIR
		if (id == R.id.lista_menu_add) {

			Intent i = new Intent(this, EdicionProducto.class);
			startActivityForResult(i, ACTIVITY_EDICION);
			return true;

		// BUSCAR
		} else if (id == R.id.lista_menu_busqueda) {

			startActivity(new Intent(this, BusquedaProducto.class));
			return true;
			
		// INICIAR SESIÓN
		} else if (id == R.id.lista_menu_iniciar_sesion) {
			startActivity(new Intent(this, IniciarSesion.class));
			return true;

		// TESTS DE LA APP
		} else if (id == R.id.lista_menu_test) {

			startActivity(new Intent(this, LanzadorPruebas.class));
			return true;

		// ???
		} else {

			return super.onOptionsItemSelected(item);
		}
	}

	// MENU CONTEXTUAL

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, EDITAR_PRODUCTO, 0, R.string.lista_menu_editar);
		menu.add(1, ELIMINAR_PRODUCTO, 1, R.string.lista_menu_eliminar);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = null;

		// Identifica el Producto seleccionado
		info = (AdapterContextMenuInfo) item.getMenuInfo();
		Producto p = (Producto) getListAdapter().getItem(info.position);

		// Guarda posicion
		ultimaPosicion = info.position;

		// EDITAR
		if (item.getItemId() == EDITAR_PRODUCTO) {

			Intent i = new Intent(this, EdicionProducto.class);

			// Añade Producto seleccionado
			i.putExtra(EXTRA_PRODUCTO, p);

			startActivityForResult(i, ACTIVITY_EDICION);

			// ELIMINAR
		} else if (item.getItemId() == ELIMINAR_PRODUCTO) {

			bd.borrarProducto(p.getId());

			cargarListado();
		}

		return super.onContextItemSelected(item);

	}

}
