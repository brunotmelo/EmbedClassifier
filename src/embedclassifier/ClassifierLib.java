/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embedclassifier;

import ace.CommandLine;
import ace.datatypes.SegmentedClassification;
import static ace.xmlparsers.XMLDocumentParser.parseXMLDocument;
import jAudio.JAudioCommandLine;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno
 */
public class ClassifierLib {

    /**
     * @param args the command line arguments
     */
    
    
    /*receive audios to classify and the classifier name
    returns the strings*/
    /**
     * Classifies audios according to the classifier passed as the second argument.
     * 
     * @param audiosPaths the paths of the audios to be classified
     * @param classifierName the name of the classifier
     * @return audio classifications. the first indice is the audio, the second
     * the classifications
     */
    public static String[][] classifyMany(String[] audiosPaths,String classifierName){
        
        String[][] classifications;
        
        //extract features
        //the features will be extracted in: classifiers/"classifierName"/feature_values
        extractFeatures(audiosPaths, classifierName);
        
        //load selectedClassifier and classify with ace
        classify(classifierName);
        
        //extract infos from xml
        classifications = parseXml();
        
        
        
        return classifications;
    }
    
    
    /*  - will read classifications.xml and return the strings with the awnsers
        - it is a bidimensional array because every recording may have more than one classification.
            the first index will be the audio file.
        - awnsers will come in order
    */
    /**
     * will read classifications.xml and return the strings with the classifications
     * @return a bidimensional array with recordings in the first indice and classifications in the second
     */
    public static String[][] parseXml(){
        String[][] classification;
        
        Object[] awnsers;
        
        try{
            awnsers = (Object[]) parseXMLDocument("classifications.xml", "classifications_file");
            
            int nClassifications = awnsers.length;
            
            SegmentedClassification fAwnser;
            classification = new String[nClassifications][];
            
            for(int i = 0; i< nClassifications; i++){
                fAwnser = (SegmentedClassification)awnsers[i];
                //fAwnser.identifier returns the audio name;
                classification[i] = fAwnser.classifications;

            }
                    
            return classification;        
            
        }catch(Exception e){
            System.out.println("Error while parsing xml file");
            e.printStackTrace();
        }
        
        
        return null;
        
        
    }
    
    
    //TODO: print stack from errors
    public static void classify(String classifierName){
        //load taxonomy.xml and model.xml. from the desired clasifier
        //load featurevalues and featuredefinitions from audios to be classifier
        
        ArrayList<String> args = new ArrayList<>();
            

        args.add("-lfkey");
        args.add("feature_valuesFK.xml");
        args.add("-lfvec");
        args.add("feature_valuesFV.xml");
        //TODO: add option to test classifier hit rate, adding instance
        //args.add("-lmclas");
        //args.add("Instances.xml");
        args.add("-ltax");
        args.add("classifiers" + File.separatorChar + classifierName + File.separatorChar+"Taxonomy.xml");
        args.add("-classify");
        args.add("classifiers" +File.separatorChar + classifierName + File.separatorChar+ "classifier.model");
        args.add("-sres");
        args.add("classifications.xml");

            
        String[] argsArray = args.toArray(new String[args.size()]);
            
        try{
            new CommandLine(argsArray).processRequests();
        }catch(Exception ex){
            ex.printStackTrace();
            //Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    public static void extractFeatures(String[] audiosPaths, String classifierName){
        
        //loads jaudio-settings.xml
        //features.xml is also needed for jaudio to run
        File jsettings = new File("jaudio-settings.xml");
        String jspath = jsettings.getAbsolutePath();
        
        //prepares arguments array
        ArrayList<String> argsArray = new ArrayList();
        argsArray.add("-s");
        argsArray.add(jspath);
        argsArray.add("feature_values");
        for (int i=0; i<audiosPaths.length; i++){
            argsArray.add(audiosPaths[i]);
        }
        
        String[]args = argsArray.toArray(new String[(argsArray.size())]);

        try{
            //executes jAudio
            JAudioCommandLine.execute(args);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
