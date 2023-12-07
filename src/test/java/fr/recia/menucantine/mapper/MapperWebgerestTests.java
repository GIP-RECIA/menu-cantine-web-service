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
package fr.recia.menucantine.mapper;

import fr.recia.menucantine.adoria.beans.Plat;
import fr.recia.menucantine.adoria.beans.SousMenu;
import fr.recia.menucantine.dto.PlatDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import fr.recia.menucantine.enums.EnumTypeSousMenu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests sur le mapper WebGerest
 * Vérifie que les objets DTO sont bien transformés en objet de l'ancien back
 * Teste tout d'abord la transformation en tant quel telle, puis le nettoyage et la complétion
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperWebgerestTests {

	@Autowired
	private Mapper mapperWebGerest;

	@Test
	public void testMapNewToOldPlat(){
		PlatDTO platDTO = new PlatDTO("Plat test 1", 1, "entree", "Plat test 1", 1,
				false, true, false, false, true, false,
				false, false, false, false, false,
				false, false, false, false, false,
				false, false, 1, false, false);
		Plat old = mapperWebGerest.buildPlat(platDTO);
		assertTrue("La liste des allergènes ne contient pas le Gluten", old.getAllergens().contains("Gluten"));
		assertEquals("Le type du plat n'est pas le bon", old.getFamily(), EnumTypeSousMenu.sousMenuFromName("entree").getNomServiceFinal());
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
		System.out.println(sousMenuList);
		assertEquals("La liste des sous-menus doit contenir 6 élements", 6, sousMenuList.size());
		assertEquals("Le premier sous-menu doit contenir 2 élements", 2, sousMenuList.get(0).getChoix().size());
		assertEquals("Le troisième objet de la liste doit être le sous-menu d'accompagnement", "Accompagnement", sousMenuList.get(2).getChoix().get(0).getFamily());
	}


}

