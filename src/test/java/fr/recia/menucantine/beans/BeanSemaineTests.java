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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "unit" })
public class BeanSemaineTests {

    private Journee journee1;
    private Journee journee2;

    @Before
    public void setup(){
        Plat plat1 = new Plat("plat1", "entree");
        SousMenu entree = new SousMenu(new ArrayList<>(Collections.singletonList(plat1)), 1);
        Plat plat2 = new Plat("plat2","plat");
        Plat plat21 = new Plat("plat21","plat");
        SousMenu plat = new SousMenu(new ArrayList<>(Arrays.asList(plat2, plat21)), 2);
        Plat plat3 = new Plat("plat3", "accompagnement");
        SousMenu accompagnement = new SousMenu(new ArrayList<>(Collections.singletonList(plat3)), 3);
        Plat plat4 = new Plat("plat4", "fromage");
        Plat plat41 = new Plat("plat41", "fromage");
        Plat plat42 = new Plat("plat42", "fromage");
        SousMenu fromage = new SousMenu(new ArrayList<>(Arrays.asList(plat4, plat41, plat42)), 4);
        Plat plat5 = new Plat("plat5", "dessert");
        SousMenu dessert = new SousMenu(new ArrayList<>(Collections.singletonList(plat5)), 5);
        Plat plat6 = new Plat("plat6", "autre");
        SousMenu autre = new SousMenu(new ArrayList<>(Collections.singletonList(plat6)), 6);
        Service service1 = new Service("Déjeuner", 2, false);
        service1.setMenu(Arrays.asList(entree, plat, accompagnement, fromage, dessert, autre));
        Plat plat1s2 = new Plat("plat1s2", "entree");
        SousMenu entrees2 = new SousMenu(new ArrayList<>(Collections.singletonList(plat1s2)), 1);
        Plat plat2s2 = new Plat("plat2s2","plat");
        Plat plat21s2 = new Plat("plat21s2","plat");
        SousMenu plats2 = new SousMenu(new ArrayList<>(Arrays.asList(plat2s2, plat21s2)), 2);
        Plat plat3s2 = new Plat("plat3s2", "accompagnement");
        SousMenu accompagnements2 = new SousMenu(new ArrayList<>(Collections.singletonList(plat3s2)), 3);
        Plat plat4s2 = new Plat("plat4s2", "fromage");
        Plat plat41s2 = new Plat("plat41s2", "fromage");
        SousMenu fromages2 = new SousMenu(new ArrayList<>(Arrays.asList(plat4s2, plat41s2)), 4);
        Plat plat5s2 = new Plat("plat5s2", "dessert");
        Plat plat51s2 = new Plat("plat51s2", "dessert");
        SousMenu desserts2 = new SousMenu(new ArrayList<>(Arrays.asList(plat5s2, plat51s2)), 5);
        Plat plat6s2 = new Plat("plat6s2", "autre");
        SousMenu autres2 = new SousMenu(new ArrayList<>(Collections.singletonList(plat6s2)), 6);
        Service service2 = new Service("Diner", 3, false);
        service2.setMenu(Arrays.asList(entrees2, plats2, accompagnements2, fromages2, desserts2, autres2));
        service2.setMenu(Arrays.asList(entrees2, plats2, accompagnements2, fromages2, desserts2, autres2));
        Service service3 = new Service("Service vide", 4, true);
        journee1 = new Journee();
        journee1.setDestinations(new ArrayList<>(Arrays.asList(service1, service2, service3)));
        Plat j2plat1 = new Plat("j2plat1", "entree");
        SousMenu j2entree = new SousMenu(new ArrayList<>(Collections.singletonList(j2plat1)), 1);
        Plat j2plat2 = new Plat("j2plat2","plat");
        SousMenu j2plat = new SousMenu(new ArrayList<>(Collections.singletonList(j2plat2)), 2);
        Plat j2plat3 = new Plat("j2plat3", "accompagnement");
        SousMenu j2accompagnement = new SousMenu(new ArrayList<>(Collections.singletonList(j2plat3)), 3);
        Plat j2plat4 = new Plat("j2plat4", "fromage");
        Plat j2plat41 = new Plat("j2plat41", "fromage");
        SousMenu j2fromage = new SousMenu(new ArrayList<>(Arrays.asList(j2plat4, j2plat41)), 4);
        Plat j2plat5 = new Plat("j2plat5", "dessert");
        Plat j2plat51 = new Plat("j2plat51", "dessert");
        SousMenu j2dessert = new SousMenu(new ArrayList<>(Arrays.asList(j2plat5, j2plat51)), 5);
        Plat j2plat6 = new Plat("j2plat6", "autre");
        SousMenu j2autre = new SousMenu(new ArrayList<>(Collections.singletonList(j2plat6)), 6);
        Service j2service1 = new Service("Déjeuner", 2, false);
        j2service1.setMenu(Arrays.asList(j2entree, j2plat, j2accompagnement, j2fromage, j2dessert, j2autre));
        Plat j2plat1s2 = new Plat("j2plat1s2", "entree");
        SousMenu j2entrees2 = new SousMenu(new ArrayList<>(Collections.singletonList(j2plat1s2)), 1);
        Plat j2plat2s2 = new Plat("j2plat2s2","plat");
        Plat j2plat21s2 = new Plat("j2plat21s2","plat");
        Plat j2plat22s2 = new Plat("j2plat22s2","plat");
        SousMenu j2plats2 = new SousMenu(new ArrayList<>(Arrays.asList(j2plat2s2, j2plat21s2, j2plat22s2)), 2);
        Plat j2plat3s2 = new Plat("j2plat3s2", "accompagnement");
        Plat j2plat31s2 = new Plat("j2plat3s2", "accompagnement");
        SousMenu j2accompagnements2 = new SousMenu(new ArrayList<>(Arrays.asList(j2plat3s2, j2plat31s2)), 3);
        Plat j2plat4s2 = new Plat("j2plat4s2", "fromage");
        SousMenu j2fromages2 = new SousMenu(new ArrayList<>(Collections.singletonList(j2plat4s2)), 4);
        Plat j2plat5s2 = new Plat("j2plat5s2", "dessert");
        Plat j2plat51s2 = new Plat("j2plat51s2", "dessert");
        SousMenu j2desserts2 = new SousMenu(new ArrayList<>(Arrays.asList(j2plat5s2, j2plat51s2)), 5);
        Plat j2plat6s2 = new Plat("j2plat6s2", "autre");
        SousMenu j2autres2 = new SousMenu(new ArrayList<>(Collections.singletonList(j2plat6s2)), 6);
        Service j2service2 = new Service("Diner", 3, false);
        j2service2.setMenu(Arrays.asList(j2entrees2, j2plats2, j2accompagnements2, j2fromages2, j2desserts2, j2autres2));
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
        assertEquals("La semaine doit contenir 2 jours", 2, semaine.getJours().size(), 0);
        assertEquals("Chaque jour doit contenir 2 services", 2, semaine.getJours().get(0).getDestinations().size(), 0);
        assertEquals("Chaque jour doit contenir 2 services", 2, semaine.getJours().get(1).getDestinations().size(), 0);
        assertEquals("Le premier service de la première journée contient 7 sous-menus",
                7, semaine.getJours().get(1).getDestinations().get(0).getMenu().size(), 0);
        assertEquals("Les sous-menus doivent être complétés correctement : première journée, premier service, cinquième sous-menu",
                2, semaine.getJours().get(0).getDestinations().get(0).getMenu().get(5).getChoix().size());
        assertEquals("Les sous-menus doivent être complétés correctement : première journée, deuxième service, deuxième sous-menu",
                3, semaine.getJours().get(0).getDestinations().get(1).getMenu().get(2).getChoix().size());
        assertEquals("Les sous-menus doivent être complétés correctement : première journée, deuxième service, troisième sous-menu",
                2, semaine.getJours().get(0).getDestinations().get(1).getMenu().get(3).getChoix().size());
        assertEquals("Les sous-menus doivent être complétés correctement : deuxième journée, premier service, deuxième sous-menu",
                2, semaine.getJours().get(1).getDestinations().get(0).getMenu().get(2).getChoix().size());
        assertEquals("Les sous-menus doivent être complétés correctement : deuxième journée, premier service, quatrième sous-menu",
                3, semaine.getJours().get(1).getDestinations().get(0).getMenu().get(4).getChoix().size());
        assertEquals("Les sous-menus doivent être complétés correctement : deuxième journée, deuxième service, quatrième sous-menu",
                2, semaine.getJours().get(1).getDestinations().get(1).getMenu().get(4).getChoix().size());
        assertEquals("Le nombre de plat max par service doit être correct",
                1, semaine.getNbPlatMaxParService().get("Déjeuner").get(1), 0);
        assertEquals("Le nombre de plat max par service doit être correct",
                2, semaine.getNbPlatMaxParService().get("Déjeuner").get(2), 0);
        assertEquals("Le nombre de plat max par service doit être correct",
                3, semaine.getNbPlatMaxParService().get("Déjeuner").get(4), 0);
        assertEquals("Le nombre de plat max par service doit être correct",
                2, semaine.getNbPlatMaxParService().get("Diner").get(3), 0);
        assertEquals("Le nombre de plat max par service doit être correct",
                2, semaine.getNbPlatMaxParService().get("Diner").get(5), 0);
        assertEquals("Le nombre de plat max par service doit être correct",
                1, semaine.getNbPlatMaxParService().get("Diner").get(6), 0);
    }

}
