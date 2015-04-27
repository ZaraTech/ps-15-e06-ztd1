package com.zaratech.smarket.aplicacion;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;
import com.zaratech.smarket.utiles.EnviarMail;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.graphics.Paint;

public class EnvioPedido extends Activity {

	/*
	 * Variables asociadas a los campos del layout
	 */
	private TextView nombreProducto;
	private TextView marcaProducto;
	private TextView precioProducto;
	private TextView precioOfertaProducto;
	private TextView tipoProducto;
	private TextView sistemaOperativo;
	private EditText idPedidoCliente;
	private ImageView imagenProducto;
	
	/**
	 * Clave que identifica un Producto dentro del campo extras del Intent
	 */
	private final String EXTRA_PRODUCTO = "Producto";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_envio_pedido);

		Producto producto = getIntent().getExtras().
				getParcelable(EXTRA_PRODUCTO);

		nombreProducto = (TextView)findViewById(R.id.envio_producto);
		marcaProducto = (TextView)findViewById(R.id.envio_marca);
		tipoProducto = (TextView)findViewById(R.id.envio_tipo);
		sistemaOperativo = (TextView)findViewById(R.id.envio_sistema_operativo);
		precioProducto = (TextView)findViewById(R.id.envio_precio);
		precioOfertaProducto = (TextView)findViewById(R.id.envio_precio_oferta);
		idPedidoCliente = (EditText)findViewById(R.id.envio_pedido_cliente);
		imagenProducto = (ImageView) findViewById(R.id.envio_imagen);

		Button botonEnviar = (Button) findViewById(R.id.envio_enviar);

		nombreProducto.setText(producto.getNombre());
		marcaProducto.setText(producto.getMarca().getNombre());
		tipoProducto.setText(AdaptadorBD.obtenerTipo(producto.getTipo()));
		sistemaOperativo.setText(AdaptadorBD.obtenerSistemaOperativo(
				producto.getSistemaOperativo()));
		precioProducto.setText(String.format("%.2f %s", producto.getPrecio(),
				getString(R.string.app_ud_monetaria)));

		if (producto.isOferta()) {
			precioProducto.setPaintFlags(precioProducto.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);

			precioOfertaProducto.setText(String.format("%.2f %s",
					producto.getPrecioOferta(),
					getString(R.string.app_ud_monetaria)));
		}

		if(producto.getImagen() != null){
			imagenProducto.setImageBitmap(producto.getImagen());
		}	


		botonEnviar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				// Enviar email
				String idPedido = idPedidoCliente.getText().toString();

				if (!idPedido.isEmpty()) {

					// Ejecuta el envio en 2o plano
					EnviarMail envio = new EnviarMail(EnvioPedido.this);
					envio.execute(idPedido);


				} else {
					Toast.makeText(getBaseContext(), R.string.envio_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
}
