/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type2.core;

import fuzzylogic.generic.InfTuple;
import fuzzylogic.generic.LeftComparator;
import fuzzylogic.generic.RightComparator;
import fuzzylogic.generic.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author abilgin
 */
public abstract class MembershipFunction implements GT2Interface {
    
    public static final int discretizationLevel = 1000;
    public static final int secondarydiscretizationLevel = 100;
    public static final double tolerance = 0.001;
    
    public Tuple getCentroidInterval() {

        double yIterz, yStart, yEnd, teta, yDBPrime = 0.0, height, delta, yPrime;
        double iter, sumNom = 0.0, sumDenom = 0.0, sumNomi, sumDenomi;
        boolean foundR = false, foundL = false;
        int e;
        Tuple interval, yResults = new Tuple(0,0);

        interval = this.getSupport();
        yStart = interval.getLeft();
        yEnd = interval.getRight();
        iter = (yEnd - yStart) / (discretizationLevel-1);
        yIterz = yStart;

        while (yIterz <= yEnd) {

            height = (this.getUpper(yIterz)+this.getLower(yIterz))/2.0;
            teta = height;

            sumNom += yIterz*teta;
            sumDenom += teta;

            yIterz += iter;

        }

        yPrime = sumNom/sumDenom;

        int count = 0, counte = 0;
        while (!foundL) {

            for (e = 0; e <= discretizationLevel-1; e++) {
                if ( yPrime >= yStart+e*iter && yPrime <= yStart+(e+1)*iter) {
                    counte ++;
                    break;
                }
            }

            sumNomi = 0.0;
            sumDenomi = 0.0;
            yIterz = yStart;

            for (int i = 0; i <= discretizationLevel; i++) {

                height = (this.getUpper(yIterz)+this.getLower(yIterz))/2.0;
                delta = (this.getUpper(yIterz)-this.getLower(yIterz))/2.0;

                if (i <= e) {
                    teta = height + delta;
                    sumNomi += yIterz*teta;
                    sumDenomi += teta;
                }
                else if (i >= e+1) {
                    teta = height - delta;
                    sumNomi += yIterz*teta;
                    sumDenomi += teta;
                }

                yIterz += iter;

            }

            yDBPrime = sumNomi/sumDenomi;


            if (Math.abs(yPrime - yDBPrime) <= tolerance) {
                foundL = true;
            }
            else {
                yPrime = yDBPrime;
                foundL = false;
            }
            count ++;
        } // end of while

        yResults.setLeft(yDBPrime);
        count = 0;
        counte = 0;

        while (!foundR) {
            counte = 0;
            for (e = 0; e <= discretizationLevel-1; e++) {
                if ( yPrime >= yStart+e*iter && yPrime <= yStart+(e+1)*iter) {
                    counte++;
                    break;
                }
            }

            sumNomi = 0.0;
            sumDenomi = 0.0;
            yIterz = yStart;

            for (int i = 0; i < discretizationLevel; i++) {

                height = (this.getUpper(yIterz)+this.getLower(yIterz))/2.0;
                delta = (this.getUpper(yIterz)-this.getLower(yIterz))/2.0;

                if (i <= e) {
                    teta = height - delta;
                    sumNomi += yIterz*teta;
                    sumDenomi += teta;
                }
                else if (i >= e+1) {
                    teta = height + delta;
                    sumNomi += yIterz*teta;
                    sumDenomi += teta;
                }

                yIterz += iter;

            }

            yDBPrime = sumNomi/sumDenomi;
            //System.out.println("way DB praymm " + yDBPrime);

            if (Math.abs(yPrime - yDBPrime) <= tolerance) {
                foundR = true;
            }
            else {
                yPrime = yDBPrime;
                foundR = false;
            }
            count ++;
        } // end of while

        yResults.setRight(yDBPrime);

        return yResults;
    }
    
    public Tuple getTypeReducedInterval (ArrayList<InfTuple> data) {
 
        Iterator itr = data.iterator();

        InfTuple element = new InfTuple();
        while (itr.hasNext()) {

            element = (InfTuple)itr.next();
            if (element.getFiringInterval().getLeft() == 0.0 && element.getFiringInterval().getRight() == 0.0)
                itr.remove();

        }

        for (int i = 0; i<data.size(); i++) {
            System.out.print(data.get(i).getCentroidInterval().getLeft() + " ");
            System.out.print(data.get(i).getCentroidInterval().getRight()  + " ");
            System.out.print(data.get(i).getFiringInterval().getLeft()  + " ");
            System.out.println(data.get(i).getFiringInterval().getRight());
        }

        Collections.sort(data, new LeftComparator());

        Tuple yResults = new Tuple(0,0);
        double yDBPrime = 0.0, yPrime;
        double sumNom = 0.0, sumDenom = 0.0, sumNomi, sumDenomi;
        boolean foundR = false, foundL = false;
        int l,r;

        int countl = 0, countr = 0;

        for (int i = 0; i<data.size(); i++) {
            sumNom += data.get(i).getCentroidInterval().getLeft() * data.get(i).getFiringInterval().getAverage();
            sumDenom += data.get(i).getFiringInterval().getAverage();
        }

        yPrime = sumNom/sumDenom;

        while (!foundL) {

            for (l = 0; l <= data.size()-1; l++) {
                if ( yPrime >= data.get(l).getCentroidInterval().getLeft() && yPrime <= data.get(l+1).getCentroidInterval().getLeft()) {
                    countl ++;
                    break;
                }
            }

            sumNomi = 0.0;
            sumDenomi = 0.0;

            for (int k = 0; k < data.size(); k++) {

                if (k <= l) {

                    sumNomi += data.get(k).getCentroidInterval().getLeft() * data.get(k).getFiringInterval().getRight();
                    sumDenomi += data.get(k).getFiringInterval().getRight();
                }
                else if (k > l) {

                    sumNomi += data.get(k).getCentroidInterval().getLeft() * data.get(k).getFiringInterval().getLeft();
                    sumDenomi += data.get(k).getFiringInterval().getLeft();
                }

            }

            yDBPrime = sumNomi/sumDenomi;


            if (Math.abs(yPrime - yDBPrime) <= tolerance) {
                foundL = true;
            }
            else {
                yPrime = yDBPrime;
                foundL = false;
            }

        } // end of while


        yResults.setLeft(yDBPrime);

        Collections.sort(data, new RightComparator());

        sumNom = 0.0;
        sumDenom = 0.0;
        yDBPrime = 0.0;

        for (int i = 0; i<data.size(); i++) {
            sumNom += data.get(i).getCentroidInterval().getRight() * data.get(i).getFiringInterval().getAverage();
            sumDenom += data.get(i).getFiringInterval().getAverage();
        }

        yPrime = sumNom/sumDenom;

        while (!foundR) {

            for (r = 0; r <= data.size()-1; r++) {
                if ( yPrime >= data.get(r).getCentroidInterval().getRight() && yPrime <= data.get(r+1).getCentroidInterval().getRight()) {
                    countr ++;
                    break;
                }
            }

            sumNomi = 0.0;
            sumDenomi = 0.0;

            for (int i = 0; i < data.size(); i++) {

                if (i <= r) {

                    sumNomi += data.get(i).getCentroidInterval().getRight()*data.get(i).getFiringInterval().getLeft();
                    sumDenomi += data.get(i).getFiringInterval().getLeft();
                }
                else if (i > r) {

                    sumNomi += data.get(i).getCentroidInterval().getRight() * data.get(i).getFiringInterval().getRight();
                    sumDenomi += data.get(i).getFiringInterval().getRight();
                }

            }

            yDBPrime = sumNomi/sumDenomi;

            if (Math.abs(yPrime - yDBPrime) <= tolerance) {
                foundR = true;
            }
            else {
                yPrime = yDBPrime;
                foundR = false;
            }

        } // end of while


        yResults.setRight(yDBPrime);

        System.out.println("Result is : [ "+yResults.getLeft()+" , "+yResults.getRight()+" ]");

        return yResults;
    }
    
    public double defuzzify (Tuple interval) {

        return interval.getAverage();

    }


    
}
