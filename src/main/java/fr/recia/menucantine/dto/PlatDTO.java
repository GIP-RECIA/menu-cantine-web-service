package fr.recia.menucantine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlatDTO {

    private String nom;
    private int id;
    private String type;
    private String designationMenu;
    private int ordre;
    private boolean bio;
    private boolean local;
    private boolean faitmaison;
    private boolean vegetarien;
    private boolean allerg_gluten;
    private boolean allerg_crustace;
    private boolean allerg_oeuf;
    private boolean allerg_poisson;
    private boolean allerg_soja;
    private boolean allerg_lait;
    private boolean allerg_fruit_coque;
    private boolean allerg_celeri;
    private boolean allerg_moutarde;
    private boolean allerg_sesame;
    private boolean allerg_anhydride;
    private boolean allerg_lupin;
    private boolean allerg_mollusque;
    private boolean allerg_arachide;
    private int quantiteprevue;
    @JsonProperty("Conseille")
    private boolean conseille;
    @JsonProperty("aNoter")
    private boolean a_noter;

    public PlatDTO(){}

}
