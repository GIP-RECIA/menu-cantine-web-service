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
