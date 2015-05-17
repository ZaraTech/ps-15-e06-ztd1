package com.zaratech.smarket.aplicacion;

import com.zaratech.smarket.R;
import com.zaratech.smarket.utiles.EditorConfiguracion;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity que permite iniciar una sesión de administrador
 * 
 * @author Miguel Trasobares Baselga
 */
public class IniciarSesion extends Activity {
	
	// Constates resultado de la actividad
	public static int INICIAR_SESION_NO_OK = 0;
	public static int INICIAR_SESION_OK = 1;

	// Campos del layout a rellenar
	private EditText passwordEdit;
	private String passwordIntroducida;
	private Button iniciarSesionButt;
	
	// Mensajes de retroalimentación para el usuario
	private Toast mensajeFallo;
	private Toast mensajeIniciar;
	
	/**
	 * Método a ejecutar en la creación de la actividad
	 */
	@Override
	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio_sesion);

		passwordEdit = (EditText) findViewById(R.id.sesion_password);
		iniciarSesionButt = (Button) findViewById(R.id.sesion_boton_iniciar);
		// Creación de mensajes para el usuario
		mensajeIniciar= Toast.makeText(this, R.string.sesion_mensaje_iniciar, 
										Toast.LENGTH_LONG);
		mensajeFallo = Toast.makeText(this, R.string.sesion_mensaje_fallo, 
										Toast.LENGTH_LONG);
		/*
		 * Asociar la pulsación del botón de inicio de sesión con la vuelta al
		 * listado de productos
		 */
		iniciarSesionButt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				// Obtener contraseña introducida
				passwordIntroducida = passwordEdit.getText().toString();
				// Comprobar la contraseña introducida
				EditorConfiguracion configuracion = new EditorConfiguracion
								(IniciarSesion.this.getApplicationContext());
				
				if (configuracion.comprobarPassword(passwordIntroducida)) {
					setResult(INICIAR_SESION_OK);
					mensajeIniciar.show();
					finish();
				} else {
					mensajeFallo.show();
				}
			}
		});
	}
}
