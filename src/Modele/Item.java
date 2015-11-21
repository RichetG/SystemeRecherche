package Modele;

import java.io.Serializable;

import org.codehaus.jackson.annotate.*;

/**
 * @description Classe Item retranscrit en un flux JSON
 * @author Richet Guillaume - Cadio Mathieu
 * @date 17/10/2015
 */
@SuppressWarnings("serial")
public class Item implements Serializable{
	
	private String Titre, URLFlux, URLSource, Auteur, Date, Description, ID, Langue, Contenu;
	
	@JsonCreator
	/**
	 * Constructeur d'un Item
	 * @param Titre
	 * @param URLFlux
	 * @param URLSource
	 * @param Auteur
	 * @param Date
	 * @param Description
	 * @param ID
	 * @param Langue
	 * @param Contenu
	 */
	public Item(@JsonProperty("Titre") String Titre, @JsonProperty("URLFlux") String URLFlux, @JsonProperty("URLSource") String URLSource, @JsonProperty("Auteur") String Auteur, @JsonProperty("Date") String Date, @JsonProperty("Description") String Description, @JsonProperty("ID") String ID, @JsonProperty("Langue") String Langue, @JsonProperty("Contenu") String Contenu){
		this.Titre=Titre;
		this.URLFlux=URLFlux;
		this.URLSource=URLSource;
		this.Auteur=Auteur;
		this.Date=Date;
		this.Description=Description;
		this.ID=ID;
		this.Langue=Langue;
		this.Contenu=Contenu;
	}

	/**
	 * Getter de Titre
	 * @return Titre
	 */
	public String getTitre() {
		return Titre;
	}

	/**
	 * Getter de URLFlux
	 * @return URLFlux
	 */
	public String getURLFlux() {
		return URLFlux;
	}

	/**
	 * Getter de URLSource
	 * @return URLSource
	 */
	public String getURLSource() {
		return URLSource;
	}

	/**
	 * Getter de Auteur
	 * @return Auteur
	 */
	public String getAuteur() {
		return Auteur;
	}

	/**
	 * Getter de Date
	 * @return Date
	 */
	public String getDate() {
		return Date;
	}

	/**
	 * Getter de Description
	 * @return Description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * Getter de ID
	 * @return ID
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Getter de Langue
	 * @return Langue
	 */
	public String getLangue() {
		return Langue;
	}

	/**
	 * Getter de Contenu
	 * @return Contenu
	 */
	public String getContenu() {
		return Contenu;
	}
}
