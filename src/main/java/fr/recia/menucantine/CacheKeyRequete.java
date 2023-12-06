package fr.recia.menucantine;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheKeyRequete implements Serializable {

    private String uai;
    private String datemenu;
    private int service;

    public CacheKeyRequete(String uai, String datemenu, int service) {
        this.uai = uai;
        this.datemenu = datemenu;
        this.service = service;
    }
}
