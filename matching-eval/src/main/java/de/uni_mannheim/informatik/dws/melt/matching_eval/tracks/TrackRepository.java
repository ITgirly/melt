package de.uni_mannheim.informatik.dws.melt.matching_eval.tracks;

import de.uni_mannheim.informatik.dws.melt.matching_eval.evaluator.metric.cm.GoldStandardCompleteness;
import de.uni_mannheim.informatik.dws.melt.yet_another_alignment_api.Alignment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Track repository which lists all different tracks in possibly multiple versions.
 *
 * @author Sven Hertling
 */
public class TrackRepository{
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackRepository.class);
    
    /**
     * Anatomy track.
     * The anatomy real world case is about matching the Adult Mouse Anatomy (2744 classes) and the NCI Thesaurus (3304 classes) describing the human anatomy.
     */
    public static class Anatomy {
        /** Default Anatomy Testsuite which is used all the time. */
        public static Track Default = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "anatomy_track", "anatomy_track-default");//new SealsTrack("http://repositories.seals-project.eu/tdrs/", "anatomy_track", "anatomy_track-default");
    }
    
    /**
     * Conference  track.
     * The goal of the track is to find alignments within a collection of ontologies describing the domain of organising conferences. 
     * Additionally, 'complex correspondences' are also very welcome. 
     * Alignments will be evaluated automatically against reference alignments also considering its uncertain version presented at ISWC 2014. 
     * Summary results along with detail performance results for each ontology pair (test case) and comparison with tools' performance from last years will be provided.
     */
    public static class Conference {
        /** Conference Testsuite V1 which is used all the time. */
        public static Track V1 = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "conference", "conference-v1");//new SealsTrack("http://repositories.seals-project.eu/tdrs/", "conference", "conference-v1");
    }

    /**
     * Multifarm track.
     * The goal of this track is to evaluate the ability of systems to deal with ontologies in different natural languages.
     * It serves the purpose of evaluating the strengths and the weaknesses of matchers and measuring their progress, with a focus on multilingualism.
     */
    public static class Multifarm {

        private static Set<String> languagePairs = new HashSet<String>(Arrays.asList( //all in all: 45
            "ar-cn", "ar-cz", "ar-de", "ar-en", "ar-es", "ar-fr", "ar-nl", "ar-pt", "ar-ru", 
            "cn-cz", "cn-de", "cn-en", "cn-es", "cn-fr", "cn-nl", "cn-pt", "cn-ru", 
            "cz-de", "cz-en", "cz-es", "cz-fr", "cz-nl", "cz-pt", "cz-ru", 
            "de-en", "de-es", "de-fr", "de-nl", "de-pt", "de-ru",
            "en-es", "en-fr", "en-nl", "en-pt", "en-ru", 
            "es-fr", "es-nl", "es-pt", "es-ru", 
            "fr-nl", "fr-pt", "fr-ru", 
            "nl-pt", "nl-ru",
            "pt-ru"
        ));

        private static Track getTrackByLanguagePair(String languagePair){
            return new SealsTrack("http://oaei.webdatacommons.org/tdrs/", languagePair, languagePair+"-v2", "repositories.seals-project.eu_multifarm");
        }

        public static List<Track> ALL = calculateAllMultifarmTracks();

        private static List<Track> calculateAllMultifarmTracks(){
            List<Track> benchmarks = new LinkedList<>();
            for(String languagePair : languagePairs){
                benchmarks.add(getTrackByLanguagePair(languagePair));
            }
            return benchmarks;
        }

        /**
         * Returns a specific track.
         * @param languagePair Language pair in the form {@code <first_language>-<second_language> }.
         *                     Valid options for {@code <first_language> } and for {@code <second_language> }:
         *                     ar, cn cz, de, en, es, fr, nl, pt.
         * @return The specified track if it exists.
         */
        public static Track getSpecificMultifarmTrack(String languagePair){
            if(languagePair == null) return null;
            languagePair = languagePair.trim().toLowerCase();
            if(languagePairs.contains(languagePair))
                return getTrackByLanguagePair(languagePair);
            LOGGER.warn("Language pair " + languagePair + " could not found - returning null.");
            return null;
        }

        /**
         * This method returns all multifarm tracks in which the specified language is involved.
         * @param language The language for which all tracks shall be returned.
         *                 Available options: ar, cn cz, de, en, es, fr, nl, pt.
         * @return A list of tracks which contain the specified language.
         */
        public static List<Track> getMultifarmTrackForLanguage(String language){
            if (language == null) return null;
            language = language.trim().toLowerCase();
            ArrayList<String> resultTrackNames = new ArrayList<>();
            ArrayList<Track> result = new ArrayList<>();
            for(String languagePair : languagePairs){
                if(languagePair.contains(language)){
                    resultTrackNames.add(languagePair);
                }
            }
            for(String trackName : resultTrackNames){
                result.add(getSpecificMultifarmTrack(trackName));
            }
            return result;
        }

        /**
         * Returns a specific track. If no combination for {@code firstLanguage-secondLanguage} is found,
         * {@code secondLanguage-firstLanguage} is used.
         * @param firstLanguage The first language of the track. Available options: ar, cn cz, de, en, es, fr, nl, pt.
         * @param secondLanguage The second language of the track. Available options: ar, cn cz, de, en, es, fr, nl, pt.
         * @return The specified track if it exists, else null.
         */
        public static Track getSpecificMultifarmTrack(String firstLanguage, String secondLanguage){
            if(firstLanguage == null || secondLanguage == null) return null;
            firstLanguage = firstLanguage.trim().toLowerCase();
            secondLanguage = secondLanguage.trim().toLowerCase();
            String pairString = firstLanguage + "-" + secondLanguage;
            Track result =  getSpecificMultifarmTrack(pairString);
            if(result == null){
                String newPairString = secondLanguage + "-" + firstLanguage;
                LOGGER.warn("No track found for language pair: " + pairString + ". Trying now" + newPairString + ".");
                return getSpecificMultifarmTrack(newPairString);
            }
            return result;
        }
        
        public static List<TestCase> getSameOntologies(){
            return getSameOrDifferentOntologies(ALL, true);
        }
        
        public static List<TestCase> getSameOntologies(List<Track> multiFarmLanguageTracks){
            return getSameOrDifferentOntologies(multiFarmLanguageTracks, true);
        }
        
        public static List<TestCase> getDifferentOntologies(){
            return getSameOrDifferentOntologies(ALL, false);
        }
        
        public static List<TestCase> getDifferentOntologies(List<Track> multiFarmLanguageTracks){
            return getSameOrDifferentOntologies(multiFarmLanguageTracks, false);
        }
        
        
        public static List<TestCase> getSameOrDifferentOntologies(boolean same){
            return getSameOrDifferentOntologies(ALL, same);
        }
        
        public static List<TestCase> getSameOrDifferentOntologies(List<Track> multiFarmLanguageTracks, boolean same){
            List<TestCase> testCases = new ArrayList<>();
            for(Track track : multiFarmLanguageTracks){
                for(TestCase testCase : track.getTestCases()){
                    if(isTestCaseSameOntology(testCase) == same){
                        testCases.add(testCase);
                    }
                }
            }
            return testCases;
        }
        
        public static boolean isTestCaseSameOntology(TestCase tc){
            String[] splits = tc.getName().split("-");
            if(splits.length <= 1){
                LOGGER.warn("Split of test case name in multifarm track is less than 1 which should normally be 4. Return false for same Ontology test.");
                return false;
            }
            return splits[0].equals(splits[1]);
        }
    }
    
    /**
     * Complex track.
     * This track evaluates the detection of complex correspondences between ontologies of four different domains: 
     * conference, hydrography, geography and species taxonomy. Each dataset has its particularities and evaluation modalities.
     */
    public static class Complex {
        /** This dataset is based on the OntoFarm dataset [1] used in the Conference track of the OAEI campaigns. */
        public static Track Conference  = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "conference", "conference-v1");
        
        /**The hydrography dataset is composed of four source ontologies (Hydro3, HydrOntology_native, HydrOntology_translated, and Cree) that each should be aligned to a single target Surface Water Ontology (SWO).**/
        public static Track Hydrography  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "hydrography", "hydrography-v1");
        
        /**This dataset is from the GeoLink project, which was funded under the U.S. National Science Foundation's EarthCube initiative.**/
        public static Track GeoLink  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "geolink", "geolink-v1");
        
        /**PopgeoLink**/
        public static Track PopgeoLink  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "popgeolink", "popgeolink-v1");
        
        /**Popenslaved **/
        public static Track Popenslaved  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "popenslaved", "popenslaved-v1");
        
        /**Popconference **/
        public static Track Popconference0  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "popconference", "popconference-0-v1");
        public static Track Popconference20  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "popconference", "popconference-20-v1");
        public static Track Popconference40  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "popconference", "popconference-40-v1");
        public static Track Popconference60  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "popconference", "popconference-60-v1");
        public static Track Popconference80  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "popconference", "popconference-80-v1");
        public static Track Popconference100  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "popconference", "popconference-100-v1");
    }
    
    /**
     * Instance Matching
     */
    public static class InstanceMatching {

        /**GeoLinkCruise **/
        public static Track GeoLinkCruise  = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "geolinkcruise", "geolinkcruise-v1");
    }

    /**
     * Large Biomedical Ontologies.
     * This track consists of finding alignments between the Foundational Model of Anatomy (FMA), SNOMED CT, and the National Cancer Institute Thesaurus (NCI).
     * These ontologies are semantically rich and contain tens of thousands of classes. UMLS Metathesaurus has been selected as the basis for the track reference alignments.
     */
    public static class Largebio {
        
        /** 2016 version of Large Biomedical Ontologies - these are also used for 2017 and 2018. */
        public static class V2016 {
            
            /** With the this benchmark all 6 largebio matching tasks are executed. For individual matching tasks refer to other benchmarks. */
            public static Track ALL = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "largebio", "largebio-all_tasks_2016");
            
            /**
             * Task 1: FMA-NCI small fragments:
             * This task consists of matching two (relatively) small fragments of FMA and NCI. 
             * The FMA fragment contains 3,696 classes (5% of FMA), while the NCI fragment contains 6,488 classes (10% of NCI).
             * Together 10,184 classes
             */
            public static Track FMA_NCI_SMALL = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "largebio", "largebio-fma_nci_small_2016");

            /**
             * Task 2: FMA-NCI whole ontologies.
             * This task consists of matching the whole FMA and NCI ontologies, which contains 78,989 and 66,724 classes, respectively.
             * Together 145,713 classes
             */
            public static Track FMA_NCI_WHOLE = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "largebio", "largebio-fma_nci_whole_2016");
            
            /**
             * Task 3: FMA-SNOMED small fragments.
             * This task consists of matching two (relatively) small fragments of FMA and SNOMED. 
             * The FMA fragment contains 10,157 classes (13% of FMA), while the SNOMED fragment contains 13,412 classes (5% of SNOMED).
             * Together 23,569 classes
             */
            public static Track FMA_SNOMED_SMALL = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "largebio", "largebio-fma_snomed_small_2016");
            
            /**
             * Task 4: FMA whole ontology.
             * This task consists of matching the whole FMA that contains 78,989 classes with a large SNOMED fragment that contains 122,464 classes (40% of SNOMED).
             * Together 201,453 classes.
             */
            public static Track FMA_SNOMED_WHOLE = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "largebio", "largebio-fma_snomed_whole_2016");
            
            /**
             * Task 5: SNOMED-NCI small fragments. 
             * This task consists of matching two (relatively) small fragments of SNOMED and NCI. 
             * The SNOMED fragment contains 51,128 classes (17% of SNOMED), while the NCI fragment contains 23,958 classes (36% of NCI).
             * Together 75,086 classes.
             */
            public static Track SNOMED_NCI_SMALL = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "largebio", "largebio-snomed_nci_small_2016");
            
            /**
             * Task 6: NCI whole ontology with SNOMED large fragment.
             * This task consists of matching the whole NCI that contains 66,724 classes with a large SNOMED fragment that contains 122,464 classes (40% of SNOMED).
             * Together 189,188 classes.
             */
            public static Track SNOMED_NCI_WHOLE = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "largebio", "largebio-snomed_nci_whole_2016");
        }
        
        /** 2015 version of Large Biomedical Ontologies. */
        public static class V2015 {
            
            /**
             * Task 1: FMA-NCI small fragments:
             * This task consists of matching two (relatively) small fragments of FMA and NCI. 
             * The FMA fragment contains 3,696 classes (5% of FMA), while the NCI fragment contains 6,488 classes (10% of NCI).
             * Together 10,184 classes
             */
            public static Track FMA_NCI_SMALL = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-fma_nci_small_2015");

            /**
             * Task 1: FMA-NCI small fragments:
             * This task consists of matching two (relatively) small fragments of FMA and NCI. 
             * The FMA fragment contains 3,696 classes (5% of FMA), while the NCI fragment contains 6,488 classes (10% of NCI).
             * Together 10,184 classes
             * With incoherence-causing mappings in the alignment flagged as "?" (unknown).
             */
            public static Track FMA_NCI_SMALL_FLAGGED = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-fma_nci_small_flagged_2015");
            
            /**
             * Task 2: FMA-NCI whole ontologies.
             * This task consists of matching the whole FMA and NCI ontologies, which contains 78,989 and 66,724 classes, respectively.
             * Together 145,713 classes
             */
            public static Track FMA_NCI_WHOLE = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-fma_nci_whole_2015");
            
            /**
             * Task 2: FMA-NCI whole ontologies.
             * This task consists of matching the whole FMA and NCI ontologies, which contains 78,989 and 66,724 classes, respectively.
             * Together 145,713 classes
             * With incoherence-causing mappings in the alignment flagged as "?" (unknown). 
             */
            public static Track FMA_NCI_WHOLE_FLAGGED = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-fma_nci_whole_flagged_2015");
            
            /**
             * Task 3: FMA-SNOMED small fragments.
             * This task consists of matching two (relatively) small fragments of FMA and SNOMED. 
             * The FMA fragment contains 10,157 classes (13% of FMA), while the SNOMED fragment contains 13,412 classes (5% of SNOMED).
             * Together 23,569 classes
             */
            public static Track FMA_SNOMED_SMALL = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-fma_snomed_small_2015");
            
            /**
             * Task 3: FMA-SNOMED small fragments.
             * This task consists of matching two (relatively) small fragments of FMA and SNOMED. 
             * The FMA fragment contains 10,157 classes (13% of FMA), while the SNOMED fragment contains 13,412 classes (5% of SNOMED).
             * Together 23,569 classes
             * With incoherence-causing mappings in the alignment flagged as "?" (unknown).
             */
            public static Track FMA_SNOMED_SMALL_FLAGGED = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-fma_snomed_small_flagged_2015");
                        
            /**
             * Task 4: FMA whole ontology.
             * This task consists of matching the whole FMA that contains 78,989 classes with a large SNOMED fragment that contains 122,464 classes (40% of SNOMED).
             * Together 201,453 classes.
             */
            public static Track FMA_SNOMED_WHOLE = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-fma_snomed_whole_2015");
            
            /**
             * Task 4: FMA whole ontology.
             * This task consists of matching the whole FMA that contains 78,989 classes with a large SNOMED fragment that contains 122,464 classes (40% of SNOMED).
             * Together 201,453 classes.
             * With incoherence-causing mappings in the alignment flagged as "?" (unknown).
             */
            public static Track FMA_SNOMED_WHOLE_FLAGGED = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-fma_snomed_whole_flagged_2015");
            
            
            /**
             * Task 5: SNOMED-NCI small fragments. 
             * This task consists of matching two (relatively) small fragments of SNOMED and NCI. 
             * The SNOMED fragment contains 51,128 classes (17% of SNOMED), while the NCI fragment contains 23,958 classes (36% of NCI).
             * Together 75,086 classes.
             */
            public static Track SNOMED_NCI_SMALL = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-snomed_nci_small_2015");
            
            /**
             * Task 5: SNOMED-NCI small fragments. 
             * This task consists of matching two (relatively) small fragments of SNOMED and NCI. 
             * The SNOMED fragment contains 51,128 classes (17% of SNOMED), while the NCI fragment contains 23,958 classes (36% of NCI).
             * Together 75,086 classes.
             * With incoherence-causing mappings in the alignment flagged as "?" (unknown).
             */
            public static Track SNOMED_NCI_SMALL_FLAGGED = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-snomed_nci_small_flagged_2015");
            
            
            /**
             * Task 6: NCI whole ontology with SNOMED large fragment.
             * This task consists of matching the whole NCI that contains 66,724 classes with a large SNOMED fragment that contains 122,464 classes (40% of SNOMED).
             * Together 189,188 classes.
             */
            public static Track SNOMED_NCI_WHOLE = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-snomed_nci_whole_2015");
            
            /**
             * Task 6: NCI whole ontology with SNOMED large fragment.
             * This task consists of matching the whole NCI that contains 66,724 classes with a large SNOMED fragment that contains 122,464 classes (40% of SNOMED).
             * Together 189,188 classes.
             * With incoherence-causing mappings in the alignment flagged as "?" (unknown).
             */
            public static Track SNOMED_NCI_WHOLE_FLAGGED = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "largebio", "largebio-snomed_nci_whole_flagged_2015");
        }
    }
    
    /**
     * Disease and Phenotype track.
     * The Pistoia Alliance Ontologies Alignment project team organises and sponsors this track based on a real use case where it is required to find alignments between disease and phenotype ontologies.
     * Specifically, the selected ontologies are:
     * <ul>
     * <li>Human Phenotype Ontology (HP)</li>
     * <li>Mammalian Phenotype Ontology (MP)</li>
     * <li>Human Disease Ontology (DOID)</li>
     * <li>Orphanet and Rare Diseases Ontology (ORDO)</li>
     * <li>Medical Subject Headings Ontology (MESH)</li>
     * <li>Online Mendelian Inheritance in Man Ontology (OMIM)</li>
     * </ul>
     */
    public static class Phenotype {
        /** 2016 version of DiseasePhenotype Track */
        public static class V2016 {
            /** This task consists of matching the HP (11,828 entities) and MP (11,752 entities) ontologies. The BioPortal-based alignment contains 639 mappings. */
            public static Track HP_MP = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "phenotype", "phenotype-hp-mp-baseline");
            
            /** This task consists of matching the DOID (9,301 entities) and ORDO (12,974) ontologies. The BioPortal-based alignment contains 1,018 mappings. */
            public static Track DOID_ORDO = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "phenotype", "phenotype-doid-ordo-baseline");
        }
        /** 2017 version of DiseasePhenotype Track (HP_MP and DOID_ORDO also used in 2018) */
        public static class V2017 {
            
            /** This task consists of matching the HP (31,034 classes) and MP (30,273 entities) ontologies. The BioPortal-based alignment contains 696 mappings. */
            public static Track HP_MP = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "phenotype", "phenotype-hp-mp-2017-bioportal");
            
            /** This task consists of matching the DOID (38,240 classes) and ORDO (13,504) ontologies. The BioPortal-based alignment contains 1,237 mappings. */
            public static Track DOID_ORDO = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "phenotype", "phenotype-doid-ordo-2017-bioportal");
            
            /** This task consists of matching the HP (31,034 classes) and MESH (305,349) ontologies. The BioPortal-based alignment contains 2,466 mappings. Not used in 2018*/
            public static Track HP_MESH = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "phenotype", "phenotype-hp-mesh-2017-bioportal");
            
            /** This task consists of matching the HP (31,034 classes) and OMIM (93,048) ontologies. The BioPortal-based alignment contains 3,768 mappings. Not used in 2018*/
            public static Track HP_OMIM = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "phenotype", "phenotype-hp-omim-2017-bioportal");
        }        
    }
    
    
    /**
     * Biodiv track.
     * The goal of the track is to find pairwise alignments between the Environment Ontology (ENVO) and 
     * the Semantic Web for Earth and Environment Technology Ontology (SWEET), and between the Plant Trait Ontology (PTO) and 
     * the Flora Phenotype Ontology (FLOPO). These ontologies are particularly useful for biodiversity and ecology research and are being used in various projects. 
     * They have been developed in parallel and are very overlapping. They are semantically rich and contain tens of thousands of classes.
     */
    public static class Biodiv {
        /** Default Testsuite which is used all the time. */
        public static Track Default = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "biodiv", "2018");//new BioDivTrack("http://oaei.ontologymatching.org/2018/biodiv/data/biodiv.zip", "biodiv", "2018");
    }
    
    /**
     * HOBBIT Spimbench
     * The goal of this track is to determine when two OWL instances describe the same Creative Work. The datasets are generated and transformed using 
     * SPIMBENCH by altering a set of original data through value-based, structure-based, and semantics-aware transformations (simple combination of transformations).
     */
    public static class Spimbench {
        
    }
    
    /**
     * HOBBIT Link Discovery.
     * In this track two benchmark generators are proposed to deal with link discovery for spatial data where spatial data are represented as trajectories (i.e., sequences of longitude, latitude pairs).
     * This new track is based on the HOBBIT platform and it requires to follow different intructions from the SEALS-based tracks.
     */
    public static class Link {
        /** The default HOBBIT Link Discovery Task*/
        public static Track Default = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "link", "2017");//new LinkTrack("http://islab.di.unimi.it/content/im_oaei/2017/data/FORTH_sandbox.tar.gz", "link", "2017", "Tbox1.nt", "Tbox2.nt", "refalign.rdf");
    }
    
    
    /**
     * IIMB track.
     * IIMB is an OWL-based dataset that is automatically generated by introducing a set of controlled transformations in an initial OWL Abox, 
     * in order i) to provide an evaluation dataset for various kinds of data transformations, 
     * including value transformations, structural transformations, and logical transformations, 
     * and ii) to cover a wide spectrum of possible techniques and tools.
     */
    public static class IIMB {
        /** Version V1 of the IIMB track */
        public static Track V1 = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "iimb", "v1");
    }
    
    /**
     * Doremus track.
     * The DOREMUS track is based on data from the DOREMUS project describing musical works from the catalogs 
     * of two French cultural institutions: La Bibliothque Nationale de France (BnF) and La Philharmonie de Paris (PP).
     */
    public static class Doremus {
        // TODO implement
    }
    
    
    /**
     * Knowledgegraph track.
     * The Knowledge Graph Track contains nine isolated knowledge graphs with instance and schema data. 
     * The goal of the task is to match both the instances and the schema.
     */
    public static class Knowledgegraph {
        /**The Knowledge Graph Track contains nine isolated knowledge graphs with instance and schema data. The goal of the task is to match both the instances and the schema.**/
        public static Track V1 = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "knowledgegraph", "v1", true, GoldStandardCompleteness.PARTIAL_SOURCE_COMPLETE_TARGET_COMPLETE);
        
        /**The Knowledge Graph Track contains nine isolated knowledge graphs with instance and schema data. The goal of the task is to match both the instances and the schema.**/
        public static Track V2 = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "knowledgegraph", "v2", true, GoldStandardCompleteness.PARTIAL_SOURCE_COMPLETE_TARGET_COMPLETE);
        
         /**The Knowledge Graph Track contains isolated knowledge graphs with instance and schema data. The goal of the task is to match both the instances and the schema.**/
        public static Track V3 = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "knowledgegraph", "v3", true, GoldStandardCompleteness.PARTIAL_SOURCE_COMPLETE_TARGET_COMPLETE);

        /**The Knowledge Graph Track contains isolated knowledge graphs with instance and schema data. The goal of the task is to match both the instances and the schema.**/
        public static Track V4 = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "knowledgegraph", "v4", true, GoldStandardCompleteness.PARTIAL_SOURCE_COMPLETE_TARGET_COMPLETE);

        /**The Knowledge Graph Track contains isolated knowledge graphs with instance and schema data. The goal of the task is to match both the instances and the schema.**/
        public static Track V3_NonMatch_Small = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "knowledgegraph", "v3-nonmatch-small", true, GoldStandardCompleteness.PARTIAL_SOURCE_COMPLETE_TARGET_COMPLETE);
        
        /**The Knowledge Graph Track contains isolated knowledge graphs with instance and schema data. The goal of the task is to match both the instances and the schema.**/
        public static Track V3_NonMatch_Large = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "knowledgegraph", "v3-nonmatch-large", true, GoldStandardCompleteness.PARTIAL_SOURCE_COMPLETE_TARGET_COMPLETE);
    }
    
    /**
     * Laboratory Analytics Domain track.
     */
    public static class Laboratory {        
        /** Version V1 of Laboratory Track */
        public static Track V1 = new SealsTrack("http://oaei.webdatacommons.org/tdrs/", "laboratory", "laboratory-v1");
    }
    
    public static class SystematicBenchmark {
        public static class Biblio {
            public static class V2016 {
                public static Track R1 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-biblio", "2016benchmarks-biblio-r1");
                public static Track R2 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-biblio", "2016benchmarks-biblio-r2");
                public static Track R3 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-biblio", "2016benchmarks-biblio-r3");
                public static Track R4 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-biblio", "2016benchmarks-biblio-r4");
                public static Track R5 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-biblio", "2016benchmarks-biblio-r5");
            }
        }
         public static class Film {
             public static class V2016 {
                public static Track R1 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-film", "2016benchmarks-film-r1");
                public static Track R2 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-film", "2016benchmarks-film-r2");
                public static Track R3 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-film", "2016benchmarks-film-r3");
                public static Track R4 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-film", "2016benchmarks-film-r4");
                public static Track R5 = new SealsTrack("http://repositories.seals-project.eu/tdrs/", "2016benchmarks-film", "2016benchmarks-film-r5");
            }
         }
    }
    
    
    private static Set<Track> definedTracks = null;
    
    /**
     * This method returns all tracks defined in TrackRepository with java reflections.
     * It uses all public static fields (of type Track or Collection of Track) in all nested classes.
     * @return a set of Tracks defined in this class.
     */
    public static Set<Track> getDefinedTracks(){
        if(definedTracks == null){
            definedTracks = retriveDefinedTracks();
        }
        return definedTracks;
    }
    
    private static Map<String, Track> trackNameAndVersionToTrack = null;
    
    /**
     * Return a map between track name and version and the track object.
     * This is used to find tracks whenloading from file.
     * @return map between track name, version and the track object
     */
    public static Map<String, Track> getMapFromTrackNameAndVersionToTrack(){
        if(trackNameAndVersionToTrack == null){
            trackNameAndVersionToTrack = new HashMap<>();
            for(Track t : getDefinedTracks()){
                trackNameAndVersionToTrack.put(t.getNameAndVersionString(), t);
            }
        }
        return trackNameAndVersionToTrack;
    }
    
    
    /**
     * This method retrives all tracks defined in TrackRepository with java reflections.
     * It uses all public static fields (of type Track or Collection of Track) in all nested classes.
     * @return Set of defined Tracks.
     */
    private static Set<Track> retriveDefinedTracks(){
        Set<Track> tracks = new HashSet<>();
        
        Queue<Class<?>> classesToInspect = new LinkedList();
        Collections.addAll(classesToInspect, TrackRepository.class.getDeclaredClasses());
        while(!classesToInspect.isEmpty()){
            Class<?> clazz = classesToInspect.poll();
            //LOGGER.info("Inspect class {} for static Track instance (full path : {}).", clazz.getSimpleName(), clazz.getName());
            for(Field field : clazz.getDeclaredFields()){
                int fieldModifier = field.getModifiers();
                if(Modifier.isStatic(fieldModifier) && Modifier.isPublic(fieldModifier)){                    
                    if(Track.class.isAssignableFrom(field.getType())){
                        try {
                            tracks.add((Track) field.get(null));
                        } catch (IllegalArgumentException | IllegalAccessException ex) {
                            LOGGER.error("Can not get track from TrackRepository with reflection.", ex);
                        }
                    }else if(Collection.class.isAssignableFrom(field.getType())){
                        //check generic parameter of collection
                        Type genericFieldType = field.getGenericType();
                        if(genericFieldType instanceof ParameterizedType){
                            ParameterizedType aType = (ParameterizedType) genericFieldType;
                            Type[] fieldArgTypes = aType.getActualTypeArguments();
                            if(fieldArgTypes.length > 0){
                                if(Track.class.isAssignableFrom((Class<?>)fieldArgTypes[0])){
                                    try {
                                        tracks.addAll((Collection<Track>) field.get(null));
                                    } catch (IllegalAccessException | IllegalArgumentException | ClassCastException ex) {
                                        LOGGER.error("Can not get track from TrackRepository with reflection.", ex);
                                    }
                                }
                            }
                        }
                    }
                }
            }            
            Collections.addAll(classesToInspect, clazz.getDeclaredClasses());
        }
        
        return tracks;
    }
    
    
    public static TestCase generateTestCaseWithSampledReferenceAlignment(TestCase tc, double fraction, Random randomSeed){
        Alignment sample = tc.getParsedReferenceAlignment().sampleByFraction(fraction, randomSeed);
        try {
            File f = File.createTempFile("ref_sample", ".rdf");
            sample.serialize(f);
            return new TestCase(tc.getName(), tc.getSource(), tc.getTarget(), tc.getReference(), tc.getTrack(), f.toURI(), tc.getGoldStandardCompleteness());
        } catch (IOException ex) {
            LOGGER.error("Could not write sample reference alignment to file", ex);
            return tc;
        }
    }
    
    public static TestCase generateTestCaseWithSampledReferenceAlignment(TestCase tc, double fraction, long randomSeed){
        return  generateTestCaseWithSampledReferenceAlignment(tc, fraction, new Random(randomSeed));
    }
    
    public static TestCase generateTestCaseWithSampledReferenceAlignment(TestCase tc, double fraction){
        return  generateTestCaseWithSampledReferenceAlignment(tc, fraction, new Random());
    }
}
