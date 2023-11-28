package fr.recia.menucantine.adoria;

import fr.recia.menucantine.EnumTypeService;
import fr.recia.menucantine.adoria.beans.*;
import fr.recia.menucantine.beans.RequeteHelper;
import fr.recia.menucantine.beans.Semaine;
import fr.recia.menucantine.dto.JourneeDTO;
import fr.recia.menucantine.dto.PlatDTO;
import fr.recia.menucantine.dto.ServiceDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;


@Component
public class Mapper {

    public Semaine buildSemaine(List<JourneeDTO> journeeDTOList){
        final LocalDate today = LocalDate.now();
        final LocalDate lundi = RequeteHelper.jourMemeSemaine(today, 1);
        final LocalDate vendredi = RequeteHelper.jourMemeSemaine(today, 5);
        Semaine semaine = new Semaine();
        semaine.setDebut(RequeteHelper.localeDateToCompleteString(lundi));
        semaine.setFin(RequeteHelper.localeDateToCompleteString(vendredi));
        semaine.setNbJours(5);
        semaine.setPreviousWeek(lundi.minusDays(3));
        semaine.setNextWeek(lundi.plusDays(7));
        semaine.setAllGemRcn(new ArrayList<>());// TODO
        List<Journee> journeeList = new ArrayList<>();
        for(JourneeDTO journeeDTO: journeeDTOList){
            journeeList.add(this.buildJournee(journeeDTO));
        }
        semaine.setJours(journeeList);
        return semaine;
    }

    private Journee buildJournee(JourneeDTO journeeDTO){
        System.out.println(journeeDTO);
        Journee journee = new Journee();
        journee.setJour(journeeDTO.getDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH));
        journee.setDate(journeeDTO.getDate());
        journee.setTypeVide(false); // TODO ???
        List<Service> destinations = new ArrayList<>();
        for(EnumTypeService enumTypeService: journeeDTO.getMapTypeServiceToService().keySet()){
            // On n'intègre pas directement le type du service dans le Service car on ne l'a pas par le JSON, mais par l'URL
            // de laquelle on fait la requête = on ne mélange pas ce qu'on récupère depuis le JSON et les infos qu'on donne en entrée
            destinations.add(this.buildService(journeeDTO.getMapTypeServiceToService().get(enumTypeService), enumTypeService));
        }
        journee.setDestinations(destinations);
        return journee;
    }

    private Service buildService(ServiceDTO serviceDTO, EnumTypeService typeService){
        Service service = new Service();
        service.setName(typeService.name());
        service.setServiceName(typeService.name());
        service.setRank(typeService.getNumService()-1); // TODO rank ????
        service.setTypeVide(false); // TODO
        // On donne un service entier et non la liste des sous menu du service car on ne sait pas quel plat
        // appartient à quel sous menu sans avoir à parcourir la liste des plats du service
        List<SousMenu> menus = buildListSousMenu(serviceDTO);
        service.setMenu(menus);
        return service;
    }

    private List<SousMenu> buildListSousMenu(ServiceDTO serviceDTO){
        Map<String, SousMenu> sousMenuMap = new HashMap<>(); // Permet de regrouper tous les plats selon leur type en un sous-menu
        for(PlatDTO platDTO: serviceDTO.getContenu()){
            if(!sousMenuMap.containsKey(platDTO.getType())){
                SousMenu sousMenu = new SousMenu(new ArrayList<>(), 0);
                sousMenu.addChoix(buildPlat(platDTO));
                sousMenuMap.put(platDTO.getType(), sousMenu);
            }
            else{
                SousMenu sousMenu = sousMenuMap.get(platDTO.getType());
                sousMenu.addChoix(buildPlat(platDTO));
            }
        }
        return new ArrayList<>(sousMenuMap.values());
    }

    private Plat buildPlat(PlatDTO platDTO){
        Plat plat = new Plat();
        plat.setName(platDTO.getNom());
        plat.setAllergens(buildAllergens(platDTO));
        plat.setFamily(null);
        plat.setLabels(buildLabels(platDTO));
        plat.setGemrcn(buildGemrcn(platDTO));
        plat.setNutritions(buildNutritions(platDTO));
        plat.setFamilyRank(platDTO.getOrdre());
        plat.setSubFamily(null);
        plat.setTypeVide(false); // TODO
        return plat;
    }

    private List<Nutrition> buildNutritions(PlatDTO platDTO) {
        List<Nutrition> nutritionList = new ArrayList<>();
        return nutritionList;
    }

    private List<String> buildGemrcn(PlatDTO platDTO) {
        List<String> gemrcnList = new ArrayList<>();
        return gemrcnList;
    }

    private List<String> buildLabels(PlatDTO platDTO) {
        List<String> labelsList = new ArrayList<>();
        return labelsList;
    }

    private List<String> buildAllergens(PlatDTO platDTO) {
        List<String> allergensList = new ArrayList<>();
        return allergensList;
    }


}
