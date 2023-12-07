/**
 * Copyright (C) 2023 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2023 Nathan Cailbourdin <nathan.cailbourdin@recia.fr>
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

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum EnumTypeSousMenu {
    ENTREE("entree","Entrée", 1),
    PLAT("plat","Plat", 2),
    ACCOMPAGNEMENT("accompagnement","Accompagnement", 3),
    FROMAGE("fromage","Fromage", 4),
    DESSERT("dessert","Dessert", 5),
    AUTRE("autre","Autre", 6);

    private final String nomServiceAPI;
    private final String nomServiceFinal;
    private final int rank;

    private EnumTypeSousMenu(String nomServiceAPI, String nomServiceFinal, int rank) {
        this.nomServiceAPI = nomServiceAPI;
        this.nomServiceFinal = nomServiceFinal;
        this.rank = rank;
    }

    private static final Map<String, EnumTypeSousMenu> BY_NAME = new HashMap<>();

    static {
        for (EnumTypeSousMenu e: values()) {
            BY_NAME.put(e.nomServiceAPI, e);
        }
    }

    public static EnumTypeSousMenu sousMenuFromName(String name) {
        return BY_NAME.get(name);
    }
    public static int sousMenuRankFromName(String name) {
        return BY_NAME.get(name).rank;
    }

}
