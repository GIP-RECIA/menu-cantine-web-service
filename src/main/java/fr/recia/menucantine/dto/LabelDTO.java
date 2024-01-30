package fr.recia.menucantine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LabelDTO implements Serializable {

    private String code;
    private String libelle;

    public LabelDTO(){}

}
