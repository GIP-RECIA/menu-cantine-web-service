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
import net.sf.ehcache.CacheManager;
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
public class BeanSemaineTests {

    private Journee journee1;
    private Journee journee2;

    @MockitoBean
    private CacheManager cacheManager;

    /**
     * Construit un SousMenu à partir de ses plats, sans avoir à passer par une liste à la main.
     */
    private static SousMenu sousMenu(int rank, Plat... plats) {
        return new SousMenu(new ArrayList<>(Arrays.asList(plats)), rank);
    }

    /**
     * Construit un Service "rempli" à partir de ses sous-menus. Les services vides
     * (sans sous-menu) continuent d'être créés directement via `new Service(...)`.
     */
    private static Service service(String name, int rank, boolean typeVide, SousMenu... sousMenus) {
        Service service = new Service(name, rank, typeVide);
        service.setMenu(Arrays.asList(sousMenus));
        return service;
    }

    @BeforeEach
    public void setup(){
        Service service1 = service("Déjeuner", 2, false,
                sousMenu(1, new Plat("plat1", "entree")),
                sousMenu(2, new Plat("plat2", "plat"), new Plat("plat21", "plat")),
                sousMenu(3, new Plat("plat3", "accompagnement")),
                sousMenu(4, new Plat("plat4", "fromage"), new Plat("plat41", "fromage"), new Plat("plat42", "fromage")),
                sousMenu(5, new Plat("plat5", "dessert")),
                sousMenu(6, new Plat("plat6", "autre")));
        Service service2 = service("Diner", 3, false,
                sousMenu(1, new Plat("plat1s2", "entree")),
                sousMenu(2, new Plat("plat2s2", "plat"), new Plat("plat21s2", "plat")),
                sousMenu(3, new Plat("plat3s2", "accompagnement")),
                sousMenu(4, new Plat("plat4s2", "fromage"), new Plat("plat41s2", "fromage")),
                sousMenu(5, new Plat("plat5s2", "dessert"), new Plat("plat51s2", "dessert")),
                sousMenu(6, new Plat("plat6s2", "autre")));
        Service service3 = new Service("Service vide", 4, true);
        journee1 = new Journee();
        journee1.setDestinations(new ArrayList<>(Arrays.asList(service1, service2, service3)));

        Service j2service1 = service("Déjeuner", 2, false,
                sousMenu(1, new Plat("j2plat1", "entree")),
                sousMenu(2, new Plat("j2plat2", "plat")),
                sousMenu(3, new Plat("j2plat3", "accompagnement")),
                sousMenu(4, new Plat("j2plat4", "fromage"), new Plat("j2plat41", "fromage")),
                sousMenu(5, new Plat("j2plat5", "dessert"), new Plat("j2plat51", "dessert")),
                sousMenu(6, new Plat("j2plat6", "autre")));
        Service j2service2 = service("Diner", 3, false,
                sousMenu(1, new Plat("j2plat1s2", "entree")),
                sousMenu(2, new Plat("j2plat2s2", "plat"), new Plat("j2plat21s2", "plat"), new Plat("j2plat22s2", "plat")),
                sousMenu(3, new Plat("j2plat3s2", "accompagnement"), new Plat("j2plat31s2", "accompagnement")),
                sousMenu(4, new Plat("j2plat4s2", "fromage")),
                sousMenu(5, new Plat("j2plat5s2", "dessert"), new Plat("j2plat51s2", "dessert")),
                sousMenu(6, new Plat("j2plat6s2", "autre")));
        Service j2service3 = new Service("Service vide", 4, true);
        journee2 = new Journee();
        journee2.setDestinations(new ArrayList<>(Arrays.asList(j2service1, j2service2, j2service3)));
    }

    @Test
    public void testCleanAndCompleteSemaine(){
        Semaine semaine = new Semaine();
        semaine.setJours(Arrays.asList(journee1, journee2));
        semaine.clean();
        semaine.complete();
        assertEquals(2, semaine.getJours().size(), 0, "La semaine doit contenir 2 jours");
        assertEquals(2, semaine.getJours().get(0).getDestinations().size(), 0, "Chaque jour doit contenir 2 services");
        assertEquals(2, semaine.getJours().get(1).getDestinations().size(), 0, "Chaque jour doit contenir 2 services");
        assertEquals(7, semaine.getJours().get(1).getDestinations().get(0).getMenu().size(), 0,
                "Le premier service de la première journée contient 7 sous-menus");
        assertEquals(2, semaine.getJours().get(0).getDestinations().get(0).getMenu().get(5).getChoix().size(),
                "Les sous-menus doivent être complétés correctement : première journée, premier service, cinquième sous-menu");
        assertEquals(3, semaine.getJours().get(0).getDestinations().get(1).getMenu().get(2).getChoix().size(),
                "Les sous-menus doivent être complétés correctement : première journée, deuxième service, deuxième sous-menu");
        assertEquals(2, semaine.getJours().get(0).getDestinations().get(1).getMenu().get(3).getChoix().size(),
                "Les sous-menus doivent être complétés correctement : première journée, deuxième service, troisième sous-menu");
        assertEquals(2, semaine.getJours().get(1).getDestinations().get(0).getMenu().get(2).getChoix().size(),
                "Les sous-menus doivent être complétés correctement : deuxième journée, premier service, deuxième sous-menu");
        assertEquals(3, semaine.getJours().get(1).getDestinations().get(0).getMenu().get(4).getChoix().size(),
                "Les sous-menus doivent être complétés correctement : deuxième journée, premier service, quatrième sous-menu");
        assertEquals(2, semaine.getJours().get(1).getDestinations().get(1).getMenu().get(4).getChoix().size(),
                "Les sous-menus doivent être complétés correctement : deuxième journée, deuxième service, quatrième sous-menu");
        assertEquals(1, semaine.getNbPlatMaxParService().get("Déjeuner").get(1), 0,
                "Le nombre de plat max par service doit être correct");
        assertEquals(2, semaine.getNbPlatMaxParService().get("Déjeuner").get(2), 0,
                "Le nombre de plat max par service doit être correct");
        assertEquals(3, semaine.getNbPlatMaxParService().get("Déjeuner").get(4), 0,
                "Le nombre de plat max par service doit être correct");
        assertEquals(2, semaine.getNbPlatMaxParService().get("Diner").get(3), 0,
                "Le nombre de plat max par service doit être correct");
        assertEquals(2, semaine.getNbPlatMaxParService().get("Diner").get(5), 0,
                "Le nombre de plat max par service doit être correct");
        assertEquals(1, semaine.getNbPlatMaxParService().get("Diner").get(6), 0,
                "Le nombre de plat max par service doit être correct");
    }

}
