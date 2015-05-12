package com.zaratech.smarket.aplicacion;

import java.util.LinkedList;
import java.util.List;

import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.utiles.EditTextSimboloFinal;
import com.zaratech.smarket.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity que gestiona la edición e inserción de productos
 * 
 * @author Cristian Romám Morte
 */
public class EdicionProducto extends Activity {

	/** Clave que identifica un Producto dentro del campo extras del Intent */
	public static final String EXTRA_PRODUCTO = "Producto";

	// Id asignado a cada activity que se inicia
	private static final int ACTIVITY_GALERIA = 1;
	private static final int ACTIVITY_CAMARA = 2;
	private static final int ACTIVITY_RECORTAR = 3;

	// Spinner y lista de marcas
	private Spinner spinnerMarcas;
	private List<Marca> marcas;

	// Id del producto
	private int ID;

	// Base de datos
	private AdaptadorBD bd;

	/*
	 * Inicialización de la activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edicion_producto);

		/*
		 * Inicialización la BD
		 */
		bd = new AdaptadorBD(this);
		bd.open();

		/*
		 * Carga de imagenes
		 */
		Button camara = (Button) findViewById(R.id.edicion_camara); // Desde
		// camara
		camara.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(takePictureIntent, ACTIVITY_CAMARA);
				}
			}
		});
		Button galeria = (Button) findViewById(R.id.edicion_galeria); // Desde
		// galeria
		galeria.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, ACTIVITY_GALERIA);
			}
		});

		/*
		 * Descuentos
		 */
		CheckBox desctuentos = (CheckBox) findViewById(R.id.edicion_oferta);
		final EditText descuentosEdit = (EditText) findViewById(R.id.edicion_oferta_precio);
		desctuentos.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				descuentosEdit.setEnabled(isChecked);
			}
		});

		/*
		 * Simbolos de editTexts (UM y pulgadas)
		 */
		EditText pixel = (EditText) findViewById(R.id.edicion_pulgadas);
		pixel.addTextChangedListener(new EditTextSimboloFinal(pixel,
				getString(R.string.app_ud_pantalla)));

		EditText price = (EditText) findViewById(R.id.edicion_precio);
		price.addTextChangedListener(new EditTextSimboloFinal(price,
				getString(R.string.app_ud_monetaria)));

		descuentosEdit.addTextChangedListener(new EditTextSimboloFinal(
				descuentosEdit, getString(R.string.app_ud_monetaria)));

		/*
		 * Sistema Operativo
		 */
		Spinner so = (Spinner) findViewById(R.id.edicion_SO);
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
		spinnerMarcas = (Spinner) findViewById(R.id.edicion_marca);
		Button crearMarca = (Button) findViewById(R.id.edicion_crear_marca);
		actualizaSpinner();

		crearMarca.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mostrarDialogoCreacionMarcas();
			}
		});

		Button modificarMarca = (Button) findViewById(R.id.edicion_modificar_marca);
		modificarMarca.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Marca m = getMarcaSelecionada();
				if (m != null) {
					mostrarDialogoModificarMarcas(m);
				}
			}

		});

		Button eliminarMarca = (Button) findViewById(R.id.edicion_eliminar_marca);
		eliminarMarca.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (getMarcaSelecionadaString() != null) {
					bd.borrarMarca(getMarcaSelecionada().getId());
					actualizaSpinner();
				}
			}
		});

		/*
		 * Guardar
		 */
		Button guardar = (Button) findViewById(R.id.edicion_guardar);
		guardar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Producto p = formularioAProducto();
				if (p != null) {
					if (ID == -1) { // Producto nuevo
						bd.crearProducto(p);
					} else {// Producto modificado
						bd.actualizarProducto(p);
					}
					finish();
				}
			}
		});

		/*
		 * Comprueba si se ha recibido un producto y en tal caso rellena el
		 * formulario con él
		 */
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey(EXTRA_PRODUCTO)) {
			Producto p = this.getIntent().getExtras()
					.getParcelable(EXTRA_PRODUCTO);
			productoAFormulario(p);
		} else {
			ID = -1;
		}
		/*
		 * Recuperamos la imagen si es necesario
		 */
		if (savedInstanceState != null) {
			Bitmap bitmap = savedInstanceState.getParcelable("image");
			ImageView image = (ImageView) findViewById(R.id.edicion_imagen);
			image.setImageBitmap(bitmap);
		}
	}

	/*
	 * Salvamos los datos de la instancia para poder cargarlos mas adelante
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		ImageView image = (ImageView) findViewById(R.id.edicion_imagen);
		BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
		Bitmap bitmap = drawable.getBitmap();
		outState.putParcelable("image", bitmap);
		super.onSaveInstanceState(outState);
	}

	/*
	 * Creacion de dialogo de adiccion de marcas
	 */
	private void mostrarDialogoCreacionMarcas() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		final EditText texto = new EditText(this);

		int maxLength = 30; // Numero de caracteres maximo
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		texto.setFilters(fArray);

		final Context context = this;
		texto.setLayoutParams(layout);
		texto.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(texto);
		builder.setTitle(getString(R.string.app_marca));
		builder.setNegativeButton(
				getString(R.string.edicion_cancelar_marca),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.setPositiveButton(getString(R.string.edicion_crear_marca), null);

		final AlertDialog dialog = builder.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new OnClickListener() {

					public void onClick(View v) {
						String s = texto.getText() == null ? "" : texto
								.getText().toString();
						if (s.length() > 0 && s.length() < 30) {
							Marca m = new Marca(s);
							bd.crearMarca(m);
							actualizaSpinner();
							selecionarMarca(m);
							dialog.dismiss();
						} else {
							Toast.makeText(
									context,
									getString(R.string.edicion_error_marca_erronea),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	/*
	 * Creacion de dialogo de adiccion de marcas
	 */
	private void mostrarDialogoModificarMarcas(final Marca m) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		final EditText texto = new EditText(this);
		texto.setText(m.getNombre());

		int maxLength = 30; // Numero de caracteres maximo
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		texto.setFilters(fArray);

		final Context context = this;
		texto.setLayoutParams(layout);
		texto.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(texto);
		builder.setTitle(getString(R.string.app_marca));
		builder.setNegativeButton(
				getString(R.string.edicion_cancelar_marca),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.setPositiveButton(getString(R.string.edicion_modificar_marca), null);

		final AlertDialog dialog = builder.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new OnClickListener() {

					public void onClick(View v) {
						String s = texto.getText() == null ? "" : texto
								.getText().toString();
						if (s.length() > 0 && s.length() < 30) {
							m.setNombre(s);
							bd.actualizarMarca(m);
							actualizaSpinner();
							selecionarMarca(m);
							dialog.dismiss();
						} else {
							Toast.makeText(
									context,
									getString(R.string.edicion_error_marca_erronea),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	/*
	 * Resultado de las actividades
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED) { // Accion cancelada
			return;
		}
		if (requestCode == ACTIVITY_GALERIA) {// Resultado de la galeria
			Uri selectedImage = data.getData();
			obtenerImagenRecortada(selectedImage);

		} else if (requestCode == ACTIVITY_CAMARA) {// Resultado de la
			// camara
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			obtenerImagenRecortada(imageBitmap);

		} else if (requestCode == ACTIVITY_RECORTAR) {// Resultado de recortar
			// la imagen
			Bundle extras = data.getExtras();
			Bitmap selectedBitmap = extras.getParcelable("data");
			ImageView image = (ImageView) findViewById(R.id.edicion_imagen);
			image.setImageBitmap(selectedBitmap);
		}
	}

	/*
	 * LLama a la activity de recorte de imagenes a partir de un bitmap
	 */
	private void obtenerImagenRecortada(Bitmap image) {
		String path = Images.Media.insertImage(getContentResolver(), image,
				"Title", null);
		Uri uri = Uri.parse(path);
		obtenerImagenRecortada(uri);
	}

	/*
	 * LLama a la activity de recorte de imagenes a partir de una URI
	 */
	private void obtenerImagenRecortada(Uri picUri) {
		try {
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(picUri, "image/*"); // Imagen que se desea
			// modificar
			// Proporcion 1:1
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("return-data", true);
			startActivityForResult(cropIntent, ACTIVITY_RECORTAR);

		} catch (ActivityNotFoundException anfe) { // El dispositivo no tiene
			// ninguna aplicacion
			// para recortar la imagen
			String errorMessage = getString(R.string.edicion_error_recortar_no_disponible);
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * Obtiene los datos del formulario y devuelve un producto
	 * 
	 * @return Producto con los datos del formulario
	 */
	public Producto formularioAProducto() {

		// Imagen
		ImageView image = (ImageView) findViewById(R.id.edicion_imagen);
		Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

		// Nombre
		EditText nombreEdit = (EditText) findViewById(R.id.edicion_nombre);
		if (nombreEdit.getText() == null
				|| nombreEdit.getText().toString().length() == 0) {
			Toast.makeText(this,
					getString(R.string.edicion_error_nombre_vacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		if (nombreEdit.getText().toString().length() < 5
				|| nombreEdit.getText().toString().length() > 30) {
			Toast.makeText(this,
					getString(R.string.edicion_error_nombre_errorneo),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Tipo
		RadioButton smartphoneEdit = (RadioButton) findViewById(R.id.edicion_smartphone);
		RadioButton tabletEdit = (RadioButton) findViewById(R.id.edicion_tablet);
		int tipo;
		if (smartphoneEdit.isChecked()) {
			tipo = Producto.TIPO_SMARTPHONE;
		} else if (tabletEdit.isChecked()) {
			tipo = Producto.TIPO_TABLET;
		} else {
			Toast.makeText(this, getString(R.string.edicion_error_tipo_vacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Descripcion
		EditText descripcionEdit = (EditText) findViewById(R.id.edicion_descripcion);
		if (descripcionEdit.getText() == null) {
			Toast.makeText(this,
					getString(R.string.edicion_error_descripcion_vacia),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		if (descripcionEdit.getText().toString().length() > 1000) {
			Toast.makeText(this,
					getString(R.string.edicion_error_descripcion_erronea),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Pulgadas
		EditText pulgadasEdit = (EditText) findViewById(R.id.edicion_pulgadas);
		if (pulgadasEdit.getText() == null
				|| pulgadasEdit.getText().toString() == "") {
			Toast.makeText(this,
					getString(R.string.edicion_error_pulgadas_vacias),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		float pulgadas;
		try {
			pulgadas = Float.parseFloat(pulgadasEdit.getText().toString()
					.replace(getString(R.string.app_ud_pantalla), ""));
		} catch (Exception e) {
			Toast.makeText(this,
					getString(R.string.edicion_error_pulgadas_vacias),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		if (pulgadas <= 0) {
			Toast.makeText(this,
					getString(R.string.edicion_error_pulgadas_erroneas),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Precio
		EditText precioEdit = (EditText) findViewById(R.id.edicion_precio);
		if (precioEdit.getText() == null
				|| precioEdit.getText().toString() == "") {
			Toast.makeText(this,
					getString(R.string.edicion_error_precio_vacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		float precio;
		try {
			precio = Float.parseFloat(precioEdit.getText().toString()
					.replace(getString(R.string.app_ud_monetaria), ""));
		} catch (Exception e) {
			Toast.makeText(this,
					getString(R.string.edicion_error_precio_vacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		if (precio <= 0) {
			Toast.makeText(this,
					getString(R.string.edicion_error_precio_erroneo),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// SO
		Spinner soEdit = (Spinner) findViewById(R.id.edicion_SO);
		if (soEdit.getSelectedItem() == null) {
			Toast.makeText(this, getString(R.string.edicion_error_so_vacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Marca
		Marca marca = getMarcaSelecionada();

		// Oferta
		CheckBox ofertaCheck = (CheckBox) findViewById(R.id.edicion_oferta);
		boolean oferta = ofertaCheck.isChecked();

		Producto producto = new Producto(nombreEdit.getText().toString(),
				marca, tipo, precio);
		producto.setDescripcion(descripcionEdit.getText().toString());
		producto.setDimensionPantalla(pulgadas);
		producto.setSistemaOperativo(soEdit.getSelectedItemPosition());
		producto.setId(ID);
		producto.setImagen(bitmap);

		// Descuento
		if (oferta) {
			EditText descuentoEdit = (EditText) findViewById(R.id.edicion_oferta_precio);
			if (descuentoEdit.getText() == null
					|| descuentoEdit.getText().toString() == "") {
				Toast.makeText(this,
						getString(R.string.edicion_error_descuento_vacio),
						Toast.LENGTH_SHORT).show();
				return null;
			}
			float descuento;
			try {
				descuento = Float.parseFloat(descuentoEdit.getText().toString()
						.replace(getString(R.string.app_ud_monetaria), ""));
			} catch (Exception e) {
				Toast.makeText(this,
						getString(R.string.edicion_error_descuento_vacio),
						Toast.LENGTH_SHORT).show();
				return null;
			}
			if (descuento <= 0) {
				Toast.makeText(this,
						getString(R.string.edicion_error_descuento_erroneo),
						Toast.LENGTH_SHORT).show();
				return null;
			}
			if (descuento >= precio) {
				Toast.makeText(
						this,
						getString(R.string.edicion_error_descuento_superior_precio),
						Toast.LENGTH_SHORT).show();
				return null;
			}
			producto.setOferta();
			producto.setPrecioOferta(descuento);
		}
		return producto;
	}

	/**
	 * A partir de un producto rellena el formulario
	 * 
	 * @param producto
	 *            Producto a insertar en el formulario
	 */
	public void productoAFormulario(Producto producto) {

		// ID
		ID = producto.getId();

		// Imagen
		ImageView image = (ImageView) findViewById(R.id.edicion_imagen);
		image.setImageBitmap(producto.getImagen());

		// Nombre
		EditText nombreEdit = (EditText) findViewById(R.id.edicion_nombre);
		nombreEdit.setText(producto.getNombre() != null ? producto.getNombre()
				: "");

		// Descripcion
		EditText descripcionEdit = (EditText) findViewById(R.id.edicion_descripcion);
		descripcionEdit.setText(producto.getDescripcion() != null ? producto
				.getDescripcion() : "");

		// Marca
		selecionarMarca(producto.getMarca());

		// Tipo
		RadioButton smartphoneEdit = (RadioButton) findViewById(R.id.edicion_smartphone);
		RadioButton tabletEdit = (RadioButton) findViewById(R.id.edicion_tablet);
		int tipo = producto.getTipo();
		switch (tipo) {
		case Producto.TIPO_SMARTPHONE:
			smartphoneEdit.setChecked(true);
			tabletEdit.setChecked(false);
			break;
		case Producto.TIPO_TABLET:
			smartphoneEdit.setChecked(false);
			tabletEdit.setChecked(true);
			break;
		default:
			smartphoneEdit.setChecked(false);
			tabletEdit.setChecked(false);
			break;
		}

		// SO
		Spinner soEdit = (Spinner) findViewById(R.id.edicion_SO);
		if (soEdit.getCount() > producto.getSistemaOperativo()
				&& producto.getSistemaOperativo() >= 0) {
			soEdit.setSelection(producto.getSistemaOperativo());
		}

		// Precio
		EditText precioEdit = (EditText) findViewById(R.id.edicion_precio);
		precioEdit.setText(Double.toString(producto.getPrecio()));

		// Pulgadas
		EditText pulgadasEdit = (EditText) findViewById(R.id.edicion_pulgadas);
		pulgadasEdit.setText(Double.toString(producto.getDimensionPantalla()));

		// Oferta
		CheckBox ofertaCheck = (CheckBox) findViewById(R.id.edicion_oferta);
		ofertaCheck.setChecked(producto.isOferta());
		EditText descuentoEdit = (EditText) findViewById(R.id.edicion_oferta_precio);
		descuentoEdit.setText(Double.toString(producto.getPrecioOferta()));
	}

	/**
	 * Actualiza spinner de las marcas
	 */
	public void actualizaSpinner() {
		ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getMarcas());
		adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerMarcas.setAdapter(adp1);
	}

	/*
	 * Obtiene las marcas
	 */
	private List<String> getMarcas() {
		marcas = bd.obtenerMarcas();
		List<String> strs = new LinkedList<String>();
		for (Marca m : marcas) {
			strs.add(m.getNombre());
		}
		return strs;
	}

	/**
	 * Obtiene la marca selecionada
	 * 
	 * @return Marca selecionada
	 */
	public Marca getMarcaSelecionada() {
		return marcas.get(getMarcaSelecionadaPosicion());
	}

	/*
	 * Obtiene el string de la marca selecionada
	 */
	private String getMarcaSelecionadaString() {
		return spinnerMarcas.getSelectedItem() == null ? null : spinnerMarcas
				.getSelectedItem().toString();
	}

	/*
	 * Obtiene la posicion de la marca selecionada
	 */
	private int getMarcaSelecionadaPosicion() {
		return spinnerMarcas.getSelectedItem() == null ? -1 : spinnerMarcas
				.getSelectedItemPosition();
	}

	/**
	 * Seleciona una marca
	 * 
	 * @param marca
	 *            Marca a selecionar
	 */
	public void selecionarMarca(Marca marca) {
		for (int i = 0; i < marcas.size(); i++) {
			if (marcas.get(i).getId() == marca.getId()) {
				spinnerMarcas.setSelection(i);
				return;
			}
		}
	}

}
