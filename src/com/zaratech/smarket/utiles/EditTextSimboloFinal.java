package com.zaratech.smarket.utiles;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * TextWatcher para poner simbolos finales en los EditTexts
 * 
 * @auhor Cristian Román Morte
 */
public class EditTextSimboloFinal implements TextWatcher {

	// Texto a introducir al final del EditText
	private String fin;

	// EditText a controlar
	private EditText text;

	// Numero maximo de decimales
	private int decimales;

	// Valor maximo
	private float maximo;

	// Valor minimo
	private float minimo;

	// Caracter de coma flotante
	private static final char DECIMAL = '.';

	/**
	 * 
	 * @param text
	 *            EditText que se controlara
	 * @param fin
	 *            texto a introducir al final
	 */
	public EditTextSimboloFinal(EditText text, String fin) {
		this.text = text;
		this.fin = fin;
		this.decimales = 2;
		this.maximo = 9999;
		this.minimo = 0;
	}

	/**
	 * 
	 * @param text
	 *            EditText que se controlara
	 * @param fin
	 *            texto a introducir al final
	 * @param decimales
	 *            numero maximo de decimales
	 */
	public EditTextSimboloFinal(EditText text, String fin, int decimales,
			float maximo, float minimo) {
		this.text = text;
		this.fin = fin;
		this.decimales = decimales;
		this.maximo = maximo;
		this.minimo = minimo;
	}

	/**
	 * FUNCION NO IMPLEMENTADA
	 */
	public @Deprecated void afterTextChanged(Editable s) {
	}

	/**
	 * FUNCION NO IMPLEMENTADA
	 */
	public @Deprecated void beforeTextChanged(CharSequence s, int start,
			int count, int after) {
	}

	/**
	 * Corrige si es necesario el texto del TextView NO USAR MANUALMENTE
	 */
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String v = s.toString().replace(fin, "");
		text.removeTextChangedListener(this);

		//Corrige los decimales
		boolean t=false;
		int n=0;
		String vv="";
		for (int i=0;i<v.length();i++) {
			if(v.charAt(i)==DECIMAL){
				t=true;
			}else if(t){
				n++;
			}
			if(n<=decimales){
				vv+=v.charAt(i);
			}
		}
		v=vv;
		
		float f;
		try {
			f = Float.valueOf(v);
		} catch (Exception e) {
			f = 0;
		}

		// Corrige el numero
		f = f > maximo ? maximo : f;
		f = f < minimo ? minimo : f;
		v = Math.round(f) != f ? f+"" : Integer
				.toString((int) Math.round(f));

		if (!v.contains(DECIMAL + "0") && s.toString().contains(DECIMAL + "0")) {
			v = v + DECIMAL + "0";
		} else if (!v.contains(DECIMAL + "")
				&& s.toString().contains(DECIMAL + "")) {
			v = v + DECIMAL;
		}

		v += fin;

		// Cambia la posición en la que se esta escribiendo
		text.setText(v);
		int pos = 0;
		if (start == s.length()) {
			pos = v.length() - fin.length();
		} else {
			pos = start - (s.length() - v.length()) + count;
		}
		pos = pos < 0 ? 0 : pos;

		Selection.setSelection(text.getText(),
				pos < v.length() ? pos : v.length() - fin.length());

		text.addTextChangedListener(this);
	}

}