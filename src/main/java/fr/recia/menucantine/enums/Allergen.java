package fr.recia.menucantine.enums;

import fr.recia.menucantine.dto.PlatDTO;
import lombok.Getter;

import java.util.function.Predicate;

public enum Allergen {
    ANHYDRIDE("Anhydride", PlatDTO::isAllerg_anhydride),
    ARACHIDE("Arachide", PlatDTO::isAllerg_arachide),
    CELERI("Céleri", PlatDTO::isAllerg_celeri),
    CRUSTACE("Crustacé", PlatDTO::isAllerg_crustace),
    FRUIT_A_COQUE("Fruit à coque", PlatDTO::isAllerg_fruit_coque),
    GLUTEN("Gluten", PlatDTO::isAllerg_gluten),
    LAIT("Lait", PlatDTO::isAllerg_lait),
    LUPIN("Lupin", PlatDTO::isAllerg_lupin),
    MOLLUSQUE("Mollusque", PlatDTO::isAllerg_mollusque),
    MOUTARDE("Moutarde", PlatDTO::isAllerg_moutarde),
    OEUF("Oeuf", PlatDTO::isAllerg_oeuf),
    POISSON("Poisson", PlatDTO::isAllerg_poisson),
    SESAME("Sésame", PlatDTO::isAllerg_sesame),
    SOJA("Soja", PlatDTO::isAllerg_soja),
    ;

    @Getter
    private final String displayName;
    private final Predicate<PlatDTO> present;
    Allergen(String displayName, Predicate<PlatDTO> present) {
        this.displayName = displayName;
        this.present = present;
    }

    public boolean isPresentOn(PlatDTO platDTO) { return present.test(platDTO); }
}
