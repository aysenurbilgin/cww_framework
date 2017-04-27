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
import fuzzylogic.type2.core.EIAData;
import fuzzylogic.type2.core.MembershipFunction;
import fuzzylogic.type2.interval.IntervalT2MF_Trapezoidal;
import fuzzylogic.type2.zSlices.AgreementEngine;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import fuzzylogic.type2.zSlices.GenT2zUtility_FileWriter;
import gui.Utility;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author abilgin
 */
public class zSliceLGT2 extends MembershipFunction  {

    private String linguisticLabel;
    private double confidenceAngle;
    private Map<Object, Resource> orderedPoints;
    
    //type1 membership functions
    private TrapezoidMF upper;
    private TrapezoidMF lower;

    //interval type-2 set array for the number of linguistic modifiers
    private IntervalT2MF_Trapezoidal[] intervalSets;

    //hybrid version agreement zSlices for the number of linguistic modifiers
    private AgreementMF_zMFs agreementSet;
    //zValues of the agreement set
    private double[] zVals;
    
    //discretization for uncertainty measure
    private int N = 10000;
    
    //final number of slices for the created agreement set
    private int finalnumslice;
    private Tuple support;
    
    
    //********************************************* METHODS *********************************************
    
    /* Constructor for a linear general type 2 fuzzy set
     * Linear GT2V1 takes lower membership function, upper membership function and uncertainty slope
     */
    public zSliceLGT2(String name, Map<Object, Resource> database, TrapezoidMF mf, int num, String rightorleft) {

        this.linguisticLabel = name;
        this.confidenceAngle = 90;
        this.orderedPoints = new TreeMap();
        this.orderedPoints = database;
        this.upper = mf;
        this.lower = mf;

    }
    
    public zSliceLGT2(String name, Map<Object, Resource> database, TrapezoidMF lower, TrapezoidMF upper, int num, String rightorleft, boolean rc) {
        
        this.linguisticLabel = name;
        this.confidenceAngle = 90;
        this.orderedPoints = new TreeMap<Object, Resource>();
        this.orderedPoints = database;
        this.upper = upper;
        this.lower = lower;
        
        createZMFwithFOU(num, rc, rightorleft);
        
    }

    public zSliceLGT2(String name, EIAData allEIAData, int num, String rightorleft) {

        this.linguisticLabel = name;
        this.confidenceAngle = 90;
        createZMF_EIA(allEIAData, num, rightorleft);

    }

    private void createZMF_EIA(EIAData allEIAData, int numberOfSlices, String rightorleft) {

        this.finalnumslice = numberOfSlices;
        this.support = new Tuple(0.0,10.0);
        //get all data from allEIAData
        double cl0, dl0, cu2, du2, al5, bl5, au3, bu3;
        cl0 = allEIAData.getLowerMembershipFunction(0).getC();
        dl0 = allEIAData.getLowerMembershipFunction(0).getEnd();
        cu2 = allEIAData.getUpperMembershipFunction(2).getC();
        du2 = allEIAData.getUpperMembershipFunction(2).getEnd();
        al5 = allEIAData.getLowerMembershipFunction(5).getStart();
        bl5 = allEIAData.getLowerMembershipFunction(5).getB();
        au3 = allEIAData.getUpperMembershipFunction(3).getStart();
        bu3 = allEIAData.getUpperMembershipFunction(3).getB();
        //set the type-1 membership function
        //trapezoidal points for the left and right shoulder mfs
//        TrapezoidMF upperLeft = new TrapezoidMF(new double[]{this.support.getLeft(), this.support.getLeft(), cu2, du2});
//        TrapezoidMF upperRight = new TrapezoidMF(new double[]{au3, bu3, this.support.getRight(), this.support.getRight()});
//        TrapezoidMF lowerLeft = new TrapezoidMF(new double[]{this.support.getLeft(), this.support.getLeft(),cl0, dl0});
//        TrapezoidMF lowerRight = new TrapezoidMF(new double[]{al5, bl5, this.support.getRight(), this.support.getRight()});

        //to find the ratio for the third dimension, need to calculate the totals
        double totalLeft, totalRight, au4, al4, dl4, dl3, bl3, du3, au2, al2, bl2, du1, dl1, cu1, au1, al1, du0, au0, ratio2, ratio1, ratio0;
        dl4 = allEIAData.getLowerMembershipFunction(4).getEnd();
        al4 = allEIAData.getLowerMembershipFunction(4).getStart();
        dl3 = allEIAData.getLowerMembershipFunction(3).getEnd();
        bl3 = allEIAData.getLowerMembershipFunction(3).getB();
        du3 = allEIAData.getUpperMembershipFunction(3).getEnd();
        au2 = allEIAData.getUpperMembershipFunction(2).getStart();
        du1 = allEIAData.getUpperMembershipFunction(1).getEnd();
        au1 = allEIAData.getUpperMembershipFunction(1).getStart();
        du0 = allEIAData.getUpperMembershipFunction(0).getEnd();
        au0 = allEIAData.getUpperMembershipFunction(0).getStart();
        cu1 = allEIAData.getUpperMembershipFunction(1).getC();
        dl1 = allEIAData.getLowerMembershipFunction(1).getEnd();
        bl2 = allEIAData.getLowerMembershipFunction(2).getB();
        au4 = allEIAData.getUpperMembershipFunction(4).getStart();

        al1 = allEIAData.getLowerMembershipFunction(1).getStart();
        al2 = allEIAData.getLowerMembershipFunction(2).getStart();

        //primary FOU has different iterations however FOUwidthleftlower and FOUwidthrightlower will stay the same for all zSlices
        double FOUwidthLeftupper, FOUwidthLeftlower, FOUwidthRightupper, FOUwidthRightlower;
//        FOUwidthLeftlower = du2 - au2;
        FOUwidthLeftupper = du1 - cl0;
//        FOUwidthRightlower = dl3 - au3;
        FOUwidthRightupper = bl5 - au4;

        intervalSets = new IntervalT2MF_Trapezoidal[this.finalnumslice];
        int modCount = 0;

        if (rightorleft.equalsIgnoreCase("left")) {

            double startup = this.support.getLeft();
            double iterleftupper = FOUwidthLeftupper / (double) (this.finalnumslice+1);

            double currentpointupper = cl0;
            double pickprevupper = currentpointupper;
            int i = 1, wordcount = 0;

            for (int j = i; j <= numberOfSlices; j++) {

                T1MF_Trapezoidal memfunctlower, memfunctupper;
                if (currentpointupper <= al1) {
                    wordcount = 0;
                } else if (currentpointupper <= dl1) {
                    wordcount = 1;
                } else {
                    wordcount = 2;
                }
                String str = allEIAData.getLinguisticTerm(wordcount);

                memfunctlower = new T1MF_Trapezoidal(str, new double[]{startup, startup, currentpointupper, bl2});
                memfunctupper = new T1MF_Trapezoidal(str, new double[]{startup, startup, currentpointupper + iterleftupper, du2});
                intervalSets[numberOfSlices - j] = new IntervalT2MF_Trapezoidal(str, memfunctlower, memfunctupper);

                currentpointupper += iterleftupper;

            }
        }
        //for the right mf
        else {

            double end = this.support.getRight();
            double iterrightupper = FOUwidthRightupper / (double) (this.finalnumslice+1);

            double currentpointupper = au4;
            double pickprevupper = currentpointupper;
            int wordcount = 3;

            for (int j = 0; j < numberOfSlices; j++) {

                T1MF_Trapezoidal memfunctlower, memfunctupper;
                if (currentpointupper <= al4) {
                    wordcount = 3;
                }
                else if (currentpointupper <= dl4) {
                    wordcount = 4;
                }
                else {
                    wordcount = 5;
                }
                String str = allEIAData.getLinguisticTerm(wordcount);

                memfunctupper = new T1MF_Trapezoidal(str, new double[]{au3, currentpointupper, end, end});
                memfunctlower = new T1MF_Trapezoidal(str, new double[]{bl3, currentpointupper+iterrightupper, end, end});
                intervalSets[j] = new IntervalT2MF_Trapezoidal(str, memfunctlower, memfunctupper);

                currentpointupper += iterrightupper;

            }
        }

        AgreementEngine aE = new AgreementEngine();
        agreementSet = aE.findAgreement(this.linguisticLabel, intervalSets);


    }
    
    private void createZMFwithFOU(int originalnumberOfSlices, boolean reduceComplexity, String rightorleft) {
        
        //set up the interval type-2 sets
        //adjust the lower with the iteration and keep the upper same
        //first - set how many sets we will have - note that we do this for agreement purposes - u can just use one for now
        int numberOfSlices, divisor = 5;
        if (reduceComplexity && (originalnumberOfSlices > divisor)) {
            numberOfSlices = Math.round(originalnumberOfSlices / divisor);
        }
        else {
            numberOfSlices = originalnumberOfSlices;
        }
        
        this.finalnumslice = numberOfSlices;
        
        intervalSets = new IntervalT2MF_Trapezoidal[numberOfSlices];
        int modCount = 0;

        ArrayList <Double> pGranule = new ArrayList<Double>();

        for (Object t : orderedPoints.keySet()) {
            if (t instanceof Tuple) {
                pGranule.add(((Tuple) t).getRight());
            } else {
                pGranule.add((Double) t);
            }
        }
        
        if (rightorleft.equalsIgnoreCase("left")) {
            
            double startup = this.upper.getStart();
            double iter = (this.upper.getPoints()[2] - startup)/(numberOfSlices+1);
            //System.out.println("Value of iter : "+iter);
            //after changing the support to not being equal to min and max
            //startup point can be minus, yet startup should start from minimum
            double currentpoint = startup;
            double pickprev = currentpoint;
            int i = 1;

            for (int j = i; j <= numberOfSlices; j++) {
                

                T1MF_Trapezoidal memfunctlower, memfunctupper;
                if (this.orderedPoints.get(new Tuple((double)(j-1), pGranule.get(modCount))) != null) {
                    String str = this.orderedPoints.get(new Tuple((double)(j-1), pGranule.get(modCount))).getModifier();
                    if (!str.equalsIgnoreCase("")) {
                        str = str + " ";
                    }
                    memfunctlower = new T1MF_Trapezoidal(str, new double[]{startup, startup, pickprev, this.lower.getEnd()});
                    memfunctupper = new T1MF_Trapezoidal(str, new double[]{startup, startup, currentpoint, this.upper.getEnd()});
                    //intervalSets[j] = new IntervalT2MF_Trapezoidal(str + "_" + this.linguisticLabel + "_Level_"+(j+1), memfunctlower, memfunctupper);    
                    intervalSets[numberOfSlices-j] = new IntervalT2MF_Trapezoidal(str + this.linguisticLabel, memfunctlower, memfunctupper);    
                }
                else {      
                    memfunctlower = new T1MF_Trapezoidal("", new double[]{startup, startup, pickprev, this.lower.getEnd()});
                    memfunctupper = new T1MF_Trapezoidal("", new double[]{startup, startup, currentpoint, this.upper.getEnd()});
                    //intervalSets[j] = new IntervalT2MF_Trapezoidal("" + this.linguisticLabel + "_L_"+(j+1), memfunctlower, memfunctupper); 
                    intervalSets[numberOfSlices-j] = new IntervalT2MF_Trapezoidal("" + this.linguisticLabel, memfunctlower, memfunctupper); 
                }
                
                pickprev = currentpoint;
                currentpoint += iter;
                if (((modCount + 1) < pGranule.size()) && (pGranule.get(modCount+1) >= currentpoint)) {
                    modCount++;
                }
            }
        }
        //for the right mf
        else {
            double end = this.upper.getEnd();
            double start = this.upper.getPoints()[1];
            double iter = (end - start)/(numberOfSlices+1);
            double startlo = this.lower.getStart();
            double currentpoint = start;
            double pickprev = currentpoint;

            int j;
            boolean jumpFlag = true;
            for (j = 0; j < numberOfSlices; j++) {
                pickprev = currentpoint;
                currentpoint += iter;

                T1MF_Trapezoidal memfunctlower, memfunctupper;
                if (this.orderedPoints.get(new Tuple((double)j,pGranule.get(modCount))) != null) {
                    String str = this.orderedPoints.get(new Tuple((double)j,pGranule.get(modCount))).getModifier();
                    if (!str.equalsIgnoreCase("")) {
                        str = str + " ";
                    }
                    memfunctupper = new T1MF_Trapezoidal(str, new double[]{this.upper.getStart(), pickprev, end, end});
                    memfunctlower = new T1MF_Trapezoidal(str, new double[]{startlo, currentpoint, end, end});
                    //intervalSets[j] = new IntervalT2MF_Trapezoidal(str + "_" + this.linguisticLabel + "_L_"+(j+1), memfunctlower, memfunctupper);   
                    intervalSets[j] = new IntervalT2MF_Trapezoidal(str + this.linguisticLabel, memfunctlower, memfunctupper);   

                }
                else {      
                    memfunctupper = new T1MF_Trapezoidal("", new double[]{this.upper.getStart(), pickprev, end, end});
                    memfunctlower = new T1MF_Trapezoidal("", new double[]{startlo, currentpoint, end, end});
                    //intervalSets[j] = new IntervalT2MF_Trapezoidal("" + this.linguisticLabel + "_L_"+(j+1), memfunctlower, memfunctupper);
                    intervalSets[j] = new IntervalT2MF_Trapezoidal("" + this.linguisticLabel, memfunctlower, memfunctupper);      
                }

                if (((modCount + 1) < pGranule.size()) && (pGranule.get(modCount+1) <= currentpoint+iter)) {
                    modCount++;
                }
                
            }
            
            while (j < numberOfSlices) {

                pickprev = currentpoint;
                currentpoint += iter;
                
                if (currentpoint > end ) {
                    currentpoint = end;
                }
                if (currentpoint < startlo) {
                    //do nothing
                }
                else {
                    //T1MF_Trapezoidal memfunctupper = new T1MF_Trapezoidal("Extremely", new double[]{this.upper.getStart(), end, end, end});
                    T1MF_Trapezoidal memfunctupper = new T1MF_Trapezoidal("Extremely", new double[]{this.upper.getStart(), pickprev, end, end});
                    T1MF_Trapezoidal memfunctlower = new T1MF_Trapezoidal("Extremely", new double[]{startlo, currentpoint, end, end});
                    //intervalSets[j] = new IntervalT2MF_Trapezoidal("Ex_" + this.linguisticLabel + "_L_"+(j+1), memfunctlower, memfunctupper);
                    intervalSets[j] = new IntervalT2MF_Trapezoidal("Extremely " + this.linguisticLabel, memfunctlower, memfunctupper);

                    j++;

                }
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

    public synchronized void visualizeSets(String filename, String addString) {

//        System.out.println("zSLICELGT2:: Add string is : " + addString);
        GenT2zUtility_FileWriter zFW = new GenT2zUtility_FileWriter();
        //String[] colors = { "[1 1 0]", "[1 0 1]", "[0 1 1]", "[1 0 0]", "[0 1 0]", "[0 0 1]", "[1 1 1]" }; //yellow to white
        String[] colors = { "[1 0.8 0.4]", "[1 0 1]", "[0 1 1]", "[1 0 0]", "[0 1 0]", "[0 0 1]" }; //yellow to white
        
        //String[] colors = MatlabSupportTools.generateColorFades(agreementSet.getNumberOfSlices(), new double[]{1.0,0.4,0.6},new double[]{0.8,0.2,0.8});
        //String[] colors = MatlabSupportTools.generateColorFades(intervalSets.length, new double[]{1.0,0.1,0.1},new double[]{0.2,0.2,0.9});
        
        zFW.setMatlabColors(colors);
        
        String foldername = "";

        if (addString.equalsIgnoreCase("preparation time")) {
            foldername = Utility.foldername + "visuals"+ File.separator+"preptime" + File.separator;
        }
        else if (addString.equalsIgnoreCase("cooking time")) {
            foldername = Utility.foldername + "visuals"+ File.separator+"cooktime" + File.separator;
        }
        else if (addString.equalsIgnoreCase("zoverall time")) {
            foldername = Utility.foldername + "visuals"+ File.separator+"overall" + File.separator;
        }
        else {
            foldername = Utility.foldername + "visuals"+ File.separator+"diff" + File.separator;

        }
        

        //add timestamp
        Calendar cal = new GregorianCalendar();
        Format formatter = new SimpleDateFormat("MMddHHmmss");
        String timestamp = formatter.format(cal.getTime()).toString();
        
        File files = new File(foldername);
	if (!files.exists()) {
		if (files.mkdirs()) {
			System.out.println("zSLICELGT2: Multiple directories are created!");
		} else {
			System.out.println("zSLICELGT2: Failed to create multiple directories!");
		}
	}
        
        zFW.writeMatlabSquareTesselation(agreementSet, foldername + "a" + timestamp + filename +addString + ".m", 100, 1.0, true);

//        System.out.println("zSliceLGT2: Sets should have been written!");

    }
    
    public int getRealNumberofSlices() {
        return this.finalnumslice;
    }

}
