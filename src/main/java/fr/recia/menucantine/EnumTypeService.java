package fr.recia.menucantine;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum EnumTypeService {
    PETIT_DEJEUNER(1, "Petit déjeuner"), DEJEUNER(2,"Déjeuner"), GOUTER(3,"Goûter"), DINER(4,"Dîner");

    private final int numService;
    private final String nomService;

    private EnumTypeService(int numService, String nomService) {
        this.numService = numService;
        this.nomService = nomService;
    }

    private static final Map<Integer, EnumTypeService> BY_NUMBER = new HashMap<>();

    static {
        for (EnumTypeService e: values()) {
            BY_NUMBER.put(e.numService, e);
        }
    }

    public static EnumTypeService serviceNumber(int numService) {
        return BY_NUMBER.get(numService);
    }

}
