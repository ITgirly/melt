package de.uni_mannheim.informatik.dws.melt.matching_ml.python;

import de.uni_mannheim.informatik.dws.melt.matching_jena.MatcherYAAAJena;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Correspondence;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.CorrespondenceRelation;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.jena.ontology.OntModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MachineLearningScikitFilter extends MatcherYAAAJena {
    private static final Logger LOGGER = LoggerFactory.getLogger(MachineLearningScikitFilter.class);
    
    /**
     * Generator for training data. If relation is equivalence, then this is the positive class. 
     * All other relations are the negative class.
     */
    private MatcherYAAAJena trainingGenerator;
    
    /**
     * Which additional confidences should be used to train the classifier.
     */
    private List<String> confidenceNames;
    
    /**
     * Number of cross validation to execute.
     */
    private int cv;
    
    /**
     * Number of jobs to execute in parallel.
     */
    private int jobs;
    

    public MachineLearningScikitFilter() {
        this(new MatcherYAAAJena() {
            @Override
            public Alignment match(OntModel source, OntModel target, Alignment inputAlignment, Properties properties) throws Exception {
                return inputAlignment;
            }
        });
    }
    
    public MachineLearningScikitFilter(MatcherYAAAJena trainingGenerator) {
        this(trainingGenerator, null);
    }    

    public MachineLearningScikitFilter(MatcherYAAAJena trainingGenerator, List<String> confidenceNames) {
        this(trainingGenerator, confidenceNames, 5, 1);
    }
    
    public MachineLearningScikitFilter(MatcherYAAAJena trainingGenerator, int cv, int jobs) {
        this(trainingGenerator, null, cv, jobs);
    }

    /**
     * Constructor
     * @param trainingGenerator generator for traingdata
     * @param confidenceNames confidence names to use
     * @param cv Number of cross validation to execute.
     * @param jobs Number of jobs to execute in parallel.
     */
    public MachineLearningScikitFilter(MatcherYAAAJena trainingGenerator, List<String> confidenceNames, int cv, int jobs) {
        this.trainingGenerator = trainingGenerator;
        this.confidenceNames = confidenceNames;
        this.cv = cv;
        this.jobs = jobs;
    }
    
    
    
    @Override
    public Alignment match(OntModel source, OntModel target, Alignment inputAlignment, Properties properties) throws Exception {
        
        Alignment trainingAlignment = trainingGenerator.match(source, target, inputAlignment, properties);
        if(confidenceNames == null || confidenceNames.isEmpty())
            confidenceNames = getConfidenceKeys(trainingAlignment);
        if(confidenceNames.isEmpty()){
            LOGGER.warn("No attributes available for learning.");
            return inputAlignment;
        }
        File trainingFile = new File("trainingsFile.csv");
        writeDataset(new ArrayList(trainingAlignment), trainingFile, true);
        
        File testFile = new File("testFile.csv");
        List<Correspondence> testAlignment = new ArrayList(inputAlignment); // make order explicit
        writeDataset(testAlignment, testFile, false);

        List<Integer> prediction = Gensim.getInstance().learnAndApplyMLModel(trainingFile, testFile, this.cv, this.jobs);
        
        Gensim.shutDown();
        trainingFile.delete();
        testFile.delete();
        
        Alignment filteredAlignment = new Alignment(inputAlignment, false);
        Iterator<Integer> predictionIterator=prediction.iterator();
        Iterator<Correspondence> testAlignmentIterator = testAlignment.iterator();
        while(predictionIterator.hasNext() && testAlignmentIterator.hasNext()) {
            int pred = predictionIterator.next();
            Correspondence correspondence = testAlignmentIterator.next();
            if(pred == 1){
                filteredAlignment.add(correspondence);
            }
        }
        
        
        return filteredAlignment;
    }
    
    
    public void writeDataset(List<Correspondence> alignment, File file, boolean includeTarget) throws IOException{
        try(CSVPrinter csvPrinter = CSVFormat.DEFAULT.print(file, StandardCharsets.UTF_8)){            
            List<String> header = new ArrayList<>();
            header.addAll(confidenceNames);
            if(includeTarget)
                header.add("target");
            csvPrinter.printRecord(header);
            
            int positive = 0;
            int negative = 0;
            for(Correspondence c : alignment){
                List<Object> record = new ArrayList(confidenceNames.size());
                for(String confidenceName : confidenceNames){
                    record.add(c.getAdditionalConfidenceOrDefault(confidenceName, 0.0));                    
                }
                if(includeTarget){
                    //positive label is "1" by default in scikit learn
                    //https://scikit-learn.org/stable/modules/model_evaluation.html#from-binary-to-multiclass-and-multilabel
                    if(c.getRelation() == CorrespondenceRelation.EQUIVALENCE){
                        record.add(1); //positive
                        positive++;
                    }else{
                        record.add(0); //negative
                        negative++;
                    }
                }
                csvPrinter.printRecord(record);
            }
            if(includeTarget)
                LOGGER.info("Created TraningSet with {} positive and {} negative examples ({} attribute(s)).", positive, negative, confidenceNames.size());
        }
    }
    
    private List<String> getConfidenceKeys(Alignment alignment){
        Set<String> keySet = new HashSet();
        for(Correspondence c : alignment){
            keySet.addAll(c.getAdditionalConfidences().keySet());
        }
        return new ArrayList<>(keySet);
    }
}
