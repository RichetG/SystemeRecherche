package Modele;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Comparateur implements Comparator {

    Map tuple;
    public Comparateur(HashMap map) {
        this.tuple = map;
    }

    //ce comparateur ordonne les éléments dans l'ordre décroissant    
    @Override
    public int compare(Object o1, Object o2) {
       // TODO Auto-generated method stub
       if ((int) tuple.get(o1) >= (int) tuple.get(o2)) {
          return -1;
       } else {
          return 1;
       }
    }
}
