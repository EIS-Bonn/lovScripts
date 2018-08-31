package org.lov.cli;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.slf4j.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * @author Jaime M Trillos
 * @author Ana C Trillos
 * 
 * Unit test for simple App for the backend.
 */

public class AppTest {
	private final static Logger log = LoggerFactory.getLogger(AppTest.class);
	private String[] getInfoURI = new String[] { "http://www.ics.forth.gr/isl/oncm/core.owl" };
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Logger logAggregator = LoggerFactory.getLogger(Aggregator.class);
	
	
	//Test in suggest with a correct URI
	@Test
	public void test1() throws IOException {
		log.info("Start testing Suggest with a correct URI");
        System.setOut(new PrintStream(baos));
        Suggest.main(getInfoURI);
        baos.flush();
        String whatWasPrinted = new String(baos.toByteArray());
        String[] linesOfOutput = whatWasPrinted.split(//
                System.getProperty("line.separator"));
        assertEquals(1, linesOfOutput.length);
        assertEquals("{\"nbTriplesWithoutInf\":113,\"uri\":\"http://www.ics.forth.gr/isl/oncm/core\",\"uriInputSearch\":\"http://www.ics.forth.gr/isl/oncm/core.owl\",\"uriDeclared\":\"http://www.ics.forth.gr/isl/oncm/core\",\"nsp\":\"http://www.ics.forth.gr/isl/oncm/core#\",\"nspMostUsed\":\"http://www.ics.forth.gr/isl/oncm/core#\",\"nspDefault\":\"http://www.ics.forth.gr/isl/oncm/core#\",\"prefix\":\"onc\",\"prefixAssociatedNsp\":\"onc\",\"nbClasses\":8,\"nbProperties\":13,\"nbInstances\":0,\"nbDatatypes\":0,\"languages\":[{\"id\":\"54b2be018433ca9ccf1c0e0c\",\"uri\":\"http://id.loc.gov/vocabulary/iso639-2/eng\",\"label\":\"English\",\"iso639P3PCode\":\"eng\",\"iso639P1Code\":\"en\"}],\"titles\":[{\"value\":\"Open NEE Configuration Model\",\"lang\":\"en\"},{\"value\":\"The Open NEE Configuration Model\",\"lang\":\"en\"}],\"descriptions\":[{\"value\":\"The Open NEE Configuration Model defines a Linked Data-based model for describing a configuration supported by a Named Entity Extraction (NEE) service. It is based on the model proposed in \\\"Configuring Named Entity Extraction through Real-Time Exploitation of Linked Data\\\" (http://dl.acm.org/citation.cfm?doid\\u003d2611040.2611085) for configuring such services, and allows a NEE service to describe and publish as Linked Data its entity mining capabilities, but also to be dynamically configured.\",\"lang\":\"en\"}],\"dateIssued\":\"2015-02-04\",\"dateModified\":\"2015-04-03\",\"creators\":[{\"prefUri\":\"http://users.ics.forth.gr/~fafalios\"}],\"contributors\":[{\"prefUri\":\"http://users.ics.forth.gr/~fafalios\"},{\"prefUri\":\"http://users.ics.forth.gr/~tzitzik\"}],\"publishers\":[{\"prefUri\":\"http://www.ics.forth.gr/isl\"}],\"relMetadata\":[{\"nbTriplesWithoutInf\":0,\"uri\":\"https://w3id.org/seas/QUDTAlignment\",\"nsp\":\"http://purl.org/dc/terms/\",\"prefix\":\"qudta\",\"nbClasses\":0,\"nbProperties\":0,\"nbInstances\":0,\"nbDatatypes\":0},{\"nbTriplesWithoutInf\":0,\"uri\":\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\",\"nsp\":\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\",\"prefix\":\"rdf\",\"nbClasses\":0,\"nbProperties\":0,\"nbInstances\":0,\"nbDatatypes\":0},{\"nbTriplesWithoutInf\":0,\"uri\":\"http://www.w3.org/2000/01/rdf-schema#\",\"nsp\":\"http://www.w3.org/2000/01/rdf-schema#\",\"prefix\":\"rdfs\",\"nbClasses\":0,\"nbProperties\":0,\"nbInstances\":0,\"nbDatatypes\":0},{\"nbTriplesWithoutInf\":0,\"uri\":\"http://www.w3.org/2002/07/owl\",\"nsp\":\"http://www.w3.org/2002/07/owl#\",\"prefix\":\"owl\",\"nbClasses\":0,\"nbProperties\":0,\"nbInstances\":0,\"nbDatatypes\":0}],\"relSpecializes\":[{\"nbTriplesWithoutInf\":0,\"uri\":\"http://www.w3.org/2000/01/rdf-schema#\",\"nsp\":\"http://www.w3.org/2000/01/rdf-schema#\",\"prefix\":\"rdfs\",\"nbClasses\":0,\"nbProperties\":0,\"nbInstances\":0,\"nbDatatypes\":0},{\"nbTriplesWithoutInf\":0,\"uri\":\"http://www.w3.org/2004/02/skos/core\",\"nsp\":\"http://www.w3.org/2004/02/skos/core#\",\"prefix\":\"skos\",\"nbClasses\":0,\"nbProperties\":0,\"nbInstances\":0,\"nbDatatypes\":0}],\"relGeneralizes\":[],\"relExtends\":[{\"nbTriplesWithoutInf\":0,\"uri\":\"http://www.w3.org/2000/01/rdf-schema#\",\"nsp\":\"http://www.w3.org/2000/01/rdf-schema#\",\"prefix\":\"rdfs\",\"nbClasses\":0,\"nbProperties\":0,\"nbInstances\":0,\"nbDatatypes\":0}],\"relEquivalent\":[],\"relDisjunc\":[],\"relImports\":[{\"nbTriplesWithoutInf\":0,\"uri\":\"http://www.w3.org/2004/02/skos/\",\"nbClasses\":0,\"nbProperties\":0,\"nbInstances\":0,\"nbDatatypes\":0}]}", linesOfOutput[0]);
        log.info("End testing");
	}
	
	//Test in aggregator is returning a log.info
	@Test
	public void test2() throws IOException {
		log.info("Start testing Aggregator is returning something");
		Aggregator.main();
		assertEquals(true, logAggregator.isInfoEnabled());
		log.info("End testing");
	}
	
	
}
