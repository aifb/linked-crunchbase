package utilities;

import java.io.StringWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;



public class SPARQLLoader {
	private final static String CBWPREFIX = "http://km.aifb.kit.edu/services/crunchbase/api";
	private final static String SERVICE = "http://aifb-ls3-maia.aifb.kit.edu:8890/sparql";
	private final static String constructQueryString = 
			"PREFIX cb: <http://ontologycentral.com/2010/05/cb/vocab#>" +
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
			"CONSTRUCT {" +
						"?s foaf:homepage ?url . " +
						"?s rdf:type ?type . " +
			"} FROM <http://km.aifb.kit.edu/services/crunchbase>  where {" + 
						"?s cb:web_path ?o. " +
						"?s a ?type . " +
						"BIND (URI(CONCAT(\"https://www.crunchbase.com/\", ?o)) AS ?url) " +
						"VALUES ?s { <%s>}" +
			"}"; 
	
	private final static Property sameAs = ResourceFactory.createProperty("http://www.w3.org/2002/07/owl#sameAs");
	private final static Property document = ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/page");
	private final static Property license = ResourceFactory.createProperty("http://creativecommons.org/ns#license");
	
	public SPARQLLoader() {
		// TODO Auto-generated constructor stub
	}
	
	public static String loadTriple(String permalink, MappingUtility mu, String type) {
		String identifier = permalink.substring(permalink.lastIndexOf("/")+1)+"#id"; // sth like "facebook#id"
		String uri = CBWPREFIX+permalink+"#id";
		String docUri = CBWPREFIX+permalink;
		Query query = QueryFactory.create(String.format(constructQueryString, uri));
		QueryExecution quexec = QueryExecutionFactory.sparqlService(SERVICE, query);
		
		Model results = quexec.execConstruct();
		String mapping = mu.getMapping(identifier);
		//System.out.println(identifier + " -- " + mapping);
		
		Resource cbEntity = results.getResource(uri);
		Resource cbDoc = results.createResource(docUri);
		if (mapping != null) {
			cbEntity.addProperty(sameAs, results.createResource(mapping));
		}
		cbEntity.addProperty(document, cbDoc);
		cbDoc.addProperty(license, results.createResource("http://creativecommons.org/licenses/by-nc/4.0/"));		
		
		StringWriter out = new StringWriter();
		results.write(out, type);
		return out.toString();
	}
}
