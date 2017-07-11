package org.lov.vocidex.describers;


import org.codehaus.jackson.node.ObjectNode;
import org.lov.SPARQLRunner;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Produces a JSON description of a pilot, using the metadata present in LOV.
 * 
 * @author trillos
 */

public class PilotDescriber extends SPARQLDescriber {
	public final static String TYPE = "pilot";

	public PilotDescriber(SPARQLRunner source) {
		super(source);
	}
	
	public void describe(Resource pilot, ObjectNode descriptionRoot) {
		QuerySolution qs = getSource().getOneSolution("pilot-name.sparql", "pilot", pilot);
		descriptionRoot.put("type", TYPE);
		putString(descriptionRoot, "name", qs.get("name").asLiteral().getLexicalForm()); 
		
		ResultSet rsComments = getSource().getResultSet("pilot-description.sparql", "pilot", pilot);
		while(rsComments.hasNext()){
			QuerySolution qs2 = rsComments.next();
			putString(descriptionRoot, "comment", qs2.get("comment").asLiteral().getLexicalForm());
		}
	}	
}