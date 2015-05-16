package com.zaratech.smarket.componentes;

public class Conexion {
	
	private String direccion = "smarket-zt.ddns.net";
	private String puerto = "3306";
	private String usuario = "smarket";
	private String pass = "SmarketZT1506";
	private String bd = "smarket";
	
	public Conexion(){
		
	}

	public Conexion(String direccion, String puerto, String usuario, String pass,
			String bd) {
		
		this.direccion = direccion;
		this.puerto = puerto;
		this.usuario = usuario;
		this.pass = pass;
		this.bd = bd;
	}
	
	public String generarConexionMySQL(){
		return "jdbc:mysql://" + direccion + ":" + puerto + "/" + bd;
	}


	public String getDireccion() {
		return direccion;
	}


	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}


	public String getPuerto() {
		return puerto;
	}


	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}


	public String getUsuario() {
		return usuario;
	}


	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public String getBd() {
		return bd;
	}


	public void setBd(String bd) {
		this.bd = bd;
	}
	
	

}
