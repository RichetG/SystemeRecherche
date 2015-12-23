package Modele;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @description Classe Comparateur
 * @author Richet Guillaume - Cadio Mathieu
 * @date 23/12/2015
 *
 */

@SuppressWarnings("rawtypes")
public class Comparateur implements Comparator {

    Map tuple;
    
    /**
     * Constructeur de Comparateur
     * @param map
     */
    public Comparateur(HashMap map) {
        this.tuple = map;
    }

    /**
     * Comparaison de 2 valeurs d'une table de hachage pour le classé de façcon decroissante
     */
    public int compare(Object o1, Object o2) {
       if ((int) tuple.get(o1) >= (int) tuple.get(o2)) {
          return -1;
       } else {
          return 1;
       }
    }
}
