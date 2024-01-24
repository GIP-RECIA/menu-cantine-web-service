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

import fr.recia.menucantine.adoria.beans.NbPlatParSsMenu;
import fr.recia.menucantine.adoria.beans.Plat;
import fr.recia.menucantine.adoria.beans.Service;
import fr.recia.menucantine.adoria.beans.SousMenu;
import net.sf.ehcache.CacheManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "unit" })
public class BeanServiceTests {

    private Plat plat1;
    private Plat plat2;
    private Plat plat21;
    private Plat plat3;
    private Plat plat4;
    private Plat plat41;
    private Plat plat42;
    private Plat plat5;
    private Plat plat6;
    private SousMenu entree;
    private SousMenu plat;
    private SousMenu accompagnement;
    private SousMenu fromage;
    private SousMenu dessert;
    private SousMenu autre;

    @MockBean
    private CacheManager cacheManager;

    @Before
    public void setup(){
        plat1 = new Plat("plat1", "entree");
        entree = new SousMenu(Collections.singletonList(plat1), 1);
        plat2 = new Plat("plat2", "plat");
        plat21 = new Plat("plat21", "plat");
        plat = new SousMenu(Arrays.asList(plat2, plat21), 2);
        plat3 = new Plat("plat3", "accompagnement");
        accompagnement = new SousMenu(Collections.singletonList(plat3), 3);
        plat4 = new Plat("plat4", "fromage");
        plat41 = new Plat("plat41", "fromage");
        plat42 = new Plat("plat42", "fromage");
        fromage = new SousMenu(Arrays.asList(plat4, plat41, plat42), 4);
        plat5 = new Plat("plat5", "dessert");
        dessert = new SousMenu(Collections.singletonList(plat5), 5);
        plat6 = new Plat("plat6", "autre");
        autre = new SousMenu(Collections.singletonList(plat6), 6);
    }

    @Test
    public void testCleanService(){
        Service service = new Service();
        service.setMenu(Arrays.asList(entree, plat, accompagnement, fromage, dessert, autre));
        NbPlatParSsMenu nbPlatParSsMenu = service.clean();
        assertEquals("Le sousmenu de rank 1 possède 1 plat", 1, nbPlatParSsMenu.get(1), 0);
        assertEquals("Le sousmenu de rank 2 possède 2 plat", 2, nbPlatParSsMenu.get(2), 0);
        assertEquals("Le sousmenu de rank 3 possède 1 plat", 1, nbPlatParSsMenu.get(3), 0);
        assertEquals("Le sousmenu de rank 4 possède 3 plat", 3, nbPlatParSsMenu.get(4), 0);
        assertEquals("Le sousmenu de rank 5 possède 1 plat", 1, nbPlatParSsMenu.get(5), 0);
        assertEquals("Le sousmenu de rank 6 possède 1 plat", 1, nbPlatParSsMenu.get(6), 0);
    }

}