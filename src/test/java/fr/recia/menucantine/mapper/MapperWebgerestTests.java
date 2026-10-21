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
import fr.recia.menucantine.dto.LabelDTO;
import fr.recia.menucantine.dto.PlatDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import net.sf.ehcache.CacheManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests sur le mapper WebGerest
 * Vérifie que les objets DTO sont bien transformés en objet de l'ancien back
 * Teste la transformation en tant que telle pour tous les objets
 * Le nettoyage et la complétion sont testé directement dans les tests des beans adoria
 * Teste aussi bien la création d'objets remplis que d'objets vides
 */
@SpringBootTest
@ActiveProfiles({ "unit" })
public class MapperWebgerestTests {

	@Autowired
	private IMapper mapperWebGerest;

	@Autowired
	private MapperConfig mapperConfig;

	@MockitoBean
	private CacheManager cacheManager;

	/**
	 * Construit un PlatDTO "de base" tel que l'API le renverrait : local, avec du gluten,
	 * une quantité prévue de 1, et aucun des autres attributs (allergènes, labels, etc.).
	 * On ne précise que ce qui varie d'un test à l'autre (nom, id, type, ordre) ; le reste
	 * peut être surchargé via les setters du builder renvoyé si un test en a besoin.
	 */
	private static PlatDTO.PlatDTOBuilder platDTO(String nom, int id, String type, int ordre) {
		return PlatDTO.builder()
				.nom(nom)
				.id(id)
				.type(type)
				.designationMenu(nom)
				.ordre(ordre)
				.local(true)
				.allerg_gluten(true)
				.quantiteprevue(1);
	}

	@Test
	public void testMapPlatDTOToPlat(){
		List<LabelDTO> labelDTOS = new ArrayList<>();
		labelDTOS.add(new LabelDTO("1", "Label Test"));
		PlatDTO platDTO = platDTO("Plat test 1", 1, "entree", 1)
				.labels(labelDTOS)
				.build();
		Plat old = mapperWebGerest.buildPlat(platDTO);
		assertTrue(old.getAllergens().contains("Gluten"), "La liste des allergènes ne contient pas le Gluten");
		assertEquals(old.getFamily(), mapperConfig.getSousMenuFinalName("entree"), "Le type du plat n'est pas le bon");
		assertTrue(old.getLabels().isEmpty(), "La liste labels doit être vide");
        assertEquals(2, old.getLabelsInfo().size(), "La liste labelsInfo doit contenir les deux labels");
		assertEquals("Plat test 1", old.getName(), "Le nom du plat n'est pas le bon nom ");
		assertEquals(1, old.getFamilyRank(), 0, "Le familyrank doit valoir 1");
		assertNull(old.getNutritions(), "La liste nutritions doit être null");
		assertEquals("", old.getSubFamily(), "La subfamily doit être la châine vide");
		assertEquals(false, old.getTypeVide(), "Le plat ne doit pas être de type vide ");
	}

	@Test
	public void testMapListePlatToListeSousMenu(){
		PlatDTO platDTO1 = platDTO("Plat test 1", 1, "entree", 1).build();
		PlatDTO platDTO2 = platDTO("Plat test 2", 1, "entree", 2).build();
		PlatDTO platDTO3 = platDTO("Plat test 3", 3, "plat", 3).build();
		PlatDTO platDTO4 = platDTO("Plat test 4", 4, "accompagnement", 4).build();
		PlatDTO platDTO5 = platDTO("Plat test 5", 5, "laitage", 5).build();
		PlatDTO platDTO6 = platDTO("Plat test 6", 6, "dessert", 6).build();
		PlatDTO platDTO7 = platDTO("Plat test 7", 7, "autre", 7).build();
		List<PlatDTO> platDTOList = Arrays.asList(platDTO1, platDTO2, platDTO3, platDTO4, platDTO5, platDTO6, platDTO7);
		ServiceDTO serviceDTO = new ServiceDTO();
		serviceDTO.setContenu(platDTOList);
		List<SousMenu> sousMenuList = mapperWebGerest.buildListSousMenu(serviceDTO);
		assertEquals(6, sousMenuList.size(), "La liste des sous-menus doit contenir 6 élements");
		assertEquals(2, sousMenuList.get(0).getChoix().size(), "Le premier sous-menu doit contenir 2 élements");
		assertEquals("Accompagnement", sousMenuList.get(2).getChoix().get(0).getFamily(), "Le troisième objet de la liste doit être le sous-menu d'accompagnement");
	}

	@Test
	public void testMapListePlatVideToListeSousMenu(){
		ServiceDTO serviceDTO = new ServiceDTO();
		serviceDTO.setContenu(new ArrayList<>());
		List<SousMenu> sousMenuList = mapperWebGerest.buildListSousMenu(serviceDTO);
		assertEquals(0, sousMenuList.size(), "La liste des sous-menus doit être vide");
	}

	@Test
	public void testMapServiceDTOToService(){
		PlatDTO platDTO = platDTO("Plat test 1", 1, "entree", 1).build();
		List<PlatDTO> platDTOList = Collections.singletonList(platDTO);
		ServiceDTO serviceDTO = new ServiceDTO();
		serviceDTO.setContenu(platDTOList);
		Service service = mapperWebGerest.buildService(serviceDTO, "Déjeuner");
		assertEquals("Déjeuner", service.getName(), "Le nom du service doit valoir Déjeuner");
		assertEquals("Déjeuner", service.getServiceName(), "Le nom du service doit valoir Déjeuner");
		assertEquals(1, service.getRank(), 0, "Le rank du service doit valoir 1");
		assertFalse(service.getTypeVide(), "Le type de service ne doit pas valoir false");
		assertEquals(1, service.getMenu().size(), "Le liste des plats du service ne doit pas être vide");
	}

	@Test
	public void testMapJourneeDTOToJournee(){
		JourneeDTO journeeDTO = new JourneeDTO();
		LocalDate now = LocalDate.of(2023, 12, 5);
		PlatDTO platDTO1 = platDTO("Plat test 1", 1, "entree", 1).build();
		List<PlatDTO> platDTOList1 = Collections.singletonList(platDTO1);
		ServiceDTO serviceDTO1 = new ServiceDTO();
		serviceDTO1.setContenu(platDTOList1);
		PlatDTO platDTO2 = platDTO("Plat test 2", 2, "entree", 1).build();
		List<PlatDTO> platDTOList2 = Collections.singletonList(platDTO2);
		ServiceDTO serviceDTO2 = new ServiceDTO();
		serviceDTO2.setContenu(platDTOList2);
		journeeDTO.setDate(now);
		journeeDTO.addService("Déjeuner", serviceDTO1);
		journeeDTO.addService("Dîner", serviceDTO2);
		Journee journee = mapperWebGerest.buildJournee(journeeDTO);
		assertEquals("mardi", journee.getJour(), "Le jour de la journée est mardi");
		assertFalse(journee.isVide(), "La journée ne doit pas être vide");
		assertEquals(2, journee.getDestinations().size(), 0, "Il doit y avoir deux services dans la journée");
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
		assertTrue(journee2.isVide(), "La journée doit être vide");
		assertTrue(journee3.isVide(), "La journée doit être vide");
	}

	@Test
	public void testMapListeJourneeDTOToSemaine(){
		JourneeDTO journeeDTO1 = new JourneeDTO();
		JourneeDTO journeeDTO2 = new JourneeDTO();
		LocalDate now1 = LocalDate.of(2023, 12, 5);
		LocalDate now2 = LocalDate.of(2023, 12, 5);
		PlatDTO platDTO1 = platDTO("Plat test 1", 1, "entree", 1).build();
		List<PlatDTO> platDTOList1 = Collections.singletonList(platDTO1);
		ServiceDTO serviceDTO1 = new ServiceDTO();
		serviceDTO1.setContenu(platDTOList1);
		PlatDTO platDTO2 = platDTO("Plat test 2", 2, "entree", 1).build();
		List<PlatDTO> platDTOList2 = Collections.singletonList(platDTO2);
		ServiceDTO serviceDTO2 = new ServiceDTO();
		serviceDTO2.setContenu(platDTOList2);
		journeeDTO1.setDate(now1);
		journeeDTO1.addService("Déjeuner", serviceDTO1);
		journeeDTO2.setDate(now2);
		journeeDTO2.addService("Déjeuner", serviceDTO2);
		List<JourneeDTO> journeeDTOList = Arrays.asList(journeeDTO1, journeeDTO2);
		Semaine semaine = mapperWebGerest.buildSemaine(journeeDTOList, now1, "000000F");
		assertEquals(2, semaine.getNbJours(), 0, "Le nombre de jours de la semaine doit valoir 2");
		assertEquals("4 décembre 23", semaine.getDebut(), "Le début de la semaine est le 4 décembre 23");
		assertEquals("8 décembre 23", semaine.getFin(), "Le début de la semaine est le 8 décembre 23");
		assertNotNull(semaine.getRequete(), "La requête ne doit pas être nulle");
		assertEquals(LocalDate.of(2023, 12, 1), semaine.getPreviousWeek(), "La semaine précédente doit être 2023-12-01");
		assertEquals(LocalDate.of(2023, 12, 11), semaine.getNextWeek(), "La semaine suivante doit être 2023-12-11");
		assertEquals(2, semaine.getJours().size(), 0, "La liste des jours de la semaine doit contenir deux jours");
		assertNotNull(semaine.getNbPlatMaxParService(), "Le nombre max de plats par service ne doit pas être null");
	}

	@Test
	public void testMapRequete(){
		LocalDate now = LocalDate.of(2023, 12, 5);
		String uai = "000000F";
		Requete requete = mapperWebGerest.buildRequete(now, uai);
		assertEquals(49, requete.getSemaine(), 0, "La semaine de la requête doit valoir 49");
		assertEquals(2023, requete.getAnnee(), 0, "L'année de la requête doit valoir 2023");
		assertEquals(5, requete.getJour(), 0, "Le jour de la requête doit valoir 5");
		assertEquals("000000F", requete.getUai(), "L'UAI de la requête doit valoir 000000F");
		assertEquals("05/12/2023", requete.getDateJour(), "La date de la requête doit valoir 05/12/2023");
	}

}

