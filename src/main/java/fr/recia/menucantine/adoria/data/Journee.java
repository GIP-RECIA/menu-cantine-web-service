package fr.recia.menucantine.adoria.data;

import java.util.List;

import lombok.Data;

@Data
public class Journee {
	String date;
	List<Service> destinations;
}
