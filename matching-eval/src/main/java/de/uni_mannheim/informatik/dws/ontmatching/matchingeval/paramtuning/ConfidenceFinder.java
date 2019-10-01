package de.uni_mannheim.informatik.dws.ontmatching.matchingeval.paramtuning;

import de.uni_mannheim.informatik.dws.ontmatching.matchingeval.ExecutionResult;
import de.uni_mannheim.informatik.dws.ontmatching.matchingeval.ExecutionResultSet;
import de.uni_mannheim.informatik.dws.ontmatching.matchingeval.evaluator.metric.cm.ConfusionMatrix;
import de.uni_mannheim.informatik.dws.ontmatching.matchingeval.evaluator.metric.cm.ConfusionMatrixMetric;
import de.uni_mannheim.informatik.dws.ontmatching.matchingeval.refinement.ConfidenceRefiner;
import de.uni_mannheim.informatik.dws.ontmatching.yetanotheralignmentapi.Alignment;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ConfidenceFinder{
    
    
    public static Set<Double> getSteps(double start, double end, double stepwidth){
        Set<Double> set = new HashSet<>();
        for(double d = start; d <= end; d += stepwidth){
            set.add(d);
        }
        return set;
    }
    
    public static Set<Double> getOccurringConfidences(Alignment a){
        Set<Double> set = new HashSet<>();
        for(Double c : a.getDistinctConfidences()){
            set.add(c);
        }
        return set;
    }
    
    public static Set<Double> getOccurringConfidences(Alignment a, double begin, double end){
        Set<Double> set = new HashSet<>();
        for(Double c : a.getDistinctConfidences()){
            if(c >= begin && c <= end){
                set.add(c);
            }
        }
        return set;
    }
    
    public static Set<Double> getOccurringConfidences(Alignment a, int decimalPrecision){
        Set<Double> set = new HashSet<>();
        for(Double c : a.getDistinctConfidences()){
            BigDecimal bd = new BigDecimal(c);
            bd = bd.setScale(decimalPrecision, RoundingMode.HALF_UP);
            set.add(bd.doubleValue());
        }
        return set;
    }
    
    public static Set<Double> getOccurringConfidences(Alignment a, int decimalPrecision, double begin, double end){
        Set<Double> set = new HashSet<>();
        for(Double c : a.getDistinctConfidences()){
            BigDecimal bd = new BigDecimal(c);
            bd = bd.setScale(decimalPrecision, RoundingMode.HALF_UP);
            double d = bd.doubleValue();
            if(d >= begin && d <= end){
                set.add(d);
            }
        }
        return set;
    }

    
    public static Double getBestConfidenceForFmeasure(ExecutionResult executionResult){
        ConfusionMatrix m = new ConfusionMatrixMetric().compute(executionResult);

        List<Double> systemConfidences = new ArrayList(getOccurringConfidences(executionResult.getSystemAlignment(), 2));
        Collections.sort(systemConfidences);
        double bestConf = 1.0d;
        double bestValue = 0.0d;
        for(Double conf : systemConfidences){
            int tpSize = m.getTruePositive().cut(conf).size();
            int fpSize = m.getFalsePositive().cut(conf).size();
            int fnSize = m.getFalseNegative().cut(conf).size();
            
            double precision = divideWithTwoDenominators(tpSize, tpSize, fpSize);
            double recall = divideWithTwoDenominators(tpSize, tpSize, fnSize);
            double f1measure = divideWithTwoDenominators(2*precision*recall, precision, recall);
            
            if(f1measure >= bestValue){
                bestConf = conf;
                bestValue = f1measure;
            }
        }
        return bestConf;
    }
    
    
    public static ExecutionResultSet getConfidenceResultSet(ExecutionResult executionResult){
        ExecutionResultSet s = new ExecutionResultSet();
        s.add(executionResult);
        List<Double> systemConfidences = new ArrayList(getOccurringConfidences(executionResult.getSystemAlignment(), 2));
        Collections.sort(systemConfidences);
        for(Double conf : systemConfidences){
            s.get(executionResult, new ConfidenceRefiner(conf));
        }
        return s;
    }
    
    
    /**
     * Simple division that is to be performed. The two denominators will be added.
     *
     * @param numerator      Numerator of fraction
     * @param denominatorOne Denominator 1
     * @param denominatorTwo Denominator 2
     * @return Result as double.
     */
    private static double divideWithTwoDenominators(double numerator, double denominatorOne, double denominatorTwo) {
        if ((denominatorOne + denominatorTwo) > 0.0) {
            return numerator / (denominatorOne + denominatorTwo);
        } else {
            return 0.0;
        }
    }
    
}