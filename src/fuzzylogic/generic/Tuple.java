/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.generic;

import java.io.Serializable;

/**
 * Tuple is a generic data type that has a left and a right point representing an interval
 * @author abilgin
 */
public class Tuple implements Serializable, Comparable {
    
    private double left;
    private double right;
    
    public Tuple() {
        this.left = 0.0;
        this.right = 0.0;
    }
    
    public Tuple(double l, double r) {
        this.left = l;
        this.right = r;
    }
    
    public Tuple(Tuple t) {
        this.left = t.getLeft();
        this.right = t.getRight();
    }
    
    public double getLeft() {
        return this.left;
    }
    
    public double getRight() {
        return this.right;
    }
    
    public double getAverage() {
        return (this.left + this.right)/2.0;
    }
    
    public void setLeft(double l) {
        this.left = l;
    }
    
    public void setRight(double r) {
        this.right = r;
    }
    
    public Tuple performAddition(Tuple t) {
        return new Tuple(this.left+t.getLeft(), this.right+t.getRight());
    }
    
    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("(").append(this.left).append(",").append(this.right).append(")").toString();
        return s;
    }

    @Override
    public int compareTo(Object o) {

        if (o instanceof Tuple) {
            Tuple comp = (Tuple) o;

            return ((Double) this.getLeft()).compareTo((Double) comp.getLeft());
        } else {
            return ((Double) this.getRight()).compareTo((Double) o);
        }

    }
}
