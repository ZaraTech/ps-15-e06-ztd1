package com.zaratech.smarket.aplicacion;

import java.util.LinkedList;
import java.util.List;

import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
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

public class InsertModificationProduct extends Activity {

	private static final String EXTRA_PRODUCTO = "Producto";
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int RESULT_IMAGE_CAPTURE = 2;
	private static final int RESULT_PIC_CROP = 3;
	private static final String UM = "€";
	private static final String PULGADAS = "'";
	private Spinner brands;
	private int ID;
	private AdaptadorBD bd;
	private List<Marca> marcas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_modification_product);

		/*
		 * Inicializamos la BD
		 */
		bd = new AdaptadorBD();

		/*
		 * Carga de imagenes
		 */
		Button take = (Button) findViewById(R.id.AIMPTakePhoto); // Desde camara
		take.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(takePictureIntent,
							RESULT_IMAGE_CAPTURE);
				}
			}
		});
		Button open = (Button) findViewById(R.id.AIMPGallery); // Desde galeria
		open.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		/*
		 * Descuentos
		 */
		CheckBox txtbox1 = (CheckBox) findViewById(R.id.AIMPDiscountBox);
		final EditText discount = (EditText) findViewById(R.id.AIMPDiscountNum);
		txtbox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				discount.setEnabled(isChecked);
			}
		});

		/*
		 * Simbolos de editTexts
		 */
		EditText pixel = (EditText) findViewById(R.id.AIMPInchesEdit);
		pixel.addTextChangedListener(new lastSimbol(pixel, PULGADAS));

		EditText price = (EditText) findViewById(R.id.AIMPPriceEdit);
		price.addTextChangedListener(new lastSimbol(price, UM));

		discount.addTextChangedListener(new lastSimbol(discount, UM));

		/*
		 * Marcas
		 */
		brands = (Spinner) findViewById(R.id.AIMPBrandSpinner);
		Button CreateBrand = (Button) findViewById(R.id.AIMPBrandCreate);
		updateSpinner();

		CreateBrand.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				createDialog().show();
			}
		});
		Button RemoveBrand = (Button) findViewById(R.id.AIMPBrandDelete);
		RemoveBrand.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (getBrandSelectedString() != null) {
					bd.borrarMarca(getBrandSelected().getId());
					updateSpinner();
				}
			}
		});

		/*
		 * Guardar
		 */
		Button save = (Button) findViewById(R.id.AIMPSave);
		save.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Producto p = Formulario2Producto();
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
		 * Recuperar producto
		 */

		if (getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey(EXTRA_PRODUCTO)) {
			Producto p = this.getIntent().getExtras()
					.getParcelable(EXTRA_PRODUCTO);
			Producto2Formulario(p);
		} else {
			ID = -1;
		}

	}

	/*
	 * Creacion de dialogo de adiccion de marcas
	 */
	private AlertDialog.Builder createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);
		builder.setPositiveButton("Crear",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String s = input.getText() == null ? null : input
								.getText().toString();
						if (s != null) {
							bd.crearMarca(new Marca(s));
							updateSpinner();
							selectBrand(s);
						}
						input.setText("");
					}
				});
		builder.setNegativeButton("Cancelar",
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
		if (requestCode == RESULT_LOAD_IMAGE) {// Resultado de la galeria
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
			obtenerImagenRecortada(bitmap);

		} else if (requestCode == RESULT_IMAGE_CAPTURE) {// Resultado de la
															// camara
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			obtenerImagenRecortada(imageBitmap);

		} else if (requestCode == RESULT_PIC_CROP) {// Resultado de recortar la
													// imagen
			Bundle extras = data.getExtras();
			Bitmap selectedBitmap = extras.getParcelable("data");
			ImageView image = (ImageView) findViewById(R.id.AIMPProductImage);
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
			cropIntent.putExtra("crop", "true");

			// Proporcion 1:1
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("return-data", true);
			startActivityForResult(cropIntent, RESULT_PIC_CROP);

		} catch (ActivityNotFoundException anfe) { // El dispositivo no tiene
													// ninguna aplicacion
													// para recortar la imagen
			String errorMessage = "Su dispositivo no soporta la acción de recortar :(";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/*
	 * Obtiene los datos del formulario y devuelve un producto
	 */
	public Producto Formulario2Producto() {
		// Nombre
		EditText nombreEdit = (EditText) findViewById(R.id.AIMPNameEdit);
		if (nombreEdit.getText() == null
				|| nombreEdit.getText().toString().length() == 0) {
			Toast.makeText(this, "No se ha completado el campo del nombre",
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Tipo
		RadioButton smartphoneEdit = (RadioButton) findViewById(R.id.AIMPSmartphone);
		RadioButton tabletEdit = (RadioButton) findViewById(R.id.AIMPTablet);
		int tipo;
		if (smartphoneEdit.isChecked()) {
			tipo = Producto.TIPO_SMARTPHONE;
		} else if (tabletEdit.isChecked()) {
			tipo = Producto.TIPO_TABLET;
		} else {
			Toast.makeText(this, "No se ha selecionado un tipo",
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Descripcion
		EditText descripcionEdit = (EditText) findViewById(R.id.AIMPDescriptionEdit);
		if (descripcionEdit.getText() == null) {
			Toast.makeText(this,
					"No se ha completado el campo de la descripción",
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Pulgadas
		EditText pulgadasEdit = (EditText) findViewById(R.id.AIMPInchesEdit);
		if (pulgadasEdit.getText() == null
				|| pulgadasEdit.getText().toString() == "") {
			Toast.makeText(this,
					"No se ha completado el campo de las pulgadas",
					Toast.LENGTH_SHORT).show();
			return null;
		}
		float pulgadas;
		try {
			pulgadas = Float.parseFloat(pulgadasEdit.getText().toString()
					.replace(PULGADAS, ""));
		} catch (Exception e) {
			Toast.makeText(this, "El dato de las pulgadas es incorrecto",
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Precio
		EditText precioEdit = (EditText) findViewById(R.id.AIMPPriceEdit);
		if (precioEdit.getText() == null
				|| precioEdit.getText().toString() == "") {
			Toast.makeText(this, "No se ha completado el campo del precio",
					Toast.LENGTH_SHORT).show();
			return null;
		}
		float precio;
		try {
			precio = Float.parseFloat(precioEdit.getText().toString()
					.replace(UM, ""));
		} catch (Exception e) {
			Toast.makeText(this, "El dato del precio es incorrecto",
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// SO
		Spinner soEdit = (Spinner) findViewById(R.id.AIMPSOSpinner);
		if (soEdit.getSelectedItem() == null) {
			Toast.makeText(this, "No se ha selecionado un sistema operativo",
					Toast.LENGTH_SHORT).show();
			return null;
		}

		// Marca
		Marca marca = getBrandSelected();

		// Oferta
		CheckBox ofertaCheck = (CheckBox) findViewById(R.id.AIMPDiscountBox);
		boolean oferta = ofertaCheck.isChecked();

		Producto producto = new Producto(nombreEdit.getText().toString(),
				marca, tipo, precio);
		producto.setDescripcion(descripcionEdit.getText().toString());
		producto.setDimensionPantalla(pulgadas);
		producto.setSistemaOperativo(soEdit.getSelectedItemPosition());
		producto.setId(ID);

		if (oferta) {
			// Descuento
			EditText descuentoEdit = (EditText) findViewById(R.id.AIMPDiscountNum);
			if (descuentoEdit.getText() == null
					|| descuentoEdit.getText().toString() == "") {
				Toast.makeText(this,
						"No se ha completado el campo de descuento",
						Toast.LENGTH_SHORT).show();
				return null;
			}
			float descuento;
			try {
				descuento = Float.parseFloat(descuentoEdit.getText().toString()
						.replace(UM, ""));
			} catch (Exception e) {
				Toast.makeText(this,
						"El dato del precio de descuento es incorrecto",
						Toast.LENGTH_SHORT).show();
				return null;
			}
			producto.setOferta();
			producto.setPrecioOferta(descuento);
		}
		return producto;
	}

	/*
	 * A partir de un producto rellena el formulario
	 */
	public void Producto2Formulario(Producto producto) {
		// ID
		ID = producto.getId();

		// Nombre
		EditText nombreEdit = (EditText) findViewById(R.id.AIMPNameEdit);
		nombreEdit.setText(producto.getNombre() != null ? producto.getNombre()
				: "");

		// Descripcion
		EditText descripcionEdit = (EditText) findViewById(R.id.AIMPDescriptionEdit);
		descripcionEdit.setText(producto.getDescripcion() != null ? producto
				.getDescripcion() : "");

		// Marca
		selectBrand(producto.getMarca().getNombre());

		// Tipo
		RadioButton smartphoneEdit = (RadioButton) findViewById(R.id.AIMPSmartphone);
		RadioButton tabletEdit = (RadioButton) findViewById(R.id.AIMPTablet);
		int tipo = producto.getTipo();
		switch (tipo) {
		case Producto.TIPO_SMARTPHONE:
			smartphoneEdit.setSelected(true);
			tabletEdit.setSelected(false);
			break;
		case Producto.TIPO_TABLET:
			smartphoneEdit.setSelected(false);
			tabletEdit.setSelected(true);
			break;
		default:
			smartphoneEdit.setSelected(false);
			tabletEdit.setSelected(false);
			break;
		}

		// SO
		Spinner soEdit = (Spinner) findViewById(R.id.AIMPSOSpinner);
		if (soEdit.getCount() < producto.getSistemaOperativo()
				&& soEdit.getCount() >= 0) {
			soEdit.setId(producto.getSistemaOperativo());
		}

		// Precio
		EditText precioEdit = (EditText) findViewById(R.id.AIMPPriceEdit);
		precioEdit.setText(producto.getPrecio() + UM);

		// Pulgadas
		EditText pulgadasEdit = (EditText) findViewById(R.id.AIMPInchesEdit);
		pulgadasEdit.setText(producto.getDimensionPantalla() + PULGADAS);

		// Oferta
		CheckBox ofertaCheck = (CheckBox) findViewById(R.id.AIMPDiscountBox);
		ofertaCheck.setChecked(producto.isOferta());
		EditText descuentoEdit = (EditText) findViewById(R.id.AIMPDiscountNum);
		descuentoEdit.setText(producto.getPrecioOferta() + UM);
	}

	/*
	 * Actualiza spinner de las marcas
	 */
	private void updateSpinner() {
		ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getBrands());
		adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		brands.setAdapter(adp1);

	}

	/*
	 * Obtiene las marcas
	 */
	private List<String> getBrands() {
		marcas = bd.obtenerMarcas();
		List<String> strs = new LinkedList<String>();
		for (Marca m : marcas) {
			strs.add(m.getNombre());
		}
		return strs;
	}

	/*
	 * Obtiene la marca selecionada
	 */
	private Marca getBrandSelected() {
		return marcas.get(getBrandSelectedId());
	}

	/*
	 * Obtiene el string de la marca selecionada
	 */
	private String getBrandSelectedString() {
		return brands.getSelectedItem() == null ? null : brands
				.getSelectedItem().toString();
	}

	/*
	 * Obtiene el id de la marca selecionada
	 */
	private int getBrandSelectedId() {
		return brands.getSelectedItem() == null ? -1 : brands
				.getSelectedItemPosition();
	}

	/*
	 * Seleciona una marca
	 */
	private void selectBrand(String br) {
		for (int i = 0; i < brands.getCount(); i++) {
			if (brands.getItemAtPosition(i).toString().equalsIgnoreCase(br)) {
				brands.setSelection(i);
				return;
			}
		}
	}

	/*
	 * TextWatcher para poner simbolos finales en los EditTexts
	 */
	private class lastSimbol implements TextWatcher {
		private String fin;
		private EditText text;

		public lastSimbol(EditText text, String fin) {
			this.text = text;
			this.fin = fin;
		}

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String v = s.toString().replace(fin, "") + fin;
			text.removeTextChangedListener(this);

			text.setText(v);
			Selection.setSelection(text.getText(), v.length() - 1);

			text.addTextChangedListener(this);

		}

	}

}