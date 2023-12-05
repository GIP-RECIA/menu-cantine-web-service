package fr.recia.menucantine.adoria;

import fr.recia.menucantine.EnumTypeService;
import fr.recia.menucantine.adoria.beans.*;
import fr.recia.menucantine.beans.Requete;
import fr.recia.menucantine.beans.Semaine;
import fr.recia.menucantine.dto.JourneeDTO;
import fr.recia.menucantine.dto.PlatDTO;
import fr.recia.menucantine.dto.ServiceDTO;

import java.time.LocalDate;
import java.util.List;

public interface Mapper {

    /**
     * Construit une semaine de type Semaine à envoyer au front tel quel
     * @param journeeDTOList la liste des journées récupérées directement depuis l'API
     * @param requestDate la date demandée par la requête
     * @param uai l'uai demandé par la requête
     * @return La semaine construite et prête à être envoyée au front
     */
    Semaine buildSemaine(List<JourneeDTO> journeeDTOList, LocalDate requestDate, String uai);

    /**
     * Construit une requete de type Requete à envoyer au front avec la semaine
     * @param requestDate la date demandée par la requête
     * @param uai l'uai demandé par la requête
     * @return La requête construite et prête à être envoyée au front
     */
    Requete buildRequete(LocalDate requestDate, String uai);

    /**
     * Construit une Journee à partir d'une JourneeDTO
     * @param journeeDTO la journée à transformer
     * @return La journée construite et prête à être ajoutée à la semaine
     */
    Journee buildJournee(JourneeDTO journeeDTO);

    /**
     * Construit un Service à partir d'un ServiceDTO. A besoin du type de service car on ne le stocke pas dans ServiceDTO.
     * @param serviceDTO le service à transformer
     * @param typeService le type de service (Déjeuner, Diner, ect..)
     * @return La service construit et prêt à être ajouté à la journée
     */
    Service buildService(ServiceDTO serviceDTO, EnumTypeService typeService);

    /**
     * Construit une liste de SousMenu à partir d'un serviceDTO. On ne retourne pas un seul sous-menu car on fait
     * le travail de différencier les sous-menus dans cette méthode et non pas dans buildService.
     * @param serviceDTO le service duquel on veut extraire les sous-menus
     * @return La liste des sous menus du service
     */
    List<SousMenu> buildListSousMenu (ServiceDTO serviceDTO);

    /**
     * Construit un Plat à partir d'une PlatDTO
     * @param platDTO le plat à transformer
     * @return Le plat construit et prête à être ajouté au sous-menu
     */
    Plat buildPlat(PlatDTO platDTO);

    /**
     * Construit la liste des données de nutrition d'un plat
     * @param platDTO le plat dont on veut récupérer les données de nutrution
     * @return La liste des données de nutrition du plat, null s'il n'y en a pas
     */
    List<Nutrition> buildNutritions(PlatDTO platDTO);

    /**
     * Construit la liste des données gemrcn d'un plat
     * @param platDTO le plat dont on veut récupérer les données gemrcn
     * @return La liste des données gemrcn du plat, null s'il n'y en a pas
     */
    List<String> buildGemrcn(PlatDTO platDTO);

    /**
     * Construit la liste des lables d'un plat
     * @param platDTO le plat dont on veut récupérer les labels
     * @return La liste des labels du plat, null s'il n'y en a pas
     */
    List<Labels> buildLabels(PlatDTO platDTO);

    /**
     * Construit la liste des allèrgenes d'un plat
     * @param platDTO le plat dont on veut récupérer les allèrgenes
     * @return La liste des allèrgenes du plat, null s'il n'y en a pas
     */
    List<String> buildAllergens(PlatDTO platDTO);

}
