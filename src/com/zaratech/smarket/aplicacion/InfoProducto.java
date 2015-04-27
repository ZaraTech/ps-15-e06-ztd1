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
	private TextView tipo;
	private ImageView imagen;
	private TextView nombre;
	private TextView descripcion;
	private TextView marca;
	private TextView pantalla;
	private TextView sistemaOp;
	private TextView precio;
	private TextView precioOferta;
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
		tipo = (TextView) findViewById(R.id.info_tipo);
		imagen = (ImageView) findViewById(R.id.info_imagen);
		nombre = (TextView) findViewById(R.id.info_nombre);
		descripcion = (TextView) findViewById(R.id.info_descripcion);
		marca = (TextView) findViewById(R.id.info_marca);
		pantalla = (TextView) findViewById(R.id.info_pantalla);
		sistemaOp = (TextView) findViewById(R.id.info_sistema_operativo);
		precio = (TextView) findViewById(R.id.info_precio);
		precioOferta = (TextView) findViewById(R.id.info_precio_oferta);
		comprarButt = (Button) findViewById(R.id.info_boton_comprar);

		// Rellenado de campos del layout
		tipo.setText(AdaptadorBD.obtenerTipo(p.getTipo()));
		imagen.setImageBitmap(p.getImagen());
		nombre.setText(p.getNombre());
		descripcion.setText(p.getDescripcion());
		marca.setText(p.getMarca().getNombre());
		pantalla.setText(String.format("%.2f %s", p.getDimensionPantalla(),
										getString(R.string.app_ud_pantalla)));
		sistemaOp.setText(AdaptadorBD.obtenerSistemaOperativo(p
													.getSistemaOperativo()));
		precio.setText(String.format("%.2f %s", p.getPrecio(),
										getString(R.string.app_ud_monetaria)));

		// Mostrar el precio de oferta si el producto está de oferta
		if (p.isOferta()) {
			// Mostrar el precio anterior tachado
			precio.setPaintFlags(precio.getPaintFlags()
										| Paint.STRIKE_THRU_TEXT_FLAG);
			// Mostrar el precio anterior desplazado en el centro de pantalla
			precio.setLayoutParams(new LinearLayout.LayoutParams(
											LayoutParams.MATCH_PARENT,
											LayoutParams.WRAP_CONTENT, 0.8f));
			precioOferta.setText(String.format("%.2f %s",
					p.getPrecioOferta(), getString(R.string.app_ud_monetaria)));
			// Mostrar el precio de oferta a la dereacha de la pantalla
			precioOferta.setLayoutParams(new LinearLayout.LayoutParams(
												LayoutParams.MATCH_PARENT, 
												LayoutParams.WRAP_CONTENT, 1f));
		}
	}
}
