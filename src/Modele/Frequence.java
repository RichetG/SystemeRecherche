package Modele;

public class Frequence {

	private int tf;
	private double idf;
	
	/**
	 * Constructeur Frequence par défault
	 */
	public Frequence(){
		tf=1;
		idf=-1.0;
	}
	
	/**
	 * Constructeur Frequence 
	 * @param tf
	 * @param idf
	 */
	public Frequence(int tf, double idf){
		this.tf=tf;
		this.idf=idf;
	}
	
	/**
	 * Incrementation de tf
	 */
	public void IncrementTf(){
		tf++;
	}
	
	/**
	 * Calcul de la idf
	 * @param nbDoc
	 */
	public void CalcIdf(int nbDoc){
		idf=-Math.log(tf/nbDoc);
	}
	
	/**
	 * Getter de tf
	 * @return tf
	 */
	public int getTf(){
		return tf;
	}
	
	/**
	 * Getter de idf
	 * @return idf
	 */
	public double getIdf(){
		return idf;
	}
	
	/**
	 * Setter de tf
	 * @param tf
	 */
	public void setTf(int tf){
		this.tf=tf;
	}
	
	/**
	 * Setter de idf
	 * @param idf
	 */
	public void setIdf(double idf){
		this.idf=idf;
	}
}
