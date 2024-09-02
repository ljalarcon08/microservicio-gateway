package com.example.la.gateway.domain;

public class RequestCambioPass {
	
	private String email;
	private String nuevoPassword;
	private String anteriorPassword;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNuevoPassword() {
		return nuevoPassword;
	}
	public void setNuevoPassword(String nuevoPassword) {
		this.nuevoPassword = nuevoPassword;
	}
	public String getAnteriorPassword() {
		return anteriorPassword;
	}
	public void setAnteriorPassword(String anteriorPassword) {
		this.anteriorPassword = anteriorPassword;
	}
	
	
	
}
