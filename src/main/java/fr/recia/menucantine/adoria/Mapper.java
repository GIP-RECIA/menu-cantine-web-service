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
    Semaine buildSemaine(List<JourneeDTO> journeeDTOList, LocalDate requestDate, String uai);
    Requete buildRequete(LocalDate requestDate, String uai);
    Journee buildJournee(JourneeDTO journeeDTO);
    Service buildService(ServiceDTO serviceDTO, EnumTypeService typeService);
    List<SousMenu> buildListSousMenu (ServiceDTO serviceDTO);
    Plat buildPlat(PlatDTO platDTO);
    List<Nutrition> buildNutritions(PlatDTO platDTO);
    List<String> buildGemrcn(PlatDTO platDTO);
    List<Labels> buildLabels(PlatDTO platDTO);
    List<String> buildAllergens(PlatDTO platDTO);
}
