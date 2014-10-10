package vn.edu.uit.chuong.owl2.learning_owl2api;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.debugging.BlackBoxOWLDebugger;
import org.semanticweb.owlapi.debugging.OWLDebugger;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.swrlapi.core.SWRLAPIFactory;
import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngineFactory;
import org.swrlapi.drools.core.DroolsSWRLRuleEngineCreator;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.parser.SWRLParser;
import org.swrlapi.sqwrl.SQWRLQuery;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class FindSomeRulePatterns {
	private static final String FILE_PATH = "/home/r2/Downloads/tranport_swrl.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
    private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer(); 

	public static void main(String[] args)  throws 
		InconsistentOntologyException, 
		UnsupportedOperationException, 
		OWLException, 
		IOException,
		SWRLParseException, SQWRLException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI.create(new File(FILE_PATH)));
		//
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		//
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont, new SimpleConfiguration());
		reasoner.precomputeInferences();
		//
		OWLDebugger debugger = new BlackBoxOWLDebugger(manager, ont, reasonerFactory);
		//
		OWLDataFactory factory = manager.getOWLDataFactory();
		//
		DefaultExplanationGenerator explanator = new DefaultExplanationGenerator(manager, reasonerFactory, ont, null);
		//
		DefaultPrefixManager pm = new DefaultPrefixManager(BASE_URL);
		//
		pm.setDefaultPrefix(BASE_URL + "#");
		// Set SWRL Core Built-in prefix and SQWRL prefix 
		pm.setPrefix("swrlb:", "http://www.w3.org/2003/11/swrlb#");
		pm.setPrefix("sqwrl:", "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#");
		// Convert ontology to SWRL ontology	
        SWRLAPIOWLOntology swrlOntology = SWRLAPIFactory.createOntology(ont, pm);
        //
		SWRLRuleEngineFactory ruf = SWRLAPIFactory.createSWRLRuleEngineFactory();
		// Use Drool as a rule engine
		ruf.registerRuleEngine(new DroolsSWRLRuleEngineCreator());
//		SWRLParser parser = new SWRLParser(swrlOntology);
//		SQWRLQueryEngine queryEngine =  ruf.createSQWRLQueryEngine(swrlOntology);
//		SQWRLQuery query1 = swrlOntology.createSQWRLQuery(
//				"query1",
//				"canCarryNumberOfPassenger(?x,?y) -> sqwrl:select(?x,?y)");
//		SQWRLResult result = queryEngine.runSQWRLQuery("query1");
		Set<SWRLAPIRule> rules = swrlOntology.getSWRLAPIRules();		
//      System.out.println(result);
        
		for(SWRLAPIRule rule: rules) {
        	if(!rule.isSQWRLQuery()) {
        		System.out.println(rule.getClassAtomPredicates());
        		SWRLRule r = (SWRLRule)rule;
        		r.getNNF();
        	}
        }
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
