package de.uni_mannheim.informatik.dws.ontmatching.matchingeval.evaluator;

import de.uni_mannheim.informatik.dws.ontmatching.matchingeval.ExecutionResult;
import de.uni_mannheim.informatik.dws.ontmatching.matchingeval.ExecutionResultSet;
import de.uni_mannheim.informatik.dws.ontmatching.matchingeval.tracks.TrackRepository;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    /**
     * Note that a network connection might be required.
     */
    @Test
    void getResultsDirectoryTrackMatcher() {

        // TODO uncomment as soon as SEALS servers are up again
        /*
        try {
            ExecutionResult result = new ExecutionResult(TrackRepository.Conference.V1.getTestCases().get(0), "myMatcher", null, null);
            ExecutionResultSet results = new ExecutionResultSet();
            results.add(result);
            EvaluatorCSV evaluatorCSV = new EvaluatorCSV(results);
            File solution = new File("./results/conference_conference-v1/aggregated");
            assertEquals(evaluatorCSV.getResultsDirectoryTrackMatcher(new File("./results"), TrackRepository.Conference.V1).getCanonicalPath(), solution.getCanonicalPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        */

    }


}