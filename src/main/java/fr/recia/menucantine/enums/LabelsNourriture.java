package fr.recia.menucantine.enums;

import fr.recia.menucantine.dto.PlatDTO;
import lombok.Getter;

import java.util.function.Predicate;

public enum LabelsNourriture {

    BIO("BIO", PlatDTO::isBio),
    FAIT_MAISON("Fait Maison", PlatDTO::isFaitmaison),
    LOCAL("Local", PlatDTO::isLocal),
    VEGETARIEN("Végetarien", PlatDTO::isVegetarien),
    FRUITS_ET_LEGUMES("Fruits et légumes à l’école", PlatDTO::isAgrimer_fl),
    LAIT_ET_PRODUIT_LAITIERS("Lait et produits laitiers à l’école", PlatDTO::isAgrimer_lait),
    ;

    @Getter
    private final String displayName;
    private final Predicate<PlatDTO> present;
    LabelsNourriture(String displayName, Predicate<PlatDTO> present) {
        this.displayName = displayName;
        this.present = present;
    }

    public boolean isPresentOn(PlatDTO platDTO) { return present.test(platDTO); }
}
