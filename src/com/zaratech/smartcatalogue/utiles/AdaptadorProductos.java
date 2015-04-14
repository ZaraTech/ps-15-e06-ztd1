package com.zaratech.smartcatalogue.utiles;

import java.util.ArrayList;

import com.zaratech.smartcatalogue.R;
import com.zaratech.smartcatalogue.componentes.Marca;
import com.zaratech.smartcatalogue.componentes.Producto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 *
 */
public class AdaptadorProductos extends ArrayAdapter<Producto> {

	public AdaptadorProductos(Context context, ArrayList<Producto> productos) {
		super(context, 0, productos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Obtener el producto correspondiente
		Producto producto = getItem(position);
		
		// Obtener view (esquema xml) si no lo tiene ya
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.row_lista_productos, parent, false);
		}
		
		// Asociar elementos xml a variables
		ImageView imagen = (ImageView) convertView.findViewById(R.id.Imagen);
		
		TextView nombre = (TextView) convertView.findViewById(R.id.Nombre);
		TextView datos = (TextView) convertView.findViewById(R.id.Datos);
		TextView precio = (TextView) convertView.findViewById(R.id.Precio);
		
		
		// Rellenar elementos xml con valores del producto
		
		AdaptadorBD BD = new AdaptadorBD();
		
		
		imagen.setImageBitmap(producto.getImagen());
		
		Marca marca = BD.obtenerMarca(producto.getMarca());
		String tipo = AdaptadorBD.obtenerTipo(producto.getTipo());
		
		nombre.setText(producto.getNombre());
		datos.setText(tipo + " - " + marca.getNombre());
		precio.setText(String.format("%.2f €", producto.getPrecio()));
		
		
		return convertView;
	}
}