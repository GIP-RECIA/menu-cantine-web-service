package fr.recia.menucantine.adoria.beans;

import java.io.Serializable;

import lombok.Data;

@Data
public class Nutrition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -696270684351252698L;
	String name;
	Float value;
	String unit;
}
