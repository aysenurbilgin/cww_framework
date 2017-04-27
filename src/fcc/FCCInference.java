/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fcc;

import fuzzylogic.generic.Tuple;
import fuzzylogic.inference.Couple;
import fuzzylogic.inference.RuleAntecedentGroup;
import fuzzylogic.inference.RuleConsequentGroup;
import fuzzylogic.type2.interval.IntervalT2MF_Interface;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author abilgin
 */
public class FCCInference implements Serializable {

    private ArrayList<Tuple> fs;
    private double[] zVals;

    private ArrayList<String> positives;
    private ArrayList<String> negatives;
    
    private String type;
    
    Map<String, Double> outputwordprev = new TreeMap<String, Double>();
    
    private Random rand1, rand2, rand3;

    public FCCInference (ArrayList<AgreementMF_zMFs[]> inputFunctions, int num, ArrayList<String> positives, ArrayList<String> negatives, String type) {
      
        //randomness of the self evaluate function
        rand1 = new Random(29071985);
        rand2 = new Random(24111987);
        //rand3 = new Random(17021948);

        this.fs = new ArrayList<Tuple>();
        
        this.positives = new ArrayList<String>();
        this.positives = positives;
        this.negatives = new ArrayList<String>();
        this.negatives = negatives;

        this.type = type;
        int numberOfSlices = num;
        this.zVals = new double[numberOfSlices];

        this.zVals = inputFunctions.get(0)[0].getZValues();  //depends on the overallzSlice number and equal throughout the run

    }
        
    private Tuple getAdditivetNorm(ArrayList<Double> inputVals, RuleAntecedentGroup current, int zLevel) {

        Tuple firTemp = new Tuple(0,0);
        ArrayList<Tuple> firings = new ArrayList<Tuple>();
        ArrayList<Double> effects = new ArrayList<Double>();

        if (inputVals.size() != current.getSize()) {
        }
        else {

            for (int i = 0; i < current.getSize(); i++) {
                //double end = current.getAntecedent(i).getMembershipFunction().getDomain().getRight();
                //System.out.println(" The domain of " + current.getAntecedent(i).getMembershipFunction().getName() + " is " + current.getAntecedent(i).getMembershipFunction().getDomain().toString());
                //System.out.println(" The domain of the evaluated " + current.getAntecedent(i).getLabel() + " is " + current.getAntecedent(i).getMembershipFunction().getZSlice(zLevel).getDomain().toString() +  " and the value of input is " + inputVals.get(i));
                //handling the out of domain values as the shoulders should go beyond
                AgreementMF_zMFs membershipFunction = current.getAntecedent(i).getMembershipFunction();
                IntervalT2MF_Interface zSlice = membershipFunction.getZSlice(zLevel);
                if (membershipFunction.isLeftShoulder()) {
                    if (inputVals.get(i) <= zSlice.getDomain().getLeft()) {
                        firings.add(new Tuple(1.0, 1.0));
                    } else {
                        firings.add(zSlice.getFS(inputVals.get(i)));
                    }
                } else if (membershipFunction.isRightShoulder()) {
                    if (inputVals.get(i) >= zSlice.getDomain().getRight()) {
                        firings.add(new Tuple(1.0, 1.0));
                    } else {
                        firings.add(zSlice.getFS(inputVals.get(i)));
                    }
                }
                
                if (positives.contains(current.getAntecedent(i).getLabel()) || isPositive(current.getAntecedent(i).getLabel())) {
                    effects.add(1.0);
                }
                else if (negatives.contains(current.getAntecedent(i).getLabel()) || isNegative(current.getAntecedent(i).getLabel())) {
                    effects.add(-1.0);
                }
                else {
                    effects.add(0.0);
                }
            }

            double fl = 0.0, fr = 0.0;
            double sumeff = 0.0;
            
            //added september 2013 
            //if the rule is not firing no need to boost
            boolean shouldnotprocess = true;
            for (int i = 0; i < firings.size(); i++) {

                if (firings.get(i).getRight() == 0.0 && firings.get(i).getLeft() == 0.0) {
                    //do nothing
                    shouldnotprocess &= true;
                }
                else {
                    shouldnotprocess &= false;
                }
            }
            
            if (!shouldnotprocess) {
                for (int i = 0; i < firings.size(); i++) {

                    if (effects.get(i) == -1.0) {
                        //then get the 1-mu (complement of simple IT2)
                        fl += (1.0-firings.get(i).getRight());
                        fr += (1.0-firings.get(i).getLeft());
    //                    System.out.println("Negative deducting!");
                    }
                    else {
                        //SMC2013 paper because when sumeff = 1.0 then firing strength exceeds 1!
                        fl += firings.get(i).getLeft()*effects.get(i);
                        fr += firings.get(i).getRight()*effects.get(i);

                    }
                    sumeff += Math.abs(effects.get(i));

                }
            }
            
            //normalize for the sum of the effects
            if (sumeff != 0.0) {
                firTemp.setLeft(fl/sumeff);
                firTemp.setRight(fr/sumeff);
            }
            //added september 2013
            else {
                firTemp.setLeft(fl);
                firTemp.setRight(fr);
            }
        }

        return firTemp;
    }

    public Tuple newevaluateMO(ArrayList<Double> inputVals, Map<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>> rulebase) {

        ArrayList<Couple> data = new ArrayList<Couple>();
        ArrayList<Tuple> sliceList = new ArrayList<Tuple>();

        int countoffiredrules;
        
        //for each zSlice treat like an IT2 controller
        //apply the similar philosophy of LWA to the overall slices
        for (int z = 0; z < zVals.length; z++) {

            data.clear();
            fs.clear();
            Iterator iterator = rulebase.keySet().iterator();
            
            //for each rule
            while(iterator.hasNext()) {
                RuleAntecedentGroup current = (RuleAntecedentGroup) iterator.next();
                fs.add(getAdditivetNorm(inputVals, current, z));
            }
            
            //within the zSlice gather the output label and the firing strength
            //fs.size = outputList.size
            Tuple slicetotal = new Tuple();
            double sliceleft = 0.0, sliceright = 0.0;
            countoffiredrules = 0;
            for (int l = 0; l < fs.size(); l++) {
                if (!((fs.get(l).getLeft() == 0.0) && (fs.get(l).getRight() == 0.0))) {
                    countoffiredrules++;

                    sliceleft += fs.get(l).getLeft();
                    sliceright += fs.get(l).getRight();
                }
            }
            
            if (countoffiredrules != 0) {
                slicetotal.setLeft(sliceleft / (double) countoffiredrules);
                slicetotal.setRight(sliceright / (double) countoffiredrules);
            }
            
            sliceList.add(slicetotal);

        }//end of each zSlice

        //calculate the fuzzy firing strength output of all zSlices
        double nomleft = 0.0, nomright = 0.0, denom = 0.0;
        for (int i = 0; i < sliceList.size(); i++) {
            nomleft += sliceList.get(i).getLeft() * zVals[i];
            nomright += sliceList.get(i).getRight() * zVals[i];
            denom += zVals[i];

        }

        Tuple finalOutput = new Tuple(nomleft/denom, nomright/denom);

        return finalOutput;

    }//end evaluate
    
    public Map<String, Double> findFiringProportions(ArrayList<Double> inputVals, Map<RuleAntecedentGroup, ArrayList<RuleConsequentGroup>> rulebase) {

        ArrayList<Couple> data = new ArrayList<Couple>();
        Map<String, Double> outputwordnew = new TreeMap<String, Double>();
        
        //for each zSlice treat like an IT2 controller
        //apply the similar philosophy of LWA to the overall slices
        for (int z = 0; z < zVals.length; z++) {
            data.clear();
            fs.clear();
            Iterator iterator = rulebase.keySet().iterator();
            
            //for each rule
            while(iterator.hasNext()) {
                RuleAntecedentGroup current = (RuleAntecedentGroup) iterator.next();
                fs.add(getAdditivetNorm(inputVals, current, z));
            }
            
            //within the zSlice gather the output label and the firing strength
            //fs.size = outputList.size
            int countoffiredrules = 0;
            for (int l = 0; l < fs.size(); l++) {
                if (!((fs.get(l).getLeft() == 0.0) && (fs.get(l).getRight() == 0.0))) {
                    //System.out.print("If " +rulebase.keySet().toArray()[l].toString());
                    //System.out.println(" then "+rulebase.get(rulebase.keySet().toArray()[l]).toString()+" --> Fired! because fs: "+fs.toString());
                    for (RuleConsequentGroup rcg : rulebase.get(rulebase.keySet().toArray()[l])) {
                    countoffiredrules++;
                    String o = rcg.getConsequent(0).getLabel();
                    String[] split = o.split(" ", 2);
                    if (split.length > 1) {
                        if (!outputwordnew.containsKey(split[1])) {
                            outputwordnew.put(split[1], 0.0);
                        }
                        outputwordnew.put(split[1], outputwordnew.get(split[1]) + 1.0);
                    } else {
                        if (!outputwordnew.containsKey(o)) {
                            outputwordnew.put(o, 0.0);
                        }
                        outputwordnew.put(o, outputwordnew.get(o) + 1.0);
                    }
                    }
                }
            }
        }//end of each zSlice
        
        for (String s: outputwordnew.keySet()) {
            outputwordnew.put(s, outputwordnew.get(s)/zVals.length);
        }
        
        if (!outputwordnew.isEmpty()) {
            outputwordprev = outputwordnew;
        }
        
        return outputwordprev;

    }//end evaluate


    private boolean isPositive(String label) {
        
        for (String str: positives) {
            if (label.contains(str)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isNegative(String label) {
        
        for (String str: negatives) {
            if (label.contains(str)) {
                return true;
            }
        }
        
        return false;
    }
    
}
