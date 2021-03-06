package de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CorrespondenceTest {

    @Test
    void parseRelation() {
        assertEquals(CorrespondenceRelation.EQUIVALENCE, CorrespondenceRelation.parse("="));
    }
    
    @Test
    void parseVarargsExtensions() {
        Correspondence c = new Correspondence("one", "two", 0.0, CorrespondenceRelation.EQUIVALENCE, "key1", "value1", "key2", "value2");
        
        assertEquals("value1", c.getExtensions().getOrDefault("key1", ""));
        assertEquals("value2", c.getExtensions().getOrDefault("key2", ""));
    }
    
    @Test
    void parseVarargsExtensionsUneven() {
        Correspondence c = new Correspondence("one", "two", 0.0, CorrespondenceRelation.EQUIVALENCE, "key1", "value1", "key2");
        
        assertEquals("value1", c.getExtensions().getOrDefault("key1", ""));
        assertEquals("", c.getExtensions().getOrDefault("key2", ""));
    }
    
    
    @Test
    void testAdditionalConfidences() {
        Correspondence c = new Correspondence("one", "two");
        c.addAdditionalConfidence("mykey", 0.5);
        c.addAdditionalConfidence("test", 0.6);
        c.addExtensionValue("http://test.com", 0.7);
        
        assertEquals(null, c.getAdditionalConfidence("notExistent"));
        assertEquals(0.6, c.getAdditionalConfidence("test"));
        assertEquals(2, c.getAdditionalConfidences().size());
        assertTrue(c.getAdditionalConfidences().containsKey("mykey"));
        assertTrue(c.getAdditionalConfidences().containsKey("test"));
        
        //test getordefaut
        assertEquals(0.5, c.getAdditionalConfidenceOrDefault("notExistent", 0.5));
        assertEquals(0.6, c.getAdditionalConfidenceOrDefault("test", 0.5));
    }
    
    @Test
    void testAdditionalExplanations() {
        Correspondence c = new Correspondence("one", "two");
        c.addAdditionalExplanation("mykey", "hello");
        c.addAdditionalExplanation("test", "hello2");
        c.addExtensionValue("http://test.com", 0.7);
        
        assertEquals(null, c.getAdditionalExplanation("notExistent"));
        assertEquals("hello", c.getAdditionalExplanation("mykey"));
        assertEquals(2, c.getAdditionalExplanations().size());
        assertTrue(c.getAdditionalExplanations().containsKey("mykey"));
        assertTrue(c.getAdditionalExplanations().containsKey("test"));
    }
    
    @Test
    void testToString() {
        Correspondence c = new Correspondence("one", "two");
        c.addExtensionValue("http://test.com", 0.7);
        
        assertEquals("<one,two,1.0,=>", c.toString());
        assertEquals("<one,two,1.0,=,{http://test.com=0.7}>", c.toStringWithExtensions());
    }
    
    @Test
    void testAddAdditonalConfidenceIfHigher() {
        Correspondence c = new Correspondence("one", "two");
        c.addAdditionalConfidenceIfHigher("test", 0.6);
        c.addAdditionalConfidenceIfHigher("test", 0.3);
        assertEquals(0.6, c.getAdditionalConfidence("test"));
        
        c.addAdditionalConfidenceIfHigher("test", 0.7);
        assertEquals(0.7, c.getAdditionalConfidence("test"));
    }
}