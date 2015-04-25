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

		Producto pPrueba = getIntent().getExtras().getParcelable("Producto");

		mNombreProducto = (TextView)findViewById(R.id.Nombre);
		mMarcaProducto = (TextView)findViewById(R.id.Marca);
		mTipoProducto = (TextView)findViewById(R.id.Tipo);
		mSistemaOperativo = (TextView)findViewById(R.id.Sistema_operativo);
		mPrecioProducto = (TextView)findViewById(R.id.Precio);
		mPrecioOfertaProducto = (TextView)findViewById(R.id.Precio_oferta);
		mIdPedidoCliente = (EditText)findViewById(R.id.PedidoCliente);
		mImagenProducto = (ImageView) findViewById(R.id.Imagen);

		Button botonEnviar = (Button) findViewById(R.id.Enviar);

		mNombreProducto.setText(pPrueba.getNombre());
		mMarcaProducto.setText(pPrueba.getMarca().getNombre());
		mTipoProducto.setText(AdaptadorBD.obtenerTipo(pPrueba.getTipo()));
		mSistemaOperativo.setText(AdaptadorBD.obtenerSistemaOperativo(
				pPrueba.getSistemaOperativo()));
		mPrecioProducto.setText(String.format("%.2f %s", pPrueba.getPrecio(),
				getString(R.string.app_ud_monetaria)));

		if (pPrueba.isOferta()) {
			mPrecioProducto.setPaintFlags(mPrecioProducto.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);

			mPrecioOfertaProducto.setText(String.format("%.2f %s",
					pPrueba.getPrecioOferta(),
					getString(R.string.app_ud_monetaria)));
		}

		if(pPrueba.getImagen() != null){
			mImagenProducto.setImageBitmap(pPrueba.getImagen());
		}	


		botonEnviar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				// Enviar email
				String idPedido = mIdPedidoCliente.getText().toString();

				if (!idPedido.isEmpty()) {

					// Ejecuta el envio en 2o plano
					EnviarMail em = new EnviarMail(EnvioPedido.this);
					em.execute(idPedido);


				} else {
					Toast.makeText(getBaseContext(), R.string.pedido_envio_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
}
