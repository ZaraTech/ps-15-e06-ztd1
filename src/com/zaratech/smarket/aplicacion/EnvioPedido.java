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
	private TextView mNombreProducto;
	private TextView mMarcaProducto;
	private TextView mPrecioProducto;
	private TextView mPrecioOfertaProducto;
	private TextView mTipoProducto;
	private TextView mSistemaOperativo;
	private EditText mIdPedidoCliente;
	private ImageView mImagenProducto;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_envio_pedido);

		Producto producto = getIntent().getExtras().
				getParcelable(getString(R.id.app_producto));

		mNombreProducto = (TextView)findViewById(R.id.app_producto);
		mMarcaProducto = (TextView)findViewById(R.id.app_marca);
		mTipoProducto = (TextView)findViewById(R.id.app_tipo);
		mSistemaOperativo = (TextView)findViewById(R.id.app_sistema_operativo);
		mPrecioProducto = (TextView)findViewById(R.id.app_precio);
		mPrecioOfertaProducto = (TextView)findViewById(R.id.app_precio_oferta);
		mIdPedidoCliente = (EditText)findViewById(R.id.envio_pedido_cliente);
		mImagenProducto = (ImageView) findViewById(R.id.app_imagen);

		Button botonEnviar = (Button) findViewById(R.id.envio_enviar);

		mNombreProducto.setText(producto.getNombre());
		mMarcaProducto.setText(producto.getMarca().getNombre());
		mTipoProducto.setText(AdaptadorBD.obtenerTipo(producto.getTipo()));
		mSistemaOperativo.setText(AdaptadorBD.obtenerSistemaOperativo(
				producto.getSistemaOperativo()));
		mPrecioProducto.setText(String.format("%.2f %s", producto.getPrecio(),
				getString(R.string.app_ud_monetaria)));

		if (producto.isOferta()) {
			mPrecioProducto.setPaintFlags(mPrecioProducto.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);

			mPrecioOfertaProducto.setText(String.format("%.2f %s",
					producto.getPrecioOferta(),
					getString(R.string.app_ud_monetaria)));
		}

		if(producto.getImagen() != null){
			mImagenProducto.setImageBitmap(producto.getImagen());
		}	


		botonEnviar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				// Enviar email
				String idPedido = mIdPedidoCliente.getText().toString();

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
