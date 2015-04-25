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
	 * Corrige si es necesario el texto del TextView si es necesario
	 * NO USAR MANUALMENTE
	 */
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String v = s.toString().replace(fin, "") + fin;
		text.removeTextChangedListener(this);

		// Cambia la posición en la que se esta escribiendo
		text.setText(v);
		int pos = 0;
		if (start == s.length()) {
			pos = v.length() - fin.length();
		} else {
			pos = start - (s.length() - v.length()) + 1;
		}
		Selection.setSelection(text.getText(),
				pos < v.length() ? pos : v.length() - fin.length());

		text.addTextChangedListener(this);
	}

}