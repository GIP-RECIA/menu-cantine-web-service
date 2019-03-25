package fr.recia.menucantine.adoria;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.recia.menucantine.adoria.beans.ReponseAdoria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class RestAdoriaTestClient {
	private static final Logger log = LoggerFactory.getLogger(RestAdoriaTestClient.class);

	static public ReponseAdoria call(File file)  throws IOException {
	//	String fileName = String.format("/home/legay/SpringBoot/menu-cantine/src/main/resources/static/test/menuTest%d.json", semaine);
		
		log.debug("open file : {}", file.getName());
		ObjectMapper objectMapper = new ObjectMapper();

		// utile pour l'usage des nouvelles class du pakage jackson-datatype-jsr310 comme par exemple localDate   
        objectMapper.findAndRegisterModules();  	
        // pour ignoré les données non declarée dans les class
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
      //  objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        ReponseAdoria reponse = objectMapper.readValue(file, ReponseAdoria.class);
        log.info(reponse.toString());
        return reponse.clean();
	}
}
