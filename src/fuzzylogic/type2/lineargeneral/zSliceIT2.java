/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type2.lineargeneral;

import fuzzylogic.generic.*;
import fuzzylogic.type1.T1MF_Trapezoidal;
import fuzzylogic.type1.core.T1Interface;
import fuzzylogic.type1.core.TrapezoidMF;
import fuzzylogic.type2.core.MembershipFunction;
import fuzzylogic.type2.interval.IntervalT2MF_Trapezoidal;
import fuzzylogic.type2.zSlices.AgreementEngine;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author abilgin
 */
public class zSliceIT2 extends MembershipFunction  {

    private String linguisticLabel;
    private double confidenceAngle;
    private Map<Double, Resource> orderedPoints;
    
    //type1 membership functions
    private TrapezoidMF upper;
    private TrapezoidMF lower;

    //interval type-2 set array for the number of linguistic modifiers
    private IntervalT2MF_Trapezoidal[] intervalSets;

    //agreement zSlices for the number of linguistic modifiers
    private AgreementMF_zMFs agreementSet;

    public zSliceIT2(String name, T1Interface l, T1Interface u, double teta) {
        this.linguisticLabel = name;
        this.confidenceAngle = teta;
    }

    public zSliceIT2 (String name, T1Interface low, T1Interface up) {
        
        this.linguisticLabel = name;
        this.confidenceAngle = 90;
        
    }
    
    public zSliceIT2(String name, Map<Double, Resource> database, TrapezoidMF mf, int num, String rightorleft) {
        
        this.linguisticLabel = name;
        this.confidenceAngle = 90;
        this.orderedPoints = new TreeMap();
        this.orderedPoints = database;
        this.upper = mf;
        this.lower = mf;
        
        createITMF(num, true, rightorleft);
        
    }
    
    public zSliceIT2(String name, Map<Double, Resource> database, TrapezoidMF lower, TrapezoidMF upper, int num, String rightorleft) {
        
        this.linguisticLabel = name;
        this.confidenceAngle = 90;
        this.orderedPoints = new TreeMap();
        this.orderedPoints = database;
        this.upper = upper;
        this.lower = lower;
        
        creatITMFwithFOU(num, true, rightorleft);
        
    }
    
    private void createITMF(int originalnumberOfSlices, boolean reduceComplexity, String rightorleft) {
        
        //set up the interval type-2 sets for the series of uMeasures selected
        //first - set how many sets we will have - note that we do this for agreement purposes - u can just use one for now
        int numberOfSlices, divisor = 10;
        if (reduceComplexity) {
            numberOfSlices = Math.round(originalnumberOfSlices / divisor);
        }
        else {
            numberOfSlices = originalnumberOfSlices;
        }
        intervalSets = new IntervalT2MF_Trapezoidal[numberOfSlices];
        double start = this.upper.getStart();
        double iter = (this.upper.getEnd() - start)/(numberOfSlices+1);
        double currentpoint = start;
        int modCount = 0;
        
        ArrayList<Double> pGranule = new ArrayList<Double>(orderedPoints.keySet());

        if (rightorleft.equalsIgnoreCase("left")) {
            int i = 0;
            do {

                T1MF_Trapezoidal memfunct = new T1MF_Trapezoidal("Ex", new double[]{start, start, currentpoint, this.upper.getEnd()});
                intervalSets[i] = new IntervalT2MF_Trapezoidal("Ex_" + this.linguisticLabel + "_L_"+(i+1), memfunct, memfunct);
                i++;
                currentpoint += iter;

            } while (currentpoint <= pGranule.get(modCount));

            modCount++;

            for (int j = i; j < numberOfSlices; j++) {

                T1MF_Trapezoidal memfunct;
                if (this.orderedPoints.get(pGranule.get(modCount)) != null) {    
                    memfunct = new T1MF_Trapezoidal(this.orderedPoints.get(pGranule.get(modCount)).getModifier(), new double[]{start, start, currentpoint, this.upper.getEnd()});
                    intervalSets[j] = new IntervalT2MF_Trapezoidal(this.orderedPoints.get(pGranule.get(modCount)).getModifier() + "_" + this.linguisticLabel + "_Level_"+(j+1), memfunct, memfunct);    
                }
                else {      
                    memfunct = new T1MF_Trapezoidal("", new double[]{start, start, currentpoint, this.upper.getEnd()});
                    intervalSets[j] = new IntervalT2MF_Trapezoidal("" + this.linguisticLabel + "_L_"+(j+1), memfunct, memfunct);     
                }
                if (pGranule.get(modCount) - currentpoint < iter) {     
                    modCount++;
                }       
                currentpoint += iter;
            }
        }
        //for the right mf
        else {
            double end = this.upper.getEnd();
            int j;
            for (j = 0; j < numberOfSlices; j++) {

                T1MF_Trapezoidal memfunct;
                currentpoint += iter;
                if (modCount+1 == pGranule.size()) {
                    //maximum is reached so go to extremely case
                    break;
                }
                 
                if (this.orderedPoints.get(pGranule.get(modCount)) != null) {    
                    memfunct = new T1MF_Trapezoidal(this.orderedPoints.get(pGranule.get(modCount)).getModifier(), new double[]{start, currentpoint, end, end});
                    intervalSets[j] = new IntervalT2MF_Trapezoidal(this.orderedPoints.get(pGranule.get(modCount)).getModifier() + "_" + this.linguisticLabel + "_L_"+(j+1), memfunct, memfunct);    
                }
                else {      
                    memfunct = new T1MF_Trapezoidal("", new double[]{start, currentpoint, end, end});
                    intervalSets[j] = new IntervalT2MF_Trapezoidal("" + this.linguisticLabel + "_L_"+(j+1), memfunct, memfunct);     
                }
                
                if (pGranule.get(modCount+1) < currentpoint + iter) {     
                    modCount++;
                }
                
            }
            
            while (currentpoint <= end && j < numberOfSlices) {

                T1MF_Trapezoidal memfunct = new T1MF_Trapezoidal("Ex", new double[]{start, currentpoint, end, end});
                intervalSets[j] = new IntervalT2MF_Trapezoidal("Ex_" + this.linguisticLabel + "_L_"+(j+1), memfunct, memfunct);
                j++;
                currentpoint += iter;
            }

        }

        AgreementEngine aE = new AgreementEngine();
        agreementSet = aE.findAgreement(this.linguisticLabel, intervalSets);

    }
    
    private void creatITMFwithFOU(int originalnumberOfSlices, boolean reduceComplexity, String rightorleft) {

        int numberOfSlices, divisor = 10;
        if (reduceComplexity) {
            numberOfSlices = Math.round(originalnumberOfSlices / divisor);
        }
        else {
            numberOfSlices = originalnumberOfSlices;
        }
        intervalSets = new IntervalT2MF_Trapezoidal[numberOfSlices];
        double startup = this.upper.getStart();
        double iter = (this.upper.getEnd() - startup)/(numberOfSlices+1);
        System.out.println("Value of iter : "+iter);
        double currentpoint = startup;
        int modCount = 0;
        
        ArrayList<Double> pGranule = new ArrayList<Double>(orderedPoints.keySet());

        T1MF_Trapezoidal memfunctupper = new T1MF_Trapezoidal("Ex", this.upper.getPoints());
        
        if (rightorleft.equalsIgnoreCase("left")) {
            int i = 0;
            
            do {
                T1MF_Trapezoidal memfunctlower = new T1MF_Trapezoidal("Ex", this.lower.getPoints());
                intervalSets[i] = new IntervalT2MF_Trapezoidal("Ex_" + this.linguisticLabel + "_L_"+(i+1), memfunctlower, memfunctupper);
                i++;
                currentpoint += iter;

            } while (currentpoint <= pGranule.get(modCount));

            modCount++;

            for (int j = i; j < numberOfSlices; j++) {

                if (this.orderedPoints.get(pGranule.get(modCount)) != null) {    
                    T1MF_Trapezoidal memfunctlower = new T1MF_Trapezoidal(this.orderedPoints.get(pGranule.get(modCount)).getModifier(), this.lower.getPoints());
                    intervalSets[j] = new IntervalT2MF_Trapezoidal(this.orderedPoints.get(pGranule.get(modCount)).getModifier() + "_" + this.linguisticLabel + "_Level_"+(j+1), memfunctlower, memfunctupper);    
                }
                else {      
                    T1MF_Trapezoidal memfunctlower = new T1MF_Trapezoidal("",this.lower.getPoints());
                    intervalSets[j] = new IntervalT2MF_Trapezoidal("" + this.linguisticLabel + "_L_"+(j+1), memfunctlower, memfunctupper);     
                }
                if (pGranule.get(modCount) - currentpoint < iter) {     
                    modCount++;
                }       
                currentpoint += iter;
            }
        }
        //for the right mf
        else {
            double end = this.upper.getEnd();
            int j;
            double startlo = this.lower.getStart();
            for (j = 0; j < numberOfSlices; j++) {

                currentpoint += iter;
                if (modCount+1 == pGranule.size()) {
                    //maximum is reached so go to extremely case
                    break;
                }
                 
                if (this.orderedPoints.get(pGranule.get(modCount)) != null) {    
                    T1MF_Trapezoidal memfunctlower = new T1MF_Trapezoidal(this.orderedPoints.get(pGranule.get(modCount)).getModifier(), this.lower.getPoints());
                    intervalSets[j] = new IntervalT2MF_Trapezoidal(this.orderedPoints.get(pGranule.get(modCount)).getModifier() + "_" + this.linguisticLabel + "_L_"+(j+1), memfunctlower, memfunctupper);    
                }
                else {      
                    T1MF_Trapezoidal memfunctlower = new T1MF_Trapezoidal("", this.lower.getPoints());
                    intervalSets[j] = new IntervalT2MF_Trapezoidal("" + this.linguisticLabel + "_L_"+(j+1), memfunctlower, memfunctupper);     
                }
                
                if (pGranule.get(modCount+1) < currentpoint + iter) {     
                    modCount++;
                }      
                
            }
            
            while (currentpoint <= end && j < numberOfSlices) {

                T1MF_Trapezoidal memfunctlower = new T1MF_Trapezoidal("Ex", this.lower.getPoints());
                intervalSets[j] = new IntervalT2MF_Trapezoidal("Ex_" + this.linguisticLabel + "_L_"+(j+1), memfunctlower, memfunctupper);
                j++;
                currentpoint += iter;
            }
        }

        AgreementEngine aE = new AgreementEngine();
        agreementSet = aE.findAgreement(this.linguisticLabel, intervalSets);

    }

    @Override
    public Tuple getSupport() {
        return upper.getSupport();
    }

    @Override
    public String getLinguisticLabel() {
        return linguisticLabel;
    }

    @Override
    public void setLinguisticLabel(String lbl) {
        this.linguisticLabel = lbl;
    }

    @Override
    public double getUpper(double x) {
        return upper.getMembershipDegree(x);
    }

    @Override
    public double getLower(double x) {
        return lower.getMembershipDegree(x);
    }
    
    @Override
    public double getConfidenceAngle() {
        return confidenceAngle;
    }

    @Override
    public void setConfidenceAngle(double teta) {
        this.confidenceAngle = teta;
    }
    
    /*
     * Function getMembershipInterval returns the interval FOU for a x value
     */
    @Override
    public Tuple getMembershipInterval(double x) {
        return new Tuple(this.getLower(x), this.getUpper(x));
    }
    
    /*
     * Function getSlice returns the slice for a singleton or a nonsingleton input
     */
    @Override
    public Slice getSlice(Object o) {

        if (o instanceof Double) {
            return getVerticalSlice((Double) o);
        }
        else {
            return getTiltedSlice((T1Interface) o);
        }
    }

    private VerticalSlice getVerticalSlice(double x) {
        return new VerticalSlice(this.getLower(x), this.getUpper(x), x);   
    }

    private TiltedSlice getTiltedSlice(T1Interface nsinput) {
        return new TiltedSlice(this.lower.getMembershipDegree(nsinput), this.upper.getMembershipDegree(nsinput));
    }

    @Override
    public double getSecondaryDegree(Slice s) {

        if (s instanceof VerticalSlice) {
            return getVSecondaryDegree((VerticalSlice) s);
        }
        else {
            return getTSecondaryDegree((TiltedSlice) s);
        }

    }

    /* Function getSecondaryDegree calculates the third dimension
     * The angle teta determines the slope of the xz plane wrt xy plane in degrees
     * There are two steps: 1-calculate amplitude on xz where y = (x-b)/(c-b)
     * 2-calculate the projection on xz where proj = y*sin(teta*PI/180) - includes conversion to radians
     */
    private double getVSecondaryDegree(VerticalSlice vs) {

        double y = 0;
        double x = vs.getX();

        if ((Double.compare(vs.getInterval().getLeft(), vs.getInterval().getRight()) == 0) && vs.getInterval().getAverage() == 0.0) {
            return y;
        }
        //step1
        //for the same upper and lower membership degrees, we need to differentiate linearly
        if ((Double.compare(vs.getInterval().getLeft(), vs.getInterval().getRight()) == 0)) {

            if (this.upper.isLeftShoulder() && this.lower.isLeftShoulder()) {
                //descending linear
                y = (this.upper.getPoints()[2] - x)/(this.upper.getPoints()[2] - this.upper.getPoints()[1]);
            }
            else if (this.upper.isRightShoulder() && this.lower.isRightShoulder()) {
                //ascending linear
                y = (x - this.upper.getPoints()[1])/(this.upper.getPoints()[2] - this.upper.getPoints()[1]);
            }
        }
        else {
            //the upper and lower membership degrees are different
            //we are dealing with the FOU so the vertical slice is either a perpendicular triangle (right for right shoulder and left for left shoulder)
            //or a trapezoid - cut off head of the triangle as above, same conditions still apply for right and left
            //we want to find the center of gravity of the vertical slice, namely the centroid, for secondary degree
            //find the base of the triangle and the upper base of the trapezoid depending on the boundaries of the UMF and LMF
            double upperbase = 0, lowerbase = 0;
            
            if (this.upper.isLeftShoulder() && this.lower.isLeftShoulder()) {
                //descending linear
                
                double horizontalFOU = this.upper.getPoints()[2] - this.lower.getPoints()[2];
                
                //calculate lowerbase of both triangle and trapezoid
                lowerbase = horizontalFOU/(this.upper.getPoints()[2] - this.upper.getPoints()[1]);
                        
                //check the boundaries of UMF and LMF in order to calculate upperbase
                if (x < this.upper.getPoints()[2] && x > this.lower.getPoints()[2]) {
                    upperbase = ((this.upper.getPoints()[2] - x)/horizontalFOU) * lowerbase;
                }
                
                y = vs.calculateCentroid(lowerbase, upperbase);
                //System.out.println("Within FOU Left shoulder centroid y value : " + y);

            }
            else if (this.upper.isRightShoulder() && this.lower.isRightShoulder()) {
                //ascending linear
                
                double horizontalFOU = this.lower.getPoints()[1] - this.upper.getPoints()[1];
                
                //calculate lowerbase of both triangle and trapezoid
                lowerbase = horizontalFOU/(this.upper.getPoints()[2] - this.upper.getPoints()[1]);
                        
                //check the boundaries of UMF and LMF in order to calculate upperbase
                if (x > this.upper.getPoints()[1] && x < this.lower.getPoints()[1]) {
                    upperbase = ((x - this.upper.getPoints()[1])/horizontalFOU)* lowerbase;
                }
                
                y = vs.calculateCentroid(lowerbase, upperbase);
                //System.out.println("Within FOU Right shoulder centroid y value : " + y);
            }   
        }

        //step2
        return y*Math.sin(confidenceAngle*Math.PI/180);
        
    }  

    private double getTSecondaryDegree(TiltedSlice ts) {
        
        double z = 0;
        double lowerx = ts.getLowerX();
        double lowery = ts.getLowerY();
        double upperx = ts.getUpperX();
        double uppery = ts.getUpperY();
        
        if ((Double.compare(lowery, uppery) == 0.0) && uppery == 0) {
            return z;
        }
        //step1
        //for the same upper and lower membership degrees, we need to differentiate linearly
        if ((Double.compare(lowerx, upperx) == 0) && (Double.compare(lowery, uppery) == 0.0)) {

            if (this.upper.isLeftShoulder() && this.lower.isLeftShoulder()) {
                //descending linear
                z = (this.upper.getPoints()[2] - lowerx)/(this.upper.getPoints()[2] - this.upper.getPoints()[1]);
            }
            else if (this.upper.isRightShoulder() && this.lower.isRightShoulder()) {
                //ascending linear
                z = (lowerx - this.upper.getPoints()[1])/(this.upper.getPoints()[2] - this.upper.getPoints()[1]);
            }       
        }
        else {
            //the upper and lower membership degrees are different
            //we are dealing with the FOU so the tilted slice is either a perpendicular triangle (right for right shoulder and left for left shoulder)
            //or a trapezoid - cut off head of the triangle as above, same conditions still apply for right and left
            //we want to find the center of gravity of the tilted slice, namely the centroid, for secondary degree
            //find the base of the triangle and the upper base of the trapezoid depending on the boundaries of the UMF and LMF
            //for tilted slice the values of points are changing so we need the distance between two points to carry the calculations
            double upperbase = 0, lowerbase = 0;
            
            if (this.upper.isLeftShoulder() && this.lower.isLeftShoulder()) {
                //descending linear
                
                double horizontalFOU = this.upper.getPoints()[2] - this.lower.getPoints()[2];
                
                //calculate lowerbase of both triangle and trapezoid
                lowerbase = horizontalFOU/(this.upper.getPoints()[2] - this.upper.getPoints()[1]);
                        
                //check the boundaries of UMF and LMF in order to calculate upperbase
                if (upperx < this.upper.getPoints()[2] && upperx > this.lower.getPoints()[2]) {
                    upperbase = ((this.upper.getPoints()[2] - upperx)/horizontalFOU) * lowerbase;
                }
                
                z = ts.calculateCentroid(lowerbase, upperbase);
                System.out.println("Within FOU Left shoulder centroid z value : " + z);

            }
            else if (this.upper.isRightShoulder() && this.lower.isRightShoulder()) {
                //ascending linear
                
                double horizontalFOU = this.lower.getPoints()[1] - this.upper.getPoints()[1];
                
                //calculate lowerbase of both triangle and trapezoid
                lowerbase = horizontalFOU/(this.upper.getPoints()[2] - this.upper.getPoints()[1]);
                        
                //check the boundaries of UMF and LMF in order to calculate upperbase
                if (upperx > this.upper.getPoints()[1] && upperx < this.lower.getPoints()[1]) {
                    upperbase = ((upperx - this.upper.getPoints()[1])/horizontalFOU)* lowerbase;
                }
                
                z = ts.calculateCentroid(lowerbase, upperbase);
                System.out.println("Within FOU Right shoulder centroid z value : " + z);
            }
            
        }

        //step2
        return z*Math.sin(confidenceAngle*Math.PI/180);
        
    }
    
    public AgreementMF_zMFs getAgreementSets()
    {
        return agreementSet;
    }

    public IntervalT2MF_Trapezoidal[][] getIntervalT2Sets()
    {
        return new IntervalT2MF_Trapezoidal[][]{intervalSets};
    }

}
