/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzylogic.type2.zSlices;

import fuzzylogic.generic.Tuple;
import fuzzylogic.type2.interval.IntervalT2MF_Interface;
import fuzzylogic.type2.interval.IntervalT2MF_Trapezoidal;
import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

/**
 * The class encodes a fuzzy set which models agreement using intersections and the union of a large number of
 * Interval Type-2 fuzzy sets which are "stacked" using zSlices based on their "agreement".
 * @author Christian Wagner
 * Copyright 2010 Christian Wagner All Rights Reserved.
 */
public class AgreementMF_zMFs extends GenT2zMF_Prototype implements Serializable
{

    /**
     * This constructor is only used by the AgreementEngine.
     * @param name
     * @param zLevels
     */
    protected AgreementMF_zMFs(String name, Vector<IntervalT2MF_Interface> zLevels)
    {
        super(name);
        this.numberOfzLevels = zLevels.size();
        IntervalT2MF_Interface[] zLevelsArray = new IntervalT2MF_Interface[zLevels.size()];
        this.zSlices = zLevels.toArray(zLevelsArray);
        this.domain = new Tuple(this.zSlices[0].getDomain().getLeft(),this.zSlices[0].getDomain().getRight());
    }

    protected AgreementMF_zMFs(String name, Map<Integer, IntervalT2MF_Interface> zLevelsFinal) {

        super(name);
        this.numberOfzLevels = zLevelsFinal.size();
        IntervalT2MF_Interface[] zLevelsArray = new IntervalT2MF_Interface[zLevelsFinal.size()];
        this.zSlices = zLevelsFinal.values().toArray(zLevelsArray);
        int i = 0;
        for (Map.Entry<Integer, IntervalT2MF_Interface> entry: zLevelsFinal.entrySet()) {
            this.zSlices[i] = entry.getValue();
            //System.out.println("The source name here is "+this.zSlices[i].getName());
            //System.out.println("However the hashmap name here is "+entry.getValue().getName());
            i++;
        }
        this.domain = new Tuple(this.zSlices[0].getDomain().getLeft(),this.zSlices[0].getDomain().getRight());
        //System.out.println("Burdayim");

    }

    /* @author abilgin
     * Copy constructor
     */
    public AgreementMF_zMFs(AgreementMF_zMFs set)
    {
        super(set.getName());
        this.numberOfzLevels = set.getNumberOfSlices();
        //IntervalT2MF_Interface[] zLevelsArray = new IntervalT2MF_Interface[this.numberOfzLevels];
        this.zSlices = new IntervalT2MF_Interface[this.numberOfzLevels];

        for (int i = 0; i < this.numberOfzLevels; i++) {
            //zLevelsArray[i] = set.getZSlice(i);
            this.zSlices[i] = set.getZSlice(i);
        }


        this.domain = new Tuple(this.zSlices[0].getDomain().getLeft(),this.zSlices[0].getDomain().getRight());
    }

    public int getNumberofSlices() {
        return this.numberOfzLevels;
    }


    public double getInnerBound(double x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getOuterBound(double x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public double getPeak() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isLeftShoulder() {

        return ((IntervalT2MF_Trapezoidal) this.zSlices[0]).getLMF().getA() == ((IntervalT2MF_Trapezoidal) this.zSlices[0]).getLMF().getB();
    }

    @Override
    public boolean isRightShoulder() {

        return ((IntervalT2MF_Trapezoidal) this.zSlices[0]).getLMF().getC() == ((IntervalT2MF_Trapezoidal) this.zSlices[0]).getLMF().getD();
    }

    @Override
    public double getLeftShoulderStart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getRightShoulderStart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        String s = "";
        //s = (new StringBuilder()).append(s).append("AgreementMF: ").append(super.getName()).append(", domain: ").append(super.getDomain().toString()).toString();
        s = (new StringBuilder()).append(s).append(super.getName()).toString();
        return s;
    }


}

