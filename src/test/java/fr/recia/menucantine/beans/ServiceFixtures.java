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
package fr.recia.menucantine.beans;

import fr.recia.menucantine.adoria.beans.Service;
import fr.recia.menucantine.adoria.beans.SousMenu;

import java.util.Arrays;

interface ServiceFixtures {

    /**
     * Construit un Service "rempli" à partir de ses sous-menus. Les services vides
     * (sans sous-menu) continuent d'être créés directement via `new Service(...)`.
     */
    default Service service(String name, int rank, boolean typeVide, SousMenu... sousMenus) {
        Service service = new Service(name, rank, typeVide);
        service.setMenu(Arrays.asList(sousMenus));
        return service;
    }

}
