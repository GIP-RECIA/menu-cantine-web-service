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
		  
		  @GetMapping(path = "/hello")
		    public  ResponseEntity<Object> get() {
	
		   
		        return new ResponseEntity<Object>(lastCall4debug, HttpStatus.OK);
		    }
		  
		  @PostMapping(path="/hello", consumes = "application/json", produces = "application/json")
		  public ResponseEntity<Object> post(
			//	  @RequestHeader(name = "X-COM-PERSIST", required = true) String headerPersist,
				  @RequestBody Requete requete) {
			
			  log.debug("post requete =  {}", requete);
			  
			  
			//  Greeting newOne = new Greeting(String.format(template, name));
			 
			  try {
				lastCall4debug = services.findSemaine(requete);
			} catch (RestAdoriaClientException e) {
				return new ResponseEntity<Object>(e.getMap(), HttpStatus.PARTIAL_CONTENT);
			}
			 return new ResponseEntity<Object>(lastCall4debug, HttpStatus.OK);
			  //return new ResponseEntity<Object>(newOne, HttpStatus.OK);
		  }
		    
		    /*
		    @RequestMapping(value = "/files/{fileID}", method = RequestMethod.GET)
		    public void getFile(
		        @PathVariable("fileID") String fileName, 
		        HttpServletResponse response) throws IOException {
		            String src= "/home/legay/SpringBoot/" + fileName;
		            InputStream is = new FileInputStream(src);
		            IOUtils.copy(is, response.getOutputStream());
		            response.flushBuffer();
		    }
		    */
	

}
