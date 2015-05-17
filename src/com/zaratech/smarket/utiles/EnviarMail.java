package com.zaratech.smarket.utiles;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.zaratech.smarket.R;
import com.zaratech.smarket.aplicacion.ListaProductos;
import com.zaratech.smarket.componentes.Producto;

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
	
	/**
	 * Clave que identifica un Producto dentro del campo extras del Intent
	 */
	private final String EXTRA_PRODUCTO = "Producto";

	/*
	 * Datos a incluir en el correo
	 */
	private String destinatario = "";
	private final String ASUNTO_MSJ = "[SMARKET] - Pedido ";
	private String texto_msj = "";

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
		
		EditorConfiguracion configuracion = new EditorConfiguracion(enviarMailActivity);
		destinatario = configuracion.obtenerCorreoCaja();

	}

	@Override
	protected void onPreExecute() {

		// Crea el cuadro de texto que mostrara el estado durante el envio
		estado = new ProgressDialog(enviarMailActivity);
		estado.setMessage(enviarMailActivity.getString(R.string.envio_preparar));
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

		AlertDialog.Builder constructor = new AlertDialog.Builder(enviarMailActivity);
		String resultado = (String)result;

		if (resultado.compareTo(ENVIO_OK) == 0) {

			constructor.setTitle(enviarMailActivity
					.getString(R.string.envio_exito));
			
			constructor.setMessage(String.format("\n%s\n%s\n",
					enviarMailActivity.getString(R.string.envio_enviado),
					enviarMailActivity.getString(R.string.envio_pedido_recoger)));
			
			constructor.setNeutralButton(
					enviarMailActivity.getString(R.string.envio_boton_aceptar),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface d, int which) {

							// vuelve al listado
							Intent i = new Intent(enviarMailActivity
									.getBaseContext(), ListaProductos.class);
							enviarMailActivity.startActivity(i);
						}
					});

		} else {

			constructor.setTitle(enviarMailActivity
					.getString(R.string.envio_error));
			
			constructor.setMessage(String.format("\n%s\n",
					enviarMailActivity.getString(R.string.envio_no_enviado)));
			
			constructor.setNeutralButton(
					enviarMailActivity.getString(R.string.envio_boton_aceptar),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface d, int which) {
							// si error no vuelve al listado
						}
					});

		}

		// Muestra el mensaje de alerta
		AlertDialog alerta = constructor.create();
		alerta.show();
	}

	protected String doInBackground(String... idPedidoCliente) {
		try {			
			Session sesion = Session.getInstance(props);
			Transport t = sesion.getTransport("smtp");
			Message message = new MimeMessage(sesion);
			
			Producto producto = enviarMailActivity.getIntent().getExtras().
					getParcelable(EXTRA_PRODUCTO);
			
			String nombreMarca = "Sin Marca";
			if(producto.getMarca() != null && producto.getMarca().getNombre() != null){
				nombreMarca = producto.getMarca().getNombre();
			}
			
			texto_msj = enviarMailActivity.getString(R.string.envio_pedido_realizado) + " "
					+ idPedidoCliente[0] +"<br/><br/>"
					+ "<strong>" + producto.getNombre() + "</strong>" + "<br/>"
					+ nombreMarca + "<br/>"
					+ AdaptadorBD.obtenerTipo(producto.getTipo()) + "<br/>"
					+ AdaptadorBD.obtenerSistemaOperativo(producto.getSistemaOperativo()) + "<br/>"
					+ String.format("%.2f", producto.getPrecio())
					+ enviarMailActivity.getString(R.string.app_ud_monetaria_texto);

			message.setFrom(new InternetAddress(DIRECCION_ORIGEN));
			message.setContent(texto_msj, "text/html");
			message.setRecipients(MimeMessage.RecipientType.TO,
					InternetAddress.parse(destinatario));
			message.setSubject(ASUNTO_MSJ + " \"" + idPedidoCliente[0] + "\"");

			t.connect(NOMBRE_USUARIO, PASS);
			publishProgress(enviarMailActivity.getString(R.string.envio_enviar));
			t.sendMessage(message, message.getAllRecipients());	

			return ENVIO_OK;

		} catch (Exception e) {
			Log.e("Smarket_EnviarMail_Exception", e.getMessage());

			return ENVIO_KO;
		}
	}

}
