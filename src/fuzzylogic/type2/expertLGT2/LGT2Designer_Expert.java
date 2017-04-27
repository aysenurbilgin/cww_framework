/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

package fuzzylogic.type2.expertLGT2;

import fcc.Word;
import fuzzylogic.generic.Resource;
import fuzzylogic.generic.Tuple;
import fuzzylogic.type1.core.TrapezoidMF;
import fuzzylogic.type2.lineargeneral.zSliceLGT2;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by abilgin on 31/01/2015.
 */
public class LGT2Designer_Expert {

    private Tuple support;                          //holds the support of the MF which tells the whole possible domain of the variable
    private Word linguisticData;

    private zSliceLGT2 leftShoulder;
    private zSliceLGT2 rightShoulder;
    private Map<Tuple, Resource> leftorderedGranules;
    private Map<Tuple, Resource> rightorderedGranules;

    public final int numberOfSlices = 5;

    private TrapezoidMF upperLeft;
    private TrapezoidMF upperRight;
    private TrapezoidMF lowerLeft;
    private TrapezoidMF lowerRight;

    /**
     @param fouWidthPercentage is in decimal point and will directly be multiplied
     @param leftPercentage is in decimal point and will directly be multiplied
     @param rightPercentage is in decimal point and will directly be multiplied
     */
    public LGT2Designer_Expert(Tuple supportData, Word linguisticData, double fouWidthPercentage, double leftPercentage, double rightPercentage) {

        this.support = new Tuple(supportData);
        this.linguisticData = new Word(linguisticData);

        this.leftorderedGranules = new TreeMap<Tuple, Resource>();
        this.rightorderedGranules = new TreeMap<Tuple, Resource>();

        organize(fouWidthPercentage, leftPercentage, rightPercentage);

        formZsliceSets();

    }

    public Tuple getSupport() {
        return this.support;
    }

    /**
     * Method orders the base transition points according to the most prevailing ones
     */
    private void organize(double fouWidthPercentage, double leftPercentage, double rightPercentage) {

        //set the type-1 membership function
        //trapezoidal points for the left and right shoulder mfs
        //calculate from the percentages

        double width = this.support.getRight() - this.support.getLeft();
        double left =  this.support.getLeft() + width * leftPercentage;
        double right =  this.support.getLeft() + width * rightPercentage;

        upperLeft = new TrapezoidMF(setPointsLeft(left, right));
        upperRight = new TrapezoidMF(setPointsRight(left, right));

        //expert design
        double FOUwidth = width * fouWidthPercentage;

        if (left < FOUwidth) {
            lowerLeft = new TrapezoidMF(setPointsLeft(left, right));
        }
        else {
            lowerLeft = new TrapezoidMF(setPointsLeft(left-FOUwidth, right-FOUwidth));
        }
        lowerRight = new TrapezoidMF(setPointsRight(left+FOUwidth, right+FOUwidth));

        double iterleft = left / (double) this.numberOfSlices;

        //create for the left shoulder mf
        //partition the most prevailing values and the resources for the left
        for (int i = 0; i < this.numberOfSlices; i++) {

            Resource res;
            if (i == 0) {
                res = new Resource("extremely " + this.linguisticData.getNegPerception());
            }
            else if ((i == 1) || (i == 2)) {
                res = new Resource("very "+ this.linguisticData.getNegPerception());
            }
            else {
                res = new Resource(this.linguisticData.getNegPerception());
            }

            leftorderedGranules.put(new Tuple(i, iterleft*i), res);

        }

        double iterright = (1.0 - right) / (double) this.numberOfSlices;

        //create for the right shoulder mf
        //partition the most prevailing values and the resources for the right
        for (int i = 0; i < this.numberOfSlices; i++) {

            Resource res;
            if (i == this.numberOfSlices-1) {
                res = new Resource("extremely "+ this.linguisticData.getPosPerception());
            }
            else if ((i == this.numberOfSlices-2) || (i == this.numberOfSlices-3)) {
                res = new Resource("very "+ this.linguisticData.getPosPerception());
            }
            else {
                res = new Resource(this.linguisticData.getPosPerception());
            }

            rightorderedGranules.put(new Tuple(i,(left + (iterright*i))), res);
        }

    }

    private void formZsliceSets() {
        this.leftShoulder = new zSliceLGT2(this.linguisticData.getNegPerception(), (Map<Object, Resource>) ((Object)leftorderedGranules), lowerLeft, upperLeft, numberOfSlices, "left", false);
        this.rightShoulder = new zSliceLGT2(this.linguisticData.getPosPerception(), (Map<Object, Resource>) ((Object)rightorderedGranules), lowerRight, upperRight, numberOfSlices, "right", false);
    }

    private double[] setPointsLeft(double c, double d) {

        double als, bls, cls, dls;

        //for the left shoulder membership function
        //UKCI paper Eqn 11-13
        als = this.support.getLeft();
        bls = als;
        cls = c;
        dls = d;

        return new double[]{als, bls, cls, dls};

    }

    private double[] setPointsRight(double a, double b) {

        double ars, brs, crs, drs;

        //for the right shoulder membership function
        ars = a;
        brs = b;
        crs = this.support.getRight();
        drs = crs;

        return new double[]{ars, brs, crs, drs};

    }

    public void visualizeLeftShoulder() {
        this.leftShoulder.visualizeSets(this.linguisticData.getNegPerception(), "expert");
    }

    public void visualizeRightShoulder() {
        this.rightShoulder.visualizeSets(this.linguisticData.getPosPerception(), "expert");
    }

    public void visualizeLV() {
        visualizeLeftShoulder();
        visualizeRightShoulder();
    }

    public int getNumberOfSlices() {
        return this.numberOfSlices;
    }

    public AgreementMF_zMFs getRightShoulder() {

        return this.rightShoulder.getAgreementSets();
    }

    public AgreementMF_zMFs getLeftShoulder() {

        return this.leftShoulder.getAgreementSets();

    }

    public AgreementMF_zMFs[] getAgreementSets() {

        AgreementMF_zMFs[] setscombined;

        setscombined = new AgreementMF_zMFs[]{getLeftShoulder(), getRightShoulder()};

        return setscombined;
    }
}
