package dev.zabi94.timetracker;

import java.awt.Color;

public enum RegistrationStatus {

	UNREGISTERED(new Color(0x333333), "Non registrato", 0), 
	REQUESTED_WAITING(new Color(0xd61a1f), "Attendo chiamata", 2), 
	REQUESTED_FULFILLED(new Color(0xd6731a), "Da registrare", 2|4), 
	REGISTERED_LOTUS(new Color(0x7fb927), "Registrato su Lotus", 1|2|4), 
	REGISTERED_ECO(new Color(0xa3c1e0), "Registrato su Eco", 1|2|4), 
	WONT_REGISTER(new Color(0x603690), "Considera registrato", 1);

	private Color color;
	private String repr;
	private int registerMap; // 1 = registrato, 2 = richiesta fatta, 4 = chiamata esiste 

	RegistrationStatus(Color color, String name, int map) {
		this.color = color;
		this.repr = name;
		this.registerMap = map;
	}

	public Color getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return repr;
	}
	
	public boolean isRegistered() {
		return (registerMap & 1) != 0;
	}
	
	public boolean isRequested() {
		return (registerMap & 2) != 0;
	}
	
	public boolean isCallAvailable() {
		return (registerMap & 4) != 0;
	}
	
}
