package fr.recia.menucantine.adoria.beans;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SousMenu implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = 6998445821377161534L;

    @NonNull
    List<Plat> choix;
    @NonNull
    Integer rank;

    Integer nbPlats;

    Boolean typeVide = false;

    public void addChoix(Plat choix){
        this.nbPlats += 1;
        this.choix.add(choix);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SousMenu clone = (SousMenu) super.clone();
        if (choix != null) {
            clone.choix = new ArrayList<Plat>(choix.size());
            for (Plat plat :choix) {
                clone.choix.add((Plat) plat.clone());
            }
        }
        return clone;
    }

}
