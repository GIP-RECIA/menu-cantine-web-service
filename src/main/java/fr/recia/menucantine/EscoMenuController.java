package fr.recia.menucantine;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class EscoMenuController {
	private static final Logger log = LoggerFactory.getLogger(EscoMenuController.class);	
		  private static final String template = "Hello, %s!";
		 
		    @RequestMapping("/hello")
		    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
	//	    	 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    	log.debug("hello {}", name);
	//	    	name = auth.getName();
		        return new Greeting(String.format(template, name));
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
