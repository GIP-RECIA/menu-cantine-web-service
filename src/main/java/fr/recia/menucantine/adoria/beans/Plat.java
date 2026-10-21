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
package fr.recia.menucantine.adoria.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public final class Plat implements Serializable, Cloneable {

    private static final long serialVersionUID = 6660011102902753149L;
    String name = " ";
    String family = " "; // ce sont des blancs insécables
    String subFamily;
    Boolean typeVide = false;

    @JsonInclude(Include.NON_NULL)
    List<String> allergens;

    @JsonInclude(Include.NON_NULL)
    List<String> labels;

    @JsonInclude(Include.NON_NULL)
    List<Labels> labelsInfo;

    Integer familyRank;

    public Plat() {
    }

    public Plat(String name, String family) {
        this.name = name;
        this.family = family;
    }

    /**
     * Méthode statique permettant de créer un plat vide
     * @return Le plat créé
     */
    public static Plat platVide() {
        Plat p = new Plat();
        p.typeVide = true;
        return p;
    }

    boolean isVide() {
        return typeVide;
    }

    /**
     * Nettoyage d'un plat :
     * - Annule les liste des allergens vide.
     * - Remplace les labels par leurs info.
     */
    void clean() {
        if (allergens != null && allergens.isEmpty()) {
            allergens = null;
        }
        if (labels != null) {
            int s = labels.size();
            if (s > 0) {
                labelsInfo = new ArrayList<Labels>(s);
                for (String labelName : labels) {
                    Labels l = Labels.getLabel(labelName);
                    if (l != null) {
                        labelsInfo.add(l);
                    }
                }
                labelsInfo.sort((a, b) -> Integer.compare(a.getOrdre(), b.getOrdre()));
            }
            labels = null;
        }

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Plat clone = (Plat) super.clone();
        if (allergens != null) {
            clone.allergens = new ArrayList<String>(allergens);
        }
        if (labels != null) {
            clone.labels = new ArrayList<String>(labels);
        }
        if (labelsInfo != null) {
            clone.labelsInfo = new ArrayList<Labels>(labelsInfo.size());
            for (Labels labels : labelsInfo) {
                clone.labelsInfo.add((Labels) labels.clone());
            }

        }
        return clone;
    }
}
