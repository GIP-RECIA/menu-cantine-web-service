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
package fr.recia.menucantine.adoria;

import java.io.File;
import java.io.IOException;

import javax.annotation.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import fr.recia.menucantine.adoria.beans.RequeteAdoria;


// @Configuration
@ManagedBean
@Component("adoriaTest")
public class RestAdoriaTestClient implements IRestAdoriaClient , ResourceLoaderAware{
	private static final Logger log = LoggerFactory.getLogger(RestAdoriaTestClient.class);

	static public ReponseAdoria call(File file)  throws IOException {
	//	String fileName = String.format("/home/legay/SpringBoot/menu-cantine/src/main/resources/static/test/menuTest%d.json", semaine);
		
		log.debug("open file : {}", file.getName());
		ObjectMapper objectMapper = new ObjectMapper();

			// utile pour l'usage des nouvelles class du pakage jackson-datatype-jsr310 comme par exemple localDate   
        objectMapper.findAndRegisterModules();  	
        	
        	// pour ignoré les données non declarée dans les class
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        	// objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        ReponseAdoria reponse = objectMapper.readValue(file, ReponseAdoria.class);
        
        log.info(reponse.toString());
        return reponse.clean();
	}

	private ResourceLoader resourceLoader;
	
	@Value("${adoria.test.format-file-name}")
	private String formatFileName;

	public RestAdoriaTestClient() {
		super();
	}

	@Override
	public ReponseAdoria call(RequeteAdoria requete) throws RestAdoriaClientException {
		
		try {
			String fileName = String.format(formatFileName, requete.getWeekNumber());
		
			File file = getResourceLoader().getResource(fileName).getFile();
			
			return RestAdoriaTestClient.call(file);
		
		} catch (IOException e) {
		
			log.error(e.getMessage());
			
			throw new RestAdoriaClientException(e, requete);
		}
	}

	private ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
