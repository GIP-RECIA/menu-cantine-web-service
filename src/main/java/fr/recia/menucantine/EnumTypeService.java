package fr.recia.menucantine;

import java.util.HashMap;
import java.util.Map;

public enum EnumTypeService {
    PETIT_DEJEUNER(1), DEJEUNER(2), GOUTER(3), DINER(4);

    private final int numService;

    private EnumTypeService(int numService) {
        this.numService = numService;
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

    public int getNumService(){
        return this.numService;
    }
}
