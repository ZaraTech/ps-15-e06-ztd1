package com.zaratech.smarket.aplicacion;

import com.zaratech.smarket.R;
import com.zaratech.smarket.utiles.EditorConfiguracion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * Activity que permite modificar la configuración persistente de la aplicación
 * 
 * @author Miguel Trasobares Baselga
 */
public class EditarConfiguracion extends Activity {

	// Campos del layout a rellenar
	private TextView contrasenia;
	private View infoBDRemota;
	private RadioGroup BDTipo;
	private EditText usuarioBD;
	private EditText contraseniaBD;
	private EditText direccionBD;
	private EditText puertoBD;
	private EditText correoCaja;
	private Button botonGuardar;

	private EditorConfiguracion configuracion;

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

		rellenarCampos();

		/*
		 * Asociar la pulsación del botón de guardar con el cambio a la
		 * actividad ListaProductos
		 */
		botonGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(EditarConfiguracion.this,
						ListaProductos.class);
				guardarConfiguracion();
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
		infoBDRemota = (View) findViewById(R.id.configuracion_BD_remota);
		BDTipo = (RadioGroup) findViewById(R.id.configuracion_BD_tipo);
		usuarioBD = (EditText) findViewById(R.id.configuracion_usuario_BD);
		contraseniaBD = (EditText) findViewById(R.id.configuracion_contrasenia_BD);
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
		String usuarioBDActual = configuracion.obtenerUsuarioBD();
		if (usuarioBDActual != null) {
			usuarioBD.setText(usuarioBDActual);
		}
		String contraseniaBDActual = configuracion.obtenerContraseniaBD();
		if (contraseniaBDActual != null) {
			contraseniaBD.setText(contraseniaBDActual);
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
		String nuevoUsuarioBD, nuevaContraseniaBD, nuevaDireccionBD, nuevoCorreoCaja;
		int nuevoPuertoBD, usoBD;
		// Obtener la información de los campos del layout
		nuevoUsuarioBD = usuarioBD.getText().toString();
		nuevaContraseniaBD = contraseniaBD.getText().toString();
		nuevaDireccionBD = direccionBD.getText().toString();
		try {
			nuevoPuertoBD = Integer.parseInt(puertoBD.getText().toString());
		} catch(Exception e) {
			nuevoPuertoBD = 0;
		}
		nuevoCorreoCaja = correoCaja.getText().toString();
		usoBD = BDTipo.getCheckedRadioButtonId();
		// Almacenar la configuración
		configuracion.modificarUsuarioBD(nuevoUsuarioBD);
		configuracion.modificarContraseniaBD(nuevaContraseniaBD);
		configuracion.modificarDireccionBD(nuevaDireccionBD);
		configuracion.modificarPuertoBD(nuevoPuertoBD);
		configuracion.modificarCorreoCaja(nuevoCorreoCaja);
		if (usoBD == R.id.configuracion_BD_tipo_local) {
			configuracion.modificarUsoBDLocal(true);
		} else {
			configuracion.modificarUsoBDLocal(false);
		}
	}
}
