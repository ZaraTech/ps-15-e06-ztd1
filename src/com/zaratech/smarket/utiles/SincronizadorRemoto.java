package com.zaratech.smarket.utiles;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.zaratech.smarket.componentes.Conexion;

/**
 * Gestor del sincronizador en segundo plano
 * 
 * @author Juan
 */
public class SincronizadorRemoto {

	private AdaptadorBD bdLocal;
	private Conexion bdRemota;

	private ThreadPoolExecutor tpe1 = null;
	private ThreadPoolExecutor tpe2 = null;

	public SincronizadorRemoto(AdaptadorBD bdLocal, Conexion bdRemota) {

		this.bdLocal = bdLocal;
		this.bdRemota = bdRemota;

		tpe1 = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		tpe2 = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		SincronizadorRemotoAsincrono sra = new SincronizadorRemotoAsincrono(
				bdLocal, bdRemota);

		// Crea la BD remota si no existe
		sra.executeOnExecutor(tpe1, SincronizadorRemotoAsincrono.OP_CREAR);

	}

	/**
	 * Obtiene cambios de BD remota, y los aplica en BD local
	 */
	public void pull() {

		SincronizadorRemotoAsincrono sra = new SincronizadorRemotoAsincrono(
				bdLocal, bdRemota);

		sra.executeOnExecutor(tpe1, SincronizadorRemotoAsincrono.OP_PULL);
	}

	/**
	 * Aplica cambios en BD local a BD remota
	 */
	public void push(int op, int id) {

		SincronizadorRemotoAsincrono sra = new SincronizadorRemotoAsincrono(
				bdLocal, bdRemota);

		sra.executeOnExecutor(tpe1, 
				SincronizadorRemotoAsincrono.OP_PUSH, op, id);
	}

	/**
	 * Despues de un tiempo [segundos], obtiene cambios de BD remota, y los
	 * aplica en BD local
	 */
	public void temporizador(int segundos) {

		SincronizadorRemotoAsincrono sra = new SincronizadorRemotoAsincrono(
				bdLocal, bdRemota);

		tpe2.setKeepAliveTime(segundos * 2, TimeUnit.SECONDS);

		sra.executeOnExecutor(tpe2, 
				SincronizadorRemotoAsincrono.OP_TMP, segundos);
	}

}
