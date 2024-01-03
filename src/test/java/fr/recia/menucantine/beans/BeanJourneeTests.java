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
import fr.recia.menucantine.adoria.beans.SousMenu;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "unit" })
public class BeanJourneeTests {

    private Service service1;
    private Service service2;
    private Service service3;

    @Before
    public void setup(){
        Plat plat1 = new Plat("plat1", "entree");
        SousMenu entree = new SousMenu(Collections.singletonList(plat1), 1);
        Plat plat2 = new Plat("plat2","plat");
        Plat plat21 = new Plat("plat21","plat");
        SousMenu plat = new SousMenu(Arrays.asList(plat2, plat21), 2);
        Plat plat3 = new Plat("plat3", "accompagnement");
        SousMenu accompagnement = new SousMenu(Collections.singletonList(plat3), 3);
        Plat plat4 = new Plat("plat4", "fromage");
        Plat plat41 = new Plat("plat41", "fromage");
        Plat plat42 = new Plat("plat42", "fromage");
        SousMenu fromage = new SousMenu(Arrays.asList(plat4, plat41, plat42), 4);
        Plat plat5 = new Plat("plat5", "dessert");
        SousMenu dessert = new SousMenu(Collections.singletonList(plat5), 5);
        Plat plat6 = new Plat("plat6", "autre");
        SousMenu autre = new SousMenu(Collections.singletonList(plat6), 6);
        service1 = new Service("Déjeuner", 2, false);
        service1.setMenu(Arrays.asList(entree, plat, accompagnement, fromage, dessert, autre));
        Plat plat1s2 = new Plat("plat1s2", "entree");
        SousMenu entrees2 = new SousMenu(Collections.singletonList(plat1s2), 1);
        Plat plat2s2 = new Plat("plat2s2","plat");
        Plat plat21s2 = new Plat("plat21s2","plat");
        SousMenu plats2 = new SousMenu(Arrays.asList(plat2s2, plat21s2), 2);
        Plat plat3s2 = new Plat("plat3s2", "accompagnement");
        SousMenu accompagnements2 = new SousMenu(Collections.singletonList(plat3s2), 3);
        Plat plat4s2 = new Plat("plat4s2", "fromage");
        Plat plat41s2 = new Plat("plat41s2", "fromage");
        SousMenu fromages2 = new SousMenu(Arrays.asList(plat4s2, plat41s2), 4);
        Plat plat5s2 = new Plat("plat5s2", "dessert");
        Plat plat51s2 = new Plat("plat51s2", "dessert");
        SousMenu desserts2 = new SousMenu(Arrays.asList(plat5s2, plat51s2), 5);
        Plat plat6s2 = new Plat("plat6s2", "autre");
        SousMenu autres2 = new SousMenu(Collections.singletonList(plat6s2), 6);
        service2 = new Service("Diner", 3, false);
        service2.setMenu(Arrays.asList(entrees2, plats2, accompagnements2, fromages2, desserts2, autres2));
        service3 = new Service("Petit déjeuner", 1, true);
    }

    @Test
    public void testCleanJournee(){
        Journee journee = new Journee();
        journee.setDestinations(new ArrayList<>(Arrays.asList(service1, service2, service3)));
        journee.clean();
        assertEquals("La journée doit contenir 3 services", 3, journee.getDestinations().size());
        assertEquals("Le premier service doit être celui avec le plus petit rank", 1, journee.getDestinations().get(0).getRank(), 0);
        assertEquals("Le dernier service doit être celui avec le plus grand rank", 3, journee.getDestinations().get(2).getRank(), 0);
        assertEquals("On doit avoir un serviceChoixNbPlats pour le Déjeuner", journee.getServiceChoixNbPlats().get("Déjeuner").size(), 6);
        assertEquals("On doit avoir un serviceChoixNbPlats pour le Diner", journee.getServiceChoixNbPlats().get("Diner").size(), 6);
        assertEquals("On ne doit avoir que 2 services dans serviceChoixNbPlats", journee.getServiceChoixNbPlats().keySet().size(), 2);
    }

}
