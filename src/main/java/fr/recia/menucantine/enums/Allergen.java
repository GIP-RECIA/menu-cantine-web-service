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
