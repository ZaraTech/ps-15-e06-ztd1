package com.zaratech.smarket.aplicacion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Filtro;
import com.zaratech.smarket.componentes.Listado;
import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Orden;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.utiles.AdaptadorProductos;
import com.zaratech.smarket.utiles.CargadorAsincrono;
import com.zaratech.smarket.utiles.EditTextSimboloFinal;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Activity encargada de las busquedas de productos
 * 
 * @author Aitor
 * @author Cristian Romám Morte
 */
@SuppressLint("DefaultLocale")
public class BusquedaProducto extends ListActivity implements TextWatcher, Listado {

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
	 * Ids de los posibles filtros
	 */
	public static final int MARCA_FILTRO = 0;
	public static final int PRECIO_FILTRO = 1;
	public static final int PULGADAS_FILTRO = 2;
	public static final int SO_FILTRO = 3;
	public static final int TIPO_FILTRO = 4;
	public static final int OFERTAS_FILTRO = 5;

	/**
	 * Formularios de cada uno de los posibles filtros
	 */
	public static final int[] LAYOUT_FILTROS = { R.id.busqueda_marca_layout,
			R.id.busqueda_precio_layout, R.id.busqueda_pulgadas_layout,
			R.id.busqueda_so_layout, R.id.busqueda_tipo_layout };

	/**
	 * Nombre de cada uno de los filtros
	 */
	public static final int[] NOMBRES_FILTROS = { R.string.app_marca,
			R.string.app_precio, R.string.app_pulgadas,
			R.string.app_sistema_operativo, R.string.app_tipo,
			R.string.busqueda_solo_ofertas };

	/**
	 * Nombre de cada uno de las posibles ordenaciones
	 */
	public static final int[] NOMBRES_ORDENACION = {
			R.string.ordenacion_nombre_ascendente,
			R.string.ordenacion_nombre_descendente,
			R.string.ordenacion_precio_ascendente,
			R.string.ordenacion_precio_descendente };

	/**
	 * Posibles ordenaciones
	 */
	public static final Orden[] ORDEN = {
			new Orden(AdaptadorBD.DB_ORDENACION_NOMBRE,
					AdaptadorBD.DB_ORDENACION_ASC),
			new Orden(AdaptadorBD.DB_ORDENACION_NOMBRE,
					AdaptadorBD.DB_ORDENACION_DESC),
			new Orden(AdaptadorBD.DB_ORDENACION_PRECIO,
					AdaptadorBD.DB_ORDENACION_ASC),
			new Orden(AdaptadorBD.DB_ORDENACION_PRECIO,
					AdaptadorBD.DB_ORDENACION_DESC), };

	/**
	 * Elementos utilizados durante la activity
	 */
	private Button buscar;
	private LinearLayout ordenarLayout;
	private TextView extender;

	// Listado de marcas
	private List<Marca> marcas;

	// Variable que denota si el usuario tiene o no permisos de administrador
	private boolean admin = false;

	/**
	 * Clave que identifica si tienen permisos de administrador dentro del campo
	 * extras del Intent
	 */
	public static final String EXTRA_ADMIN = "Admin";

	/**
	 * Método privado que carga el istado de productos de la BD
	 */	
	public void cargarListado() {
		
		CargadorAsincrono ca = new CargadorAsincrono(this, bd);
		ca.execute();
	}

	public void cargarListadoAsincrono(List<Producto> productos){

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
				android.R.layout.simple_list_item_1, filtros);
		filtrar.setAdapter(adapter);

		filtrar.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				esconderTodos(LAYOUT_FILTROS);
				
				if (position >= 0 && position < LAYOUT_FILTROS.length) {
					mostrar((LinearLayout) findViewById(LAYOUT_FILTROS[position]));
				}
				//mostrar solo ofertas
				else if (position == LAYOUT_FILTROS.length){
					// cambia la imagen del boton de extender
					extender.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_action_collapse);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				esconderTodos(LAYOUT_FILTROS);
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
		so.setAdapter(adp1);

		/*
		 * Marcas
		 */
		Spinner spinnerMarcas = (Spinner) findViewById(R.id.busqueda_marca);
		// Obtiene las marcas
		marcas = bd.obtenerMarcas();
		List<String> nombres = new LinkedList<String>();

		for (Marca m : marcas) {
			nombres.add(m.getNombre());
		}
		// Actualiza spinner de las marcas
		adp1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, nombres);
		spinnerMarcas.setAdapter(adp1);

		/*
		 * Ordenacion
		 */
		this.ordenarLayout = (LinearLayout) findViewById(R.id.busqueda_ordenar_layout);
		final Spinner ordenarSpinner = (Spinner) findViewById(R.id.busqueda_ordenar);

		String[] ordenacion = new String[NOMBRES_ORDENACION.length];
		for (int i = 0; i < NOMBRES_ORDENACION.length; i++) {
			ordenacion[i] = getString(NOMBRES_ORDENACION[i]);
		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, ordenacion);
		ordenarSpinner.setAdapter(adapter);

		/*
		 * Boton Buscar
		 */
		buscar = (Button) findViewById(R.id.busqueda_aplicar);
		final Context context = this;
		buscar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Filtro filtro = getFiltro();
				int pos = ordenarSpinner.getSelectedItemPosition();
				Orden orden;
				if (pos >= 0 && pos < ORDEN.length) {
					orden = ORDEN[pos];
				} else {
					orden = new Orden();
				}
				List<Producto> productos = bd.FiltrarYOrdenarProducto(filtro,
						orden);
				AdaptadorProductos adaptador = new AdaptadorProductos(context,
						productos);
				setListAdapter(adaptador);
			}
		});

		/*
		 * Separador personalizado de elementos de listado
		 */
		int[] colors = { 0, 0xFFFFFFFF, 0 };
		GradientDrawable divider = new GradientDrawable(Orientation.RIGHT_LEFT,
				colors);
		getListView().setDivider(divider);
		getListView().setDividerHeight(2);

		/*
		 * Listado
		 */
		cargarListado();
		actualizarSugerencias();

		/*
		 * Permisos de administrador
		 */
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey(EXTRA_ADMIN)) {
			admin = this.getIntent().getExtras().getBoolean(EXTRA_ADMIN);
		}
		// Opciones de administrador
		if (admin) {
			registerForContextMenu(getListView());
		}

	}

	/*
	 * Obtiene filtro a partir del formulario
	 */
	private Filtro getFiltro() {
		Filtro filtro = new Filtro();
		Spinner filtrar = (Spinner) findViewById(R.id.busqueda_filtrar);
		EditText edit;
		Spinner spinner;
		int pos;
		switch (filtrar.getSelectedItemPosition()) {
		case MARCA_FILTRO:
			spinner = (Spinner) findViewById(R.id.busqueda_marca);
			pos = spinner.getSelectedItemPosition();
			if (pos >= 0 && pos < marcas.size()) {
				filtro.addFiltroMarca(marcas.get(pos).getId());
			}
			break;
		case PRECIO_FILTRO:
			edit = (EditText) findViewById(R.id.busqueda_precio_desde);
			float precioDesde;
			try {
				precioDesde = Float.parseFloat(edit.getText().toString()
						.replace(getString(R.string.app_ud_monetaria), ""));
			} catch (Exception e) {
				precioDesde = 0;
			}

			edit = (EditText) findViewById(R.id.busqueda_precio_hasta);
			float precioHasta;
			try {
				precioHasta = Float.parseFloat(edit.getText().toString()
						.replace(getString(R.string.app_ud_monetaria), ""));
			} catch (Exception e) {
				precioHasta = Integer.MAX_VALUE;
			}
			filtro.addFiltroPrecio(precioDesde, precioHasta);
			break;
		case PULGADAS_FILTRO:
			edit = (EditText) findViewById(R.id.busqueda_pulgadas_desde);
			float pulgadasDesde;
			try {
				pulgadasDesde = Float.parseFloat(edit.getText().toString()
						.replace(getString(R.string.app_pulgadas), ""));
			} catch (Exception e) {
				pulgadasDesde = 0;
			}

			edit = (EditText) findViewById(R.id.busqueda_pulgadas_hasta);
			float pulgadasHasta;
			try {
				pulgadasHasta = Float.parseFloat(edit.getText().toString()
						.replace(getString(R.string.app_pulgadas), ""));
			} catch (Exception e) {
				pulgadasHasta = Integer.MAX_VALUE;
			}
			filtro.addFiltroPulgadas(pulgadasDesde, pulgadasHasta);
			break;
		case SO_FILTRO:
			spinner = (Spinner) findViewById(R.id.busqueda_SO);
			pos = spinner.getSelectedItemPosition();
			if (pos >= 0 && pos < 4) {
				filtro.addFiltroSO(pos);
			}
			break;
		case TIPO_FILTRO:
			RadioButton smartphoneRadio = (RadioButton) findViewById(R.id.busqueda_smartphone);
			RadioButton tabletRadio = (RadioButton) findViewById(R.id.busqueda_tablet);
			int tipo;
			if (smartphoneRadio.isChecked()) {
				tipo = Producto.TIPO_SMARTPHONE;
			} else if (tabletRadio.isChecked()) {
				tipo = Producto.TIPO_TABLET;
			} else {
				return filtro;
			}
			filtro.addFiltroTipo(tipo);
			break;
		case OFERTAS_FILTRO:
			filtro.addFiltroOferta(true);
			break;
		default:
			break;
		}
		return filtro;
	}

	/*
	 * Actualiza el AutoCompleteTextView
	 */
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
		String[] nombreMarcas = bd.obtenerNombreMarcas();

		// Rellenar Listado de Producto para autocompletado
		String[] nombreProductos = bd.obtenerNombreProductos();

		int totalProductos = nombreProductos.length;
		int totalMarcas = nombreMarcas.length;
		int totalDispositivos = 2;

		nombres = new String[totalMarcas + totalProductos + totalDispositivos];

		// Añade los nombres de las marcas al array de sugerencias
		for (int i = 0; i < totalMarcas; i++) {
			nombres[i] = nombreMarcas[i];
		}

		// Añade los nombres de los productos al array de sugerencias
		for (int i = 0; i < totalProductos; i++) {
			nombres[(totalMarcas) + i] = nombreProductos[i];
		}
		nombres[totalMarcas + totalProductos] = "Tablet";
		nombres[totalMarcas + totalProductos + 1] = "SmartPhone";
		return nombres;
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
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

			Spinner spinnerMarcas = (Spinner) findViewById(R.id.busqueda_marca);
			// Obtiene las marcas
			marcas = bd.obtenerMarcas();
			List<String> nombres = new LinkedList<String>();

			for (Marca m : marcas) {
				nombres.add(m.getNombre());
			}
			// Actualiza spinner de las marcas
			ArrayAdapter<String> adpadtador = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, nombres);
			spinnerMarcas.setAdapter(adpadtador);
			
			actualizarSugerencias();
		}

	}

	/**
	 * Realiza la búsqueda y la muestra
	 */
	@SuppressLint("DefaultLocale")
	private void realizarBusqueda(String cadena) {

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
				productosBusqueda.add(p);

				// Vemos si la entrada esta contenido en algun nombre
			} else if (p.getNombre().toLowerCase().indexOf(cadena) != -1) {
				productosBusqueda.add(p);

				// Vemos si la entrada esta contenido en alguna marca
			} else if (p.getMarca() != null
					&& p.getMarca().getNombre() != null
					&& p.getMarca().getNombre().toLowerCase().indexOf(cadena) != -1) {
				productosBusqueda.add(p);

				// Vemos si la entrada esta contenido en algun dispositivo
			} else if (tipoDispositivo.toLowerCase().indexOf(cadena) != -1) {
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
		a.setFillAfter(true);
		
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
		a.setFillAfter(false);
		
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
			
			// cambia la imagen del boton de extender
			extender.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_action_expand);
			
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
		ordenarLayout.bringToFront();
		buscar.bringToFront();
		extender.bringToFront();
		
		// cambia la imagen del boton de extender
		extender.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_action_collapse);
		
		for (int i = 0; i < v.getChildCount(); i++) {
			v.getChildAt(i).setEnabled(true);
		}
		v.setEnabled(true);
	}

}
