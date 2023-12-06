package fr.recia.menucantine.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceDTO implements Serializable {

    private int error;
    private int nbObjet;
    private List<PlatDTO> contenu;

    public ServiceDTO(){}

}
