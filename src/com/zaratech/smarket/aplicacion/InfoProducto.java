package com.zaratech.smarket.aplicacion;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Producto;
import com.zaratech.smarket.utiles.AdaptadorBD;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;

public class InfoProducto extends Activity {

	private ImageView imagen;
	private TextView nombreText;
    private TextView descripcionText;
    private TextView marcaText;
    private TextView pantallaText;
    private TextView sistemaOpText;
    private TextView precioText;
    private TextView precioOfertaText;
    private Button comprarButt;
	
    private Producto p;
    
	/**
	 * Clave que identifica un Producto dentro del campo extras del Intent
	 */
	private final String EXTRA_PRODUCTO = "Producto";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_producto);
		
		p = this.getIntent().getExtras().getParcelable(EXTRA_PRODUCTO);

        //setContentView(R.layout.note_edit);
        //setTitle(R.string.edit_note);
		imagen = (ImageView) findViewById(R.id.image);
	    nombreText = (TextView) findViewById(R.id.name_text);
	    descripcionText = (TextView) findViewById(R.id.description_text);
	    marcaText = (TextView) findViewById(R.id.brand_text);
	    pantallaText = (TextView) findViewById(R.id.screen_text);
	    sistemaOpText = (TextView) findViewById(R.id.operative_system_text);
	    precioText = (TextView) findViewById(R.id.price_text);
	    precioOfertaText = (TextView) findViewById(R.id.price_ofert_text);
	    comprarButt = (Button) findViewById(R.id.buy_button);
	    
	    // Rellenado de campos
	    imagen.setImageBitmap(p.getImagen());
	    nombreText.setText (p.getNombre());
	    String descripcion_prueba = "Este teléfono es muy bueno, el mejor del mercado en calidad/precio";
	    //descripcionText.setText (p.getDescripcion());
	    descripcionText.setText (descripcion_prueba);
	    marcaText.setText (p.getMarca().getNombre());
	    pantallaText.setText (String.format("%.2f %s", p.getDimensionPantalla(), getString(R.string.UINCH)));
	    sistemaOpText.setText (AdaptadorBD.obtenerSistemaOperativo(p.getSistemaOperativo()));
	    precioText.setText (String.format("%.2f %s", p.getPrecio(), getString(R.string.ud_monetaria)));
	    
	    if (p.isOferta()) {
	    	precioText.setPaintFlags(precioText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	    	precioText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.8f));
	    	precioOfertaText.setText(String.format("%.2f %s", p.getPrecioOferta(), getString(R.string.ud_monetaria)));
	    	precioOfertaText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
	    }
	    
		comprarButt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent i = new Intent(InfoProducto.this, EnvioPedido.class);
            	i.putExtra("source", "noteEdit");
        		
        		// Añade Producto recibido
        		i.putExtra("Producto", p);

        		startActivity(i);
            }
        });
	}
}
