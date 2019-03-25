package fr.recia.menucantine.adoria;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.annotation.ManagedBean;

import org.hibernate.validator.internal.util.privilegedactions.GetResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import fr.recia.menucantine.adoria.beans.RequeteAdoria;


@ManagedBean
public class RestAdoriaClient implements ResourceLoaderAware {
	private static final Logger log = LoggerFactory.getLogger(RestAdoriaClient.class);	

	
	ResourceLoader resourceLoader;
	
	public RestAdoriaClient() {
		super();
	}

	@Value("${test.format-file-name}")
	String formatFileName;
	
	@Value("${test.no-web-service}")
	Boolean noWebService;
	
	@Autowired
    private IRestAdoriaWebClient adoriaClient ;
	
	public  ReponseAdoria callTest() {
		Logger logOk = LoggerFactory.getLogger("FileOk");
		Logger logKo = LoggerFactory.getLogger("FileKo");

		String [] uais = { 
				"0370001A",
				"0410017W",
				"0371418R",
				"0450066C",
				"0450042B",
				"0180007K",
				"0360011S",
				"0280036M",
				"0180042Y",
				"0180006J",
				"0280864M",
				"0281047L",
				"0360002G",
				"0370009J",
				"0370039S",
				"0410031L",
				"0410959V",
				"0451483T",
				"0180008L",
				"0180823X",
				"0280044W",
				"0280925D",
				"0360003H",
				"0360008N",
				"0410030K",
				"0370040T",
				"0450064A",
				"0451304Y",
				"0180024D",
				"0280021W",
				"0281077U",
				"0360009P",
				"0370036N",
				"0371417P",
				"0410832G",
				"0450051L",
				"0450062Y",
				"0451442Y",
				"0180026F",
				"0180005H",
				"0180009M",
				"0180010N",
				"0180036S",
				"0280009H",
				"0280015P",
				"0280022X",
				"0360005K",
				"0360019A",
				"0370037P",
				"0370038R",
				"0370053G",
				"0370054H",
				"0371100V",
				"0371211R",
				"0410002E",
				"0410036S",
				"0410718H",
				"0410899E",
				"0450050K",
				"0450750W",
				"0450782F",
				"0451067R",
				"0451484U",
				"0451526P",
				"0450822X",
				"0180025E",
				"0180035R",
				"0280007F",
				"0280019U",
				"0280700J",
				"0281021H",
				"0360043B",
				"0370032J",
				"0370035M",
				"0370771M",
				"0370888P",
				"0371099U",
				"0371123V",
				"0410001D",
				"0450029M",
				"0450040Z",
				"0450043C",
				"0450049J",
				"0450786K",
				"0451037H",
				"0451462V",
				"0410626H",
				"0370781Y",
				"0360017Y",
				"0370794M",
				"0410018X",
				"0410629L",
				"0370878D",
				"0180585N",
				"0280706R",
				"0450094H",
				"0451535Z",
				"0450027K",
				"0360024F",
				"0370016S",
				};
		ReponseAdoria res = null;
		for (String uai : uais) {
			try {
				if ("0180823X".equals(uai)) {
					res =  adoriaClient.call(new RequeteAdoria(uai, 6, 2019));
					
					log.debug("etab ok : {}", uai);
				//	logOk.info("reponse = {}", res); 
				}
			} catch (RestAdoriaClientException e){
				logKo.info("{} {}", uai, e.getJson());
			}	
		}
		return  res;
		
	}
	
	public  ReponseAdoria callTest(String uai, Integer semaine, Integer annee) {
		try {
			String fileName = String.format(formatFileName, semaine);
			File file = resourceLoader.getResource(fileName).getFile();
			return RestAdoriaTestClient.call(file);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return null;
	}
		
	public  ReponseAdoria call(String uai, Integer semaine, Integer annee) throws RestAdoriaClientException {
		ReponseAdoria res = null;
		if (noWebService) {
			return callTest(uai, semaine, annee);
		}
		try {
			
			res = adoriaClient.call(new RequeteAdoria(uai, semaine, annee));
			
		} catch (RestAdoriaClientException e){
			log.error(e.getMessage());
			throw e;
		}
		return res;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
				this.resourceLoader = resourceLoader;
	}
	
	 
}
