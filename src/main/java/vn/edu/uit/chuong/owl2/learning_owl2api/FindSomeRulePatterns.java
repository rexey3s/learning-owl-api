package vn.edu.uit.chuong.owl2.learning_owl2api;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.swrlapi.core.SWRLAPIFactory;
import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngineFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import uk.ac.manchester.cs.owl.explanation.ordering.Tree;
import vn.edu.uit.swrlapi.collector.impl.DataPropertyAtomCollector;
import vn.edu.uit.swrlapi.collector.impl.PropertyAtomSearcher;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class FindSomeRulePatterns {
	private static final String FILE_PATH = "/home/r1/workspace/git/transport.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();

	public static void main(String[] args)  throws 
	InconsistentOntologyException, UnsupportedOperationException, 
	OWLException, IOException,
	SWRLParseException, SQWRLException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = manager
				.loadOntologyFromOntologyDocument(IRI.create(new File(FILE_PATH)));
		DefaultPrefixManager pm = 
				new DefaultPrefixManager(BASE_URL);
		//
		pm.setDefaultPrefix(BASE_URL + "#");
		// Set SWRL Core Built-in prefix and SQWRL prefix 
		pm.setPrefix("swrlb:",
				"http://www.w3.org/2003/11/swrlb#");
		pm.setPrefix("sqwrl:",
				"http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#");
		// Convert ontology to SWRL ontology	
		SWRLAPIOWLOntology ruleont = SWRLAPIFactory.createOntology(ont, pm);

		OWLReasonerFactory reasonerFactory = 
				PelletReasonerFactory.getInstance();
		//
		OWLReasoner reasoner = reasonerFactory
				.createReasoner(ont, new SimpleConfiguration());
		reasoner.precomputeInferences();

		//
		OWLDataFactory df = ruleont.getOWLDataFactory(); // ont.getOWLDataFactory is accepted
		//
		SWRLRuleEngineFactory ruf = SWRLAPIFactory.createSWRLRuleEngineFactory();
		// Use Drool as a rule engine
//		ruf.registerRuleEngine(new DroolsSWRLRuleEngineCreator());
		//		SWRLParser parser = new SWRLParser(ruleont);
		//		SQWRLQueryEngine queryEngine =  ruf.createSQWRLQueryEngine(ruleont);
		//		SQWRLQuery query1 = swrlOntology.createSQWRLQuery(
		//				"query1",
		//				"canCarryNumberOfPassenger(?x,?y) -> sqwrl:select(?x,?y)");
		//		SQWRLResult result = queryEngine.runSQWRLQuery("query1");
		Set<SWRLAPIRule> rules = ruleont.getSWRLAPIRules();
		OWLClass vehicle = df.getOWLClass(":Aircraft", pm);
		//		Set<OWLNamedIndividual> vehicleInstances = reasoner
		//				.getInstances(vehicle,true).getFlattened();
//		PossibleAnswersCollector visitor = new PossibleAnswersCollector(vehicle);
		DataPropertyAtomCollector visitor = new DataPropertyAtomCollector(vehicle);
		for(SWRLAPIRule rule: rules) {
			if(!rule.isSQWRLQuery()) {	
				rule.accept(visitor);	
			}
		}
		System.out.println(visitor.getRecommendedAnswers());
		PropertyAtomSearcher searcher = new PropertyAtomSearcher(ruleont);
		System.out.println(searcher.getObjectPropertySuggestionByClass(vehicle));
	}

	private static void printIndented(Tree<OWLAxiom> node, String indent) {
		OWLAxiom axiom = node.getUserObject(); 
		System.out.println(indent + renderer.render(axiom)); 
		if (!node.isLeaf()) { 
			for (Tree<OWLAxiom> child : node.getChildren()) { 
				printIndented(child, indent + "    "); 
			} 
		} 
	}
	
}

