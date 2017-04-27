/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Index is the abstract object that will be used to retrieve cases from the case base
 * @author abilgin
 */
public abstract class Index implements Serializable {
    
    public Object index;
    public ArrayList<Feature> features;
    
    public abstract Object getIndex();
    public abstract Feature getFeature(String name);
    public abstract ArrayList<Feature> getFeatures();

}
