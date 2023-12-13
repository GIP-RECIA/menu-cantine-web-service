/**
 * Copyright (C) 2019 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2019 Pierre Legay <pierre.legay@recia.fr>
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
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BeanJourneeTests {

    private Service service1;
    private Service service2;
    private Service service3;

    @Before
    public void setup(){
        service1 = new Service();
        Plat plat1 = new Plat();
        plat1.setName("plat1");
        plat1.setFamily("entree");
        SousMenu entree = new SousMenu(Collections.singletonList(plat1), 1);
        Plat plat2 = new Plat();
        plat1.setName("plat2");
        plat1.setFamily("plat");
        Plat plat21 = new Plat();
        plat1.setName("plat21");
        plat1.setFamily("plat");
        SousMenu plat = new SousMenu(Arrays.asList(plat2, plat21), 2);
        Plat plat3 = new Plat();
        plat1.setName("plat3");
        plat1.setFamily("accompagnement");
        SousMenu accompagnement = new SousMenu(Collections.singletonList(plat3), 3);
        Plat plat4 = new Plat();
        plat1.setName("plat4");
        plat1.setFamily("fromage");
        Plat plat41 = new Plat();
        plat1.setName("plat41");
        plat1.setFamily("fromage");
        Plat plat42 = new Plat();
        plat1.setName("plat42");
        plat1.setFamily("fromage");
        SousMenu fromage = new SousMenu(Arrays.asList(plat4, plat41, plat42), 4);
        Plat plat5 = new Plat();
        plat1.setName("plat5");
        plat1.setFamily("dessert");
        SousMenu dessert = new SousMenu(Collections.singletonList(plat5), 5);
        Plat plat6 = new Plat();
        plat1.setName("plat6");
        plat1.setFamily("autre");
        SousMenu autre = new SousMenu(Collections.singletonList(plat6), 6);
        service1.setMenu(Arrays.asList(entree, plat, accompagnement, fromage, dessert, autre));
        service1.setTypeVide(false);
        service1.setName("Déjeuner");
        service1.setRank(2);
        service2 = new Service();
        Plat plat1s2 = new Plat();
        plat1.setName("plat1");
        plat1.setFamily("entree");
        SousMenu entrees2 = new SousMenu(Collections.singletonList(plat1s2), 1);
        Plat plat2s2 = new Plat();
        plat1.setName("plat2");
        plat1.setFamily("plat");
        Plat plat21s2 = new Plat();
        plat1.setName("plat21");
        plat1.setFamily("plat");
        SousMenu plats2 = new SousMenu(Arrays.asList(plat2s2, plat21s2), 2);
        Plat plat3s2 = new Plat();
        plat1.setName("plat3");
        plat1.setFamily("accompagnement");
        SousMenu accompagnements2 = new SousMenu(Collections.singletonList(plat3), 3);
        Plat plat4s2 = new Plat();
        plat1.setName("plat4");
        plat1.setFamily("fromage");
        Plat plat41s2 = new Plat();
        plat1.setName("plat41");
        plat1.setFamily("fromage");
        SousMenu fromages2 = new SousMenu(Arrays.asList(plat4s2, plat41s2), 4);
        Plat plat5s2 = new Plat();
        plat1.setName("plat5s2");
        plat1.setFamily("dessert");
        Plat plat52s2 = new Plat();
        plat1.setName("plat52s2");
        plat1.setFamily("dessert");
        SousMenu desserts2 = new SousMenu(Arrays.asList(plat5s2, plat52s2), 5);
        Plat plat6s2 = new Plat();
        plat1.setName("plat6s2");
        plat1.setFamily("autre");
        SousMenu autres2 = new SousMenu(Collections.singletonList(plat6s2), 6);
        service2.setMenu(Arrays.asList(entrees2, plats2, accompagnements2, fromages2, desserts2, autres2));
        service2.setTypeVide(false);
        service2.setRank(3);
        service2.setName("Diner");
        service3 = new Service();
        service3.setTypeVide(true);
        service3.setName("Petit déjeuner");
        service3.setRank(1);
    }

    @Test
    public void testCleanJournee(){
        Journee journee = new Journee();
        journee.setDestinations(new ArrayList<>(Arrays.asList(service1, service2, service3)));
        journee.clean();
        assertEquals("La journée doit contenir 2 services", 2, journee.getDestinations().size());
        assertEquals("Le premier service doit être celui avec le plus petit rank", 2, journee.getDestinations().get(0).getRank(), 0);
        assertEquals("Le dernier service doit être celui avec le plus grand rank", 3, journee.getDestinations().get(1).getRank(), 0);
        assertEquals("On doit avoir un serviceChoixNbPlats pour le Déjeuner", journee.getServiceChoixNbPlats().get("Déjeuner").size(), 6);
        assertEquals("On doit avoir un serviceChoixNbPlats pour le Diner", journee.getServiceChoixNbPlats().get("Diner").size(), 6);
        assertEquals("On ne doit avoir que 2 services dans serviceChoixNbPlats", journee.getServiceChoixNbPlats().keySet().size(), 2);
    }

}
