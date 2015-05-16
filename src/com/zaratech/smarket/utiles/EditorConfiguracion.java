package com.zaratech.smarket.utiles;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase que se utiliza para editar la configuración general de la aplicación
 * 
 * @author Miguel Trasobares Baselga
 */
public class EditorConfiguracion {
	// Objetos para acceder y modificar las preferencias
	private static SharedPreferences configuracion;
	private static SharedPreferences.Editor editor;
	
	// Claves de las preferencias a guardar
	private static String FICHERO_PREFS = "preferencias";
	private static String PASSWORD_KEY = "password";
	private static String DIRECCION_BD_KEY = "direccionBD";
	private static String PUERTO_BD_KEY = "puertoBD";
	private static String NOMBRE_BD_KEY = "nombreBD";
	private static String USUARIO_BD_KEY = "usuarioBD";
	private static String CONTRASENIA_BD_KEY = "passwordBD";
	private static String USO_BD_LOCAL_KEY = "BDlocal";
	private static String CORREO_CAJA_KEY = "correoCaja";
	
	private static int MODO_USO_PRIVADO = 0;
	
	// Contraseña por defecto de la aplicación
	private static String PASSWORD_DEFECTO = "admin";
	
	/**
	 * Constructor que prepara el objeto para interactuar con el fichero de 
	 * configuración
	 */
	public EditorConfiguracion(Context context) {
		configuracion = context.getSharedPreferences(FICHERO_PREFS, 
															MODO_USO_PRIVADO);
		editor = configuracion.edit();
	}
	
	/**
	 * Comprueba si la password introducida es igual a la almacenada de
	 * forma persistente en el dispositivo
	 * 
	 * @param passwordIntroducida	Contraseña a comprobar introducida 
	 * 								por el usuario
	 * @return		Cierto si ambas contraseñas son idénticas o si no hay
	 * 				ninguna almacenada en el dispositivo
	 */
	public boolean comprobarPassword(String passwordIntroducida) {
	    String passwordAlmacenada = configuracion.getString(PASSWORD_KEY, 
	    													PASSWORD_DEFECTO);
	    if (passwordAlmacenada.equals(passwordIntroducida)) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
	
	/**
	 * Modifica la password del usuario administrador
	 * 
	 * @param nuevoPassword		La nueva contraseña del usuario 
	 * 								administrador
	 */
	public void modificarPassword(String nuevoPassword) {
	    editor.putString(PASSWORD_KEY, nuevoPassword);
	    editor.commit();
	}
	
	/**
	 * Devuelve la direccion IP de la base de datos remota
	 * 
	 * @return		La direccion IP de la base de datos remota
	 */
	public String obtenerDireccionBD() {
		String direccion = configuracion.getString(DIRECCION_BD_KEY, null);
		return direccion;
	}
	
	/**
	 * Modifica la dirección de la base de datos remota
	 * 
	 * @param nuevaDireccion	La nueva direccion IP de la base de datos remota
	 */
	public void modificarDireccionBD(String nuevaDireccion) {
		editor.putString(DIRECCION_BD_KEY, nuevaDireccion);
	    editor.commit();
	}
	
	/**
	 * Devuelve el puerto por el que se accede a la base de datos remota
	 * 
	 * @return		El puerto por el que se accede a la base de datos remota
	 */
	public int obtenerPuertoBD() {
		int puerto = configuracion.getInt(PUERTO_BD_KEY, 0);
		return puerto;
	}
	
	/**
	 * Modifica el puerto por el que se accede a la base de datos remota
	 * 
	 * @param nuevoPuerto			El nuevo puerto por el que se accede a 
	 * 								la base de datos remota
	 */
	public void modificarPuertoBD(int nuevoPuerto) {
		editor.putInt(PUERTO_BD_KEY, nuevoPuerto);
	    editor.commit();
	}
	
	/**
	 * Devuelve el nombre de la base de datos remota
	 * 
	 * @return		El nombre de la base de datos remota
	 */
	public String obtenerNombreBD() {
	    String nombre = configuracion.getString(NOMBRE_BD_KEY, null);
	    return nombre;
	}
	
	/**
	 * Modifica el nombre de la base de datos remota
	 * 
	 * @param nuevoNombre			El nuevo nombre de la base de datos remota
	 */
	public void modificarNombreBD(String nuevoNombre) {
		editor.putString(NOMBRE_BD_KEY, nuevoNombre);
	    editor.commit();
	}
	
	/**
	 * Devuelve el usuario que accede a la base de datos remota
	 * 
	 * @return		El usuario que accede a la base de datos remota
	 */
	public String obtenerUsuarioBD() {
	    String usuario = configuracion.getString(USUARIO_BD_KEY, null);
	    return usuario;
	}
	
	/**
	 * Modifica el usuario que accede a la base de datos remota
	 * 
	 * @param nuevoUsuario			El nuevo usuario que accede a la base 
	 * 								de datos remota
	 */
	public void modificarUsuarioBD(String nuevoUsuario) {
		editor.putString(USUARIO_BD_KEY, nuevoUsuario);
	    editor.commit();
	}
	
	/**
	 * Devuelve la contraseña con la que accede el usuario a la base de 
	 * datos remota
	 * 
	 * @return					La contraseña con la que accede el usuario 
	 * 							a la base de datos remota
	 */
	public String obtenerPasswordBD() {
	    String password = configuracion.getString(CONTRASENIA_BD_KEY, null);
	    return password;
	}
	
	/**
	 * Modifica la contraseña con la que accede el usuario a la base de 
	 * datos remota
	 * 
	 * @param nuevoPassword		Nueva contraseña del usuario que accede a
	 * 								la base de datos remota
	 */
	public void modificarPasswordBD(String nuevoPassword) {
		editor.putString(CONTRASENIA_BD_KEY, nuevoPassword);
	    editor.commit();
	}
	
	/**
	 * Devuelve cierto si y solo si se está usando la base de datos local.
	 * Devuelve falso si se está usando una base de datos remota
	 * 
	 * @return				Cierto si y solo si se está usando la base de 
	 * 						datos local, falso en otro caso
	 */
	public boolean usoBDLocal() {
		boolean usoBDLocal = configuracion.getBoolean(USO_BD_LOCAL_KEY, true);
	    return usoBDLocal;
	}
	
	/**
	 * Alterna el uso entre la base de datos local y una remota
	 * 
	 * @param usoLocal			Cierto si y solo si el nuevo uso de la base de
	 * 							datos es de forma local. Falso en otro caso
	 */
	public void modificarUsoBDLocal(boolean usoLocal) {
		editor.putBoolean(USO_BD_LOCAL_KEY, usoLocal);
	    editor.commit();
	}
	
	/**
	 * Devuelve la dirección de correo de la caja registradora
	 * 
	 * @return			La dirección de correo de la caja registradora
	 */
	public String obtenerCorreoCaja() {
		String correoCaja = configuracion.getString(CORREO_CAJA_KEY, null);
	    return correoCaja;
	}
	
	/**
	 * Modifica la dirección de correo de la caja registradora
	 * 
	 * @return			La nueva dirección de correo de la caja registradora
	 */
	public void modificarCorreoCaja(String correoCaja) {
		editor.putString(CORREO_CAJA_KEY, correoCaja);
	    editor.commit();
	}
}
