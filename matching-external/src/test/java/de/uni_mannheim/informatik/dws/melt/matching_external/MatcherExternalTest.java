package de.uni_mannheim.informatik.dws.melt.matching_external;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the external matcher (especially the search url functions)
 */
public class MatcherExternalTest {
    
    private String matcherresult = "2019-09-10 07:28:56 INFO  OntologyCacheJena:56 - Reading model into cache (http://repositories.seals-project.eu/tdrs/testdata/persistent/conference/conference-v1/suite/cmt-confof/component/source/)\n" +
" 2019-09-10 07:28:56 INFO  OntologyCacheJena:56 - Reading model into cache (http://repositories.seals-project.eu/tdrs/testdata/persistent/conference/conference-v1/suite/cmt-confof/component/target/)\n" +
" file:/tmp/alignment3572747689589156227.rdf\n"+
" Test";
    
    private List<String> possibleURIs = Arrays.asList(
            "file:/home/onetwothree.txt",
            "file:/tmp/alignment3572747689589156227.rdf",
            "file:/C:/Users/foobar/AppData/Local/Temp/alignment7123099207203017737.rdf",
            "file://localhost/etc/fstab",
            "file:///etc/fstab",
            "file://localhost/c$/WINDOWS/clock.avi",
            "file:///c:/WINDOWS/clock.avi",
            "file://hostname/path/to/the%20file.txt",
            "file:///c:/path/to/the%20file.txt",
            "file:////remotehost/share/dir/file.txt");
    
    @Test
    void getLastUrlTest() throws MalformedURLException {

        assertEquals(URI.create("file://foo.bar").toURL(), ProcessOutputCollector.findLastURL("Test file://test.de\nand so on\nfile://foo.bar\nnext line with some content."));
        assertEquals(URI.create("file:/tmp/alignment3572747689589156227.rdf").toURL(), ProcessOutputCollector.findLastURL(matcherresult));
        for(String uri : possibleURIs){
            assertEquals(URI.create(uri).toURL(), ProcessOutputCollector.findLastURL("test\nFoo" + uri + " bar\n test\n"));
        }
    }
}
