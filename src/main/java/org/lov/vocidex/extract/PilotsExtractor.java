package org.lov.vocidex.extract;

import java.util.Iterator;

import org.lov.SPARQLRunner;
import org.lov.vocidex.VocidexDocument;
import org.lov.vocidex.describers.PilotDescriber;

import com.hp.hpl.jena.query.Dataset;

/**
 * Extracts Pilots from a dataset containing the LOV dump. 
 * Will create one document for each pilot.
 * 
 * @author trillos
 */
public class PilotsExtractor  implements Extractor {
	private final SPARQLRunner source;
	
	public PilotsExtractor(Dataset dataset) {
		this.source = new SPARQLRunner(dataset,"rdf2es");
	}
	
	public Iterator<VocidexDocument> pilots() {
		return createDescriptionIterator("list-lov-pilots.sparql", "pilot", new PilotDescriber(source));
	}
	
	@Override
	public Iterator<VocidexDocument> iterator() {
		return pilots();
	}
	
	private Iterator<VocidexDocument> createDescriptionIterator(
			String sparqlFileName, String sparqlResultVariable, PilotDescriber describer) {
		return new DescriberIterator(
				source.getURIs(sparqlFileName, null, null, sparqlResultVariable),describer);
	}
}