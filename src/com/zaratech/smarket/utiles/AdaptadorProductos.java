package com.zaratech.smarket.utiles;

import java.util.ArrayList;

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

	/**
	 * Contexto de la aplicacion. Necesario para obtener strings
	 */
	private Context context;


	/**
	 * Constructor del adaptador
	 * @param bd conexion a una base de datos
	 */
	public AdaptadorProductos(Context context, ArrayList<Producto> productos) {
		super(context, 0, productos);

		this.context = context;
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

		// Obtiene el simbolo de la unidad monetaria a usar
		String ud_monetaria = context.getString(R.string.app_ud_monetaria);

		// Obtener el producto correspondiente
		Producto producto = getItem(position);
		
		ImageView imagen;
		TextView nombre;
		TextView datos;
		TextView precio;


		// NO ESTA EN OFERTA
		if(!producto.isOferta()){

			// Establecer layout
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.row_lista_productos, parent, false);

			// Asociar elementos xml a variables
			imagen = (ImageView) convertView.findViewById(R.id.lista_row_imagen);
			nombre = (TextView) convertView.findViewById(R.id.lista_row_nombre);
			datos = (TextView) convertView.findViewById(R.id.lista_row_datos);
			precio = (TextView) convertView.findViewById(R.id.lista_row_precio);

		// SI ESTA EN OFERTA
		} else {

			// Establecer layout
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.row_lista_productos_oferta, parent, false);

			// Asociar elementos xml a variables
			imagen = (ImageView) convertView.findViewById(R.id.lista_row_oferta_imagen);
			nombre = (TextView) convertView.findViewById(R.id.lista_row_oferta_nombre);
			datos = (TextView) convertView.findViewById(R.id.lista_row_oferta_datos);
			precio = (TextView) convertView.findViewById(R.id.lista_row_oferta_precio);
			TextView precioOferta = (TextView) convertView.findViewById(R.id.lista_row_oferta_precio_oferta);

			// Tacha precio antiguo
			precio.setPaintFlags(precio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

			precioOferta.setText(String.format("%.2f %s", producto.getPrecioOferta(), ud_monetaria));

		}

		// Rellenar elementos xml con valores del producto			

		imagen.setImageBitmap(producto.getImagen());
		String tipo = AdaptadorBD.obtenerTipo(producto.getTipo());
		nombre.setText(producto.getNombre());
		
		String marca = producto.getMarca().getNombre();
		if(marca == null || marca.trim().equals("")){
			marca = "Sin Marca";
		}
		
		datos.setText(tipo + "\n" + marca);
		precio.setText(String.format("%.2f %s", producto.getPrecio(), ud_monetaria));

		
		return convertView;
	}
}