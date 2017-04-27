/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fuzzylogic.inference;

import fuzzylogic.generic.Tuple;

/**
 *
 * @author abilgin
 */
public class Couple {

    private Tuple centroidInterval;
    private Tuple firingInterval;

    public Couple() {
        this.centroidInterval = new Tuple(0,0);
        this.firingInterval = new Tuple(0,0);
    }

    public Couple(Tuple ci, Tuple fi) {
        this.centroidInterval = new Tuple(ci.getLeft(), ci.getRight());
        this.firingInterval = new Tuple(fi.getLeft(), fi.getRight());
    }

    public Tuple getCentroidInterval() {
        return this.centroidInterval;
    }

    public Tuple getFiringInterval() {
        return this.firingInterval;
    }

}
