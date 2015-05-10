package com.zaratech.smarket.aplicacion;

import java.util.ArrayList;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.utiles.AdaptadorProductos;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.text.Editable;
import android.text.TextWatcher;

@SuppressLint("DefaultLocale")
public class BusquedaProducto extends ListActivity implements TextWatcher {

	/**
	 * Guarda la ultima posicion en la que se quedo el listado
	 */
	private int ultimaPosicion;

	/**
	 * Clave que identifica un Producto dentro del campo extras del Intent
	 */
	private final String EXTRA_PRODUCTO = "Producto";

	/**
	 * Constante que hace referencia a la activity de informacion de Producto.
	 * Se usara para identificar de donde se vuelve (startActivityForResult)
	 */
	private final int ACTIVITY_INFO = 0;

	/**
	 * Atributos locales de la clase
	 */
	private AutoCompleteTextView edicion;

	/**
	 * Conexion con la BD
	 */
	private AdaptadorBD bd;
	
	
	/**
	 * Método privado que carga el istado de productos de la BD
	 */
	private void cargarListado() {

		// Rellenar lista
		ArrayList<Producto> productos = (ArrayList<Producto>) bd.obtenerProductos();

		AdaptadorProductos adaptador = new AdaptadorProductos(this, productos);

		setListAdapter(adaptador);

		setSelection(ultimaPosicion - 2);
	}

	/**
	 * Metodo llamado en la creación del activity
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busqueda_producto);
		
		bd = new AdaptadorBD(this);
		bd.open();
		
		cargarListado();

		// rellenamos array de autocompletado
		String[] nombres = rellenarArraySugerencias();
		// String[]nombres={"Producto"};
		// Monta el autocompletado
		edicion = (AutoCompleteTextView) findViewById(R.id.busqueda_miautocompletado);
		edicion.addTextChangedListener(this);
		edicion.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, nombres));

	}

	/**
	 * Metodo privado que rellena el array de sugerecias
	 */
	private String[] rellenarArraySugerencias() {
		String[] nombres = null;

		// Rellenar Listado de Marcas para autocompletado
		ArrayList<Marca> marcas = (ArrayList<Marca>) bd.obtenerMarcas();
		
		// Rellenar Listado de Producto para autocompletado
		ArrayList<Producto> productos = (ArrayList<Producto>) bd.obtenerProductos();
		int totalProductos = productos.size();
		int totalMarcas = marcas.size();
		int totalDispositivos = 2;
		nombres = new String[totalMarcas + totalProductos + totalDispositivos];
		
		// Añade los nombres de las marcas al array de sugerencias
		for (int i = 0; i < totalMarcas; i++) {
			nombres[i] = marcas.get(i).getNombre();
		}
		
		// Añade los nombres de los productos al array de sugerencias
		for (int i = 0; i < totalProductos; i++) {
			nombres[(totalMarcas) + i] = productos.get(i).getNombre();
		}
		nombres[totalMarcas + totalProductos] = "Tablet";
		nombres[totalMarcas + totalProductos + 1] = "SmartPhone";
		return nombres;
	}


	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	/**
	 * Metodo que indica sugerencias de autocompletado
	 */
	public void onTextChanged(CharSequence s, int inicio, int despues,
			int cuenta) {

	}

	/**
	 * Método que realiza la busqueda una vez que se ha modificado el texto
	 */
	public void afterTextChanged(Editable s) {
		realizarBusqueda(edicion.getText().toString().toLowerCase());

	}

	/**
	 * Al hacer click en un producto
	 */
	protected void onListItemClick(ListView l, View v, int posicion, long id) {
		super.onListItemClick(l, v, posicion, id);

		ultimaPosicion = posicion;

		Intent i = new Intent(this, InfoProducto.class);

		// Añade Producto seleccionado
		Producto p = (Producto) getListAdapter().getItem(posicion);
		i.putExtra(EXTRA_PRODUCTO, p);

		startActivityForResult(i, ACTIVITY_INFO);
	}

	/**
	 * Se ejecuta al volver de otra Activity
	 */
	protected void onResume() {

		super.onResume();

		cargarListado();
	}

	/**
	 * Realiza la búsqueda y la muestra
	 */
	@SuppressLint("DefaultLocale")
	private void realizarBusqueda(String cadena) {
		
		// Obtener imagen
		Resources res = getBaseContext().getResources();
		int id = R.drawable.smarket;
		Bitmap imagen = BitmapFactory.decodeResource(res, id);
		
		// Creamos 2 arrays en uno metemos todos y en el otro los que coincidan
		// con la busqueda
		ArrayList<Producto> productos = (ArrayList<Producto>) bd.obtenerProductos();
		ArrayList<Producto> productosBusqueda = new ArrayList<Producto>();
		
		for (Producto p : productos) {
			// Establecemos tipo Dispositivo
			String tipoDispositivo = AdaptadorBD.obtenerTipo(p.getTipo());
			
			// Vemos si la entrada es vacia
			if (cadena.equals("")) {
				p.setImagen(imagen);
				productosBusqueda.add(p);
				
			// Vemos si la entrada esta contenido en algun nombre
			} else if (p.getNombre().toLowerCase().indexOf(cadena) != -1) {
				p.setImagen(imagen);
				productosBusqueda.add(p);
				
			// Vemos si la entrada esta contenido en alguna marca
			} else if (p.getMarca().getNombre().toLowerCase().indexOf(cadena) != -1) {
				p.setImagen(imagen);
				productosBusqueda.add(p);
				
			// Vemos si la entrada esta contenido en algun dispositivo
			} else if (tipoDispositivo.toLowerCase().indexOf(cadena) != -1) {
				p.setImagen(imagen);
				productosBusqueda.add(p);
			}
		}
		AdaptadorProductos adaptador = new AdaptadorProductos(this,
				productosBusqueda);
		this.setListAdapter(adaptador);
	}

}
