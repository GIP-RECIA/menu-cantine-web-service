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
import fr.recia.menucantine.config.MapperConfig;
import fr.recia.menucantine.dto.ServiceDTO;
import fr.recia.menucantine.exception.CustomMenuCantineException;
import fr.recia.menucantine.exception.NoDataExchangeException;
import fr.recia.menucantine.exception.UnknownUAIException;
import fr.recia.menucantine.exception.WebgerestRequestException;
import fr.recia.menucantine.mapper.MapperWebGerest;
import fr.recia.menucantine.webgerest.APIClient;
import net.sf.ehcache.CacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({ "unit" })
public class MenuCantineServiceTests {

    @Mock
    private APIClient apiClient;

    @MockBean
    private CacheManager cacheManager;

    @Mock
    private MapperWebGerest mapperWebGerest;

    @Mock
    private MapperConfig mapperConfig;

    @InjectMocks
    private MenuCantineServices menuCantineServices;

    @Captor
    private ArgumentCaptor<String> uaiCaptor;

    @Captor
    private ArgumentCaptor<String> datemenuCaptor;

    @Captor
    private ArgumentCaptor<Integer> serviceCaptor;

    @Test
    public void testNewFindSemaine() {

        // Paramètres d'appels
        String dateJour = "11/12/2023";
        String uai = "000000A";

        // Valeurs de retour
        Semaine expectedResponseBuildSemaine = new Semaine();
        expectedResponseBuildSemaine.setNbJours(5);
        ServiceDTO[] expectedResponsesApiCall = { new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(),
                new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(),
                new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO(), new ServiceDTO() };

        try {

            // 1. Définition des comportements des mocks

            // Comportement sur les appels à makeAuthenticatedApiCallGetMenu
            when(apiClient.makeAuthenticatedApiCallGetMenu(anyString(), anyString(), anyInt()))
                .thenAnswer(new Answer<ServiceDTO>() {
                    int invocationCount = 0;
                    @Override
                    public ServiceDTO answer(InvocationOnMock invocation) throws Throwable {
                        ServiceDTO response = expectedResponsesApiCall[invocationCount];
                        invocationCount++;
                        return response;
                    }
                });

            // Comportement sur l'appel à buildSemaine
            when(mapperWebGerest.buildSemaine(anyList(), any(LocalDate.class), anyString()))
                    .thenReturn(expectedResponseBuildSemaine);

            // Comportement sur l'appel à mapperConfig.serviceKeyFromValue
            when(mapperConfig.serviceKeyFromValue(anyInt()))
                    .thenReturn("Test Service");

            // 2. Appel de la méthode newFindSemaine à tester
            Requete requete =  new Requete();
            requete.setDateJour(dateJour);
            requete.setUai(uai);
            menuCantineServices.newFindSemaine(requete);

            // 3. Vérification des appels effectués aux mocks

            // Vérification de nombre d'appels de makeAuthenticatedApiCallGetMenu
            verify(apiClient, times(expectedResponsesApiCall.length))
                    .makeAuthenticatedApiCallGetMenu(uaiCaptor.capture(), datemenuCaptor.capture(), serviceCaptor.capture());

            // Vérification des paramètres de chaque appel
            assertArrayEquals("La liste des uai demandés doit être correcte",
                    new String[]{"000000A", "000000A", "000000A", "000000A", "000000A", "000000A", "000000A", "000000A",
                            "000000A", "000000A", "000000A", "000000A", "000000A", "000000A", "000000A", "000000A",
                            "000000A", "000000A", "000000A", "000000A"},
                    uaiCaptor.getAllValues().toArray(new String[0]));
            assertArrayEquals("La liste des dates demandées doit être correcte",
                    new String[]{"20231211", "20231211", "20231211", "20231211",
                            "20231212", "20231212", "20231212", "20231212", "20231213", "20231213", "20231213", "20231213",
                            "20231214", "20231214", "20231214", "20231214", "20231215", "20231215", "20231215", "20231215"},
                    datemenuCaptor.getAllValues().toArray(new String[0]));
            assertArrayEquals("La liste des services demandées doit être correcte",
                    new Integer[]{1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4},
                    serviceCaptor.getAllValues().toArray(new Integer[0]));

            // Vérification de l'appel à buildSemaine
            verify(mapperWebGerest).buildSemaine(anyList(), any(LocalDate.class), anyString());

        } catch (CustomMenuCantineException e) {
            throw new RuntimeException(e);
        }
    }

}
