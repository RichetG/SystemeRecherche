package Modele;

import java.util.ArrayList;
import java.util.HashMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

public class Classication {

	private ArrayList<Attribute> attribut;
	private FastVector fvWekaAttributes, fvClassVal;
	@SuppressWarnings("unused")
	private Instances isTrainingSet;
	private Attribute ClassAttribute;
	
	public Classication(Dictionnaire dico){
		attribut=new ArrayList<Attribute>(dico.size());
		for(String i:dico.listStem()){
			attribut.add(new Attribute(i));
		}
		fvClassVal = new FastVector(10);
        fvClassVal.addElement("SportFr");
        fvClassVal.addElement("SportEn");
        fvClassVal.addElement("SanteFr");
        fvClassVal.addElement("SanteEn");
        fvClassVal.addElement("CinemaFr");
        fvClassVal.addElement("CinemaEn");
        fvClassVal.addElement("ScienceFr");
        fvClassVal.addElement("ScienceEn");
        fvClassVal.addElement("EconoFr");
        fvClassVal.addElement("EconoEn");
        ClassAttribute = new Attribute("Classes", fvClassVal);
        
        fvWekaAttributes = new FastVector(dico.size()+1);
        for(int i=0; i<dico.size(); i++)
       	fvWekaAttributes.addElement(attribut.get(i));        
        fvWekaAttributes.addElement(ClassAttribute);
        
        isTrainingSet = new Instances("instances", fvWekaAttributes, dico.size()+1);
	}
	
	public void instance(Categorie categorie){
		for(int i=0; i<categorie.size(); i++){
			String traitementTit=categorie.get(i).getTitre().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
			String traitementDes=categorie.get(i).getDescription().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
			HashMap<String, Integer> liste=tableau(traitementTit, traitementDes);			
			Instance instance=new Instance(attribut.size()+1);
			for(int j=0; j<attribut.size(); j++){
				double val=0.01;
				if(liste.get((Attribute)fvWekaAttributes.elementAt(j))!=null){
					val=liste.get((Attribute)fvWekaAttributes.elementAt(j));
				}
				instance.setValue((Attribute)fvWekaAttributes.elementAt(j), val);
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(attribut.size()), categorie.getCategorie());
			isTrainingSet.add(instance);
		}
	}
	
	@SuppressWarnings("unused")
	private HashMap<String, Integer> tableau(String traitementTit, String traitementDes){
		String[] tit=traitementTit.split(" ");
		String[] des=traitementDes.split(" ");
		HashMap<String, Integer>listeItem=new HashMap<String, Integer>();
		for(int j=0; j<tit.length; j++){
			for(String k:listeItem.keySet()){
				if(tit[j].equals(k)){
					listeItem.put(k, listeItem.get(k)+1);
					break;
				}else{
					listeItem.put(k, 1);
				}
			}
		}
		for(int j=0; j<des.length; j++){
			for(String k:listeItem.keySet()){
				if(des[j].equals(k)){
					listeItem.put(k, listeItem.get(k)+1);
					break;
				}else{
					listeItem.put(k, 1);
				}
			}
		}
		return listeItem;
	}
}
