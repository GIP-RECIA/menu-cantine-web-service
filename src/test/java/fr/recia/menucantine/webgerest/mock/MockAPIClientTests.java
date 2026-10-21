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
import fr.recia.menucantine.enums.Allergen;
import fr.recia.menucantine.enums.LabelsNourriture;
import fr.recia.menucantine.exception.UnknownUAIException;
import fr.recia.menucantine.exception.WebgerestRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockAPIClientTests {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 2024-01-03 est un mercredi
    private static final String MERCREDI = "20240103";
    // 2024-01-04 est un jeudi, sans aucune particularité
    private static final String JEUDI = "20240104";

    private final MockAPIClient mockAPIClient = new MockAPIClient();

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    void menuVideLeMercrediPourGouterEtDiner(int service) throws WebgerestRequestException, UnknownUAIException {
        ServiceDTO serviceDTO = mockAPIClient.makeAuthenticatedApiCallGetMenu("UAI", MERCREDI, service);
        assertTrue(serviceDTO.getContenu().isEmpty(),
                "Le service " + service + " ne doit renvoyer aucun plat le mercredi");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void menuRempliLeMercrediPourPetitDejeunerEtDejeuner(int service) throws WebgerestRequestException, UnknownUAIException {
        ServiceDTO serviceDTO = mockAPIClient.makeAuthenticatedApiCallGetMenu("UAI", MERCREDI, service);
        assertFalse(serviceDTO.getContenu().isEmpty(),
                "Le service " + service + " doit renvoyer des plats même le mercredi");
    }

    @Test
    void menuContientLeNombreDePlatsAttenduParService() throws WebgerestRequestException, UnknownUAIException {
        assertEquals(2, nbPlats(1), "Petit déjeuner : laitage + autre");
        assertEquals(5, nbPlats(2), "Déjeuner : entrée + plat + accompagnement + laitage + dessert");
        assertEquals(2, nbPlats(3), "Goûter : laitage + dessert");
        assertEquals(4, nbPlats(4), "Dîner : entrée + plat + laitage + dessert");
    }

    @Test
    void serviceInconnuRenvoieUneListeVide() throws WebgerestRequestException, UnknownUAIException {
        ServiceDTO serviceDTO = mockAPIClient.makeAuthenticatedApiCallGetMenu("UAI", JEUDI, 99);
        assertTrue(serviceDTO.getContenu().isEmpty(), "Un numéro de service inconnu ne doit pas faire planter l'appel");
    }

    @Test
    void uaiErrorUnknownUaiDeclencheUnknownUAIException() {
        assertThrows(UnknownUAIException.class,
                () -> mockAPIClient.makeAuthenticatedApiCallGetMenu("ERROR_UNKNOWN_UAI", JEUDI, 2),
                "L'UAI magique ERROR_UNKNOWN_UAI doit déclencher UnknownUAIException");
    }

    @Test
    void uaiErrorWebgerestDeclencheWebgerestRequestException() {
        assertThrows(WebgerestRequestException.class,
                () -> mockAPIClient.makeAuthenticatedApiCallGetMenu("ERROR_WEBGEREST", JEUDI, 2),
                "L'UAI magique ERROR_WEBGEREST doit déclencher WebgerestRequestException");
    }

    @Test
    void uaiErrorUnexpectedDeclencheUneRuntimeException() {
        assertThrows(IllegalStateException.class,
                () -> mockAPIClient.makeAuthenticatedApiCallGetMenu("ERROR_UNEXPECTED", JEUDI, 2),
                "L'UAI magique ERROR_UNEXPECTED doit déclencher une erreur inattendue (RuntimeException)");
    }

    @Test
    void uaiErrorBlockedRenvoieUneReponseEchangeBloque() throws WebgerestRequestException, UnknownUAIException {
        ServiceDTO serviceDTO = mockAPIClient.makeAuthenticatedApiCallGetMenu("ERROR_BLOCKED", JEUDI, 2);
        assertEquals(1, serviceDTO.getError(), "L'UAI magique ERROR_BLOCKED doit renvoyer une réponse en erreur");
        assertEquals("L'etablissement a bloqué l'echange de donnees.", serviceDTO.getMessage(),
                "Le message doit correspondre exactement à celui attendu par MenuCantineServices "
                        + "pour reconnaître un échange de données bloqué");
    }

    @Test
    void menuEstDeterministeAJourEtServiceFixes() throws WebgerestRequestException, UnknownUAIException {
        List<String> premierAppel = noms(mockAPIClient.makeAuthenticatedApiCallGetMenu("UAI", JEUDI, 2));
        List<String> secondAppel = noms(mockAPIClient.makeAuthenticatedApiCallGetMenu("UAI", JEUDI, 2));
        assertEquals(premierAppel, secondAppel,
                "Deux appels sur le même jour et le même service doivent renvoyer le même menu");
    }

    @Test
    void lesAllergenesEtLabelsCodesEnDurCorrespondentAuNomDuPlat() throws WebgerestRequestException, UnknownUAIException {
        // Le service 2 (déjeuner) n'est jamais vide et couvre les types entree/plat/laitage/dessert ;
        // sur 5 jours consécutifs, on est sûr de voir passer tous les plats possibles de ces listes
        // (la plus longue liste utilisée par createPlat fait 5 entrées).
        List<PlatDTO> plats = new ArrayList<>();
        for (int jour = 1; jour <= 5; jour++) {
            String datemenu = LocalDate.ofYearDay(2024, jour).format(DATE_FORMAT);
            plats.addAll(mockAPIClient.makeAuthenticatedApiCallGetMenu("UAI", datemenu, 2).getContenu());
        }

        Set<String> platsAvecCasParticulier = Set.of("Omelette", "Poisson pané", "Pâtes",
                "Yaourt nature", "Fromage blanc", "Camembert", "Emmental", "Carottes râpées", "Salade verte");

        for (PlatDTO plat : plats) {
            switch (plat.getNom()) {
                case "Omelette" -> {
                    assertTrue(plat.isAllerg_oeuf(), "Omelette devrait avoir l'allergène oeuf");
                    assertTrue(plat.isVegetarien(), "Omelette devrait être végétarien");
                }
                case "Poisson pané" -> {
                    assertTrue(plat.isAllerg_poisson(), "Poisson pané devrait avoir l'allergène poisson");
                    assertTrue(plat.isAllerg_gluten(), "Poisson pané devrait avoir l'allergène gluten");
                }
                case "Pâtes" -> assertTrue(plat.isAllerg_gluten(), "Pâtes devrait avoir l'allergène gluten");
                case "Yaourt nature", "Fromage blanc", "Camembert", "Emmental" ->
                        assertTrue(plat.isAllerg_lait(), plat.getNom() + " devrait avoir l'allergène lait");
                case "Carottes râpées", "Salade verte" -> {
                    assertTrue(plat.isBio(), plat.getNom() + " devrait être labellisé bio");
                    assertTrue(plat.isVegetarien(), plat.getNom() + " devrait être végétarien");
                }
                default -> {
                    if (!platsAvecCasParticulier.contains(plat.getNom())) {
                        assertAucunAllergeneNiLabel(plat);
                    }
                }
            }
        }
    }

    private void assertAucunAllergeneNiLabel(PlatDTO plat) {
        for (Allergen allergen : Allergen.values()) {
            assertFalse(allergen.isPresentOn(plat), plat.getNom() + " ne devrait avoir aucun allergène codé en dur");
        }
        for (LabelsNourriture label : LabelsNourriture.values()) {
            assertFalse(label.isPresentOn(plat), plat.getNom() + " ne devrait avoir aucun label codé en dur");
        }
    }

    private int nbPlats(int service) throws WebgerestRequestException, UnknownUAIException {
        return mockAPIClient.makeAuthenticatedApiCallGetMenu("UAI", JEUDI, service).getContenu().size();
    }

    private List<String> noms(ServiceDTO serviceDTO) {
        List<String> noms = new ArrayList<>();
        for (PlatDTO plat : serviceDTO.getContenu()) {
            noms.add(plat.getNom());
        }
        return noms;
    }

}
