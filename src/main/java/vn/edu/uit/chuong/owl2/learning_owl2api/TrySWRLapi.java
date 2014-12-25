package vn.edu.uit.chuong.owl2.learning_owl2api;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.debugging.BlackBoxOWLDebugger;
import org.semanticweb.owlapi.debugging.OWLDebugger;
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
import org.swrlapi.parser.SWRLParser;
import org.swrlapi.sqwrl.SQWRLQuery;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owl.explanation.ordering.Tree;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class TrySWRLapi {
	
	private static final String FILE_PATH = "/home/r2/Downloads/tranport_swrl.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();

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
		// Set default prefix URI
		pm.setDefaultPrefix(BASE_URL + "#");
		pm.setPrefix("swrlb:", "http://www.w3.org/2003/11/swrlb#");
		pm.setPrefix("sqwrl:", "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#");
		//
		OWLNamedIndividual yellowbus = factory.getOWLNamedIndividual(":YellowBus", pm);
		OWLClass Bus = factory.getOWLClass(":Bus", pm); 
	    
        
		
        SWRLAPIOWLOntology swrlOntology = SWRLAPIFactory.createOntology(ont, pm);
        
		SWRLRuleEngineFactory ruf = SWRLAPIFactory.createSWRLRuleEngineFactory();
		SWRLParser parser = new SWRLParser(swrlOntology);
		SQWRLQueryEngine queryEngine =  ruf.createSQWRLQueryEngine(swrlOntology);
		SQWRLQuery query1 = swrlOntology.createSQWRLQuery("query1", "Vehicle(?v) -> sqwrl:select(?v)");
		SQWRLResult result = queryEngine.runSQWRLQuery("query1");
        System.out.println(result);
		SWRLAPIRule busRule = swrlOntology.createSWRLRule("BusRule",
				"OnRoadAndOffRoadVehicle(?v) ^ hasNumberOfSeats(?v,?s) ^ swrlb:greaterThan(?s,20) -> Bus(?v)");
		System.out.println(busRule.getBodyAtoms());
		System.out.println(busRule);
//		manager.applyChange(new AddAxiom(ont, busRule.getSimplified()));
//		manager.saveOntology(ont);
		reasoner.flush();

		
		OWLClassAssertionAxiom axiomToExplain = factory.getOWLClassAssertionAxiom(Bus, yellowbus); 
	    System.out.println("Is YellowBus a Bus ? : " + reasoner.isEntailed(axiomToExplain)); 
		DefaultExplanationGenerator explanationGenerator = 
                new DefaultExplanationGenerator( 
                        manager, reasonerFactory, ont, reasoner, new SilentExplanationProgressMonitor()); 
		Set<OWLAxiom> explanation = explanationGenerator.getExplanation(axiomToExplain); 
        ExplanationOrderer deo = new ExplanationOrdererImpl(manager); 
        ExplanationTree explanationTree = deo.getOrderedExplanation(axiomToExplain, explanation);
        System.out.println(); 
        System.out.println("-- explanation why Yellow Bus is a Bus --"); 
        System.out.println(); 
        printIndented(explanationTree, "");
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
