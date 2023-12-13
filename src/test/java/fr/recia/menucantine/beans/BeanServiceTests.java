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

import fr.recia.menucantine.adoria.beans.NbPlatParSsMenu;
import fr.recia.menucantine.adoria.beans.Plat;
import fr.recia.menucantine.adoria.beans.Service;
import fr.recia.menucantine.adoria.beans.SousMenu;
import fr.recia.menucantine.dto.PlatDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import fr.recia.menucantine.enums.EnumTypeService;
import fr.recia.menucantine.mapper.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BeanServiceTests {

    @Autowired
    private Mapper mapperWebGerest;

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

    @Before
    public void setup(){
        plat1 = new Plat();
        plat1.setName("plat1");
        plat1.setFamily("entree");
        entree = new SousMenu(Collections.singletonList(plat1), 1);
        plat2 = new Plat();
        plat2.setName("plat2");
        plat2.setFamily("plat");
        plat21 = new Plat();
        plat21.setName("plat21");
        plat21.setFamily("plat");
        plat = new SousMenu(Arrays.asList(plat2, plat21), 2);
        plat3 = new Plat();
        plat3.setName("plat3");
        plat3.setFamily("accompagnement");
        accompagnement = new SousMenu(Collections.singletonList(plat3), 3);
        plat4 = new Plat();
        plat4.setName("plat4");
        plat4.setFamily("fromage");
        plat41 = new Plat();
        plat41.setName("plat41");
        plat41.setFamily("fromage");
        plat42 = new Plat();
        plat42.setName("plat42");
        plat42.setFamily("fromage");
        fromage = new SousMenu(Arrays.asList(plat4, plat41, plat42), 4);
        plat5 = new Plat();
        plat5.setName("plat5");
        plat5.setFamily("dessert");
        dessert = new SousMenu(Collections.singletonList(plat5), 5);
        plat6 = new Plat();
        plat6.setName("plat6");
        plat6.setFamily("autre");
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
        System.out.println(service);
        System.out.println(nbPlatParSsMenu);
    }

    @Test
    public void testMapServiceDTOToService() {
        PlatDTO platDTO1 = new PlatDTO("Plat test 1", 1, "entree", "Plat test 1", 1,
                false, true, false, false, true, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, 1, false, false);
        PlatDTO platDTO2 = new PlatDTO("Plat test 2", 1, "plat", "Plat test 2", 2,
                false, true, false, false, true, false,
                false, false, false, false, false,
                false, false, false, false, false,
                false, false, 1, false, false);
        PlatDTO platDTO3 = new PlatDTO("Plat test 3", 3, "entree", "Plat test 3", 3,
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
        Service service = mapperWebGerest.buildService(serviceDTO, EnumTypeService.DEJEUNER);
        System.out.println(service);
    }

}