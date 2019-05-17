package fr.recia.menucantine.adoria;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import fr.recia.menucantine.adoria.beans.ReponseAdoria;
import fr.recia.menucantine.adoria.beans.RequeteAdoria;


@ManagedBean
@Component
public class AdoriaHelper implements ResourceLoaderAware {
	private static final Logger log = LoggerFactory.getLogger(AdoriaHelper.class);	

	
	ResourceLoader resourceLoader;
	
	public AdoriaHelper() {
		super();
	}

	
	@Autowired
    private IRestAdoriaClient adoriaClient ;
	
	public  List<String> callTest(IRestAdoriaClient client, Integer semaine, Integer annee) {
		// Logger logOk = LoggerFactory.getLogger("FileOk");
		Logger logKo = LoggerFactory.getLogger("FileKo");
		
		List<String> uaiOk = new ArrayList<String>();
		
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
				//if ("0180823X".equals(uai)) {
					res =  client.call(new RequeteAdoria(uai, semaine, annee));
					uaiOk.add(uai);
					log.debug("etab ok : {}", uai);
				//	logOk.info("reponse = {}", res); 
				//}
			} catch (RestAdoriaClientException e){
				logKo.info("{} {}", uai, e.getJson());
			}	
		}
		if (uaiOk.isEmpty()) {
			uaiOk.add(" Il n'y pas de menu valide pour cette semaine");
		}
		return uaiOk;
	}
		
	public  ReponseAdoria call(String uai, Integer semaine, Integer annee) throws RestAdoriaClientException {
		
		ReponseAdoria res = null;
		
		String lock = String.format("%s%d%d", uai, semaine, annee).intern();
		
		synchronized (lock) {
			try {
				res = adoriaClient.call(new RequeteAdoria(uai, semaine, annee));
			} catch (RestAdoriaClientException e){
				log.error(e.getMessage());
				throw e;
			}
			return res;
		}
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
				this.resourceLoader = resourceLoader;
	}
	
	 
}
