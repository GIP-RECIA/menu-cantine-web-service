package fr.recia.menucantine.adoria.data;

import java.util.List;

import lombok.Data;

@Data
public class Service {
	String name;
	String serviceName;
	
	List<Plat> recipes;
	
	String rank;
}
