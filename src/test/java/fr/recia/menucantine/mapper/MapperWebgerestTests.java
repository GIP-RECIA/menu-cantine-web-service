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
package fr.recia.menucantine.mapper;

import fr.recia.menucantine.adoria.beans.Journee;
import fr.recia.menucantine.adoria.beans.Plat;
import fr.recia.menucantine.adoria.beans.Service;
import fr.recia.menucantine.adoria.beans.SousMenu;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.Semaine;
import fr.recia.menucantine.config.MapperConfig;
import fr.recia.menucantine.dto.JourneeDTO;
import fr.recia.menucantine.dto.PlatDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests sur le mapper WebGerest
 * Vérifie que les objets DTO sont bien transformés en objet de l'ancien back
 * Teste la transformation en tant que telle pour tous les objets
 * Le nettoyage et la complétion sont testé directement dans les tests des beans adoria
 * Teste aussi bien la création d'objets remplis que d'objets vides
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "unit" })
public class MapperWebgerestTests {

	@Autowired
	private IMapper mapperWebGerest;

	@Autowired
	private MapperConfig mapperConfig;

	@Test
	public void testMapPlatDTOToPlat(){
		PlatDTO platDTO = new PlatDTO("Plat test 1", 1, "entree", "Plat test 1", 1,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		Plat old = mapperWebGerest.buildPlat(platDTO);
		assertTrue("La liste des allergènes ne contient pas le Gluten", old.getAllergens().contains("Gluten"));
		assertEquals("Le type du plat n'est pas le bon", old.getFamily(), mapperConfig.getSousMenuFinalName("entree"));
		assertTrue("La liste labels doit être vide", old.getLabels().isEmpty());
        assertEquals("La liste labelsInfo doit contenir le label local", 1, old.getLabelsInfo().size());
		assertNull("La liste gemrcn doit être null", old.getGemrcn());
		assertEquals("Le nom du plat n'est pas le bon nom ", "Plat test 1", old.getName());
		assertEquals("Le familyrank doit valoir 1", 1, old.getFamilyRank(), 0);
		assertNull("La liste nutritions doit être null", old.getNutritions());
		assertEquals("La subfamily doit être la châine vide", "", old.getSubFamily());
		assertEquals("Le plat ne doit pas être de type vide ", false, old.getTypeVide());
	}

	@Test
	public void testMapListePlatToListeSousMenu(){
		PlatDTO platDTO1 = new PlatDTO("Plat test 1", 1, "entree", "Plat test 1", 1,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		PlatDTO platDTO2 = new PlatDTO("Plat test 2", 1, "entree", "Plat test 2", 2,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		PlatDTO platDTO3 = new PlatDTO("Plat test 3", 3, "plat", "Plat test 3", 3,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		PlatDTO platDTO4 = new PlatDTO("Plat test 4", 4, "accompagnement", "Plat test 4", 4,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		PlatDTO platDTO5 = new PlatDTO("Plat test 5", 5, "fromage", "Plat test 5", 5,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		PlatDTO platDTO6 = new PlatDTO("Plat test 6", 6, "dessert", "Plat test 6", 6,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		PlatDTO platDTO7 = new PlatDTO("Plat test 7", 7, "autre", "Plat test 7", 7,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		List<PlatDTO> platDTOList = Arrays.asList(platDTO1, platDTO2, platDTO3, platDTO4, platDTO5, platDTO6, platDTO7);
		ServiceDTO serviceDTO = new ServiceDTO();
		serviceDTO.setContenu(platDTOList);
		List<SousMenu> sousMenuList = mapperWebGerest.buildListSousMenu(serviceDTO);
		assertEquals("La liste des sous-menus doit contenir 6 élements", 6, sousMenuList.size());
		assertEquals("Le premier sous-menu doit contenir 2 élements", 2, sousMenuList.get(0).getChoix().size());
		assertEquals("Le troisième objet de la liste doit être le sous-menu d'accompagnement", "Accompagnement", sousMenuList.get(2).getChoix().get(0).getFamily());
	}

	@Test
	public void testMapListePlatVideToListeSousMenu(){
		ServiceDTO serviceDTO = new ServiceDTO();
		serviceDTO.setContenu(new ArrayList<>());
		List<SousMenu> sousMenuList = mapperWebGerest.buildListSousMenu(serviceDTO);
		assertEquals("La liste des sous-menus doit être vide", 0, sousMenuList.size());
	}

	@Test
	public void testMapServiceDTOToService(){
		PlatDTO platDTO = new PlatDTO("Plat test 1", 1, "entree", "Plat test 1", 1,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		List<PlatDTO> platDTOList = Collections.singletonList(platDTO);
		ServiceDTO serviceDTO = new ServiceDTO();
		serviceDTO.setContenu(platDTOList);
		Service service = mapperWebGerest.buildService(serviceDTO, "Déjeuner");
		assertEquals("Le nom du service doit valoir Déjeuner", "Déjeuner", service.getName());
		assertEquals("Le nom du service doit valoir Déjeuner", "Déjeuner", service.getServiceName());
		assertEquals("Le rank du service doit valoir 1", 1, service.getRank(), 0);
		assertFalse("Le type de service ne doit pas valoir false", service.getTypeVide());
		assertEquals("Le liste des plats du service ne doit pas être vide", 1, service.getMenu().size());
	}

	@Test
	public void testMapJourneeDTOToJournee(){
		JourneeDTO journeeDTO = new JourneeDTO();
		LocalDate now = LocalDate.of(2023, 12, 5);
		PlatDTO platDTO1 = new PlatDTO("Plat test 1", 1, "entree", "Plat test 1", 1,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		List<PlatDTO> platDTOList1 = Collections.singletonList(platDTO1);
		ServiceDTO serviceDTO1 = new ServiceDTO();
		serviceDTO1.setContenu(platDTOList1);
		PlatDTO platDTO2 = new PlatDTO("Plat test 2", 2, "entree", "Plat test 2", 1,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		List<PlatDTO> platDTOList2 = Collections.singletonList(platDTO2);
		ServiceDTO serviceDTO2 = new ServiceDTO();
		serviceDTO2.setContenu(platDTOList2);
		journeeDTO.setDate(now);
		journeeDTO.addService("Déjeuner", serviceDTO1);
		journeeDTO.addService("Dîner", serviceDTO2);
		Journee journee = mapperWebGerest.buildJournee(journeeDTO);
		assertEquals("Le jour de la journée est mardi", "mardi", journee.getJour());
		assertFalse("La journée ne doit pas être vide", journee.isVide());
		assertEquals("Il doit y avoir deux services dans la journée", 2, journee.getDestinations().size(), 0);
	}

	@Test
	public void testMapJourneeVideDTOToJournee(){
		JourneeDTO journeeDTO2 = new JourneeDTO();
		ServiceDTO serviceDTO3 = new ServiceDTO();
		LocalDate now = LocalDate.of(2023, 12, 5);
		serviceDTO3.setContenu(new ArrayList<>());
		journeeDTO2.setDate(now);
		journeeDTO2.addService("Déjeuner", serviceDTO3);
		Journee journee2 = mapperWebGerest.buildJournee(journeeDTO2);
		JourneeDTO journeeDTO3 = new JourneeDTO();
		journeeDTO3.setDate(now);
		Journee journee3 = mapperWebGerest.buildJournee(journeeDTO3);
		assertTrue("La journée doit être vide", journee2.isVide());
		assertTrue("La journée doit être vide", journee3.isVide());
	}

	@Test
	public void testMapListeJourneeDTOToSemaine(){
		JourneeDTO journeeDTO1 = new JourneeDTO();
		JourneeDTO journeeDTO2 = new JourneeDTO();
		LocalDate now1 = LocalDate.of(2023, 12, 5);
		LocalDate now2 = LocalDate.of(2023, 12, 5);
		PlatDTO platDTO1 = new PlatDTO("Plat test 1", 1, "entree", "Plat test 1", 1,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		List<PlatDTO> platDTOList1 = Collections.singletonList(platDTO1);
		ServiceDTO serviceDTO1 = new ServiceDTO();
		serviceDTO1.setContenu(platDTOList1);
		PlatDTO platDTO2 = new PlatDTO("Plat test 2", 2, "entree", "Plat test 2", 1,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		List<PlatDTO> platDTOList2 = Collections.singletonList(platDTO2);
		ServiceDTO serviceDTO2 = new ServiceDTO();
		serviceDTO2.setContenu(platDTOList2);
		journeeDTO1.setDate(now1);
		journeeDTO1.addService("Déjeuner", serviceDTO1);
		journeeDTO2.setDate(now2);
		journeeDTO2.addService("Déjeuner", serviceDTO2);
		List<JourneeDTO> journeeDTOList = Arrays.asList(journeeDTO1, journeeDTO2);
		Semaine semaine = mapperWebGerest.buildSemaine(journeeDTOList, now1, "000000F");
		assertEquals("Le nombre de jours de la semaine doit valoir 2", 2, semaine.getNbJours(), 0);
		assertEquals("Le début de la semaine est le 4 décembre 23", "4 décembre 23", semaine.getDebut());
		assertEquals("Le début de la semaine est le 8 décembre 23", "8 décembre 23", semaine.getFin());
		assertNotNull("La requête ne doit pas être nulle", semaine.getRequete());
		assertEquals("La liste des allGemRcn doit être vide", 0, semaine.getAllGemRcn().size(), 0);
		assertEquals("La semaine précédente doit être 2023-12-01", LocalDate.of(2023, 12, 1), semaine.getPreviousWeek());
		assertEquals("La semaine suivante doit être 2023-12-11", LocalDate.of(2023, 12, 11), semaine.getNextWeek());
		assertEquals("La liste des jours de la semaine doit contenir deux jours", 2, semaine.getJours().size(), 0);
		assertNotNull("Le nombre max de plats par service ne doit pas être null", semaine.getNbPlatMaxParService());
	}

	@Test
	public void testMapRequete(){
		LocalDate now = LocalDate.of(2023, 12, 5);
		String uai = "000000F";
		Requete requete = mapperWebGerest.buildRequete(now, uai);
		assertEquals("La semaine de la requête doit valoir 49", 49, requete.getSemaine(), 0);
		assertEquals("L'année de la requête doit valoir 2023", 2023, requete.getAnnee(), 0);
		assertEquals("Le jour de la requête doit valoir 5", 5, requete.getJour(), 0);
		assertEquals("L'UAI de la requête doit valoir 000000F", "000000F", requete.getUai());
		assertEquals("La date de la requête doit valoir 05/12/2023", "05/12/2023", requete.getDateJour());
	}

}

