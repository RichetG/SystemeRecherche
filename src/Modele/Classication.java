package Modele;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

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

	public Classication(Dictionnaire dico){
		discriminant=new Discriminant();
		langue=dico.getLangue();
		attribut=new ArrayList<Attribute>(dico.size());
		for(String i:dico.listStem()){
			attribut.add(new Attribute(i));
		}
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

	public void instance(Categorie categorie){
		for(int i=0; i<categorie.size(); i++){
			String traitementTit=categorie.get(i).getTitre().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
			String traitementDes=categorie.get(i).getDescription().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
			HashMap<String, Integer> liste=tableau(traitementTit, traitementDes);	
			Instance instance=new Instance(attribut.size()+1);
			for(int j=0; j<attribut.size(); j++){
				double val=0.01;
				if(liste.get(fvWekaAttributes.elementAt(j).toString().replace(" numeric", "").replace("@attribute ", ""))!=null){
					val=liste.get(fvWekaAttributes.elementAt(j).toString().replace(" numeric", "").replace("@attribute ", ""));
				}
				instance.setValue((Attribute)fvWekaAttributes.elementAt(j), val);
			}
			instance.setValue((Attribute)fvWekaAttributes.elementAt(attribut.size()), categorie.getCategorie());
			isTrainingSet.add(instance);
		}
		
	}

	public String instanceInconnu(Item item){
		String traitementTit=item.getTitre().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
		String traitementDes=item.getDescription().toLowerCase().replace(".", "").replace("/", "").replace(":", "").replace("?", "").replace("(", "").replace(",", "");	
		HashMap<String, Integer> liste=tableau(traitementTit, traitementDes);	
		Instance instance=new Instance(attribut.size()+1);
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
		int indexMax=-1, instanceMax=-1;
		double valMax=1.5;
		for(int i=0; i<isTrainingSet.lastInstance().numValues()-1; i++){
			if(valMax<=isTrainingSet.lastInstance().value(i)){
				valMax=isTrainingSet.lastInstance().value(i);
				indexMax=i;
			}
		}
		Set<String>catRandom=new HashSet<String>();
		if(indexMax!=-1){
			valMax=0;
			for(int i=0; i<isTrainingSet.numInstances()-1; i++){
				if(valMax<isTrainingSet.instance(i).value(indexMax)){
					valMax=isTrainingSet.instance(i).value(indexMax);
					instanceMax=i;
					catRandom.clear();
					catRandom.add(isTrainingSet.instance(i).stringValue((isTrainingSet.instance(i).numValues())-1));
				}else if(valMax==isTrainingSet.instance(i).value(indexMax)){
					catRandom.add(isTrainingSet.instance(i).stringValue((isTrainingSet.instance(i).numValues())-1));
				}
			}
			String test="";
			Iterator<String>iterator=catRandom.iterator();
			while(iterator.hasNext()){
				test+=iterator.next()+" ";
			}
			System.out.println(test);
			Random random=new Random();
			int nb=random.nextInt(catRandom.size());
			//System.out.println("index "+indexMax+" valeur "+isTrainingSet.instance(instanceMax).value(indexMax)+" nbInstance "+isTrainingSet.numInstances()+" instance "+instanceMax+" "+isTrainingSet.instance(instanceMax).stringValue((isTrainingSet.instance(instanceMax).numValues())-1));
			String[] tab=catRandom.toArray(new String[catRandom.size()]);
			cat=tab[nb];
			System.out.println("result "+cat);
			//cat=isTrainingSet.instance(instanceMax).stringValue((isTrainingSet.instance(instanceMax).numValues())-1);
		}else{
			cat="";
			//cat="Autre"+item.getLangue();
		}
		return cat;
	}

	public void save() throws Exception{
		cModel.buildClassifier(isTrainingSet);
		weka.core.SerializationHelper.write(langue+".model", cModel);

		saver = new ArffSaver();
		saver.setInstances(isTrainingSet);
		saver.setFile(new File(langue+".arff"));
		saver.writeBatch();
	}

	public void load() throws Exception{
		cls = (Classifier) weka.core.SerializationHelper.read(langue+".model");
	}

	private HashMap<String, Integer> tableau(String traitementTit, String traitementDes){
		String[] tit=traitementTit.split(" ");
		String[] des=traitementDes.split(" ");
		listeItem=new HashMap<String, Integer>();
		for(int j=0; j<tit.length; j++){
			if(langue.equals("Fr")){
				if(!discriminant.existFr(tit[j])){
					if(parcours(tit[j])){
						listeItem.replace(tit[j], listeItem.get(tit[j])+1);
					}else{
						listeItem.put(tit[j], 1);
					}
				}else if(!discriminant.existEn(tit[j])){
					if(parcours(tit[j])){
						listeItem.replace(tit[j], listeItem.get(tit[j])+1);
					}else{
						listeItem.put(tit[j], 1);
					}
				}
			}
		}
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
