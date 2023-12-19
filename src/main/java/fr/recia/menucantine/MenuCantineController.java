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
package fr.recia.menucantine;

import fr.recia.menucantine.exception.NoMenuException;
import fr.recia.menucantine.exception.UnknownUAIException;
import fr.recia.menucantine.exception.WebgerestRequestException;
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

import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.Semaine;

@RestController
@RequestMapping(path = "/api")
public class MenuCantineController {
	private static final Logger log = LoggerFactory.getLogger(MenuCantineController.class);	
			  
	@Autowired
	private MenuCantineServices services;
	
	@PostMapping(
			path="/demomenu", 
			consumes = "application/json", 
			produces = "application/json"
		)
	public ResponseEntity<Object> postDemo(@RequestBody Requete requete) {
		log.trace("Requête sur la path /demomenu");
		try{
			Semaine semaine = services.newFindSemaine(requete);
			return new ResponseEntity<Object>(semaine, HttpStatus.OK);
		}catch (UnknownUAIException | WebgerestRequestException | NoMenuException exception){
			log.error(exception.getMessage());
		}
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}


	@PostMapping(
			path="/menu",
			consumes = "application/json",
			produces = "application/json"
	)
	public ResponseEntity<Object> getMenu(@RequestBody Requete requete){
		log.trace("Requête sur la path /menu");
		try{
			Semaine semaine = services.newFindSemaine(requete);
			return new ResponseEntity<Object>(semaine, HttpStatus.OK);
		}catch (UnknownUAIException | WebgerestRequestException | NoMenuException exception){
			log.error(exception.getMessage());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}
}
