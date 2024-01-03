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

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SousMenu implements Serializable, Cloneable {

    private static final long serialVersionUID = 6998445821377161534L;

    @NonNull
    List<Plat> choix;
    @NonNull
    Integer rank;
    Integer nbPlats;
    Boolean typeVide = false;

    public void addChoix(Plat choix){
        this.nbPlats += 1;
        this.choix.add(choix);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SousMenu clone = (SousMenu) super.clone();
        if (choix != null) {
            clone.choix = new ArrayList<Plat>(choix.size());
            for (Plat plat :choix) {
                clone.choix.add((Plat) plat.clone());
            }
        }
        return clone;
    }

}
