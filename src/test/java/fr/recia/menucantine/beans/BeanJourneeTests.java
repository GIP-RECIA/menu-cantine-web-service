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

import fr.recia.menucantine.adoria.beans.Journee;
import fr.recia.menucantine.adoria.beans.Plat;
import fr.recia.menucantine.adoria.beans.Service;
import javax.cache.CacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles({ "unit" })
public class BeanJourneeTests implements ServiceFixtures, SousMenuFixtures{

    private Service service1;
    private Service service2;
    private Service service3;

    @MockitoBean
    private CacheManager cacheManager;
    @BeforeEach
    public void setup(){
        service1 = service("Déjeuner", 2, false,
                sousMenu(1, new Plat("plat1", "entree")),
                sousMenu(2, new Plat("plat2", "plat"), new Plat("plat21", "plat")),
                sousMenu(3, new Plat("plat3", "accompagnement")),
                sousMenu(4, new Plat("plat4", "fromage"), new Plat("plat41", "fromage"), new Plat("plat42", "fromage")),
                sousMenu(5, new Plat("plat5", "dessert")),
                sousMenu(6, new Plat("plat6", "autre")));
        service2 = service("Diner", 3, false,
                sousMenu(1, new Plat("plat1s2", "entree")),
                sousMenu(2, new Plat("plat2s2", "plat"), new Plat("plat21s2", "plat")),
                sousMenu(3, new Plat("plat3s2", "accompagnement")),
                sousMenu(4, new Plat("plat4s2", "fromage"), new Plat("plat41s2", "fromage")),
                sousMenu(5, new Plat("plat5s2", "dessert"), new Plat("plat51s2", "dessert")),
                sousMenu(6, new Plat("plat6s2", "autre")));
        service3 = new Service("Petit déjeuner", 1, true);
    }

    @Test
    public void testCleanJournee(){
        Journee journee = new Journee();
        journee.setDestinations(new ArrayList<>(Arrays.asList(service1, service2, service3)));
        journee.clean();
        assertEquals(3, journee.getDestinations().size(), "La journée doit contenir 3 services");
        assertEquals(1, journee.getDestinations().get(0).getRank(), 0, "Le premier service doit être celui avec le plus petit rank");
        assertEquals(3, journee.getDestinations().get(2).getRank(), 0, "Le dernier service doit être celui avec le plus grand rank");
        assertEquals(journee.getServiceChoixNbPlats().get("Déjeuner").size(), 6, "On doit avoir un serviceChoixNbPlats pour le Déjeuner");
        assertEquals(journee.getServiceChoixNbPlats().get("Diner").size(), 6, "On doit avoir un serviceChoixNbPlats pour le Diner");
        assertEquals(journee.getServiceChoixNbPlats().keySet().size(), 2, "On ne doit avoir que 2 services dans serviceChoixNbPlats");
    }

}
