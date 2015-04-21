package com.zaratech.smarket.aplicacion;

import com.zaratech.smarket.R;
import com.zaratech.smarket.componentes.Producto;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class InfoProducto extends Activity {

	private ImageView imagen;
	private TextView nombreText;
    private TextView descripcionText;
    private TextView marcaText;
    private TextView pantallaText;
    private TextView sistemaOpText;
    private TextView precioText;
    private Button comprarButt;
	
    private Producto p;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_producto);
		
		/* Prueba de recibir Producto */
		p = this.getIntent().getExtras().getParcelable("Producto");
		Toast.makeText(this, p.getNombre(), Toast.LENGTH_LONG).show();
		/* ************************** */
        //setContentView(R.layout.note_edit);
        //setTitle(R.string.edit_note);
		imagen = (ImageView) findViewById(R.id.image);
	    nombreText = (TextView) findViewById(R.id.name_text);
	    descripcionText = (TextView) findViewById(R.id.description_text);
	    marcaText = (TextView) findViewById(R.id.brand_text);
	    pantallaText = (TextView) findViewById(R.id.screen_text);
	    sistemaOpText = (TextView) findViewById(R.id.operative_system_text);
	    precioText = (TextView) findViewById(R.id.price_text);
	    comprarButt = (Button) findViewById(R.id.buy_button);
	    
	    // Rellenado de campos
	    imagen.setImageBitmap(p.getImagen());
	    nombreText.setText (p.getNombre());
	    String DESCRIPCION_PRUEBA = "Este telefono es muy bueno, el mejor del mercado en calidad/precio";
	    //descripcionText.setText (p.getDescripcion());
	    descripcionText.setText (DESCRIPCION_PRUEBA);
	    marcaText.setText (p.getMarca().getNombre());
	    pantallaText.setText (String.format("%.2f %s", p.getDimensionPantalla(), getString(R.string.UINCH)));
	    sistemaOpText.setText (String.format("%d", p.getSistemaOperativo()));
	    precioText.setText (String.format("%.2f %s", p.getPrecio(), getString(R.string.ud_monetaria)));
	    
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
