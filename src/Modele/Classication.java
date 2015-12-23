package Modele;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * @description Classe Classification
 * @author Richet Guillaume - Cadio Mathieu
 * @date 23/12/2015
 *
 */

public class Classication {

	private ArrayList<Attribute> attribut;
	private FastVector fvWekaAttributes, fvClassVal;
	private Instances isTrainingSet;
	private Attribute ClassAttribute;
	@SuppressWarnings("unused")
	private Classifier cModel, cls;  
	private ArffSaver saver;
	private Discriminant discriminant;
	private String langue;
	private HashMap<String, Integer>listeItem;

	/**
	 * Constructeur de Classification
	 * @param dico
	 */
	public Classication(Dictionnaire dico){
		discriminant=new Discriminant();
		langue=dico.getLangue();
		//Mise en place de tous les stems du dictionnaire en attribut de weka
		attribut=new ArrayList<Attribute>(dico.size());
		for(String i:dico.listStem()){
			attribut.add(new Attribute(i));
		}
		//Mise en place des differentes classes 
		fvClassVal = new FastVector(5);
		fvClassVal.addElement("Sport"+langue);
		fvClassVal.addElement("Sante"+langue);
		fvClassVal.addElement("Cinema"+langue);
		fvClassVal.addElement("Science"+langue);
		fvClassVal.addElement("Econo"+langue);
		ClassAttribute = new Attribute("Classes", fvClassVal);

		fvWekaAttributes = new FastVector(dico.size()+1);
		for(int i=0; i<dico.size(); i++)
			fvWekaAttributes.addElement(attribut.get(i));        
		fvWekaAttributes.addElement(ClassAttribute);

		isTrainingSet = new Instances("instances", fvWekaAttributes, dico.size()+1);
		isTrainingSet.setClassIndex(dico.size());

		cModel=(Classifier)new NaiveBayes(); 
	}

	/**
	 * Détermination des instances des items d'une catégorie
	 * @param categorie
	 */
	public void instance(Categorie categorie){
		for(int i=0; i<categorie.size(); i++){
			//nettoyage du contenu textuelle pour une meilleur analyse
			String traitementTit=categorie.get(i).getTitre().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
			String traitementDes=categorie.get(i).getDescription().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
			//appel de la méthode privé plus bas
			HashMap<String, Integer> liste=tableau(traitementTit, traitementDes);	
			//Création de l'instance pour chaque item 
			Instance instance=new Instance(attribut.size()+1);
			for(int j=0; j<attribut.size(); j++){
				double val=0.01;
				//Détermination de nombre d'occurance pour chaque mot de l'item pour connaitre la valeur de celle-ci dans l'instance
				if(liste.get(fvWekaAttributes.elementAt(j).toString().replace(" numeric", "").replace("@attribute ", ""))!=null){
					val=liste.get(fvWekaAttributes.elementAt(j).toString().replace(" numeric", "").replace("@attribute ", ""));
				}
				instance.setValue((Attribute)fvWekaAttributes.elementAt(j), val);
			}
			//ajout du nom de la categorie pour l'identification
			instance.setValue((Attribute)fvWekaAttributes.elementAt(attribut.size()), categorie.getCategorie());
			isTrainingSet.add(instance);
		}
	}

	/**
	 * Détermination de la categorie d'un item inconnu
	 * @param item
	 * @return nom categorie
	 */
	public String instanceInconnu(Item item){
		//nettoyage du contenu textuelle pour une meilleur analyse
		String traitementTit=item.getTitre().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
		String traitementDes=item.getDescription().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
		//appel de la méthode privé plus bas
		HashMap<String, Integer> liste=tableau(traitementTit, traitementDes);	
		Instance instance=new Instance(attribut.size()+1);
		//Création de l'instance pour l'item inconnu 
		for(int j=0; j<attribut.size(); j++){
			double val=0.01;
			if(liste.get(fvWekaAttributes.elementAt(j).toString().replace(" numeric", "").replace("@attribute ", ""))!=null){
				val=liste.get(fvWekaAttributes.elementAt(j).toString().replace(" numeric", "").replace("@attribute ", ""));
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(j), val);
		}		
		isTrainingSet.add(instance);
		/*
		 * parcours de l'instance courante pour déterminer la valeur max >1:
		 * si oui on conserve sur num indexation (le dernier max trouvé)
		 * si non on stocke l'item dans la categorie autre
		 * ensuite on parcous toute les instances des valeurs de l'indexation trouvé pour connaitre celle avec le num d'accurance max le dernier max trouvé)
		 * stocke l'item avec la categorie trouvé
		 */
		String cat="";
		@SuppressWarnings("unused")
		int indexMax=-1, instanceMax=-1;
		double valMax=1.5;
		//parcours de l'instance de l'item inconnu crée précédemment pour connaitre l'attribut dont son poid est le plus important
		for(int i=0; i<isTrainingSet.lastInstance().numValues()-1; i++){
			if(valMax<=isTrainingSet.lastInstance().value(i)){
				valMax=isTrainingSet.lastInstance().value(i);
				indexMax=i;
			}
		}
		Set<String>catRandom=new HashSet<String>();
		//si une valeur max trouvé dont le poid est supérieur à 1.5
		if(indexMax!=-1){
			valMax=0;
			//parcours de tous les instances sauf celui de l'item inconnu pour connaitre l'instance comportant l'attribut de poid le plus fort
			for(int i=0; i<isTrainingSet.numInstances()-1; i++){
				if(valMax<isTrainingSet.instance(i).value(indexMax)){
					valMax=isTrainingSet.instance(i).value(indexMax);
					instanceMax=i;
					catRandom.clear();
					catRandom.add(isTrainingSet.instance(i).stringValue((isTrainingSet.instance(i).numValues())-1));
				//mise en place d'une liste des catégorie comportant le meme poids le plus fort en cas d'execo
				}else if(valMax==isTrainingSet.instance(i).value(indexMax)){
					catRandom.add(isTrainingSet.instance(i).stringValue((isTrainingSet.instance(i).numValues())-1));
				}
			}
			//détermination de la catégorie avec de l'aléatoire (si plus de 2 catégories)
			Random random=new Random();
			int nb=random.nextInt(catRandom.size());
			String[] tab=catRandom.toArray(new String[catRandom.size()]);
			cat=tab[nb];
		}else{
			cat="";
		}
		return cat;
	}

	/**
	 * Sauvegarde de la liste d'instances
	 * @throws Exception
	 */
	public void save() throws Exception{
		cModel.buildClassifier(isTrainingSet);
		weka.core.SerializationHelper.write(langue+".model", cModel);

		saver = new ArffSaver();
		saver.setInstances(isTrainingSet);
		saver.setFile(new File(langue+".arff"));
		saver.writeBatch();
	}

	/**
	 * Stockage de la liste d'instances
	 * @throws Exception
	 */
	public void load() throws Exception{
		cls = (Classifier) weka.core.SerializationHelper.read(langue+".model");
	}

	/**
	 * Mise en place pour chaque mot de l'item son nombre d'occurance 
	 * @param traitementTit
	 * @param traitementDes
	 * @return un tableau de hachage comportant un mot de l'item et son nombre d'occurance
	 */
	private HashMap<String, Integer> tableau(String traitementTit, String traitementDes){
		String[] tit=traitementTit.split(" ");
		String[] des=traitementDes.split(" ");
		listeItem=new HashMap<String, Integer>();
		//parcours de tous les mots du titre de l'item
		for(int j=0; j<tit.length; j++){
			//si item francais
			if(langue.equals("Fr")){
				//s'il ne fait pas parti des terms francais dans la classe Discriminant
				if(!discriminant.existFr(tit[j])){
					//s'il existe dans la table de hachage, on incremente son nombre d'occurance
					if(parcours(tit[j])){
						listeItem.replace(tit[j], listeItem.get(tit[j])+1);
					}else{
						//sinon on l'ajoute
						listeItem.put(tit[j], 1);
					}
				//meme méthode mais pour la version anglaise
				}else if(!discriminant.existEn(tit[j])){
					if(parcours(tit[j])){
						listeItem.replace(tit[j], listeItem.get(tit[j])+1);
					}else{
						listeItem.put(tit[j], 1);
					}
				}
			}
		}
		//meme methode mais pour la liste de mots de description
		for(int j=0; j<des.length; j++){
			if(langue.equals("Fr")){
				if(!discriminant.existFr(des[j])){
					if(parcours(des[j])){
						listeItem.replace(des[j], listeItem.get(des[j])+1);
					}else{
						listeItem.put(des[j], 1);
					}
				}else if(!discriminant.existEn(des[j])){
					if(parcours(des[j])){
						listeItem.replace(des[j], listeItem.get(des[j])+1);
					}else{
						listeItem.put(des[j], 1);
					}
				}
			}
		}
		return listeItem;
	}

	/**
	 * Parcours de la table de hachage pour connaitre l'existance du mot dans celle-ci
	 * @param mots
	 * @return un boolean
	 */
	private boolean parcours(String mots){
		boolean test=false;
		for(String i:listeItem.keySet()){
			if(i.equals(mots)){
				test=true;
				break;
			}
		}
		return test;
	}
}
