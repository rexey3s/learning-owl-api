package vn.edu.uit.chuong.owl2.learning_owl2api;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.debugging.BlackBoxOWLDebugger;
import org.semanticweb.owlapi.debugging.OWLDebugger;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
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
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.core.SWRLRuleEngineFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.parser.SWRLParser;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class TrySWRLapi {
	
	private static final String FILE_PATH = "/home/r2/Downloads/tranport_swrl.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
    private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer(); 

	public static void main(String[] args)  throws 
		InconsistentOntologyException, 
		UnsupportedOperationException, 
		OWLException, 
		IOException,
		SWRLParseException {
		
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
		//
		OWLNamedIndividual yellowbus = factory.getOWLNamedIndividual(":YellowBus", pm);
		OWLClass Bus = factory.getOWLClass(":Bus", pm); 
	    
        
		
        SWRLAPIOWLOntology swrlapiOntology = SWRLAPIFactory.createOntology(ont, pm);
//		SWRLRuleEngineFactory ruf = SWRLAPIFactory.createSWRLRuleEngineFactory();
//		SWRLRuleEngine ruleEngine = ruf.createSWRLRuleEngine(swrlapiOntology);
		SWRLParser parser = SWRLAPIFactory.createSWRLParser(swrlapiOntology);

		SWRLAPIRule busRule = swrlapiOntology.createSWRLRule("BusRule",
				"OnRoadAndOffRoadVehicle(?v) ^ hasNumberOfSeats(?v,?s) ^ swrlb:greaterThan(?s,20) -> Bus(?v)");
				
//		ruleEngine.run();
		System.out.println(busRule);
//		manager.applyChange(new AddAxiom(ont, busRule.getSimplified()));
        // Now save the ontology. The ontology will be saved to the location
        // where we loaded it from, in the default ontology format
//        manager.saveOntology(ont);
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
