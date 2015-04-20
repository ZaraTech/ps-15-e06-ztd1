package com.zaratech.smarket.utiles;

import java.util.ArrayList;

import com.zaratech.smarket.componentes.Marca;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.R;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Adaptador que permite gestionar como se muestran los productos (oferta o no),
 * al mismo tiempo que asigna los valores necesarios a los campos correspondientes
 * 
 * @author Juan
 */
public class AdaptadorProductos extends ArrayAdapter<Producto> {

	public AdaptadorProductos(Context context, ArrayList<Producto> productos) {
		super(context, 0, productos);
	}
	
	/**
	 * Especifica que habrá dos tipos distintos de vistas para la lista
	 */
	@Override
	public int getViewTypeCount() {
	    return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Obtener el producto correspondiente
		Producto producto = getItem(position);
		
		
		// NO ESTA EN OFERTA
		if(!producto.isOferta()){
			
			// Establecer layout
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.row_lista_productos, parent, false);
			
			
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
			
			
		// SI ESTA EN OFERTA
		} else {
			
			// Establecer layout
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.row_lista_productos_oferta, parent, false);
			
			
			// Asociar elementos xml a variables
			ImageView imagen = (ImageView) convertView.findViewById(R.id.Imagen);
			
			TextView nombre = (TextView) convertView.findViewById(R.id.Nombre);
			TextView datos = (TextView) convertView.findViewById(R.id.Datos);
			TextView precio = (TextView) convertView.findViewById(R.id.Precio);
			
			TextView precioOferta = (TextView) convertView.findViewById(R.id.Precio_Oferta);
			
			
			// Rellenar elementos xml con valores del producto
			
			AdaptadorBD BD = new AdaptadorBD();
			
			
			imagen.setImageBitmap(producto.getImagen());
			
			Marca marca = BD.obtenerMarca(producto.getMarca());
			String tipo = AdaptadorBD.obtenerTipo(producto.getTipo());
			
			nombre.setText(producto.getNombre());
			datos.setText(tipo + " - " + marca.getNombre());
			
			precio.setText(String.format("%.2f €", producto.getPrecio()));
			precio.setPaintFlags(precio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			
			precioOferta.setText(String.format("%.2f €", producto.getPrecioOferta()));
			
		}
		
		
		
		
		
		return convertView;
	}
}