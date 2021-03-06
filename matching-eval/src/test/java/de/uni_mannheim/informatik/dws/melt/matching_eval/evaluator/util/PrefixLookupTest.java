package de.uni_mannheim.informatik.dws.melt.matching_eval.evaluator.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class PrefixLookupTest {
    @Test
    void getPrefixedUri() {
        assertEquals("protege:", PrefixLookup.DEFAULT.getPrefix("http://protege.stanford.edu/plugins/owl/protege#"));
        assertEquals("protege:Test", PrefixLookup.DEFAULT.getPrefix("http://protege.stanford.edu/plugins/owl/protege#Test"));
        assertEquals("owl:Class", PrefixLookup.DEFAULT.getPrefix("http://www.w3.org/2002/07/owl#Class"));
        assertEquals("http://my-ontology-uri.com/vocabulary#concept", PrefixLookup.DEFAULT.getPrefix("http://my-ontology-uri.com/vocabulary#concept"));
        assertEquals("http://my-ontology-uri.com/vocabulary/concept", PrefixLookup.DEFAULT.getPrefix("http://my-ontology-uri.com/vocabulary/concept"));
    }

}
