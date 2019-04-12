package fr.recia.menucantine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.recia.menucantine.adoria.RestAdoriaClientException;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.Semaine;

@RestController
@RequestMapping(path = "/api")
public class MenuCantineController {
	private static final Logger log = LoggerFactory.getLogger(MenuCantineController.class);	
			  
	@Autowired
	private MenuCantineServices services;
	  
	private Semaine lastCall4debug;
	  
	/**
	 * Renvoie le dernier flux json demandé par la methode POST:
	 * Utile uniquement en dev
	 * @return
	 */
	@GetMapping(path = "/lastmenu")
	public  ResponseEntity<Object> get() {
		if (lastCall4debug == null) {
			return new ResponseEntity<Object>(services.loadAllSemaine(),HttpStatus.OK) ;
		}
		return new ResponseEntity<Object>(lastCall4debug, HttpStatus.OK);
	}
	
	  
	@PostMapping(
			path="/menu", 
			consumes = "application/json", 
			produces = "application/json"
		)
	public ResponseEntity<Object> post( @RequestBody Requete requete) {
		log.debug("post requete =  {}", requete);
		Semaine semaine;
		
		try {
			
			lastCall4debug = semaine =  services.findSemaine(requete);
			
		} catch (RestAdoriaClientException e) {
			
			return new ResponseEntity<Object>(e.getMap(), HttpStatus.PARTIAL_CONTENT);
			
		}
		
		return new ResponseEntity<Object>(semaine, HttpStatus.OK);
	}
	
	@PostMapping(
			path="/demomenu", 
			consumes = "application/json", 
			produces = "application/json"
		)
	public ResponseEntity<Object> postDemo( @RequestBody Requete requete) {
		return post(requete);
	}
}
