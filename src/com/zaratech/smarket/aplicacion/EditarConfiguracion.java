package com.zaratech.smarket.aplicacion;

import com.zaratech.smarket.R;
import com.zaratech.smarket.utiles.EditorConfiguracion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * Activity que permite modificar la configuración persistente de la aplicación
 * 
 * @author Miguel Trasobares Baselga
 */
public class EditarConfiguracion extends Activity {

	// Campos del layout a rellenar
	private TextView password;
	private View infoBDRemota;
	private RadioGroup BDTipo;
	private EditText nombreBD;
	private EditText usuarioBD;
	private EditText passwordBD;
	private EditText direccionBD;
	private EditText puertoBD;
	private EditText correoCaja;
	private Button botonGuardar;

	// Editor para modificar las preferencias de la aplicación
	private EditorConfiguracion configuracion;
	
	// Mensajes de retroalimentación para el usuario
	private Toast mensajePasswordFallo;
	private Toast mensajePasswordModificar;
	private Toast mensajeGuardar;
	

	/**
	 * Método a ejecutar en la creación de la actividad.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editar_configuracion);
		// Inicializar el editor para obtener y modificar la configuración
		configuracion = new EditorConfiguracion(
				EditarConfiguracion.this.getApplicationContext());
		// Creación de mensajes para el usuario
		mensajePasswordFallo = Toast.makeText(this, R.string.configuracion_mensaje_password_fallo, Toast.LENGTH_LONG);
		mensajePasswordModificar = Toast.makeText(this, R.string.configuracion_mensaje_password_modificar, Toast.LENGTH_LONG);
		mensajeGuardar = Toast.makeText(this, R.string.configuracion_mensaje_guardar, Toast.LENGTH_LONG);
		
		rellenarCampos();
		
		/*
		 * Asociar la pulsación del botón de guardar con el cambio a la
		 * actividad ListaProductos
		 */
		password.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mostrarDialogoCambiarPassword();
			}
		});

		/*
		 * Asociar la pulsación del botón de guardar con el cambio a la
		 * actividad ListaProductos
		 */
		botonGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(EditarConfiguracion.this,
						ListaProductos.class);
				guardarConfiguracion();
				mensajeGuardar.show();
				startActivity(i);
			}
		});

		/*
		 * Muestra u oculta las opciones de la BD remota si su opción está
		 * marcada o no respectivamente
		 */
		BDTipo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.configuracion_BD_tipo_local:
					infoBDRemota.setVisibility(View.GONE);
					break;
				case R.id.configuracion_BD_tipo_remota:
					infoBDRemota.setVisibility(View.VISIBLE);
					break;
				}
			}
		});
	}

	/**
	 * Muestra la configuración actual rellenando los campos del layout
	 */
	private void rellenarCampos() {
		// Localizar los campos del layout
		password = (TextView) findViewById(R.id.configuracion_cambiar_password);
		infoBDRemota = (View) findViewById(R.id.configuracion_BD_remota);
		BDTipo = (RadioGroup) findViewById(R.id.configuracion_BD_tipo);
		nombreBD = (EditText) findViewById(R.id.configuracion_nombre_BD);
		usuarioBD = (EditText) findViewById(R.id.configuracion_usuario_BD);
		passwordBD = (EditText) findViewById(R.id.configuracion_password_BD);
		direccionBD = (EditText) findViewById(R.id.configuracion_direccion_BD);
		puertoBD = (EditText) findViewById(R.id.configuracion_puerto_BD);
		correoCaja = (EditText) findViewById(R.id.configuracion_correo);
		botonGuardar = (Button) findViewById(R.id.configuracion_boton_guardar);
		// Rellenar los campos con la información
		boolean usoBDLocal = configuracion.usoBDLocal();
		if (usoBDLocal) {
			BDTipo.check(R.id.configuracion_BD_tipo_local);
			infoBDRemota.setVisibility(View.GONE);
		} else {
			BDTipo.check(R.id.configuracion_BD_tipo_remota);
		}
		String nombreBDActual = configuracion.obtenerNombreBD();
		if (nombreBDActual != null) {
			nombreBD.setText(nombreBDActual);
		}
		String usuarioBDActual = configuracion.obtenerUsuarioBD();
		if (usuarioBDActual != null) {
			usuarioBD.setText(usuarioBDActual);
		}
		String passwordBDActual = configuracion.obtenerPasswordBD();
		if (passwordBDActual != null) {
			passwordBD.setText(passwordBDActual);
		}
		String direccionBDActual = configuracion.obtenerDireccionBD();
		if (direccionBDActual != null) {
			direccionBD.setText(direccionBDActual);
		}
		int puertoBDActual = configuracion.obtenerPuertoBD();
		if (puertoBDActual != 0) {
			puertoBD.setText(Integer.toString(puertoBDActual));
		}
		String correoCajaActual = configuracion.obtenerCorreoCaja();
		if (correoCajaActual != null) {
			correoCaja.setText(correoCajaActual);
		}
	}

	/**
	 * Guarda la configuración actual de forma persistente
	 */
	private void guardarConfiguracion() {
		// Rellenar los campos con la información
		String nuevoNombreBD, nuevoUsuarioBD, nuevaPasswordBD, nuevaDireccionBD, 
				nuevoCorreoCaja;
		int nuevoPuertoBD, usoBD;
		// Obtener la información de los campos del layout
		nuevoNombreBD = nombreBD.getText().toString();
		nuevoUsuarioBD = usuarioBD.getText().toString();
		nuevaPasswordBD = passwordBD.getText().toString();
		nuevaDireccionBD = direccionBD.getText().toString();
		try {
			nuevoPuertoBD = Integer.parseInt(puertoBD.getText().toString());
		} catch(Exception e) {
			nuevoPuertoBD = 0;
		}
		nuevoCorreoCaja = correoCaja.getText().toString();
		usoBD = BDTipo.getCheckedRadioButtonId();
		// Almacenar la configuración
		configuracion.modificarNombreBD(nuevoNombreBD);
		configuracion.modificarUsuarioBD(nuevoUsuarioBD);
		configuracion.modificarPasswordBD(nuevaPasswordBD);
		configuracion.modificarDireccionBD(nuevaDireccionBD);
		configuracion.modificarPuertoBD(nuevoPuertoBD);
		configuracion.modificarCorreoCaja(nuevoCorreoCaja);
		if (usoBD == R.id.configuracion_BD_tipo_local) {
			configuracion.modificarUsoBDLocal(true);
		} else {
			configuracion.modificarUsoBDLocal(false);
		}
	}
	
	/*
	 * Diálogo para modificar contraseña
	 */
	private void mostrarDialogoCambiarPassword() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
		final View layout = inflater.inflate(R.layout.dialog_modificar_password, (ViewGroup) findViewById(R.id.configuracion_dialog_raiz));
	    final EditText password1 = (EditText) layout.findViewById(R.id.configuracion_dialog_password1);
	    final EditText password2 = (EditText) layout.findViewById(R.id.configuracion_dialog_password2);
		
	    builder.setTitle(getString(R.string.configuracion_cambiar_password));
		builder.setView(layout);
		
		/*
		 * Asociar la pulsación del botón de cancelar con el cierre del dialog
		 */
		builder.setNegativeButton(
				getString(R.string.configuracion_cancelar_password),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		
		builder.setPositiveButton(getString(R.string.configuracion_confirmar), null);
		final AlertDialog dialog = builder.show();
		/*
		 * Asociar la pulsación del botón de aceptar con el cambio de contraseña
		 */
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						String passwordEscrita1, passwordEscrita2;
						passwordEscrita1 = password1.getText().toString();
						passwordEscrita2 = password2.getText().toString();
						if (passwordEscrita1.equals(passwordEscrita2)) {
							// Las contraseñas coinciden
							configuracion.modificarPassword(passwordEscrita1);
							mensajePasswordModificar.show();
							dialog.dismiss();
						} else {
							// Las contraseñas no coinciden
							mensajePasswordFallo.show();
							password2.setText("");
						}
					}
				});
	}
}