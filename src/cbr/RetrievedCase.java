/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

/**
 *
 * @author abilgin
 */
public class RetrievedCase extends Case {
    
    private double similarityValue;
    
    public RetrievedCase(Case c, double similarity) {
        super(c);
        this.similarityValue = similarity;
    }
    
    public RetrievedCase(RetrievedCase rc) {
        super(rc.getCase());
        this.similarityValue = rc.getSimilarity();
    }
    
    public double getSimilarity() {
        return this.similarityValue;
    }
    
    public Case getCase() {
        return this;
    }
    
    
    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("Retrieved case: ").append(super.toString()).append(", similarity value: ").append(this.similarityValue).toString();
        return s;
    }
    
}
