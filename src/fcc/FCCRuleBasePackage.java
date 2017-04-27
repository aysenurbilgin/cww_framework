/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fcc;

import fuzzylogic.inference.RuleAntecedent;
import fuzzylogic.inference.RuleAntecedentGroup;
import fuzzylogic.inference.RuleConsequent;
import fuzzylogic.inference.RuleConsequentGroup;
import fuzzylogic.type2.interval.IntervalT2MF_Interface;
import fuzzylogic.type2.interval.IntervalT2MF_Trapezoidal;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import gui.Utility;

import java.io.*;
import java.util.*;

/**
 *
 * @author abilgin
 */
public class FCCRuleBasePackage implements Serializable {

    private Map<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>> finalrulebase;
    
    public FCCRuleBasePackage() {
        this.finalrulebase = new TreeMap<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>>();
    }

    public FCCRuleBasePackage(FCCRuleBasePackage fccrb) {
        this.finalrulebase = new TreeMap<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>>();
        this.finalrulebase.putAll(fccrb.getRulebase());
    }
    
    public Map<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>> getRulebase() {
        return finalrulebase;
    }

    public String addRule(RuleAntecedentGroup newRAG, RuleConsequentGroup newRCG) {

        StringBuilder message;

        synchronized (finalrulebase) {
            ArrayList<RuleConsequentGroup> rcglist;
            rcglist = finalrulebase.get(newRAG);
            if (rcglist == null) {  
                rcglist = new ArrayList<RuleConsequentGroup>();
            }

            rcglist.add(newRCG);
            finalrulebase.put(newRAG, rcglist);
            
        }

        message = (new StringBuilder("ADDING NEW PREFERENCE..")).append(newRAG.toString()).append("; the ").append(newRCG.toString()).append(".");

        return message.toString();
    }

    //september 2013 adding overall time
    public void doLearning(double overalltime, ArrayList<AgreementMF_zMFs> feedbackSets, String compositeOutput, ArrayList<AgreementMF_zMFs[]> inputconceptSets, ArrayList<String> conceptNames, AgreementMF_zMFs outputConceptSet) {

        //create or update rule to reflect the current state
        //find out which input sets match the current environment
        int index_max_input = findAmatchRETRO(overalltime, inputconceptSets.get(2));
        String labelOverallTime = inputconceptSets.get(2)[index_max_input].getName();
        AgreementMF_zMFs overalltimeSet = findMF(labelOverallTime, inputconceptSets.get(2));

        //in fuzzycompositeconcept case the output is a string
        feedbackSets.add(overalltimeSet);

        RuleAntecedentGroup newRAG = new RuleAntecedentGroup();
        //for all input concepts
        for (int i = 0; i < feedbackSets.size(); i++) {
            RuleAntecedent ra = new RuleAntecedent();
            ra.setName(conceptNames.get(i));
            String comparison = feedbackSets.get(i).getName();
            ra.setLabel(comparison);
            ra.setMembershipFunction(feedbackSets.get(i));
            newRAG.setAntecedent(ra);
        }
        //outputs
        RuleConsequentGroup newRCG = new RuleConsequentGroup();
        RuleConsequent rc = new RuleConsequent();
        rc.setName("difficulty");

        String[] split = compositeOutput.toLowerCase().split(" ", 2);
        if (split.length > 1) {
            rc.setLabel(split[1].trim());
        }
        else {
            rc.setLabel(compositeOutput.toLowerCase());
        }

        rc.setMembershipFunction(outputConceptSet);
        newRCG.setConsequent(rc);

    }


    private AgreementMF_zMFs findMF(String str, AgreementMF_zMFs[] inputSets) {

        for (AgreementMF_zMFs fset : inputSets) {
//            System.out.println("set name : " + fset.getName() + " vs str: " + str);
            if (str.toLowerCase().contains(fset.getName())) {
//                System.out.println("Set name : " + fset.getName() + " and " + perception);
                return fset;
            }
        }

        return null;

    }
    
    public static int findAmatchRETRO(double valToEval, AgreementMF_zMFs[] sets) {
        
        ArrayList<Double> membershipAverages = new ArrayList<Double>();
        double avg;
        //for the input
        for (int f = 0; f < sets.length; f++) {

            //zSliced general type 2 upper and lower membership value
            double upVal = 0.0, lowVal = 0.0, zWeight = 0.0;

            for (int z = 0; z < sets[f].getNumberOfSlices(); z++) {
                double up, low, zval;
                IntervalT2MF_Interface slice = sets[f].getZSlice(z);
                //if the valToEval is out of bounds then return 1 for the extreme values
                if (valToEval <= slice.getDomain().getLeft() && (((IntervalT2MF_Trapezoidal)slice).getUMF().getA() == ((IntervalT2MF_Trapezoidal)slice).getUMF().getB()))  {
                    up = 1.0;
                    low = 1.0;
                }
                else if (valToEval >= slice.getDomain().getRight() && (((IntervalT2MF_Trapezoidal)slice).getUMF().getC() == ((IntervalT2MF_Trapezoidal)slice).getUMF().getD()))      {
                    up = 1.0;
                    low = 1.0;
                }
                else {
                    up = slice.getUpperBound(valToEval);
                    low = slice.getLowerBound(valToEval);
                }

                zval = sets[f].getZValue(z);
                upVal +=  up * zval;
                lowVal +=  low * zval;
                zWeight += zval;
                
            }

            upVal = upVal / zWeight;
            lowVal = lowVal / zWeight;
            avg = (upVal + lowVal)/2;
            membershipAverages.add(avg);

        }//end of each fuzzy set
        
        //get the max membership value into the rule
        double max = Collections.max(membershipAverages);
        int index_max_input = membershipAverages.indexOf(max); //index for the inputFunction giving the max value
     
        membershipAverages.clear(); 
        
        return index_max_input; 
  
    }
    
        
    public void writeRuleBasetoFile (String name) {

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        

            File files = new File(Utility.foldername);
            if (!files.exists()) {
                if (files.mkdirs()) {
                    System.out.println("FCCRULEBASEPACK: Multiple directories are created!");
                } else {
                    System.out.println("FCCRULEBASEPACK: Failed to create multiple directories!");
                }
            }

            String filename = Utility.foldername + name;

        System.out.println("FCCRULEBASEPACK: filename : " + filename);
        File f;
        f = new File(filename);

        if(f.exists())
        {
            f.delete();

            try
            {
                synchronized (f) {
                    f.createNewFile(); 
                }
                synchronized (f) {
                    fos = new FileOutputStream(f);
                }
                out = new ObjectOutputStream(fos);
                out.writeObject(finalrulebase);
                out.flush();
                
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            finally {
                try {
                    out.close();
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
            }// end try catch
            
        }//end existing file
 
    }
    
    public synchronized void resetRulebase() {

        this.finalrulebase = new TreeMap<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>>();
    }
    
        
    public boolean setupRuleBase(String name) {
        FileInputStream fis;
        ObjectInputStream in;

        finalrulebase = null;
        
        String filename = Utility.foldername + name;
        
        File files = new File(Utility.foldername);
        if (!files.exists()) {
            if (files.mkdirs()) {
                System.out.println("FCCRULEBASEPACK: Multiple directories are created!");
            } else {
                System.out.println("FCCRULEBASEPACK: Failed to create multiple directories!");
            }
        }

        File f;
        f = new File(filename); // local
        System.out.println("FCCRULEBASEPACK: write/read from to file " + filename);
        
        if(!f.exists())
        {
            try 
            {
                finalrulebase = new TreeMap();
                f.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            return false;
        }
        else {
            try {
                
                fis = new FileInputStream(f);
                in = new ObjectInputStream(fis);
                
                try
                {
                    finalrulebase = (TreeMap) in.readObject();
                }
                catch (ClassNotFoundException ex)
                {
                    ex.printStackTrace();
                }
                finally {
                    in.close();
                    fis.close();
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
            
            return true;
        }
        
    }
    
    public void populateRule(String[] inputLabels, String outputLabel, 
            ArrayList<AgreementMF_zMFs[]> inputconceptSets, ArrayList<String> conceptNames, String fccname) {


        RuleAntecedentGroup newRAG = new RuleAntecedentGroup();
        //for all input concepts
        for (int i = 0; i < inputconceptSets.size(); i++) {
            RuleAntecedent ra = new RuleAntecedent();
            ra.setName(conceptNames.get(i));
            ra.setLabel(inputLabels[i]);
            //find the membership function
            ra.setMembershipFunction(null);
            for (AgreementMF_zMFs set: inputconceptSets.get(i)) {
                if (set.getName().equalsIgnoreCase(inputLabels[i])) {
                    ra.setMembershipFunction(set);
                }
            }
            newRAG.setAntecedent(ra);

        }

        //outputs
        RuleConsequentGroup newRCG = new RuleConsequentGroup();
        RuleConsequent rc = new RuleConsequent();
        rc.setName(fccname);
        rc.setLabel(outputLabel);
        rc.setMembershipFunction(null);
        newRCG.setConsequent(rc);

    }
    
    @Override
    public String toString() {
        return "FCCRuleBasePackage{" + "finalrulebase=" + finalrulebase + '}';
    }

    public int getRulebaseSize() {
        
        int size = 0;
        for (RuleAntecedentGroup rag: finalrulebase.keySet()) {
            
            for (RuleConsequentGroup rcg : finalrulebase.get(rag)) {
                size++;
            }
        }
        System.out.println("FCCRULEBASEPACK : size of rulebase: " + size);
        return size;
        
    }

}
