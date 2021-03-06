package com.zaratech.smarket.aplicacion;

import java.util.List;

import com.zaratech.smarket.componentes.Listado;
import com.zaratech.smarket.componentes.Orden;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.utiles.AdaptadorProductos;
import com.zaratech.smarket.utiles.CargadorAsincrono;
import com.zaratech.smarket.utiles.EditorConfiguracion;
import com.zaratech.smarket.R;

import android.os.AsyncTask;
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
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Activity que gestiona el listado de productos principal.
 * 
 * @author Juan
 */
public class ListaProductos extends ListActivity implements Listado{

	/**
	 * Guarda la ultima posicion en la que se quedo el listado
	 */
	private int ultimaPosicion;

	/**
	 * Guarda el ultimo orden seleccionado
	 */
	private Orden ultimoOrden = new Orden(
			AdaptadorBD.DB_ORDENACION_NOMBRE,
			AdaptadorBD.DB_ORDENACION_ASC);

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
	 * Constante que hace referencia a la activity IniciarSesion. Se usara
	 * para identificar de donde se vuelve (startActivityForResult)
	 */
	private final int ACTIVITY_INICIAR_SESION = 2;

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

	public void cargarListado() {
		
		CargadorAsincrono ca = new CargadorAsincrono(this, bd, ultimoOrden);
		ca.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
	}

	public void cargarListadoAsincrono(List<Producto> productos){

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
		cargarListado();

		// Separador personalizado de elementos de listado
		int[] colors = { 0, 0xFFFFFFFF, 0 };
		getListView().setDivider(
				new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		getListView().setDividerHeight(2);


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
				android.R.layout.simple_list_item_1, ordenacion);
		ordenar.setAdapter(adapter);

		ordenar.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position >= 0 && position < BusquedaProducto.ORDEN.length) {

					ultimoOrden = BusquedaProducto.ORDEN[position];
					cargarListado();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
	}

	@Override
	protected void onDestroy() {

		if(bd.isSincronizacionRemotaPeriodica()){
			bd.unSetSincronizacionRemotaPeriodica();
		}

		bd.close();

		super.onDestroy();
	}

	// REANUDAR Activity

	@Override
	protected void onResume() {

		// SINCRONIZACION REMOTA
		EditorConfiguracion configuracion = new EditorConfiguracion(this);

		// CONFIG: ACTIVAR BD REMOTA
		if(!configuracion.usoBDLocal()){

			bd.setSincronizacionRemota();

			if(!configuracion.sincBDManual() && !bd.isSincronizacionRemotaPeriodica()){
				bd.setSincronizacionRemotaPeriodica();
				bd.initSincronizacionRemotaPeriodica();
				
			} else if (configuracion.sincBDManual()) {
				bd.unSetSincronizacionRemotaPeriodica();
			}

			// CONFIG: DESACTIVAR BD REMOTA (si procede)
		} else {

			if(bd.isSincronizacionRemota()){

				if(bd.isSincronizacionRemotaPeriodica()){
					bd.unSetSincronizacionRemotaPeriodica();
				}

				bd.unSetSincronizacionRemota();
			}
		}


		super.onResume();
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

			cargarListado();
		}
		// INICIAR SESIÓN
		else if (requestCode == ACTIVITY_INICIAR_SESION) {
			if (resultCode == IniciarSesion.INICIAR_SESION_OK) {
				admin = true;
				invalidateOptionsMenu();
				registerForContextMenu(getListView());
			}
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

			Intent i = new Intent(this, BusquedaProducto.class);

			// Añade Producto seleccionado
			i.putExtra(BusquedaProducto.EXTRA_ADMIN, admin);

			startActivity(i);
			return true;

			// INICIAR SESIÓN
		} else if (id == R.id.lista_menu_iniciar_sesion) {
			Intent i = new Intent(this, IniciarSesion.class);
			startActivityForResult(i, ACTIVITY_INICIAR_SESION);
			return true;

			// CONFIGURACIÓN
		} else if (id == R.id.lista_menu_configuracion) {
			startActivity(new Intent(this, EditarConfiguracion.class));
			return true;

			// ACTUALIZAR
		} else if (id == R.id.lista_menu_actualizar) {
			
			Toast.makeText(this, getString(R.string.lista_actualizar), Toast.LENGTH_SHORT).show();

			if(bd.isSincronizacionRemota()){
				bd.pullRemoto();
			} else {
				cargarListado();
			}

			return true;

			// CERRAR SESIÓN
		} else if (id == R.id.lista_menu_cerrar_sesion) {
			admin = false;
			invalidateOptionsMenu();
			unregisterForContextMenu(getListView());
			Toast.makeText(this, getString(R.string.sesion_mensaje_cerrar), Toast.LENGTH_SHORT).show();

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
