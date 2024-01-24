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

import fr.recia.menucantine.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.Semaine;

@RestController
@RequestMapping(path = "/api")
public class MenuCantineController {
	private static final Logger log = LoggerFactory.getLogger(MenuCantineController.class);	
			  
	@Autowired
	private MenuCantineServices services;
	
	@GetMapping(
			path="/demomenu",
			produces = "application/json"
		)
	public ResponseEntity<Object> getDemo(@RequestParam String uai, @RequestParam(required = false) String dateJour, @RequestParam(required = false) Integer semaine) {
		log.trace("Requête sur la path /demomenu");
		Requete requete = new Requete(uai, dateJour, semaine);
		try {
			Semaine menuSemaine = services.newFindSemaine(requete);
			return new ResponseEntity<Object>(menuSemaine, HttpStatus.OK);
		}catch (CustomMenuCantineException exception){
			log.error(exception.getMessage());
			return new ResponseEntity<>(new ResponseExceptionData(exception.getDisplayMessage()), HttpStatus.NOT_FOUND);
		}catch (Exception other){
			log.error("Une erreur innatendue s'est produite : {}", other.getMessage(), other);
			return new ResponseEntity<>(new ResponseExceptionData("Une erreur est survenue. Les menus de la cantine sont indisponibles pour le moment"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@GetMapping(
			path="/menu",
			produces = "application/json"
	)
	public ResponseEntity<Object> getMenu(@RequestParam String uai, @RequestParam(required = false) String dateJour, @RequestParam(required = false) Integer semaine) {
		log.trace("Requête sur la path /menu");
		Requete requete = new Requete(uai, dateJour, semaine);
		try {
			Semaine menuSemaine = services.newFindSemaine(requete);
			return new ResponseEntity<Object>(menuSemaine, HttpStatus.OK);
		}catch (CustomMenuCantineException exception){
			log.error(exception.getMessage());
			return new ResponseEntity<>(new ResponseExceptionData(exception.getDisplayMessage()), HttpStatus.NOT_FOUND);
		}catch (Exception other){
			log.error("Une erreur innatendue s'est produite : {}", other.getMessage(), other);
			return new ResponseEntity<>(new ResponseExceptionData("Une erreur est survenue. Les menus de la cantine sont indisponibles pour le moment"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
