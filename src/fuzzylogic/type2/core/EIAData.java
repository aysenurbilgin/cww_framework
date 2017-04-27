/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

package fuzzylogic.type2.core;

import fuzzylogic.type1.core.TrapezoidMF;

import java.util.ArrayList;

/**
 * Created by abilgin on 18/01/2015.
 */
public class EIAData {

    private String[] linguisticTerms;
    private ArrayList<TrapezoidMF> upperMembershipFunctions;
    private ArrayList<TrapezoidMF> lowerMembershipFunctions;

    public EIAData() {
        this.linguisticTerms = new String[6];
        this.upperMembershipFunctions = new ArrayList<TrapezoidMF>();
        this.lowerMembershipFunctions = new ArrayList<TrapezoidMF>();
    }

    public EIAData (String[] lingTerms) {

        this.linguisticTerms = new String[6];

        for (int i = 0; i < lingTerms.length; i++) {
            this.linguisticTerms[i] = lingTerms[i];
        }

        this.upperMembershipFunctions = new ArrayList<TrapezoidMF>();
        this.lowerMembershipFunctions = new ArrayList<TrapezoidMF>();

    }

    public void addUpperMembershipFunction(double[] parameters) {
        this.upperMembershipFunctions.add(new TrapezoidMF(parameters));
    }

    public void addLowerMembershipFunction(double[] parameters) {
        this.lowerMembershipFunctions.add(new TrapezoidMF(parameters));
    }

    public TrapezoidMF getUpperMembershipFunction(int index) {
        return this.upperMembershipFunctions.get(index);
    }

    public TrapezoidMF getLowerMembershipFunction(int index) {
        return this.lowerMembershipFunctions.get(index);
    }

    public void addLinguisticTerm(int index, String lingTerm) {
        this.linguisticTerms[index] = lingTerm;
    }

    public String getLinguisticTerm(int index) {
        return this.linguisticTerms[index];
    }

    public boolean isPopulated() {
        if (this.upperMembershipFunctions.size() == 0) {
            return false;
        }
        return true;
    }

    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append(this.linguisticTerms[0]).append(" - upper: ").append(this.upperMembershipFunctions.toString()).append(" - lower: ").append(this.lowerMembershipFunctions.toString()).toString();
        return s;
    }

}
