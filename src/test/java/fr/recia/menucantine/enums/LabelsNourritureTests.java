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

class LabelsNourritureTests {

    @ParameterizedTest
    @EnumSource(LabelsNourriture.class)
    void isPresentOnDetectsOnlyItsOwnFlag(LabelsNourriture label) {
        PlatDTO platDTO = platDTOWithOnly(label);

        assertTrue(label.isPresentOn(platDTO),
                label + " devrait être détecté quand son propre flag est à true");

        for (LabelsNourriture other : LabelsNourriture.values()) {
            if (other != label) {
                assertFalse(other.isPresentOn(platDTO),
                        other + " ne devrait pas être détecté par le flag de " + label);
            }
        }
    }

    /**
     * Construit un PlatDTO n'ayant que le flag du label donné à true, tous les autres
     * champs restant à leur valeur par défaut.
     */
    private static PlatDTO platDTOWithOnly(LabelsNourriture label) {
        PlatDTO.PlatDTOBuilder builder = PlatDTO.builder();
        switch (label) {
            case BIO -> builder.bio(true);
            case FAIT_MAISON -> builder.faitmaison(true);
            case LOCAL -> builder.local(true);
            case VEGETARIEN -> builder.vegetarien(true);
            case FRUITS_ET_LEGUMES -> builder.agrimer_fl(true);
            case LAIT_ET_PRODUIT_LAITIERS -> builder.agrimer_lait(true);
        }
        return builder.build();
    }

}
