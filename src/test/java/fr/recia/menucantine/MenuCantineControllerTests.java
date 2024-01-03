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
package fr.recia.menucantine;

import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.Semaine;
import fr.recia.menucantine.exception.UnknownUAIException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles({ "unit" })
public class MenuCantineControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuCantineServices menuCantineServices;

    @Test
    public void testCallController() throws Exception {

        // Valeurs de retour du faux service
        Semaine semaine = new Semaine();
        Requete requeteBadUAI = new Requete();
        requeteBadUAI.setDateJour("11/12/2023");
        requeteBadUAI.setUai("0000000A");
        Requete requeteOK = new Requete();
        requeteOK.setDateJour("11/12/2023");
        requeteOK.setUai("0000000B");

        // Mock du service
        when(menuCantineServices.newFindSemaine(eq(requeteBadUAI))).thenThrow(new UnknownUAIException(""));
        when(menuCantineServices.newFindSemaine(eq(requeteOK))).thenReturn(semaine);

        // Test mauvaise méthode
        mockMvc.perform(get("/api/menu")).andExpect(status().isMethodNotAllowed());

        // Test bonne méthode pas de JSON dans le body
        mockMvc.perform(post("/api/menu")).andExpect(status().isUnsupportedMediaType());

        // Test bonne méthode mauvais UAI
        mockMvc.perform(post("/api/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"dateJour\": \"11/12/2023\", \"uai\": \"0000000A\" }"))
                .andExpect(status().isNotFound());

        // Test bonne méthode bon UAI
        mockMvc.perform(post("/api/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"dateJour\": \"11/12/2023\", \"uai\": \"0000000B\" }"))
                .andExpect(status().isOk());

    }

}
