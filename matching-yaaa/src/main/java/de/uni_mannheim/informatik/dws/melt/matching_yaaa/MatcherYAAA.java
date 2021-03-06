package de.uni_mannheim.informatik.dws.melt.matching_yaaa;

import de.uni_mannheim.informatik.dws.melt.matching_base.IMatcher;
import de.uni_mannheim.informatik.dws.melt.matching_base.MatcherFile;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.AlignmentParser;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.AlignmentSerializer;

import java.io.File;
import java.net.URL;
import java.util.Properties;

/**
 * A matcher template for matchers that are based on the YAAA Framework.
 * @author Sven Hertling
 */
public abstract class MatcherYAAA extends MatcherFile implements IMatcher<Alignment, URL> {

    @Override
    public void match(URL source, URL target, URL inputAlignment, File alignmentFile) throws Exception {

        Alignment m = new Alignment();
        
        if(inputAlignment != null){
            m = AlignmentParser.parse(inputAlignment);
        }
        Properties p = new Properties();
        
        m = this.match(source, target, m, p);
        
        AlignmentSerializer.serialize(m, alignmentFile);
    }

     /**
     * Aligns two ontologies specified via URL, with an input alignment
     * as Alignment object, and returns the mapping of the resulting alignment.
     *
     * Note: This method might be called multiple times in a row when using the evaluation framework.
     * Make sure to return a mapping which is specific to the given inputs.
     *
     * @param source this url represents the source ontology
     * @param target this url represents the target ontology
     * @param inputAlignment this mapping represents the input alignment
     * @param properties additional properties
     * @return The resulting mapping of the matching process.
     * @throws Exception An exception that was risen while matching.
     */
    @Override
    public abstract Alignment match(URL source, URL target, Alignment inputAlignment, Properties properties) throws Exception ;
    
}
