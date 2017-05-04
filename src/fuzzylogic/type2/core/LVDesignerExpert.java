/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type2.core;

import fuzzylogic.generic.Resource;
import fuzzylogic.generic.Tuple;
import fuzzylogic.type1.core.TrapezoidMF;
import fuzzylogic.type2.lineargeneral.zSliceLGT2;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author abilgin
 */
public class LVDesignerExpert {
    
    private Tuple support;                          //holds the support of the MF which tells the whole possible domain of the variable
    private String negPerception;                   //holds the negative perception which is to be modelled as left smf
    private String posPerception;                   //holds the positive perception which is to be modelled as right smf
    private zSliceLGT2 leftShoulder;
    private zSliceLGT2 rightShoulder;
    private Map<Tuple, Resource> leftorderedGranules;
    private Map<Tuple, Resource> rightorderedGranules;
    public static final int numberOfSlices = 5;
    
    private TrapezoidMF upperLeft;
    private TrapezoidMF upperRight;
    private TrapezoidMF lowerLeft;
    private TrapezoidMF lowerRight;
    
    private boolean reduceComplexity;
    
    public LVDesignerExpert(String leftLabel, String rightLabel, boolean rc, double leftpoint, double rightpoint) {
        
        this.support = new Tuple(0.0,1.0);  //concept variables will be normalized
        this.negPerception = leftLabel;
        this.posPerception = rightLabel;
        
        this.leftorderedGranules = new TreeMap<Tuple, Resource>();
        this.rightorderedGranules = new TreeMap<Tuple, Resource>();
        this.reduceComplexity = rc;
        
        organize(leftpoint, rightpoint);
        
    }
    
    public Tuple getSupport() {  
        return this.support;
    }
    
    public Map<Tuple, Resource> getLeftGranules() {
        return this.leftorderedGranules;
    }
    
    public Map<Tuple, Resource> getRightGranules() {
        return this.rightorderedGranules;
    }
    
    /**
     * Method orders the base transition points according to the most prevailing ones
     */
    private void organize(double left, double right) {
 
        System.out.println("LVDesignerExpert: Left : "+left+" and right : "+right);
        //set the type-1 membership function
        //trapezoidal points for the left and right shoulder mfs
        upperLeft = new TrapezoidMF(setPointsLeft(left, right));   
        upperRight = new TrapezoidMF(setPointsRight(left, right));
        double FOUwidth = 0.05;

        if (left < FOUwidth) {
            lowerLeft = new TrapezoidMF(setPointsLeft(left, right));
        }
        else {
            lowerLeft = new TrapezoidMF(setPointsLeft(left-FOUwidth, right-FOUwidth));
        }
        lowerRight = new TrapezoidMF(setPointsRight(left+FOUwidth, right+FOUwidth));

        double iterleft = left / (double)LVDesignerExpert.numberOfSlices;
        for (int i = 0; i < LVDesignerExpert.numberOfSlices; i++) {

            Resource res;
            if (i == 0) {
                res = new Resource("extremely "+negPerception);
            }
            else if ((i == 1) || (i == 2)) {
                res = new Resource("very "+negPerception);
            }
            else {
                res = new Resource(negPerception);
            }
            leftorderedGranules.put(new Tuple(i, iterleft*i), res);
   
        }
           
        double iterright = (1.0 - right) / (double)LVDesignerExpert.numberOfSlices;
        //create for the right shoulder mf
        //partition the most prevailing values and the resources for the right
        for (int i = 0; i < LVDesignerExpert.numberOfSlices; i++) {

            Resource res;
            if (i == LVDesignerExpert.numberOfSlices-1) {
                res = new Resource("extremely "+posPerception);
            }
            else if ((i == LVDesignerExpert.numberOfSlices-2) || (i == LVDesignerExpert.numberOfSlices-3)) {
                res = new Resource("very "+posPerception);
            }
            else {
                res = new Resource(posPerception);
            }
            rightorderedGranules.put(new Tuple(i,(left + (iterright*i))), res);
        }
    }
    
    private double[] setPointsLeft(double c, double d) {
        
        double als, bls, cls, dls;
       
        //for the left shoulder membership function
        //UKCI paper Eqn 11-13
        als = this.support.getLeft();
        bls = als;
        cls = c;
        dls = d;

        return new double[]{als, bls, cls, dls};
   
    }
    
    private double[] setPointsRight(double a, double b) {
        
        double ars, brs, crs, drs;
        
        //for the right shoulder membership function
        ars = a;
        brs = b;
        crs = this.support.getRight();
        drs = crs;
        
        return new double[]{ars, brs, crs, drs};
        
    }

    public void formZsliceSets() {
        this.leftShoulder = new zSliceLGT2(negPerception, (Map<Object, Resource>) ((Object)leftorderedGranules), lowerLeft, upperLeft, numberOfSlices, "left", reduceComplexity);
        this.rightShoulder = new zSliceLGT2(posPerception, (Map<Object, Resource>) ((Object)rightorderedGranules), lowerRight, upperRight, numberOfSlices, "right", reduceComplexity);
    }
    
    public int getNumberOfSlices() {
        return LVDesignerExpert.numberOfSlices;
    }

    public AgreementMF_zMFs getRightShoulder() {
       
        return this.rightShoulder.getAgreementSets();
    }
    
    public AgreementMF_zMFs getLeftShoulder() {
        
        return this.leftShoulder.getAgreementSets();
        
    }
    
    public AgreementMF_zMFs[] getFuzzySets() {
        return new AgreementMF_zMFs[]{getLeftShoulder(),getRightShoulder()};
    }
    
    public static int getNozSlices() {
        return LVDesignerExpert.numberOfSlices;
    }

    
}
