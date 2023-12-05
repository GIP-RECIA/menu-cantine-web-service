package fr.recia.menucantine;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum EnumTypeSousMenu {
    ENTREE("entree","Entrée", 1),
    PLAT("plat","Plat", 2),
    ACCOMPAGNEMENT("accompagnement","Accompagnement", 3),
    FROMAGE("fromage","Fromage", 4),
    DESSERT("dessert","Dessert", 5),
    AUTRE("autre","Autre", 6);

    private final String nomServiceAPI;
    private final String nomServiceFinal;
    private final int rank;

    private EnumTypeSousMenu(String nomServiceAPI, String nomServiceFinal, int rank) {
        this.nomServiceAPI = nomServiceAPI;
        this.nomServiceFinal = nomServiceFinal;
        this.rank = rank;
    }

    private static final Map<String, EnumTypeSousMenu> BY_NAME = new HashMap<>();

    static {
        for (EnumTypeSousMenu e: values()) {
            BY_NAME.put(e.nomServiceAPI, e);
        }
    }

    public static EnumTypeSousMenu sousMenuFromName(String name) {
        return BY_NAME.get(name);
    }
    public static int sousMenuRankFromName(String name) {
        return BY_NAME.get(name).rank;
    }

}
