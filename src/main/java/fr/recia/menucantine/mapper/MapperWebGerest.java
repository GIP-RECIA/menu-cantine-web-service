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
package fr.recia.menucantine.mapper;

import fr.recia.menucantine.config.MapperConfig;
import fr.recia.menucantine.adoria.beans.*;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.RequeteHelper;
import fr.recia.menucantine.beans.Semaine;
import fr.recia.menucantine.dto.JourneeDTO;
import fr.recia.menucantine.dto.LabelDTO;
import fr.recia.menucantine.dto.PlatDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;


@Component
public class MapperWebGerest implements IMapper {

    private static final Logger log = LoggerFactory.getLogger(MapperWebGerest.class);

    @Autowired
    private MapperConfig mapperConfig;

    public Semaine buildSemaine(List<JourneeDTO> journeeDTOList, LocalDate requestDate, String uai){
        // Première étape : constuire la Semaine
        final LocalDate lundi = RequeteHelper.jourMemeSemaine(requestDate, 1);
        final LocalDate vendredi = RequeteHelper.jourMemeSemaine(requestDate, 5);
        Semaine semaine = new Semaine();
        semaine.setNbJours(0);
        semaine.setDebut(RequeteHelper.localeDateToStringWithSpaces(lundi));
        semaine.setFin(RequeteHelper.localeDateToStringWithSpaces(vendredi));
        semaine.setPreviousWeek(lundi.minusDays(3));
        semaine.setNextWeek(lundi.plusDays(7));
        semaine.setAllGemRcn(new ArrayList<>()); // pas de gemrcn dans cette API
        List<Journee> journeeList = new ArrayList<>();
        for(JourneeDTO journeeDTO: journeeDTOList){
            journeeList.add(this.buildJournee(journeeDTO));
        }
        semaine.setJours(journeeList);
        semaine.setRequete(buildRequete(requestDate, uai));

        // Deuxième étape : se brancher sur l'ancien back (complétion des sous-menus, tri, etc..)
        semaine.clean();
        semaine.complete();
        return semaine;
    }

    public Requete buildRequete(LocalDate requestDate, String uai){
        Requete requete = new Requete();
        requete.setSemaine(RequeteHelper.semaine(requestDate));
        requete.setJour(requestDate.getDayOfMonth());
        requete.setUai(uai);
        requete.setAnnee(requestDate.getYear());
        requete.setDateJour(RequeteHelper.localeDateToStringWithSlashes(requestDate));
        return requete;
    }

    public Journee buildJournee(JourneeDTO journeeDTO){
        Journee journee = new Journee();
        journee.setJour(journeeDTO.getDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH));
        List<Service> destinations = new ArrayList<>();
        boolean isVide = true;
        for(String typeService: journeeDTO.getMapTypeServiceToService().keySet()){
            // On n'intègre pas directement le type du service dans le ServiceDTO car on ne l'a pas par le JSON, mais par l'URL
            Service service =  this.buildService(journeeDTO.getMapTypeServiceToService().get(typeService), typeService);
            // Pour vérifier si une journée est vide, on regarde si tous ses services sont aussi vides
            if(!service.getTypeVide()){
                isVide = false;
            }
            destinations.add(service);
        }
        journee.setDestinations(destinations);
        journee.setTypeVide(isVide);
        return journee;
    }

    public Service buildService(ServiceDTO serviceDTO, String typeService){
        Service service = new Service();
        service.setName(typeService);
        service.setServiceName(typeService);
        service.setRank(mapperConfig.getNumService(typeService)-1);
        service.setTypeVide(false);
        // typeVide vaut true si jamais il n'y a pas de plat dans le service = il n'y a pas de service
        if(serviceDTO.getContenu() != null){
            if(serviceDTO.getContenu().isEmpty()){
                service.setTypeVide(true);
            }
        }else{
            service.setTypeVide(true);
        }
        service.setMenu(buildListSousMenu(serviceDTO));
        return service;
    }

    public List<SousMenu> buildListSousMenu(ServiceDTO serviceDTO){
        //Première étape : regrouper tous les plats selon leur type en sous-menus
        Map<String, SousMenu> sousMenuMap = new HashMap<>();
        Map<String, SousMenu> sousMenuInconnus = new HashMap<>();
        if(serviceDTO.getContenu() != null){
            for(PlatDTO platDTO: serviceDTO.getContenu()){
                if(!sousMenuMap.containsKey(platDTO.getType())){
                    // Tri des menus fait grâce à l'enum EnumTypeSousMenu, pas d'info dans l'API autre que le type
                    if(mapperConfig.getSousmenus().containsKey(platDTO.getType())) {
                        SousMenu sousMenu = new SousMenu(new ArrayList<>(), mapperConfig.getSousMenuRank(platDTO.getType()));
                        sousMenu.setNbPlats(0);
                        sousMenu.addChoix(buildPlat(platDTO));
                        sousMenuMap.put(platDTO.getType(), sousMenu);
                    }else{
                        // Cas ou on a un sous-menu qui n'est pas connu dans la config
                        log.error("Nom de sous-menu "+platDTO.getType()+" inconnu. Le sous-menu sera ajouté à la fin des sous-menus.");
                        if(!sousMenuInconnus.containsKey(platDTO.getType())){
                            SousMenu sousMenu = new SousMenu(new ArrayList<>(), -1);
                            sousMenu.setNbPlats(0);
                            sousMenu.addChoix(buildPlat(platDTO));
                            sousMenuInconnus.put(platDTO.getType(), sousMenu);
                        }else{
                            SousMenu sousMenu = sousMenuInconnus.get(platDTO.getType());
                            sousMenu.addChoix(buildPlat(platDTO));
                        }
                    }
                }
                else{
                    SousMenu sousMenu = sousMenuMap.get(platDTO.getType());
                    sousMenu.addChoix(buildPlat(platDTO));
                }
            }
        }

        // Deuxième étape : trier les sous-menus en fonction de leur rank
        SousMenu[] sousMenuTempo = new SousMenu[6];
        for(String typeSousMenu : sousMenuMap.keySet()){
            sousMenuTempo[sousMenuMap.get(typeSousMenu).getRank()-1] = sousMenuMap.get(typeSousMenu);
        }
        List<SousMenu> sousMenuList = new ArrayList<>();
        for (SousMenu sousMenu : sousMenuTempo) {
            if (sousMenu != null) {
                sousMenuList.add(sousMenu);
            }
        }

        //Troisième étape : ajout des sous-menus inconnus qu'on ne peut pas trier
        int rank = 7;
        for(SousMenu sousMenu : sousMenuInconnus.values()){
            sousMenu.setRank(rank);
            sousMenuList.add(sousMenu);
            rank+=1;
        }

        return sousMenuList;

    }

    public Plat buildPlat(PlatDTO platDTO){
        Plat plat = new Plat();
        String nomPlatCleaned = platDTO.getNom();
        nomPlatCleaned = nomPlatCleaned.replace("*","").replace("#","").replace("§","");
        plat.setName(Character.toUpperCase(nomPlatCleaned.charAt(0)) + nomPlatCleaned.toLowerCase().substring(1));
        plat.setAllergens(buildAllergens(platDTO));
        // La family du plat permet de changer l'intitulé du sous-menu auquel appartient le plat
        if(mapperConfig.getSousmenus().containsKey(platDTO.getType())){
            plat.setFamily(mapperConfig.getSousMenuFinalName(platDTO.getType()));
        }else{
            plat.setFamily(platDTO.getType());
        }
        plat.setLabels(new ArrayList<>());
        plat.setLabelsInfo(buildLabels(platDTO));
        plat.setGemrcn(buildGemrcn(platDTO));
        plat.setNutritions(buildNutritions(platDTO));
        plat.setFamilyRank(platDTO.getOrdre());
        plat.setSubFamily("");
        plat.setTypeVide(false);
        return plat;
    }

    public List<Nutrition> buildNutritions(PlatDTO platDTO) {
        return null;
    }

    public List<String> buildGemrcn(PlatDTO platDTO) {
        return null;
    }

    public List<Labels> buildLabels(PlatDTO platDTO) {
        List<Labels> labelsList = new ArrayList<>();
        // Labels sous forme d'attributs booléens dans l'API
        if(platDTO.isBio()){
            labelsList.add(Labels.getLabel("BIO"));
        }
        if(platDTO.isFaitmaison()){
            labelsList.add(Labels.getLabel("Fait Maison"));
        }
        if(platDTO.isLocal()){
            labelsList.add(Labels.getLabel("Local"));
        }
        if(platDTO.isVegetarien()) {
            labelsList.add(Labels.getLabel("Végetarien"));
        }

        // Labels sous forme d'une liste de code dans l'API (attention l'attribut labels peut ne pas être défini dans le JSON)
        if(platDTO.getLabels() != null){
            for(LabelDTO labelDTO: platDTO.getLabels()){
                if(labelDTO.getCode() != null && labelDTO.getLibelle() != null){
                    Labels label = Labels.getLabelFromId(Integer.parseInt(labelDTO.getCode()));
                    // Si le label est null alors on ne l'ajoute pas
                    if(label != null){
                        labelsList.add(label);
                    }
                }
            }
        }

        // Si la liste est vide retourner null, sinon retourner la liste des labels
        if(labelsList.isEmpty()){
            return null;
        }

        return labelsList;
    }

    public List<String> buildAllergens(PlatDTO platDTO) {
        List<String> allergensList = new ArrayList<>();
        if(platDTO.isAllerg_anhydride()){
            allergensList.add("Anhydride");
        }
        if(platDTO.isAllerg_arachide()){
            allergensList.add("Arachide");
        }
        if(platDTO.isAllerg_celeri()){
            allergensList.add("Céleri");
        }
        if(platDTO.isAllerg_crustace()){
            allergensList.add("Crustacé");
        }
        if(platDTO.isAllerg_fruit_coque()){
            allergensList.add("Fruit à coque");
        }
        if(platDTO.isAllerg_gluten()){
            allergensList.add("Gluten");
        }
        if(platDTO.isAllerg_lait()){
            allergensList.add("Lait");
        }
        if(platDTO.isAllerg_lupin()){
            allergensList.add("Lupin");
        }
        if(platDTO.isAllerg_mollusque()){
            allergensList.add("Mollusque");
        }
        if(platDTO.isAllerg_moutarde()){
            allergensList.add("Moutarde");
        }
        if(platDTO.isAllerg_oeuf()){
            allergensList.add("Oeuf");
        }
        if(platDTO.isAllerg_poisson()){
            allergensList.add("Poisson");
        }
        if(platDTO.isAllerg_sesame()){
            allergensList.add("Sésame");
        }
        if(platDTO.isAllerg_soja()){
            allergensList.add("Soja");
        }
        if(allergensList.isEmpty()){
            return null;
        }
        return allergensList;
    }

}
