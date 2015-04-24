package com.zaratech.smarket.aplicacion;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;

/**
 * Activity que muestra la información de un producto
 * 
 * @author Miguel Trasobares Baselga
 */
public class InfoProducto extends Activity {

	// Campos del layout a rellenar
	private TextView tipoText;
	private ImageView imagen;
	private TextView nombreText;
	private TextView descripcionText;
	private TextView marcaText;
	private TextView pantallaText;
	private TextView sistemaOpText;
	private TextView precioText;
	private TextView precioOfertaText;
	private Button comprarButt;

	private Producto p;

	// Clave que identifica un Producto dentro del campo extras del Intent
	private final String EXTRA_PRODUCTO = "Producto";

	/**
	 * Método a ejecutar en la creación de la actividad.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_producto);

		rellenarCampos();

		/*
		 * Asociar la pulsación del botón de compra con el cambio a la actividad
		 * EnvioPedido
		 */
		comprarButt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(InfoProducto.this, EnvioPedido.class);
				i.putExtra("source", "noteEdit");
				// Añade Producto recibido
				i.putExtra(EXTRA_PRODUCTO, p);
				startActivity(i);
			}
		});
	}

	/**
	 * Muestra la información del producto recibido como parámetro por la
	 * actividad rellenando los campos del layout
	 */
	private void rellenarCampos() {
		// Obtener el producto recibido como parámetro
		p = this.getIntent().getExtras().getParcelable(EXTRA_PRODUCTO);

		// Localizar los campos del layout
		tipoText = (TextView) findViewById(R.id.tipo_text);
		imagen = (ImageView) findViewById(R.id.image);
		nombreText = (TextView) findViewById(R.id.name_text);
		descripcionText = (TextView) findViewById(R.id.description_text);
		marcaText = (TextView) findViewById(R.id.brand_text);
		pantallaText = (TextView) findViewById(R.id.screen_text);
		sistemaOpText = (TextView) findViewById(R.id.operative_system_text);
		precioText = (TextView) findViewById(R.id.price_text);
		precioOfertaText = (TextView) findViewById(R.id.price_ofert_text);
		comprarButt = (Button) findViewById(R.id.buy_button);

		// Rellenado de campos del layout
		tipoText.setText(AdaptadorBD.obtenerTipo(p.getTipo()));
		imagen.setImageBitmap(p.getImagen());
		nombreText.setText(p.getNombre());
		descripcionText.setText(p.getDescripcion());
		marcaText.setText(p.getMarca().getNombre());
		pantallaText.setText(String.format("%.2f %s", p.getDimensionPantalla(),
											getString(R.string.ud_pantalla)));
		sistemaOpText.setText(AdaptadorBD.obtenerSistemaOperativo(p
													.getSistemaOperativo()));
		precioText.setText(String.format("%.2f %s", p.getPrecio(),
											getString(R.string.ud_monetaria)));

		// Mostrar el precio de oferta si el producto está de oferta
		if (p.isOferta()) {
			// Mostrar el precio anterior tachado
			precioText.setPaintFlags(precioText.getPaintFlags()
										| Paint.STRIKE_THRU_TEXT_FLAG);
			// Mostrar el precio anterior desplazado en el centro de pantalla
			precioText.setLayoutParams(new LinearLayout.LayoutParams(
											LayoutParams.MATCH_PARENT,
											LayoutParams.WRAP_CONTENT, 0.8f));
			precioOfertaText.setText(String.format("%.2f %s",
						p.getPrecioOferta(), getString(R.string.ud_monetaria)));
			// Mostrar el precio de oferta a la dereacha de la pantalla
			precioOfertaText.setLayoutParams(new LinearLayout.LayoutParams(
												LayoutParams.MATCH_PARENT, 
												LayoutParams.WRAP_CONTENT, 1f));
		}
	}
}
