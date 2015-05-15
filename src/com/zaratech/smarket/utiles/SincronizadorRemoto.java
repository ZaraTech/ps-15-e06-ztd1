package com.zaratech.smarket.utiles;

import com.zaratech.smarket.componentes.Conexion;

/**
 * Gestor del sincronizador en segundo plano
 * 
 * @author Juan
 */
public class SincronizadorRemoto {

	private SincronizadorRemotoAsincrono sra = null;

	
	
	public SincronizadorRemoto(AdaptadorBD bdLocal, Conexion bdRemota) {
		
		this.sra = new SincronizadorRemotoAsincrono(bdLocal, bdRemota);

		// Crea la BD remota si no existe
		sra.execute(SincronizadorRemotoAsincrono.OP_CREAR);
		
				
	}


	public void sincronizar(){

		sra.execute(SincronizadorRemotoAsincrono.OP_SINCRONIZAR);
	}

}
