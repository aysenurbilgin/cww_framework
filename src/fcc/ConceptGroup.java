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
import fuzzylogic.type2.lineargeneral.zSliceIT2;
import fuzzylogic.type2.lineargeneral.zSliceLGT2;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import gui.Utility;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author abilgin
 */
public class ConceptGroup {

    private ArrayList<FCCDesignerSD> dimensions; 
    private int indexN, numberM;
    private int numberOfSlices;
    private int numberofconcepts;
    
    private ArrayList<TrapezoidMF> aggupperLeft;
    private ArrayList<TrapezoidMF> aggupperRight;
    private ArrayList<TrapezoidMF> agglowerLeft;
    private ArrayList<TrapezoidMF> agglowerRight;
    
    private ArrayList<Map<Double, Resource>> leftorderedGranules;
    private ArrayList<Map<Double, Resource>> rightorderedGranules;
    
    private ArrayList<zSliceLGT2> leftShoulderZ;
    private ArrayList<zSliceLGT2> rightShoulderZ;
    
    private zSliceIT2 leftShoulderI;
    private zSliceIT2 rightShoulderI;

    private ArrayList<String> conceptNames;


    public ConceptGroup(ArrayList<FCCDesignerSD> dims) {

        this.dimensions = new ArrayList<FCCDesignerSD>();
        
        for (int i = 0; i < dims.size(); i++) {
            dimensions.add(dims.get(i));
        }

        //need to find a consensus on M and N values
        findConsensusForIndexesMN(dims);
        this.numberOfSlices = 5;                    //predefined slice num
        this.numberofconcepts = dims.get(0).getMFGenerator().getNumberofConcepts();
        
        aggupperLeft = new ArrayList<TrapezoidMF>();
        aggupperRight = new ArrayList<TrapezoidMF>();
        agglowerLeft = new ArrayList<TrapezoidMF>();
        agglowerRight = new ArrayList<TrapezoidMF>();
        
        this.leftorderedGranules = new ArrayList<Map<Double, Resource>>();
        this.rightorderedGranules = new ArrayList<Map<Double, Resource>>();
        
        leftShoulderZ = new ArrayList<zSliceLGT2>();
        rightShoulderZ = new ArrayList<zSliceLGT2>();
        
        for (int i = 0; i < numberofconcepts; i++) {
            aggregateSets(i);
        }
        
        this.conceptNames = new ArrayList<String>();
        this.conceptNames = dimensions.get(0).getConceptNames();
        
    }

    private void aggregateSets(int numberofconcept) {

        double cl, cu, dl, du;
        Tuple sup = findSupport(numberofconcept);
        //System.out.println("Support for the concept is " + sup.toString());
        double start = sup.getLeft();
        //for the right shoulder mf
        double end = sup.getRight();
        
        cu = Double.NEGATIVE_INFINITY;
        du = Double.NEGATIVE_INFINITY;
        cl = Double.POSITIVE_INFINITY;
        dl = Double.POSITIVE_INFINITY;
        
        int lastindexLeft = -1;
        int lastindexRight = -1;
        for (int j = 0; j < dimensions.size(); j++) {
            //for the primary mf find the points
            //UKCI 2012 paper Eqns 19-20 for upper

            double cinit, dinit;
            cinit = dimensions.get(j).getPGranules().get(numberofconcept).get(indexN - 1);
            int sizeofconcepts = dimensions.get(j).getPGranules().get(numberofconcept).size();
            if (numberM - indexN >= sizeofconcepts) {
                dinit = dimensions.get(j).getPGranules().get(numberofconcept).get(sizeofconcepts-1);
            }
            else {
                dinit = dimensions.get(j).getPGranules().get(numberofconcept).get(numberM - indexN);
            }
            
            if (cu <= cinit) {
                cu = cinit;
            }
            if (du <= dinit) {
                du = dinit;
                lastindexLeft = j;
            }
            
            //UKCI 2012 paper Eqns 21-22 for lower
            if (cl >= cinit) {
                cl = cinit;
                lastindexRight = j;
            }
            if (dl >= dinit) {
                dl = dinit;
            }
                        
        }// end for
        
        //UKCI 2012 paper Eqns 23-26
        if (cl == dl) {
            dl += (end - start)/10.0D;
        }
        if (cu == du) {
            du += (end - start)/10.0D;
        }
        this.agglowerLeft.add(new TrapezoidMF(start, start, cl, dl));
        this.aggupperLeft.add(new TrapezoidMF(start, start, cu, du));
        
        this.agglowerRight.add(new TrapezoidMF(cu, du, end, end));
        this.aggupperRight.add(new TrapezoidMF(cl, dl, end, end));


        //now aggregate the second dimensions
        //for the left
        int size = dimensions.get(0).getLeftGranules().get(numberofconcept).size();
        Map<Double, Resource> leftorderedGranulestemp = new TreeMap();

        //for the secondary mf also organize the granules
        for (int i = 0; i < size; i++) {
            double min = Double.POSITIVE_INFINITY;
            int minIndex = -1;
            ArrayList<Double> temp1;
            
            for (int j = 0; j < dimensions.size(); j++) {
                temp1 = new ArrayList<Double>(dimensions.get(j).getLeftGranules().get(numberofconcept).keySet());

                if (min >= temp1.get(i)) {
                    min = temp1.get(i);
                    minIndex = j;
                }   
            }
            
            leftorderedGranulestemp.put(min, dimensions.get(minIndex).getLeftGranules().get(numberofconcept).get(min));
        }//end for
        
        //for the right
        size = dimensions.get(0).getRightGranules().get(numberofconcept).size();
        Map<Double, Resource> rightorderedGranulestemp = new TreeMap();
        
        //for the secondary mf also organize the granules
        for (int i = 0; i < size; i++) {
            double max = Double.NEGATIVE_INFINITY;
            int maxIndex = -1;
            ArrayList<Double> temp1;
            for (int j = 0; j < dimensions.size(); j++) {
                
                temp1 = new ArrayList<Double>(dimensions.get(j).getRightGranules().get(numberofconcept).keySet());
                if (max < temp1.get(i)) {
                
                    max = temp1.get(i);
                    maxIndex = j;
                }
            }
            
            rightorderedGranulestemp.put(max, dimensions.get(maxIndex).getRightGranules().get(numberofconcept).get(max));
     
        }//end for
        
        this.leftorderedGranules.add(leftorderedGranulestemp);
        this.rightorderedGranules.add(rightorderedGranulestemp);
        
    }

    private Tuple findSupport(int num) {
        
        double min, max;
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
        
        for (FCCDesignerSD dim : this.dimensions) {
            
            double mininit = dim.getSupport().get(num).getLeft();
            double maxinit = dim.getSupport().get(num).getRight();
            //System.out.println("The support in conceptgroup: " + dim.getSupport().get(num).toString());
            if (mininit < min) {
                min = mininit;
            }
            if (maxinit > max) {
                max = maxinit;
            }
            
        }
        
        return new Tuple(min, max);
        
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
    
    public void formZsliceSets() {

        for (int i = 0; i < this.numberofconcepts; i++) {
//            System.out.println("Concept Group: Creating left for : "+ dimensions.get(0).getMFGenerator().getLinguistics(i).getLinguisticVariableName());
            this.leftShoulderZ.add(new zSliceLGT2(dimensions.get(0).getMFGenerator().getLinguistics(i).getNegPerception(), (Map<Object, Resource>) ((Object)leftorderedGranules.get(i)), agglowerLeft.get(i), aggupperLeft.get(i), numberOfSlices, "left", true));
//            System.out.println("Concept Group: Creating right for : "+ dimensions.get(0).getMFGenerator().getLinguistics(i).getLinguisticVariableName());
            this.rightShoulderZ.add(new zSliceLGT2(dimensions.get(0).getMFGenerator().getLinguistics(i).getPosPerception(), (Map<Object, Resource>) ((Object)rightorderedGranules.get(i)), agglowerRight.get(i), aggupperRight.get(i), numberOfSlices, "right", true));
        }
    }
    
    
    public ArrayList<AgreementMF_zMFs[]> getAgreementSets() {
    
        ArrayList<AgreementMF_zMFs[]> setscombined = new ArrayList<AgreementMF_zMFs[]>();
        
        for (int i = 0; i < this.numberofconcepts; i++) {
            setscombined.add(new AgreementMF_zMFs[]{getLeftShoulder(i), getRightShoulder(i)});
        }
        return setscombined;
            
    }
//
//    private void visualizeLeftShoulder() {
//
//        for (int i = 0; i < this.numberofconcepts; i++) {
//            if (!Utility.usingIntervalT2) {
//                this.leftShoulderZ.get(i).visualizeSets(leftShoulderZ.get(i).getLinguisticLabel(), this.conceptNames.get(i));
//            }
//            else {
//                this.leftShoulderI.visualizeSets(leftShoulderI.getLinguisticLabel(), "_interval");
//            }
//        }
//    }
//
//    private void visualizeRightShoulder() {
//
//        for (int i = 0; i < this.numberofconcepts; i++) {
//            if (!Utility.usingIntervalT2) {
//                this.rightShoulderZ.get(i).visualizeSets(rightShoulderZ.get(i).getLinguisticLabel(), this.conceptNames.get(i));
//            }
//            else {
//                this.rightShoulderI.visualizeSets(rightShoulderI.getLinguisticLabel(), "_interval");
//            }
//        }
//    }
    
    public AgreementMF_zMFs getRightShoulder(int conceptno) {
        
        if (Utility.usingIntervalT2) {
            return this.rightShoulderI.getAgreementSets();
        }
        else {
            return this.rightShoulderZ.get(conceptno).getAgreementSets();
        }
    }
    
    public AgreementMF_zMFs getLeftShoulder(int conceptno) {
        
        if (Utility.usingIntervalT2) {
            return this.leftShoulderI.getAgreementSets();
        }
        else {
            return this.leftShoulderZ.get(conceptno).getAgreementSets();
        }
    }
    
    
//    public void visualizeConcepts() {
//        visualizeLeftShoulder();
//        visualizeRightShoulder();
//    }

    public ArrayList<String> getConceptNames() {
        return this.conceptNames;
    }
    
    public int getNumberofSlices() {
        return this.leftShoulderZ.get(0).getRealNumberofSlices();
    }

    private void findConsensusForIndexesMN(ArrayList<FCCDesignerSD> dims) {
        
        //find minimum of the number of labels from dimensions and use the information
        //belonging to that dimension for the indexes M and N
        
        int min = Integer.MAX_VALUE;
        int indexDim = 0;
        for (int i = 0; i < dims.size(); i++) {
            if (min > dims.get(i).getM()) {
                min = dims.get(i).getM();
                indexDim = i;
            }
        }
        this.numberM = dims.get(indexDim).getM();
        this.indexN = dims.get(indexDim).getN();
        
    }

   
}