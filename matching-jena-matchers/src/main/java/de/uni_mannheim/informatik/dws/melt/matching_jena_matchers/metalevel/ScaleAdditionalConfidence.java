package de.uni_mannheim.informatik.dws.melt.matching_jena_matchers.metalevel;

import de.uni_mannheim.informatik.dws.melt.matching_jena.MatcherYAAAJena;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Correspondence;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.jena.ontology.OntModel;

/**
 * Scales the correspondence confidence values linearly to an given interval (by default [0,1]).
 */
public class ScaleAdditionalConfidence extends MatcherYAAAJena{

    @Override
    public Alignment match(OntModel source, OntModel target, Alignment inputAlignment, Properties properties) throws Exception {
        return scale(inputAlignment, new HashSet());
    }
    
    public static Alignment scale(Alignment alignment, Set<String> keys){
        return scale(alignment, 0.0, 1.0, keys);
    }
    
    public static Alignment scale(Alignment alignment, double newMin, double newMax, Set<String> keys){
        //find min and max:
        Map<String, MinMax> keyToMinMax = new HashMap();
        if(keys.isEmpty()){
            for(Correspondence c : alignment){
                for(Entry<String, Double> entry : c.getAdditionalConfidences().entrySet()){
                    keyToMinMax.computeIfAbsent(entry.getKey(), k -> new MinMax(k)).updateMinMax(c);
                }
            }
        }else{
            for(Correspondence c : alignment){
                for(String key : keys){
                    keyToMinMax.computeIfAbsent(key, k -> new MinMax(k)).updateMinMax(c);
                }
            }
        }
        
        for(MinMax m : keyToMinMax.values()){
            m.computeRange(); //range = max - min;
        }
        
        for(MinMax m : keyToMinMax.values()){
            for(Correspondence c : alignment){
                m.setConfidence(c, newMin, newMax);
            }
        }
        return alignment;        
    }    
}

class MinMax{
    private String key;
    private double min;
    private double max;
    
    private double range;
    
    public MinMax(String key){
        this.key = key;
        this.min = Double.MAX_VALUE;
        this.max = 0.0d;
        this.range = 0.0d;
    }
    
    public void updateMinMax(Correspondence c){
        Double value = c.getAdditionalConfidence(key);
        if(value == null)
            return;
        if(value > max){
            max = value;
        }
        if(value < min){
            min = value;
        }
    }
    
    public void setConfidence(Correspondence c, double newMin, double newMax){
        Double value = c.getAdditionalConfidence(key);
        if(value == null)
            return;
        
        double scale = newMax-newMin;        
        if(range == 0.0){
            c.addAdditionalConfidence(key, (scale * value) + newMin);//override
        }else{
            c.addAdditionalConfidence(key, ((scale * (value - min))/ range) + newMin);//override
        }
    }
    
    public void computeRange(){
        this.range = max - min;
    }
    

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
