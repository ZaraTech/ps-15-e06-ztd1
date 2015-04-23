package com.zaratech.smarket.utiles;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zaratech.smarket.R;
import com.zaratech.smarket.aplicacion.ListaProductos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class EnviarMail extends AsyncTask<String, Object, Object> {

	/**
	 * Texto que muestra el estado del envio en la aplicación
	 */
	private ProgressDialog estado;

	/**
	 * Activity que realiza la peticion de envio.
	 */
	private Activity enviarMailActivity;

	/*
	 * Datos a incluir en el correo
	 */
	private final String DESTINATARIO = "smarket.zaratech@gmail.com";
	private final String ASUNTO_MSJ = "[SMARKET] Orden de pedido - ";
	private final String TEXTO_MSJ = "Texto de la orden de pedido...";

	/*
	 * Datos de la cuenta con la que se enviara el correo
	 */
	private final String NOMBRE_USUARIO = "smarket.zaratech@gmail.com";
	private final String PASS = "SmarketZT1506";
	private final String DIRECCION_ORIGEN = NOMBRE_USUARIO;

	/*
	 * Control del envio
	 */
	private final String ENVIO_OK = "OK";
	private final String ENVIO_KO = "KO";

	/**
	 * Propiedades asociadas al envio de correo electronico
	 */
	private Properties props = new Properties();

	/**
	 * Constructor
	 * @param activity Activity que realiza la peticion de envio.
	 */
	public EnviarMail(Activity activity) {
		enviarMailActivity = activity;

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

	}

	@Override
	protected void onPreExecute() {

		// Crea el cuadro de texto que mostrara el estado durante el envio
		estado = new ProgressDialog(enviarMailActivity);
		estado.setMessage(enviarMailActivity.getString(R.string.pedido_preparar));
		estado.setIndeterminate(false);
		estado.setCancelable(false);
		estado.show();
	}

	@Override
	public void onProgressUpdate(Object... values) {
		estado.setMessage(values[0].toString());

	}

	@Override
	public void onPostExecute(Object result) {
		estado.dismiss();

		AlertDialog.Builder b = new AlertDialog.Builder(enviarMailActivity);
		String resultado = (String)result;

		if (resultado.compareTo(ENVIO_OK) == 0) {

			b.setTitle(enviarMailActivity
					.getString(R.string.pedido_envio_exito));
			b.setMessage(String.format("\n%s\n",
					enviarMailActivity.getString(R.string.pedido_enviado)));
			b.setNeutralButton(
					enviarMailActivity.getString(R.string.boton_aceptar),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface d, int which) {

							Intent i = new Intent(enviarMailActivity
									.getBaseContext(), ListaProductos.class);
							enviarMailActivity.startActivity(i);
						}
					});

		} else {

			b.setTitle(enviarMailActivity
					.getString(R.string.pedido_envio_error));
			b.setMessage(String.format("\n%s\n",
					enviarMailActivity.getString(R.string.pedido_no_enviado)));
			b.setNeutralButton(
					enviarMailActivity.getString(R.string.boton_aceptar),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface d, int which) {
							// si error no vuelve al listado
						}
					});

		}

		AlertDialog a = b.create();
		a.show();
	}

	protected String doInBackground(String... idPedidoCliente) {
		try {			
			Session s = Session.getInstance(props);
			Transport t = s.getTransport("smtp");
			Message message = new MimeMessage(s);

			message.setFrom(new InternetAddress(DIRECCION_ORIGEN));
			message.setContent(TEXTO_MSJ, "text/html");
			message.setRecipients(MimeMessage.RecipientType.TO,
					InternetAddress.parse(DESTINATARIO));
			message.setSubject(ASUNTO_MSJ + " " + idPedidoCliente[0]);

			t.connect(NOMBRE_USUARIO, PASS);
			publishProgress(enviarMailActivity.getString(R.string.pedido_enviar));
			t.sendMessage(message, message.getAllRecipients());	

			Log.i("Smarket_EnviarMail", "Correo enviado!");

			return ENVIO_OK;

		} catch (Exception e) {
			Log.e("Smarket_EnviarMail_Exception", e.getMessage());

			return ENVIO_KO;
		}
	}

}
