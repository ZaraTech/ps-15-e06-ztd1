package com.zaratech.smarket.utiles;

import java.util.List;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Listado;
import com.zaratech.smarket.componentes.Orden;
import com.zaratech.smarket.componentes.Producto;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CargadorAsincrono extends AsyncTask<Void, Void, List<Producto>>{
	
	/**
	 * Texto que muestra el estado de carga del listado
	 */
	private ProgressDialog estado;
	
	private Context context;
	private AdaptadorBD bd;
	private List<Producto> productos;
	private Orden ultimoOrden;

	public CargadorAsincrono(Context context, AdaptadorBD bd) {
		this.context = context;
		this.bd = bd;
		this.ultimoOrden = null;
	}
	
	public CargadorAsincrono(Context context, AdaptadorBD bd, Orden ultimoOrden) {
		this.context = context;
		this.bd = bd;
		this.ultimoOrden = ultimoOrden;
	}


	@Override
	protected void onPreExecute() {
		
		// Crea el cuadro de texto que mostrara el estado durante el envio
		estado = new ProgressDialog(context);
		estado.setMessage(context.getString(R.string.lista_cargando));
		estado.setIndeterminate(false);
		estado.setCancelable(false);
		estado.show();
		
		super.onPreExecute();
	}



	@Override
	protected List<Producto> doInBackground(Void... params) {

		if(ultimoOrden != null){
			productos = bd.OrdenarProducto(ultimoOrden);
		}
		else{
			productos = bd.obtenerProductos();
		}
		
		return null;
	}
	
	
	@Override
	protected void onPostExecute(List<Producto> result) {
		
		estado.dismiss();
		
		((Listado) context).cargarListadoAsincrono(productos);
		
		super.onPostExecute(result);
	}

}
