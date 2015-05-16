package com.zaratech.smarket.aplicacion;

import com.zaratech.smarket.R;
import com.zaratech.smarket.utiles.EditorConfiguracion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	private EditText contraseniaEdit;
	private String contraseniaIntroducida;
	private Button iniciarSesionButt;
	private Toast mensajeFallo;
	
	/**
	 * Método a ejecutar en la creación de la actividad
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inicio_sesion);

		contraseniaEdit = (EditText) findViewById(R.id.sesion_contrasenia);
		iniciarSesionButt = (Button) findViewById(R.id.sesion_boton_iniciar);
		mensajeFallo = Toast.makeText(this, R.string.sesion_mensaje_fallo, Toast.LENGTH_LONG);
		/*
		 * Asociar la pulsación del botón de inicio de sesión con la vuelta al
		 * listado de productos
		 */
		iniciarSesionButt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(IniciarSesion.this, ListaProductos.class);
				// Obtener contraseña introducida
				contraseniaIntroducida = contraseniaEdit.getText().toString();
				// Comprobar la contraseña introducida
				EditorConfiguracion configuracion = new EditorConfiguracion(IniciarSesion.this.getApplicationContext());
				
				if (configuracion.comprobarContrasenia(contraseniaIntroducida)) {
					setResult(INICIAR_SESION_OK);
					finish();
				} else {
					mensajeFallo.show();
				}
			}
		});
	}
}
