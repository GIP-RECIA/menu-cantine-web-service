package fr.recia.menucantine.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServiceDTO {

    private int error;
    private int nbObjet;
    private List<PlatDTO> contenu;

    public ServiceDTO(){}

}
