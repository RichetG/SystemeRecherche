package Modele;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonCreator;

/**
 * @description Classe ListItems qui prend en comte une multitude d'Item pour le flux JSON
 * @author Richet Guillaume - Cadio Mathieu
 * @date 17/10/2015
 *
 */
public class ListItems {

	public Map<String, Item>items;
	
	@JsonCreator
	/**
	 * Constructeur ListItems
	 */
	public ListItems(){
		items=new HashMap<String, Item>();
	}
	
	/**
	 * Ajout des Item dans ListItems
	 * @param item
	 */
	public void addItem(Item item){
		items.put(item.getID(), item);
	}
	
	/**
	 * Recuperation de tous les clé MD5
	 * @return liste cle MD5
	 */
	public Set<String> KeySet(){
		return items.keySet();
	}
	
	/**
	 * Getter Item
	 * @param ID
	 * @return Item
	 */
	public Item getItem(String ID){
		return items.get(ID);
	}
}
