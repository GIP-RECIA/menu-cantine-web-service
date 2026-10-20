/**
 * Copyright (C) 2024 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2024 GIP-RECIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.menucantine.enums;

import fr.recia.menucantine.dto.PlatDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllergenTests {

    @ParameterizedTest
    @EnumSource(Allergen.class)
    void isPresentOnDetectsOnlyItsOwnFlag(Allergen allergen) {
        PlatDTO platDTO = platDTOWithOnly(allergen);

        assertTrue(allergen.isPresentOn(platDTO),
                allergen + " devrait être détecté quand son propre flag est à true");

        for (Allergen other : Allergen.values()) {
            if (other != allergen) {
                assertFalse(other.isPresentOn(platDTO),
                        other + " ne devrait pas être détecté par le flag de " + allergen);
            }
        }
    }

    /**
     * Construit un PlatDTO n'ayant que le flag de l'allergène donné à true, tous les autres
     * champs restant à leur valeur par défaut.
     */
    private static PlatDTO platDTOWithOnly(Allergen allergen) {
        PlatDTO.PlatDTOBuilder builder = PlatDTO.builder();
        switch (allergen) {
            case ANHYDRIDE -> builder.allerg_anhydride(true);
            case ARACHIDE -> builder.allerg_arachide(true);
            case CELERI -> builder.allerg_celeri(true);
            case CRUSTACE -> builder.allerg_crustace(true);
            case FRUIT_A_COQUE -> builder.allerg_fruit_coque(true);
            case GLUTEN -> builder.allerg_gluten(true);
            case LAIT -> builder.allerg_lait(true);
            case LUPIN -> builder.allerg_lupin(true);
            case MOLLUSQUE -> builder.allerg_mollusque(true);
            case MOUTARDE -> builder.allerg_moutarde(true);
            case OEUF -> builder.allerg_oeuf(true);
            case POISSON -> builder.allerg_poisson(true);
            case SESAME -> builder.allerg_sesame(true);
            case SOJA -> builder.allerg_soja(true);
        }
        return builder.build();
    }

}
