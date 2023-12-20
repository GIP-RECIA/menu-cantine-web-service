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
                .andDo(print()).andExpect(status().isNotFound());

        // Test bonne méthode bon UAI
        mockMvc.perform(post("/api/menu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"dateJour\": \"11/12/2023\", \"uai\": \"0000000B\" }"))
                .andDo(print()).andExpect(status().isOk());

    }

}