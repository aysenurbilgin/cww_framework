/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type1.core;

import fuzzylogic.generic.NonSingleton;
import fuzzylogic.generic.Tuple;
import java.io.Serializable;

/**
 *
 * @author abilgin
 */
public class TrapezoidMF implements T1Interface, NonSingleton, Serializable {
    
    protected double[] points;

    public TrapezoidMF(double a, double b, double c, double d) {

        this.points = new double[4];

        this.points[0] = a;
        this.points[1] = b;
        this.points[2] = c;
        this.points[3] = d;
    }
    
    public TrapezoidMF(double[] pts) {

        this.points = new double[pts.length];
        System.arraycopy(pts, 0, this.points, 0, pts.length);
    }

    public TrapezoidMF(T1Interface function) {
        this.points = function.getPoints();
    }
    
    public double getStart() {
        return this.points[0];
    }
    
    public double getEnd() {
        return this.points[3];
    }

    public double getB() { return this.points[1]; }

    public double getC() { return this.points[2]; }

    @Override
    public Tuple getSupport() {
        return new Tuple(points[0],points[3]);
    }

    @Override
    public double getMembershipDegree(double x) {
        
        double value = 0.0;
        
        // out of support
        if (x < points[0] || x > points[3]) {
            value = 0.0;
        }
        else if (points[0] <= x && x < points[1]) {
            value = (x-points[0])/(points[1]-points[0]);
        }
        else if (points[1] <= x && x <= points[2]) {
            value = 1.0;
        }
        else if (points[2] < x && x <= points[3]) {
            value = (points[3]-x)/(points[3]-points[2]);
        }
        
        return value;
        
    }

    @Override
    public double[] getPoints() {

        double[] p = new double[4];

        for (int i = 0; i < this.points.length; i++) {
            p[i] = this.points[i];
        }

        return p;
    }

    @Override
    public boolean isLeftShoulder() {

        return Double.compare(this.points[0], this.points[1]) == 0;

    }

    @Override
    public boolean isRightShoulder() {

        return Double.compare(this.points[2], this.points[3]) == 0;

    }

    @Override
    /**
     * Non-singleton input is required 
     */
    public Tuple getMembershipDegree(T1Interface input) {
        
        //check support intersection
        double inpStart, inpEnd, value = 0, x = 0;
        
        inpStart = input.getSupport().getLeft();
        inpEnd = input.getSupport().getRight();
        
        if ((Double.compare(inpStart, this.points[3]) > 0) || (Double.compare(inpEnd, this.points[0]) < 0)) {
            //out of support
            value = 0;
        }
        else {
            double inpIter, iter = (inpEnd - inpStart) / (NonSingleton.nsdiscretizationLevel-1);
            inpIter = inpStart;
            
            while (Double.compare(inpEnd, inpIter) >= 0) {
                //get minimum inference of the input MF and current MF
                double minMD = Math.min(getMembershipDegree(inpIter), input.getMembershipDegree(inpIter));
                //get the new minimumMD if greater than the value before - supremum
                if (Double.compare(minMD, value) >= 0) {
                    value = minMD;
                    x = inpIter;
                }

                inpIter += iter;

            } //end of while for all the support of the nonsingleton input
        }
        
        return new Tuple(x, value);
    }
    
    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("(").append(this.points[0]).append(",").append(this.points[1]).append(",").append(this.points[2]).append(",").append(this.points[3]).append(")").toString();
        return s;
    }
    
}
