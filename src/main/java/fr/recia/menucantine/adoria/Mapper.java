package fr.recia.menucantine.adoria;

import fr.recia.menucantine.EnumTypeService;
import fr.recia.menucantine.adoria.beans.*;
import fr.recia.menucantine.beans.Requete;
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

    /**
     * Construit une une semaine de type Semaine à envoyer au front tel quel
     * @param journeeDTOList la liste des journées récupérées directement depuis l'API
     * @param requestDate la date demandée par la requête
     * @param uai l'uai demandé par la requête
     * @return La semaine construite et prête à être envoyée au front
     */
    public Semaine buildSemaine(List<JourneeDTO> journeeDTOList, LocalDate requestDate, String uai){
        // Première étape : constuire la semaine
        final LocalDate lundi = RequeteHelper.jourMemeSemaine(requestDate, 1);
        final LocalDate vendredi = RequeteHelper.jourMemeSemaine(requestDate, 5);
        Semaine semaine = new Semaine();
        semaine.setDebut(RequeteHelper.localeDateToCompleteString(lundi));
        semaine.setFin(RequeteHelper.localeDateToCompleteString(vendredi));
        semaine.setNbJours(5);
        semaine.setPreviousWeek(lundi.minusDays(3));
        semaine.setNextWeek(lundi.plusDays(7));
        semaine.setAllGemRcn(new ArrayList<>()); // TODO : pas de gemrcn dans la nouvelle version de l'API
        List<Journee> journeeList = new ArrayList<>();
        for(JourneeDTO journeeDTO: journeeDTOList){
            journeeList.add(this.buildJournee(journeeDTO));
        }
        semaine.setJours(journeeList);
        semaine.setRequete(buildRequete(requestDate, uai));

        // Deuxième étape : faire les modifications nécéssaires avant d'envoyer au front
        semaine.clean();
        semaine.complete();
        return semaine;
    }

    private Requete buildRequete(LocalDate requestDate, String uai){
        Requete requete = new Requete();
        requete.setSemaine(RequeteHelper.semaine(requestDate));
        requete.setJour(requestDate.getDayOfMonth());
        requete.setUai(uai);
        requete.setAnnee(requestDate.getYear());
        requete.setDateJour(RequeteHelper.localeDateToOldString(requestDate));
        return requete;
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
        String serviceName = Character.toUpperCase(typeService.name().charAt(0)) + typeService.name().substring(1);
        service.setName(serviceName);
        service.setServiceName(serviceName);
        service.setRank(typeService.getNumService()-1); // TODO rank ????
        service.setTypeVide(false);
        // typeVide vaut true si jamais il n'y a pas de plat dans le service = il n'y a pas de service
        if(serviceDTO.getContenu().isEmpty()){
            service.setTypeVide(true);
        }
        // On donne un service entier et non la liste des sous menu du service car on ne sait pas quel plat
        // appartient à quel sous menu sans avoir à parcourir la liste des plats du service
        List<SousMenu> menus = buildListSousMenu(serviceDTO);
        service.setMenu(menus);
        return service;
    }

    private List<SousMenu> buildListSousMenu(ServiceDTO serviceDTO){
        //Première étape : regrouper tous les plats selon leur type en sous-menus
        Map<String, SousMenu> sousMenuMap = new HashMap<>();
        for(PlatDTO platDTO: serviceDTO.getContenu()){
            if(!sousMenuMap.containsKey(platDTO.getType())){
                // TODO : L'API ne propose pas de moyen de trier les sous-menus, pour l'instant tri temporaire = A CHANGER
                // NOTE : Passer juste le rank au front ne suffit pas, il faut faire le calcul au préalable
                int numSousMenu = 0;
                if(platDTO.getType().equals("entree")){
                    numSousMenu = 1;
                }
                if(platDTO.getType().equals("plat")){
                    numSousMenu = 2;
                }
                if(platDTO.getType().equals("accompagnement")){
                    numSousMenu = 3;
                }
                if(platDTO.getType().equals("fromage")){
                    numSousMenu = 4;
                }
                if(platDTO.getType().equals("dessert")){
                    numSousMenu = 5;
                }
                if(platDTO.getType().equals("autre")){
                    numSousMenu = 6;
                }
                SousMenu sousMenu = new SousMenu(new ArrayList<>(), numSousMenu);
                sousMenu.setNbPlats(0);
                sousMenu.addChoix(buildPlat(platDTO));
                sousMenuMap.put(platDTO.getType(), sousMenu);
            }
            else{
                SousMenu sousMenu = sousMenuMap.get(platDTO.getType());
                sousMenu.addChoix(buildPlat(platDTO));
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
        return sousMenuList;

    }

    private Plat buildPlat(PlatDTO platDTO){
        Plat plat = new Plat();
        plat.setName(platDTO.getNom());
        plat.setAllergens(buildAllergens(platDTO));
        plat.setFamily(platDTO.getType());
        plat.setLabels(new ArrayList<>()); // TODO : utilité ?
        plat.setLabelsInfo(buildLabels(platDTO));
        plat.setGemrcn(buildGemrcn(platDTO));
        plat.setNutritions(buildNutritions(platDTO));
        plat.setFamilyRank(platDTO.getOrdre());
        plat.setSubFamily(""); // TODO : on n'a pas de notion de sous-famille dans l'API
        plat.setTypeVide(false); // TODO : qu'est ce qu'un plat vide ???
        return plat;
    }

    private List<Nutrition> buildNutritions(PlatDTO platDTO) {
        return null;
    }

    private List<String> buildGemrcn(PlatDTO platDTO) {
        return null;
    }

    private List<Labels> buildLabels(PlatDTO platDTO) {
        List<Labels> labelsList = new ArrayList<>();
        if(platDTO.isBio()){
            labelsList.add(Labels.getLabel("BIO"));
        }
        if(platDTO.isFaitmaison()){
            labelsList.add(Labels.getLabel("Fait Maison"));
        }
        if(platDTO.isLocal()){
            labelsList.add(Labels.getLabel("Local"));
        }
        if(platDTO.isVegetarien()){
            labelsList.add(Labels.getLabel("Végetarien"));
        }
        if(labelsList.isEmpty()){
            return null;
        }
        return labelsList;
    }

    private List<String> buildAllergens(PlatDTO platDTO) {
        List<String> allergensList = new ArrayList<>();
        if(platDTO.isAllerg_anhydride()){
            allergensList.add("Anhydride");
        }
        if(platDTO.isAllerg_arachide()){
            allergensList.add("Arachide");
        }
        if(platDTO.isAllerg_celeri()){
            allergensList.add("Celeri");
        }
        if(platDTO.isAllerg_crustace()){
            allergensList.add("Crustace");
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
            allergensList.add("Sesame");
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