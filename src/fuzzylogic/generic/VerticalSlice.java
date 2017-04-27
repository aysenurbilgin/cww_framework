/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzylogic.generic;

import fuzzylogic.type2.core.MembershipFunction;

/**
 * VerticalSlice is a data type detailing the singleton cut of a FOU
 * It can be represented with a Tuple and a x value
 * @author abilgin
 */
public class VerticalSlice implements Slice {

    private Tuple interval;
    private double x;

    public VerticalSlice() {
        this.interval = new Tuple(0,0);
        this.x = 0;
    }

    public VerticalSlice(double lower, double upper, double xVal) {
        this.interval = new Tuple(lower, upper);
        this.x = xVal;
    }

    public VerticalSlice(Tuple slice, double xVal) {
        this.interval = new Tuple(slice);
        this.x = xVal;
    }

    public Tuple getInterval() {
        return this.interval;
    }

    public void setInterval(Tuple slice) {
        this.interval = new Tuple(slice);
    }

    public void setInterval(double lower, double upper) {
        this.interval = new Tuple(lower, upper);
    }

    public double getX() {
        return this.x;
    }

    public void setX(double xVal) {
        this.x = xVal;
    }
    
    @Override
    public double calculateCentroid(double lowerbase, double upperbase) {

        double length = this.interval.getRight() - this.interval.getLeft();
        double sumNom = 0, sumDenom = 0, yIter = this.interval.getLeft();
        double iter = length / (MembershipFunction.secondarydiscretizationLevel - 1 );
        //discretize the shape to calculate the centroid
        while (yIter <= this.interval.getRight()) {
            sumNom += yIter * secondaryValue(yIter, this.interval.getRight(), this.interval.getLeft(), lowerbase, upperbase);
            sumDenom += yIter;
            yIter += iter;
        }
        
        return sumNom/sumDenom;
    }
    
    private double secondaryValue(double y, double upperMD, double lowerMD, double lowerbase, double upperbase) {
        
        return ((upperMD - y)/(upperMD - lowerMD))*(lowerbase - upperbase) + upperbase;
        
    }

    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("Interval at ").append(this.x).append(" is ").append(this.interval.toString()).toString();
        return s;
    }

}
