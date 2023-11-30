package fr.recia.menucantine.dto;

import fr.recia.menucantine.EnumTypeService;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class JourneeDTO {

    private Map<EnumTypeService,ServiceDTO> mapTypeServiceToService;
    private LocalDate date;

    public JourneeDTO(){
        this.mapTypeServiceToService = new HashMap<>();
    }

    public void addService(EnumTypeService enumTypeService, ServiceDTO serviceDTO){
        this.getMapTypeServiceToService().put(enumTypeService, serviceDTO);
    }
}
