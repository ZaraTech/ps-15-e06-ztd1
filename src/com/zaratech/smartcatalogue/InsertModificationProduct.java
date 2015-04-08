package com.zaratech.smartcatalogue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class InsertModificationProduct extends Activity {

	private final int RESULT_LOAD_IMAGE = 1;
	private final int RESULT_IMAGE_CAPTURE = 2;
	private final String UM = "€";
	private final String PULGADAS = "'";
	private List<String> brs = new LinkedList<String>(Arrays.asList("LG",
			"Sony"));
	private Spinner brands;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_modification_product);

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
				String s = null;
				if ((s = getBrandSelected()) != null) {
					removeBrands(s);
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
				finish();
			}
		});

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
							addBrand(s);
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

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView image = (ImageView) findViewById(R.id.AIMPProductImage);
			Bitmap bitmap=BitmapFactory.decodeFile(picturePath);
			bitmap=Bitmap.createScaledBitmap(bitmap, 50, 50, true);
			image.setImageBitmap(bitmap);
		} else if (requestCode == RESULT_IMAGE_CAPTURE
				&& resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			ImageView image = (ImageView) findViewById(R.id.AIMPProductImage);
			image.setImageBitmap(imageBitmap);
		}
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
		return brs;
	}

	/*
	 * Obtiene la marca selecionada
	 */
	private String getBrandSelected() {
		return brands.getSelectedItem() == null ? null : brands
				.getSelectedItem().toString();
	}

	/*
	 * Seleciona una marca
	 */
	private void selectBrand(String br) {

		for (int i = 0; i < brands.getCount(); i++) {
			if (brands.getItemAtPosition(i).toString() == br) {
				brands.setId(i);
				return;
			}
		}
	}

	/*
	 * Añade una nueva marca
	 */
	private void addBrand(String br) {
		brs.add(br);
	}

	/*
	 * Elimina una marca
	 */
	private void removeBrands(String br) {
		brs.remove(br);
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
