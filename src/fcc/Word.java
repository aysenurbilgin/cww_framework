/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fcc;

import java.io.Serializable;

/**
 *
 * @author abilgin
 */
public class Word implements Comparable, PerceptualJudgment, Serializable {
  
    private String lingvar;
    private String negPerception;
    private String posPerception;
    
    public Word(String linguisticvariable, String negperc, String posperc) {
        
        this.lingvar = linguisticvariable;
        this.negPerception = negperc;
        this.posPerception = posperc;
        
    }
    
    public Word() {
        this.lingvar = "";
        this.negPerception = "";
        this.posPerception = "";
    }
    
    public Word(Word w) {
        this.lingvar = w.getLinguisticVariableName();
        this.negPerception = w.getNegPerception();
        this.posPerception = w.getPosPerception();
    }
    
    @Override
    public String getLinguisticVariableName() {
        return this.lingvar;
    }
    
    @Override
    public String getNegPerception() {
        return this.negPerception;
    }
    
    @Override
    public String getPosPerception() {
        return this.posPerception;
    }
    
    @Override
    public void setLinguisticVariableName(String lv) {
        this.lingvar = lv;
    }
    
    @Override
    public void setNegPerception(String np) {
        this.negPerception = np;
    }
    
    @Override
    public void setPosPerception(String pp) {
        this.posPerception = pp;
    }

    @Override
    public int compareTo(Object t) {

        return (int) this.lingvar.compareTo(((Word) t).getLinguisticVariableName());

    }
    
    public boolean isPositive(String str) {

        return this.posPerception.equalsIgnoreCase(str);

    }
    
    public boolean isNegative(String str) {

        if (this.negPerception.equalsIgnoreCase(str)) {
            return true;
        }

        return false;

    }
    
    @Override
    public String toString() {
        return "Word{" + "lingvar=" + lingvar + ", negPerception=" + negPerception + ", posPerception=" + posPerception + '}';
    }
    
}
