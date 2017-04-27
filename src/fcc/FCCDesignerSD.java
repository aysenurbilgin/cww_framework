/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fcc;

import fuzzylogic.generic.Resource;
import fuzzylogic.generic.Tuple;
import fuzzylogic.type1.core.TrapezoidMF;
import fuzzylogic.type2.lineargeneral.zSliceLGT2;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * FCCDesignerSD takes the files that have all the data and adapt the sets for a single dimension, e.g location
 * @author abilgin
 */
public class FCCDesignerSD implements Serializable {
    
    private FCCMFGenerator gen;
    private ArrayList<zSliceLGT2> leftShoulder;
    private ArrayList<zSliceLGT2> rightShoulder;
    private int indexN, numberM;
    private ArrayList<Map<Double, Resource>> leftorderedGranules;
    private ArrayList<Map<Double, Resource>> rightorderedGranules;
    private ArrayList<ArrayList<Double>> pGranule;
    private ArrayList<Integer> numberOfSlices;
    private int numberofconcepts;
    
    private ArrayList<TrapezoidMF> upperLeft;
    private ArrayList<TrapezoidMF> upperRight;
    private TrapezoidMF lowerLeft;
    private TrapezoidMF lowerRight;
    private ArrayList<String> headings;
    
    public FCCDesignerSD(ArrayList<String> fileList) {

        this.gen = new FCCMFGenerator();
        
        for (int i = 0; i<fileList.size(); i++) {
            gen.feedFile(fileList.get(i));
        }
        
        //find the index for the equations   
        numberM = gen.getNumberOfLabels();
        if (numberM % 2 == 1) {
            indexN = ( numberM - 1)/2;
        }
        else {
            indexN = numberM / 2;
        }
        
        this.leftorderedGranules = new ArrayList();
        this.rightorderedGranules = new ArrayList();
        
        this.pGranule = new ArrayList<ArrayList<Double>>();
        
        this.upperLeft = new ArrayList<TrapezoidMF>();
        this.upperRight = new ArrayList<TrapezoidMF>();
        
        this.leftShoulder = new ArrayList<zSliceLGT2>();
        this.rightShoulder = new ArrayList<zSliceLGT2>();
        
        organize();
        
    }

    
    public FCCDesignerSD(FCCDesignerSD singledim) {

        this.gen = new FCCMFGenerator(singledim.getMFGenerator());
        
        //find the index for the equations   
        this.numberM = singledim.getM();
        this.indexN = singledim.getN();
        
        this.leftorderedGranules = new ArrayList();
        this.rightorderedGranules = new ArrayList();
        
        this.leftorderedGranules = singledim.getLeftGranules();
        this.rightorderedGranules = singledim.getRightGranules();
        
        this.pGranule = new ArrayList<ArrayList<Double>>();
        
        this.pGranule = singledim.getPGranules();
        
        this.upperLeft = new ArrayList<TrapezoidMF>();
        this.upperRight = new ArrayList<TrapezoidMF>();
        
        this.upperLeft = singledim.getUpperLeft();
        this.upperRight = singledim.getUpperRight();
        this.lowerLeft = singledim.getLowerLeft();
        this.lowerRight = singledim.getLowerRight();
        
        this.leftShoulder = new ArrayList<zSliceLGT2>();
        this.rightShoulder = new ArrayList<zSliceLGT2>();
        
        this.leftShoulder = singledim.getLeftShoulder();
        this.rightShoulder = singledim.getRightShoulder();
        
        this.headings = new ArrayList<String>();
        this.headings = singledim.getConceptNames();
        
        this.numberOfSlices = new ArrayList<Integer>();
        this.numberOfSlices = singledim.getNumberOfSlices();
        
        this.numberofconcepts = singledim.getConceptNames().size();
        
    }
    

    public int getN() {
        return this.indexN;
    }
    
    public int getM() {
        return this.numberM;
    }
    
    public FCCMFGenerator getMFGenerator() {
        return this.gen;
    }
    
    public ArrayList<zSliceLGT2> getLeftShoulder() {
        return this.leftShoulder;
    }

    public ArrayList<zSliceLGT2> getRightShoulder() {
        return this.rightShoulder;
    }
    
    public ArrayList<Map<Double, Resource>> getLeftGranules() {
        return this.leftorderedGranules;
    }
    
    public ArrayList<Map<Double, Resource>> getRightGranules() {
        return this.rightorderedGranules;
    }
    
    public ArrayList<Integer> getNumberOfSlices() {
        return this.numberOfSlices;
    }

    public ArrayList<ArrayList<Double>> getPGranules() {
        return this.pGranule;
    }
    
    public ArrayList<Tuple> getSupport() {
        return gen.getSupport();
    }
    
    public ArrayList<String> getConceptNames() {
        return this.headings;
    }
    
    public TrapezoidMF getLowerLeft() {
        return lowerLeft;
    }

    public TrapezoidMF getLowerRight() {
        return lowerRight;
    }

    public ArrayList<TrapezoidMF> getUpperLeft() {
        return upperLeft;
    }

    public ArrayList<TrapezoidMF> getUpperRight() {
        return upperRight;
    }
    
    /**
     * Method orders the base transition points according to the most prevailing ones
     */
    private void organize() {
        
        Map<String, FCCResource> original;
        ArrayList<Integer> noslices = new ArrayList<Integer>();
        
        original = gen.getResources();
        
        
//        System.out.println("FCCDesignerSD: Original resources: "+original.toString());
        
        int counter = 0;
        
        //for each variable that makes up the fcc resource
        for (Map.Entry<String, FCCResource> variable : original.entrySet()) {
            
            ArrayList<Resource> varresources = variable.getValue().getResources();
            ArrayList<Double> pGranuletemp = new ArrayList<Double>();
            for (int i = 0; i < varresources.size(); i++) {
                //pGranuletemp.add(varresources.get(i).getPrevailingValue());
                pGranuletemp.add(varresources.get(i).getWeightedAvg());
            }

            //set the type-1 membership function
            //trapezoidal points for the left and right shoulder mfs
            TrapezoidMF upperLefttemp = new TrapezoidMF(setPointsLeft(variable.getValue().getSupport(), pGranuletemp));   
            TrapezoidMF upperRighttemp = new TrapezoidMF(setPointsRight(variable.getValue().getSupport(), pGranuletemp));
            //single user - no FOU so upper = lower
            lowerLeft = new TrapezoidMF(upperLefttemp);
            lowerRight = new TrapezoidMF(upperRighttemp);

            //System.out.println("FCCDesignerSD: Before calculate slices pGranuletemp "+pGranuletemp);
            //in order to find the greatest common divisor to calculate the number of zSlices
            noslices.add(calculateSlices(counter,pGranuletemp));

            //create for the left shoulder mf
            //partition the most prevailing values and the Resources for the left
            Map<Double, Resource> leftorderedGranulestemp = new TreeMap<Double, Resource>();

            for ( int i = 0; i < varresources.size(); i++) {
                leftorderedGranulestemp.put(varresources.get(i).getWeightedAvg(), varresources.get(i));
                
                if (varresources.get(i).getModifier().equalsIgnoreCase("")) {
                    break;
                }
            }
            
            leftorderedGranulestemp.put(upperLefttemp.getEnd(), null);
            Collections.reverse(pGranuletemp);

            Map<Double, Resource> rightorderedGranulestemp = new TreeMap<Double, Resource>();

            for (int i = 0; i < pGranuletemp.size(); i++) {
                rightorderedGranulestemp.put(pGranuletemp.get(i), varresources.get(pGranuletemp.size()-i-1));
                
                if (varresources.get(pGranuletemp.size()-1-i).getModifier().equalsIgnoreCase("")) {
                    break;
                }
            }
            
            rightorderedGranulestemp.put(upperRighttemp.getStart(), null);

            counter++;
            
            leftorderedGranules.add(leftorderedGranulestemp);
            rightorderedGranules.add(rightorderedGranulestemp);
            
            //populate pGranules with regard to most prevailing ones..
            ArrayList<Double> pgt = new ArrayList<Double>();
            for (Double d : leftorderedGranulestemp.keySet()) {
                pgt.add(d);
            }
            for (Double d : rightorderedGranulestemp.keySet()) {
                pgt.add(d);
            }
            pGranule.add(pgt);
            
            upperLeft.add(upperLefttemp);
            upperRight.add(upperRighttemp);
        }
        
        //check the number of slices just in case
        int check = noslices.get(0);
        
        for (int i = 0; i < noslices.size(); i++) {
            if (noslices.get(i) != check) {
                break;
            }
            else {
                check = noslices.get(i);
            }
        }

        this.numberOfSlices = new ArrayList<Integer>();
        this.numberOfSlices = noslices;
        this.numberofconcepts = counter;
        this.headings = gen.getHeadings();

    }
    
    private double[] setPointsLeft(Tuple support, ArrayList<Double> granules) {
        
        double als, bls, cls, dls;
       
        //for the left shoulder membership function
        //UKCI paper Eqn 11-13
        als = support.getLeft();
        bls = als;
        cls = granules.get(indexN-1);
        if (numberM - indexN < granules.size()) {
            dls = granules.get(numberM - indexN);
        }
        else {
            dls = granules.get(granules.size()-1);
        }

        return new double[]{als, bls, cls, dls};
   
    }
    
    private double[] setPointsRight(Tuple support, ArrayList<Double> granules) {
        
        double ars, brs, crs, drs;
        
        //for the right shoulder membership function
        ars = granules.get(indexN-1);
        if (numberM - indexN < granules.size()) {
            brs = granules.get(numberM - indexN);
        }
        else {
            brs = granules.get(granules.size()-1);
        }
        crs = support.getRight();
        drs = crs;
        
        return new double[]{ars, brs, crs, drs};
        
    }
    
    private int calculateSlices(Integer counter, ArrayList<Double> pGranule) {
        
        //first have to find the pGranules going to the third dimension
        ArrayList<Double> secondaryDegrees = new ArrayList<Double>();
        DecimalFormat df = new DecimalFormat("#.#####");
        double tempp;
        
        //for the left shoulder membership function from U(s) to pGranule(N)
        //'extremely' is a special case
        double min = gen.getMin().get(counter);
        double supLeft = gen.getSupport().get(counter).getLeft();
        if (indexN >= pGranule.size()) {
            tempp = (pGranule.get(pGranule.size()-1) - min)/ (pGranule.get(pGranule.size()-1) - supLeft);
        }
        else {
            tempp = (pGranule.get(indexN) - min)/ (pGranule.get(indexN) - supLeft);
        }
        tempp = Math.round(tempp*100)/100.0d;
        secondaryDegrees.add(Double.parseDouble(df.format(tempp)));
        
        for (int i = 0; i < indexN; i++) {
            
            double temp = (pGranule.get(indexN - 1) - pGranule.get(i))/ (pGranule.get(indexN - 1) - supLeft);
            temp = Math.round(temp*100)/100.0d;
            temp = Double.parseDouble(df.format(temp));
            if (temp != 0) {
                secondaryDegrees.add(temp);
                
            }
            tempp = temp;
            
        }

        //for the right shoulder membership function from pGranule(M-N+1) to U(e)
        tempp = 0;
        double max = gen.getMax().get(counter);
        double supRight = gen.getSupport().get(counter).getRight();
        for (int i = numberM - indexN; i < pGranule.size(); i++) {
            
            double temp;
            if (i + 1 == pGranule.size()) {
                temp = (max - pGranule.get(numberM - indexN)) / (supRight - pGranule.get(numberM - indexN));
            }
            else {
                temp = (pGranule.get(i+1) - pGranule.get(numberM - indexN)) / (supRight - pGranule.get(numberM - indexN));
            }
            temp = Math.round(temp*100)/100.0d;
            temp = Double.parseDouble(df.format(temp));
            if (temp != 0) {
                secondaryDegrees.add(temp);
            }

            tempp = temp;
            
        }

        //find the gcd
        double result = secondaryDegrees.get(0) * 100;
        for(int i = 1; i < secondaryDegrees.size(); i++) {
            result = gcd(result, Double.parseDouble(df.format(secondaryDegrees.get(i) * 100)));
        }
        result = result/100.0d;

        //need to create zNo of zSlices
        int num = (int) (1/result);

        return num;
        
    }
    
    private static double gcd(double a, double b) {
        
        while (b > 0)
        {
            double temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    
    public void visualizeLeftShoulder() {
        
        for (int i = 0; i < this.numberofconcepts; i++) {
            this.leftShoulder.get(i).visualizeSets(leftShoulder.get(i).getLinguisticLabel(), "single"+i);
        }
    }

    public void visualizeRightShoulder() {
        
        for (int i = 0; i < this.numberofconcepts; i++) {
            this.rightShoulder.get(i).visualizeSets(rightShoulder.get(i).getLinguisticLabel(), "single"+i);
        }
    }
    
    public void visualizeLV() {
        visualizeLeftShoulder();
        visualizeRightShoulder();
    }
    
    
    public void reorganizeToAdapt(String variableName, double newvalue) {
        
        this.gen.adaptToNewExperience(variableName, newvalue);
        
        //find the index for the equations   
        numberM = gen.getNumberOfLabels();
        if (numberM % 2 == 1) {
            indexN = ( numberM - 1)/2;
        }
        else {
            indexN = numberM / 2;
        }
        
        this.leftorderedGranules = new ArrayList();
        this.rightorderedGranules = new ArrayList();
        
        this.pGranule = new ArrayList<ArrayList<Double>>();
        
        this.upperLeft = new ArrayList<TrapezoidMF>();
        this.upperRight = new ArrayList<TrapezoidMF>();
        
        organize();
    }

    
}