package fr.recia.menucantine;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(path = "/api")
public class MenuCantineController {
	private static final Logger log = LoggerFactory.getLogger(MenuCantineController.class);	
		  private static final String template = "Hello, %s!";
		 
		   
		  @GetMapping(path = "/hello")
		    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
	//	    	 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    	log.debug("hello {}", name);
	//	    	name = auth.getName();
		        return new Greeting(String.format(template, name));
		    }
		  
		  @PostMapping(path="/hello", consumes = "application/json", produces = "application/json")
		  public ResponseEntity<Object> post(
			//	  @RequestHeader(name = "X-COM-PERSIST", required = true) String headerPersist,
				  @RequestBody Greeting greeting){
			  String name = greeting.getContent();
			  log.debug("post hello {}", name);
			  Greeting newOne = new Greeting(String.format(template, name));
			  return new ResponseEntity<Object>(newOne, HttpStatus.OK);
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
