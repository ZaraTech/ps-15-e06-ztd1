package com.zaratech.smarket.pruebas;

import com.zaratech.smarket.R;
import com.zaratech.smarket.utiles.AdaptadorBD;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Clase que ejecuta de forma automatica casos de prueba
 * 
 * @author Marta
 *
 */
public class LanzadorPruebas extends Activity {

	private AdaptadorBD bd;
	private static final int TEST_INSERTAR = Menu.FIRST;
	private static final int TEST_BORRAR = Menu.FIRST + 1;
	private static final int TEST_EDITAR = Menu.FIRST + 2;
	private static final int TEST_VOLUMEN = Menu.FIRST + 3;
	private static final int TEST_NO_VOLUMEN = Menu.FIRST + 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		bd = new AdaptadorBD(this);
		bd.open();

		setContentView(R.layout.activity_pruebas);

	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
		bd.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, TEST_INSERTAR, 0, R.string.pruebas_insertar);
		menu.add(0, TEST_BORRAR, 0, R.string.pruebas_borrar);
		menu.add(0, TEST_EDITAR, 0, R.string.pruebas_editar);
		menu.add(0, TEST_VOLUMEN, 0, R.string.pruebas_volumen);
		menu.add(0, TEST_NO_VOLUMEN, 0, R.string.pruebas_deshacer_volumen);

		return result;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		String tag = "Test ";
		boolean exito = false;
		
		if (item.getItemId() == TEST_INSERTAR) {
			
			String prueba = "Insercion de producto";
			
			android.util.Log.d(tag + prueba, "Probando insercion...");
			exito = PruebasProducto.testInsertarProducto(bd, this);

			if (!exito) {

				android.util.Log.d(tag + prueba, "ERROR");
				
				Toast.makeText(this, "Error en la insercion", 
						Toast.LENGTH_SHORT).show();

			} else {

				android.util.Log.d(tag + prueba, "EXITO");
				
				Toast.makeText(this, "Insercion realizada", 
						Toast.LENGTH_SHORT).show();
			}

		} else if (item.getItemId() == TEST_BORRAR) {
			
			String prueba = "Borrado de producto";
			
			android.util.Log.d(tag + prueba, "Probando borrado...");			
			exito = PruebasProducto.testBorrarProducto(bd);
			
			if (!exito) {

				android.util.Log.d(tag + prueba, "ERROR");
				
				Toast.makeText(this, "Error en el borrado del producto de prueba", 
						Toast.LENGTH_SHORT).show();

			} else {

				android.util.Log.d(tag + prueba, "EXITO");
				
				Toast.makeText(this, "Borrado realizado", 
						Toast.LENGTH_SHORT).show();
			}

		} else if (item.getItemId() == TEST_EDITAR) {

			String prueba = "Edicion de producto";
			
			android.util.Log.d(tag + prueba, "Probando edicion...");
			exito = PruebasProducto.testEditarProducto(bd);

			if (!exito) {

				android.util.Log.d(tag + prueba, "ERROR");
				
				Toast.makeText(this, "Error en la modificacion del producto de prueba", 
						Toast.LENGTH_SHORT).show();

			} else {

				android.util.Log.d(tag + prueba, "EXITO");
				
				Toast.makeText(this, "Modificacion realizada", 
						Toast.LENGTH_SHORT).show();
			}
			
		} else if (item.getItemId() == TEST_VOLUMEN) {			

			String prueba = "Volumen";
			android.util.Log.d(tag + prueba, 
					"Creando productos y marcas...");
			
			exito = PruebasSistema.testVolumen(bd, this);
			
			if (!exito){
				
				android.util.Log.d(tag + prueba, "ERROR");
				
				Toast.makeText(this, "Error en la creacion del volumen", 
						Toast.LENGTH_SHORT).show();
				
			} else {
				
				android.util.Log.d(tag + prueba, "EXITO");
				
				Toast.makeText(this, "Prueba de volumen realizada.", 
						Toast.LENGTH_SHORT).show();
				
			}
			
			
		} else if (item.getItemId() == TEST_NO_VOLUMEN) {
		
			String prueba = "Deshacer volumen";
			exito = PruebasSistema.deshacerVolumen(bd);
			
			android.util.Log.d(tag + prueba, "Deshaciendo volumen...");
			if(!exito){
				
				android.util.Log.d(tag + prueba, "ERROR");
				
				Toast.makeText(this, "Error deshaciendo volumen",
						Toast.LENGTH_SHORT).show();
				
			} else {
				
				android.util.Log.d(tag + prueba, "EXITO");
				
				Toast.makeText(this, "Datos de la prueba de volumen eliminados",
						Toast.LENGTH_SHORT).show();			
			}		
		}

		return super.onMenuItemSelected(featureId, item);
	}

}
