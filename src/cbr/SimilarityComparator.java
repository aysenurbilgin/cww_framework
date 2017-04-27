/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import java.util.Comparator;

/**
 * Compares the similarity values of the retrieved cases
 * @author abilgin
 */
public class SimilarityComparator implements Comparator {
    
    private static final String sortingorder = "DESCENDING";
    
    @Override
    public int compare(Object o1, Object o2) {

        if (SimilarityComparator.sortingorder.equals("ASCENDING")) {
            return new Double(((RetrievedCase) o1).getSimilarity()).compareTo(((RetrievedCase) o2).getSimilarity());
        }
        else {
            return new Double(((RetrievedCase) o2).getSimilarity()).compareTo(((RetrievedCase) o1).getSimilarity());
        }

    }
    
}
