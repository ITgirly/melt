package de.uni_mannheim.informatik.dws.ontmatching.matchingjenamatchers;

import de.uni_mannheim.informatik.dws.ontmatching.matchingjena.OntologyCacheJena;
import de.uni_mannheim.informatik.dws.ontmatching.matchingjenamatchers.structurelevel.MatchPropBasedOnClass;
import de.uni_mannheim.informatik.dws.ontmatching.yetanotheralignmentapi.Alignment;
import java.io.File;
import java.net.URL;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class MatchPropBasedOnClassTest {
    private static final String namespaceSource = "http://ontmatching.dws.informatik.uni-mannheim.de/source/";
    private static final String namespaceTarget = "http://ontmatching.dws.informatik.uni-mannheim.de/target/";
    
    @Test
    void testSimple() {
        
        OntModel source = generate(namespaceSource);
        OntModel target = generate(namespaceTarget);

        Alignment inputAlignment = new Alignment();
        inputAlignment.add(namespaceSource + "domain", namespaceTarget + "domain");
        inputAlignment.add(namespaceSource + "range", namespaceTarget + "range");
        
        MatchPropBasedOnClass matcher = new MatchPropBasedOnClass();        
        Alignment output = matcher.getPropertyMatches(source, target, inputAlignment);        
        assertTrue(output.size() > 0);
        
        
        inputAlignment = new Alignment();
        inputAlignment.add(namespaceSource + "domain", namespaceTarget + "domain", 0.5);
        inputAlignment.add(namespaceSource + "range", namespaceTarget + "range", 1.0);
        
        output = matcher.getPropertyMatches(source, target, inputAlignment);        
        assertTrue(output.size() > 0);
        assertEquals(0.75, output.iterator().next().getConfidence());
    }
    
    
    private OntModel generate(String namespace){
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);        
        OntClass domain = model.createClass(namespace + "domain");        
        OntClass range = model.createClass(namespace + "range");        
        ObjectProperty prop = model.createObjectProperty(namespace + "prop");    
        prop.addDomain(domain);
        prop.addRange(range);
        return model;
    }
    
}
