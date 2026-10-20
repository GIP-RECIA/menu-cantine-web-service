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
package fr.recia.menucantine.webgerest.mock;

import fr.recia.menucantine.dto.PlatDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import fr.recia.menucantine.webgerest.IAPIClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
@Profile("mock")
public final class MockAPIClient implements IAPIClient {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Services sans école le mercredi après-midi : pas de goûter (3) ni de dîner (4)
    private static final Set<Integer> SERVICES_SANS_ECOLE_MERCREDI = new HashSet<>(Arrays.asList(3, 4));

    private static final List<String> ENTREES = Arrays.asList(
            "Salade de tomates", "Carottes râpées", "Salade verte", "Terrine de campagne", "Melon");
    private static final List<String> PLATS = Arrays.asList(
            "Steak haché", "Poisson pané", "Escalope de poulet", "Sauté de porc", "Omelette");
    private static final List<String> ACCOMPAGNEMENTS = Arrays.asList(
            "Purée de pommes de terre", "Riz", "Haricots verts", "Pâtes", "Petits pois");
    private static final List<String> LAITAGES = Arrays.asList(
            "Yaourt nature", "Fromage blanc", "Camembert", "Emmental");
    private static final List<String> DESSERTS = Arrays.asList(
            "Compote", "Tarte aux pommes", "Gâteau au chocolat", "Salade de fruits");
    private static final List<String> AUTRES = Arrays.asList(
            "Pain", "Beurre", "Confiture");

    private static final Map<String, List<String>> PLATS_PAR_TYPE = new LinkedHashMap<>();
    private static final Map<Integer, List<String>> TYPES_PAR_SERVICE = new HashMap<>();

    static {
        PLATS_PAR_TYPE.put("entree", ENTREES);
        PLATS_PAR_TYPE.put("plat", PLATS);
        PLATS_PAR_TYPE.put("accompagnement", ACCOMPAGNEMENTS);
        PLATS_PAR_TYPE.put("laitage", LAITAGES);
        PLATS_PAR_TYPE.put("dessert", DESSERTS);
        PLATS_PAR_TYPE.put("autre", AUTRES);
    }

    static {
        TYPES_PAR_SERVICE.put(1, Arrays.asList("laitage", "autre"));                                      // petit déjeuner
        TYPES_PAR_SERVICE.put(2, Arrays.asList("entree", "plat", "accompagnement", "laitage", "dessert")); // déjeuner
        TYPES_PAR_SERVICE.put(3, Arrays.asList("laitage", "dessert"));                                     // goûter
        TYPES_PAR_SERVICE.put(4, Arrays.asList("entree", "plat", "laitage", "dessert"));                   // dîner
    }

    private static PlatDTO createPlat(String nom, String type) {
        return PlatDTO.builder().nom(nom).type(type).build();
    }

    @Override
    public ServiceDTO makeAuthenticatedApiCallGetMenu(String uai, String datemenu, int service) {
        ServiceDTO serviceDto = new ServiceDTO();

        LocalDate dateRequete = LocalDate.parse(datemenu, DATE_FORMAT);
        boolean pasDeServiceCeJour = dateRequete.getDayOfWeek() == DayOfWeek.WEDNESDAY
                && SERVICES_SANS_ECOLE_MERCREDI.contains(service);

        List<PlatDTO> menu = new ArrayList<>();
        if (!pasDeServiceCeJour) {
            List<String> typesDuService = TYPES_PAR_SERVICE.getOrDefault(service, Collections.emptyList());
            for (String type : typesDuService) {
                List<String> noms = PLATS_PAR_TYPE.get(type);
                // Un seul plat par catégorie, choisi selon le jour de l'année pour varier d'un jour à l'autre
                // tout en restant stable pour une même date (deux requêtes sur le même jour renvoient le même menu)
                String nom = noms.get(dateRequete.getDayOfYear() % noms.size());
                menu.add(createPlat(nom, type));
            }
        }

        serviceDto.setContenu(menu);
        return serviceDto;
    }
}
