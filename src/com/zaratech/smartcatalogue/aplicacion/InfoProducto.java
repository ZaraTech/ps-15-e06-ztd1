package com.zaratech.smartcatalogue.aplicacion;

import com.zaratech.smartcatalogue.R;
import com.zaratech.smartcatalogue.componentes.Producto;

import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;

public class InfoProducto extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_producto);
		
		/* Prueba de recibir Producto */
		Producto p = this.getIntent().getExtras().getParcelable("Producto");
		Toast.makeText(this, p.getNombre(), Toast.LENGTH_LONG).show();
		/* ************************** */
	}

}
