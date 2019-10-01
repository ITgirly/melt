package de.uni_mannheim.informatik.dws.ontmatching.matchingeval.evaluator.metric.cm;

import de.uni_mannheim.informatik.dws.ontmatching.yetanotheralignmentapi.Alignment;

/**
 * Data Structure for an individual confusion matrix.
 * @author Sven Hertling, Jan Portisch
 */
public class ConfusionMatrix {
    
    private Alignment truePositive;
    private Alignment falsePositive;
    private Alignment falseNegative;
    
    private double precision;
    private double recall;


    /**
     * Constructor to fill confusion matrix.
     * @param truePositive True positive mapping.
     * @param falsePositive False positive mapping.
     * @param falseNegative False negative mapping
     * @param precision Precision as double [0, 1].
     * @param recall Recall as double [0, 1].
     */
    public ConfusionMatrix(Alignment truePositive, Alignment falsePositive, Alignment falseNegative, double precision, double recall){
        this.truePositive = truePositive;
        this.falsePositive = falsePositive;
        this.falseNegative = falseNegative;
        this.precision = precision;
        this.recall = recall;
    }

    /**
     * Alignments which are in the reference mapping and are also found by the matcher.
     * @return found and correct mappings (correct)
     */
    public Alignment getTruePositive() {
        return truePositive;
    }
    
    public int getTruePositiveSize() {
        return truePositive.size();
    }

    /**
     * Alignments which are not correct but found by the matcher
     * @return found but not correct mappings (too much)
     */
    public Alignment getFalsePositive() {
        return falsePositive;
    }
    
    public int getFalsePositiveSize() {
        return falsePositive.size();
    }

    /**
     * Alignments which are correct but not found by the matcher
     * @return correct but not found by the matcher (should be found)
     */
    public Alignment getFalseNegative() {
        return falseNegative;
    }
    
    public int getFalseNegativeSize() {
        return falseNegative.size();
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1measure() {
        return getFbetaMeasure(1.0);
    }
    
    public double getFbetaMeasure(double beta){
        double betaSquared = Math.pow(beta, 2);        
        return (1 + betaSquared) * (precision * recall) / ((betaSquared * precision) + recall);
    }
}