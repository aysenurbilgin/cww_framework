/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzylogic.type2.core;

import fuzzylogic.generic.Resource;
import fuzzylogic.generic.Tuple;
import fuzzylogic.type2.lineargeneral.zSliceLGT2;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * @author abilgin
 */
public class LGT2_EIA {

    private Tuple support;                          //holds the support of the MF which tells the whole possible domain of the variable
    private String negPerception;                   //holds the negative perception which is to be modelled as left smf
    private String posPerception;                   //holds the positive perception which is to be modelled as right smf
    private String lingVarName;
    private zSliceLGT2 leftShoulder;
    private zSliceLGT2 rightShoulder;
    private Map<Tuple, Resource> leftorderedGranules;
    private Map<Tuple, Resource> rightorderedGranules;
    public static final int numberOfSlices = 5;

    private EIAData allEIAData;

    public LGT2_EIA(String dataFile, String lingVarName, String leftLabel, String rightLabel) {
        
        this.support = new Tuple(0.0,10.0);  //concept variables will be normalized
        this.negPerception = leftLabel;
        this.posPerception = rightLabel;
        this.lingVarName = lingVarName;
        
        this.leftorderedGranules = new TreeMap<Tuple, Resource>();
        this.rightorderedGranules = new TreeMap<Tuple, Resource>();

        readEIAData(dataFile);
        //allEIAData consists of 3 linguistic variables and all the EIA information
        formZsliceEIASets();

    }
    
    public Tuple getSupport() {  
        return this.support;
    }
    
    public Map<Tuple, Resource> getLeftGranules() {
        return this.leftorderedGranules;
    }
    
    public Map<Tuple, Resource> getRightGranules() {
        return this.rightorderedGranules;
    }

    public void formZsliceEIASets() {
        this.leftShoulder =  new zSliceLGT2(negPerception, allEIAData, numberOfSlices, "left");
        this.rightShoulder =  new zSliceLGT2(posPerception, allEIAData, numberOfSlices, "right");
    }
    
    public int getNumberOfSlices() {
        return LGT2_EIA.numberOfSlices;
    }

    public AgreementMF_zMFs getRightShoulder() {
       
        return this.rightShoulder.getAgreementSets();
    }
    
    public AgreementMF_zMFs getLeftShoulder() {
        
        return this.leftShoulder.getAgreementSets();
        
    }
    
    public AgreementMF_zMFs[] getFuzzySets() {
        return new AgreementMF_zMFs[]{getLeftShoulder(),getRightShoulder()};
    }
    
    public static int getNozSlices() {
        return LGT2_EIA.numberOfSlices;
    }

    public String getLingVarName() {
        return this.lingVarName;
    }

    public AgreementMF_zMFs[] getAgreementSets() {

        return new AgreementMF_zMFs[]{getLeftShoulder(), getRightShoulder()};

    }

    private void readEIAData(String datafile) {

        String filename = "." + File.separator + "data" + File.separator + datafile;

        try {

            //create BufferedReader to read csv file
            BufferedReader br = new BufferedReader( new FileReader(filename));
            String strLine;
            StringTokenizer st;
            int lineNumber = 0, tokenNumber = 0;

            boolean endOfFile = false;
            allEIAData = new EIAData();
            ArrayList<Double> upperParameters = new ArrayList<Double>();
            ArrayList<Double> lowerParameters = new ArrayList<Double>();

            //read comma separated file line by line until end
            //first column is the linguistic term, next 4 columns are upper, last 4 columns are lower
            while(!endOfFile) {

                strLine = br.readLine();

                if (strLine.contains("end")) {
                    endOfFile = true;
                }
                else if(strLine == null ? "" != null : !strLine.equals("")) {

                    //break comma separated line using ","
                    st = new StringTokenizer(strLine, ",");

                    while(st.hasMoreTokens()) {

                        String str = st.nextToken();
                        //first column is linguistic term
                        if (tokenNumber == 0) {
                            allEIAData.addLinguisticTerm(lineNumber % 6, str);
                        }
                        //next 4 tokens are upper
                        else if (tokenNumber == 1 || tokenNumber == 2 || tokenNumber == 3 || tokenNumber == 4) {
                            upperParameters.add(Double.parseDouble(str));

                        }
                        else if (tokenNumber == 5 || tokenNumber == 6 || tokenNumber == 7 || tokenNumber == 8) {
                            lowerParameters.add(Double.parseDouble(str));

                        }

                        tokenNumber++;

                    }

                    allEIAData.addLowerMembershipFunction(new double[]{lowerParameters.get(0), lowerParameters.get(1), lowerParameters.get(2), lowerParameters.get(3)});
                    allEIAData.addUpperMembershipFunction(new double[]{upperParameters.get(0), upperParameters.get(1), upperParameters.get(2), upperParameters.get(3)});
                    upperParameters = new ArrayList<Double>();
                    lowerParameters = new ArrayList<Double>();

                    //reset token number
                    tokenNumber = 0;
                    lineNumber++;

                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}