package vn.edu.uit.chuong.owl2.learning_owl2api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.debugging.BlackBoxOWLDebugger;
import org.semanticweb.owlapi.debugging.OWLDebugger;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLObjectVisitor;
import org.semanticweb.owlapi.model.SWRLObjectVisitorEx;
import org.semanticweb.owlapi.model.SWRLPredicate;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.BuiltInVocabulary;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
import org.semanticweb.owlapi.vocab.SWRLVocabulary;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLVariableBuiltInArgument;
import org.swrlapi.core.SWRLAPIBuiltInAtom;
import org.swrlapi.core.SWRLAPIFactory;
import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngineFactory;
import org.swrlapi.core.impl.DefaultSWRLAPIOWLDataFactory;
import org.swrlapi.core.visitors.SWRLAPIBuiltInAtomVisitorEx;
import org.swrlapi.core.visitors.SWRLAPIEntityVisitorEx;
import org.swrlapi.core.visitors.SWRLAPIOWLAxiomVisitor;
import org.swrlapi.core.visitors.SWRLAPIOWLAxiomVisitorEx;
import org.swrlapi.core.visitors.SWRLAtomVisitor;
import org.swrlapi.drools.core.DroolsSWRLRuleEngineCreator;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.parser.SWRLParser;
import org.swrlapi.sqwrl.SQWRLQuery;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.owlapi.SWRLAtomImpl;
import uk.ac.manchester.cs.owl.owlapi.SWRLBuiltInAtomImpl;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;
import vn.edu.uit.swrlapi.visitorimpl.PossibleAnswersCollector;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.clarkparsia.pellet.rules.model.ClassAtom;

public class FindSomeRulePatterns {
	private static final String FILE_PATH = "/home/r2/Downloads/tranport_swrl.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer(); 

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
		ruf.registerRuleEngine(new DroolsSWRLRuleEngineCreator());
		//		SWRLParser parser = new SWRLParser(ruleont);
		//		SQWRLQueryEngine queryEngine =  ruf.createSQWRLQueryEngine(ruleont);
		//		SQWRLQuery query1 = swrlOntology.createSQWRLQuery(
		//				"query1",
		//				"canCarryNumberOfPassenger(?x,?y) -> sqwrl:select(?x,?y)");
		//		SQWRLResult result = queryEngine.runSQWRLQuery("query1");
		Set<SWRLAPIRule> rules = ruleont.getSWRLAPIRules();
				OWLClass vehicle = df.getOWLClass(":Vehicle",pm);
		//		Set<OWLNamedIndividual> vehicleInstances = reasoner
		//				.getInstances(vehicle,true).getFlattened();
		PossibleAnswersCollector visitor = new PossibleAnswersCollector(vehicle);
		for(SWRLAPIRule rule: rules) {
			if(!rule.isSQWRLQuery()) {	
				rule.accept(visitor);	
			}
		}
		System.out.println(visitor.getPossibleAnswers());
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

