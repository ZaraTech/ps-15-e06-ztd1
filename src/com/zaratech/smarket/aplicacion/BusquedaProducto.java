package com.zaratech.smarket.aplicacion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.utiles.AdaptadorProductos;
import com.zaratech.smarket.utiles.EditTextSimboloFinal;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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

	private static final int[] FILTROS = { R.id.busqueda_marca_layout,
		R.id.busqueda_precio_layout, R.id.busqueda_pulgadas_layout,
		R.id.busqueda_so_layout, R.id.busqueda_tipo_layout };

	private static final int[] NOMBRES_FILTROS = { R.string.app_marca,
		R.string.app_precio, R.string.app_pulgadas,
		R.string.app_sistema_operativo, R.string.app_tipo };

	private static final int[] NOMBRES_ORDENACION = {
		R.string.ordenacion_nombre_ascendente,
		R.string.ordenacion_nombre_descendente,
		R.string.ordenacion_precio_ascendente,
		R.string.ordenacion_precio_descendente };

	private Button buscar;
	private LinearLayout ordenar;
	private TextView extender;

	/**
	 * Método privado que carga el istado de productos de la BD
	 */
	private void cargarListado() {

		// Rellenar lista
		ArrayList<Producto> productos = (ArrayList<Producto>) bd
				.obtenerProductos();

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

		/*
		 * Busqueda avanzada
		 */
		final LinearLayout toogle = (LinearLayout) findViewById(R.id.busqueda_avanzada);

		TextView bot = (TextView) findViewById(R.id.busqueda_extender_avanzada);
		extender = bot;
		bot.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				cambiarVisibilidad(toogle);
			}
		});

		/*
		 * Filtros
		 */
		Spinner filtrar = (Spinner) findViewById(R.id.busqueda_filtrar);

		String[] filtros = new String[NOMBRES_FILTROS.length];
		for (int i = 0; i < NOMBRES_FILTROS.length; i++) {
			filtros[i] = getString(NOMBRES_FILTROS[i]);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, filtros);
		filtrar.setAdapter(adapter);

		filtrar.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				esconderTodos(FILTROS);
				if (position >= 0 && position < FILTROS.length) {
					mostrar((LinearLayout) findViewById(FILTROS[position]));
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				esconderTodos(FILTROS);
			}

		});

		/*
		 * Simbolos de editTexts (UM y pulgadas)
		 */
		EditText edit = (EditText) findViewById(R.id.busqueda_pulgadas_desde);
		edit.addTextChangedListener(new EditTextSimboloFinal(edit,
				getString(R.string.app_ud_pantalla)));

		edit = (EditText) findViewById(R.id.busqueda_pulgadas_hasta);
		edit.addTextChangedListener(new EditTextSimboloFinal(edit,
				getString(R.string.app_ud_pantalla)));

		edit = (EditText) findViewById(R.id.busqueda_precio_desde);
		edit.addTextChangedListener(new EditTextSimboloFinal(edit,
				getString(R.string.app_ud_monetaria)));

		edit = (EditText) findViewById(R.id.busqueda_precio_hasta);
		edit.addTextChangedListener(new EditTextSimboloFinal(edit,
				getString(R.string.app_ud_monetaria)));

		/*
		 * Sistema Operativo
		 */
		Spinner so = (Spinner) findViewById(R.id.busqueda_SO);
		String[] arr = new String[3];
		arr[Producto.SO_ANDROID] = AdaptadorBD
				.obtenerSistemaOperativo(Producto.SO_ANDROID);
		arr[Producto.SO_IOS] = AdaptadorBD
				.obtenerSistemaOperativo(Producto.SO_IOS);
		arr[Producto.SO_WINDOWSPHONE] = AdaptadorBD
				.obtenerSistemaOperativo(Producto.SO_WINDOWSPHONE);

		ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arr);
		adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		so.setAdapter(adp1);

		/*
		 * Marcas
		 */
		Spinner spinnerMarcas = (Spinner) findViewById(R.id.busqueda_marca);
		actualizaSpinner(spinnerMarcas);

		/*
		 * Ordenacion
		 */
		this.ordenar = (LinearLayout) findViewById(R.id.busqueda_ordenar_layout);
		Spinner ordenar = (Spinner) findViewById(R.id.busqueda_ordenar);

		String[] ordenacion = new String[NOMBRES_ORDENACION.length];
		for (int i = 0; i < NOMBRES_ORDENACION.length; i++) {
			ordenacion[i] = getString(NOMBRES_ORDENACION[i]);
		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, ordenacion);
		ordenar.setAdapter(adapter);

		ordenar.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

			}

			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		/*
		 * Buscar
		 */
		buscar = (Button) findViewById(R.id.busqueda_buscar);

		// Separador personalizado de elementos de listado
		int[] colors = { 0, 0xFFFFFFFF, 0 };
		getListView().setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
		getListView().setDividerHeight(2);

		/*
		 * Listado
		 */
		cargarListado();
		actualizarSugerencias();
	}

	/**
	 * Actualiza spinner de las marcas
	 */
	public void actualizaSpinner(Spinner spinnerMarcas) {
		ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getMarcas());
		adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMarcas.setAdapter(adp1);
	}

	/*
	 * Obtiene las marcas
	 */
	private List<String> getMarcas() {
		List<Marca> marcas = bd.obtenerMarcas();
		List<String> strs = new LinkedList<String>();
		for (Marca m : marcas) {
			strs.add(m.getNombre());
		}
		return strs;
	}

	private void actualizarSugerencias() {
		// rellenamos array de autocompletado
		String[] nombres = rellenarArraySugerencias();

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
		ArrayList<Producto> productos = (ArrayList<Producto>) bd
				.obtenerProductos();
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
		ArrayList<Producto> productos = (ArrayList<Producto>) bd
				.obtenerProductos();
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

	/**
	 * Reliza el efecto de deslizar hacia abajo sobre v
	 * 
	 * @param ctx
	 *            context principal
	 * @param v
	 *            vista a la que aplicar efecto
	 */
	public static void deslizarAbajo(Context ctx, View v) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.deslizar_abajo);
		if (a != null) {
			a.reset();
			if (v != null) {
				v.clearAnimation();
				v.startAnimation(a);
			}
		}
	}

	/**
	 * Reliza el efecto de deslizar hacia arriba sobre v
	 * 
	 * @param ctx
	 *            context principal
	 * @param v
	 *            vista a la que aplicar efecto
	 */
	public static void deslizarArriba(Context ctx, View v) {
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.deslizar_arriba);
		if (a != null) {
			a.reset();
			if (v != null) {
				v.clearAnimation();
				v.startAnimation(a);
			}
		}
	}

	/*
	 * Cambio de visibilidad sobre toogle
	 */
	private void cambiarVisibilidad(LinearLayout toogle) {
		if (toogle.isShown()) {
			esconder(toogle);
		} else {
			mostrar(toogle);
		}
	}

	private void esconderTodos(int[] array) {
		for (int i : array) {
			LinearLayout v = (LinearLayout) findViewById(i);
			esconder(v);
		}
	}

	private void esconder(LinearLayout v) {
		if (v.isShown()) {
			deslizarArriba(this, v);
			v.setVisibility(View.GONE);
			for (int i = 0; i < v.getChildCount(); i++) {
				v.getChildAt(i).setEnabled(false);
			}
			v.setEnabled(false);
		}
	}

	private void mostrar(LinearLayout v) {
		v.setVisibility(View.VISIBLE);
		deslizarAbajo(this, v);

		// Coloca en primer plano y ordena los elementos
		v.bringToFront();
		ordenar.bringToFront();
		buscar.bringToFront();
		extender.bringToFront();
		for (int i = 0; i < v.getChildCount(); i++) {
			v.getChildAt(i).setEnabled(true);
		}
		v.setEnabled(true);
	}

}
