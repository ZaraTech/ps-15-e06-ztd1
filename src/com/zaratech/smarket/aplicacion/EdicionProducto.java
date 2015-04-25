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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
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

		/*
		 * Carga de imagenes
		 */
		Button camara = (Button) findViewById(R.id.EDICION_CAMARA); // Desde
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
		Button galeria = (Button) findViewById(R.id.EDICION_GALERIA); // Desde
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
		CheckBox desctuentos = (CheckBox) findViewById(R.id.EDICION_DESCUENTO);
		final EditText descuentosEdit = (EditText) findViewById(R.id.EDICION_DESCUENTO_PRECIO);
		desctuentos.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				descuentosEdit.setEnabled(isChecked);
			}
		});

		/*
		 * Simbolos de editTexts (UM y pulgadas)
		 */
		EditText pixel = (EditText) findViewById(R.id.EDICION_PULGADAS);
		pixel.addTextChangedListener(new EditTextSimboloFinal(pixel,
				getString(R.string.udPantalla)));

		EditText price = (EditText) findViewById(R.id.EDICION_PRECIO);
		price.addTextChangedListener(new EditTextSimboloFinal(price,
				getString(R.string.udMonetaria)));

		descuentosEdit.addTextChangedListener(new EditTextSimboloFinal(
				descuentosEdit, getString(R.string.udMonetaria)));

		/*
		 * Marcas
		 */
		spinnerMarcas = (Spinner) findViewById(R.id.EDICION_MARCA);
		Button crearMarca = (Button) findViewById(R.id.EDICION_CREAR_MARCA);
		actualizaSpinner();

		crearMarca.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mostrarDialogoCreacionMarcas().show();
			}
		});
		Button eliminarMarca = (Button) findViewById(R.id.EDICION_ELIMINAR_MARCA);
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
		Button guardar = (Button) findViewById(R.id.EDICION_GUARDAR);
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

	}

	/*
	 * Creacion de dialogo de adiccion de marcas
	 */
	private AlertDialog.Builder mostrarDialogoCreacionMarcas() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		final EditText texto = new EditText(this);

		texto.setLayoutParams(layout);
		texto.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(texto);

		builder.setPositiveButton(getString(R.string.crearMarca),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String s = texto.getText() == null ? null : texto
								.getText().toString();
						if (s != null) {
							Marca m = new Marca(s);
							bd.crearMarca(m);
							actualizaSpinner();
							selecionarMarca(m);
						}
						texto.setText("");
					}
				});

		builder.setNegativeButton(getString(R.string.cancelarCrearMarca),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		return builder;
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
			ImageView image = (ImageView) findViewById(R.id.EDICION_IMAGEN);
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
			String errorMessage = getString(R.string.errorRecortarNoDisponible);
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
		ImageView image = (ImageView) findViewById(R.id.EDICION_IMAGEN);
		Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

		// Nombre
		EditText nombreEdit = (EditText) findViewById(R.id.EDICION_NOMBRE);
		if (nombreEdit.getText() == null
				|| nombreEdit.getText().toString().length() == 0) {
			Toast.makeText(this, getString(R.string.errorNombreVacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Tipo
		RadioButton smartphoneEdit = (RadioButton) findViewById(R.id.EDICION_SMARTPHONE);
		RadioButton tabletEdit = (RadioButton) findViewById(R.id.EDICION_TABLET);
		int tipo;
		if (smartphoneEdit.isChecked()) {
			tipo = Producto.TIPO_SMARTPHONE;
		} else if (tabletEdit.isChecked()) {
			tipo = Producto.TIPO_TABLET;
		} else {
			Toast.makeText(this, getString(R.string.errorTipoVacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Descripcion
		EditText descripcionEdit = (EditText) findViewById(R.id.EDICION_DESCRIPCION);
		if (descripcionEdit.getText() == null) {
			Toast.makeText(this, getString(R.string.errorDescripcionVacia),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Pulgadas
		EditText pulgadasEdit = (EditText) findViewById(R.id.EDICION_PULGADAS);
		if (pulgadasEdit.getText() == null
				|| pulgadasEdit.getText().toString() == "") {
			Toast.makeText(this, getString(R.string.errorPulgadasVacias),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		float pulgadas;
		try {
			pulgadas = Float.parseFloat(pulgadasEdit.getText().toString()
					.replace(getString(R.string.udPantalla), ""));
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.errorPulgadasErroneas),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Precio
		EditText precioEdit = (EditText) findViewById(R.id.EDICION_PRECIO);
		if (precioEdit.getText() == null
				|| precioEdit.getText().toString() == "") {
			Toast.makeText(this, getString(R.string.errorPrecioVacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}
		float precio;
		try {
			precio = Float.parseFloat(precioEdit.getText().toString()
					.replace(getString(R.string.udMonetaria), ""));
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.errorPrecioVacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// SO
		Spinner soEdit = (Spinner) findViewById(R.id.EDICION_SO);
		if (soEdit.getSelectedItem() == null) {
			Toast.makeText(this, getString(R.string.errorSOVacio),
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Marca
		Marca marca = getMarcaSelecionada();

		// Oferta
		CheckBox ofertaCheck = (CheckBox) findViewById(R.id.EDICION_DESCUENTO);
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
			EditText descuentoEdit = (EditText) findViewById(R.id.EDICION_DESCUENTO_PRECIO);
			if (descuentoEdit.getText() == null
					|| descuentoEdit.getText().toString() == "") {
				Toast.makeText(this, getString(R.string.errorDescuentoVacio),
						Toast.LENGTH_SHORT).show();
				return null;
			}
			float descuento;
			try {
				descuento = Float.parseFloat(descuentoEdit.getText().toString()
						.replace(getString(R.string.udMonetaria), ""));
			} catch (Exception e) {
				Toast.makeText(this, getString(R.string.errorDescuentoErroneo),
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
		ImageView image = (ImageView) findViewById(R.id.EDICION_IMAGEN);
		image.setImageBitmap(producto.getImagen());

		// Nombre
		EditText nombreEdit = (EditText) findViewById(R.id.EDICION_NOMBRE);
		nombreEdit.setText(producto.getNombre() != null ? producto.getNombre()
				: "");

		// Descripcion
		EditText descripcionEdit = (EditText) findViewById(R.id.EDICION_DESCRIPCION);
		descripcionEdit.setText(producto.getDescripcion() != null ? producto
				.getDescripcion() : "");

		// Marca
		selecionarMarca(producto.getMarca());

		// Tipo
		RadioButton smartphoneEdit = (RadioButton) findViewById(R.id.EDICION_SMARTPHONE);
		RadioButton tabletEdit = (RadioButton) findViewById(R.id.EDICION_TABLET);
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
		Spinner soEdit = (Spinner) findViewById(R.id.EDICION_SO);
		if (soEdit.getCount() > producto.getSistemaOperativo()
				&& producto.getSistemaOperativo() >= 0) {
			soEdit.setSelection(producto.getSistemaOperativo());
		}

		// Precio
		EditText precioEdit = (EditText) findViewById(R.id.EDICION_PRECIO);
		precioEdit.setText(Double.toString(producto.getPrecio()));

		// Pulgadas
		EditText pulgadasEdit = (EditText) findViewById(R.id.EDICION_PULGADAS);
		pulgadasEdit.setText(Double.toString(producto.getDimensionPantalla()));

		// Oferta
		CheckBox ofertaCheck = (CheckBox) findViewById(R.id.EDICION_DESCUENTO);
		ofertaCheck.setChecked(producto.isOferta());
		EditText descuentoEdit = (EditText) findViewById(R.id.EDICION_DESCUENTO_PRECIO);
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