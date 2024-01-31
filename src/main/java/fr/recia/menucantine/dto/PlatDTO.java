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
package fr.recia.menucantine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class PlatDTO implements Serializable {

    private static final long serialVersionUID = 655293L;

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
    @JsonProperty("labels")
    private List<LabelDTO> labels;

    public PlatDTO(){}

}
