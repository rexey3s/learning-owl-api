package vn.edu.uit.chuong.owl2.learning_owl2api;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import java.util.*;
import java.io.File;
import java.lang.reflect.Array;

public class LearnOWL {
	private static final String FILE_PATH = "/home/r2/Downloads/transport.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
	
	public static void main(String[] args)  throws OWLOntologyCreationException, OWLOntologyStorageException {
		//
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(
											IRI.create(new File(FILE_PATH)));
		//
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		//
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology,new SimpleConfiguration());
		//
		OWLDataFactory factory = manager.getOWLDataFactory();
		//
		DefaultPrefixManager pm = new DefaultPrefixManager(BASE_URL);
		// Set default prefix URI
		pm.setDefaultPrefix(BASE_URL + "#");
		
		//	Get "Transportation" class
		OWLClass transportationClass = factory.getOWLClass(":Transportation",pm);
		Collection<OWLClassExpression> subClasses = transportationClass.getSubClasses(ontology);
		for(OWLClassExpression ce : subClasses) {
			System.out.println(renderer.render(ce));
		}
		// Get all individuals who have type "Transportation"
		for(OWLNamedIndividual t : reasoner.getInstances(transportationClass,false).getFlattened()) {
			System.out.println("Transportation : "+renderer.render(t));
		}
		// get a given individual named "LifeBoat"
		OWLNamedIndividual lifeBoat = factory.getOWLNamedIndividual(":LifeBoat", pm);
		// find which classes and superClasses the individual lifeBoat belongs 
		Collection<OWLClassExpression> assertedClasses = lifeBoat.getTypes(ontology);
		for (OWLClass c : reasoner.getTypes(lifeBoat, false).getFlattened()) {
			boolean asserted = assertedClasses.contains(c);
			System.out.println((asserted ? "asserted" : "inferred") + " class for LifeBoat: "+renderer.render(c)); 
			for(OWLClassExpression ce : c.getSuperClasses( ontology)) {
				System.out.println("\t\t\t"+ renderer.render(c) +" has superClass -> "+ renderer.render(ce));
			}				
		}
		// get inverse of a property, i.e. which individuals are in relation with a given individual
		// Example : I'll get individual whose name is "Titanic" and its object property named "canCarry" 
		OWLNamedIndividual Titanic = factory.getOWLNamedIndividual(":Titanic",pm);
		// get Object property   
		OWLObjectProperty canCarryProperty = factory.getOWLObjectProperty(":canCarry",pm);
		// get Inverse of "canCarry" on "Titanic"
		OWLObjectPropertyExpression inverse = factory.getOWLObjectInverseOf(canCarryProperty);
		for (OWLNamedIndividual ind : reasoner.getObjectPropertyValues(Titanic,inverse).getFlattened()) {
			System.out.println("Titanic inverseOf (canCarry) -> "+renderer.render(ind));
		}
		// Check if the SWRL rule is used on the individuals named "Titanic" and "LifeBoat"
		OWLClass ship = factory.getOWLClass(":Ship",pm);
		OWLClass boat = factory.getOWLClass(":Boat",pm);
		OWLClassAssertionAxiom axiomToExplainShip = factory.getOWLClassAssertionAxiom(ship, Titanic);
		OWLClassAssertionAxiom axiomToExplainBoat = factory.getOWLClassAssertionAxiom(boat, lifeBoat);
		System.out.println("Is 'Titanic' a Ship ? If true, explain why : "+reasoner.isEntailed(axiomToExplainShip));
		// Explain why Titanic is a ship
		DefaultExplanationGenerator explanationGenerator = new DefaultExplanationGenerator(manager,reasonerFactory,ontology,reasoner,new SilentExplanationProgressMonitor());
		Set<OWLAxiom> explanation = explanationGenerator.getExplanation(axiomToExplainShip);
		ExplanationOrderer deo = new ExplanationOrdererImpl(manager);
		ExplanationTree explanationTree = deo.getOrderedExplanation(axiomToExplainShip,explanation);
		System.out.println();
		System.out.println("<< explanation why Titanic is a ship >>");
		printIndented(explanationTree, "");
		System.out.println("Is 'LifeBoat' a Boat ? : "+reasoner.isEntailed(axiomToExplainBoat));
		ExplanationTree explanationTree1 = deo.getOrderedExplanation(axiomToExplainBoat, explanationGenerator.getExplanation(axiomToExplainBoat));
		System.out.println();
		System.out.println("<< explanation why LifeBoat is a boat >> ");
		printIndented(explanationTree1, "");
		System.out.println("List all SWRL rules:\n");
		listSWRLRules(ontology, pm);

	
		System.out.println("EOF");
		
	}
	 public static void listSWRLRules(OWLOntology ontology, DefaultPrefixManager pm) { 
	        OWLObjectRenderer renderer = new DLSyntaxObjectRenderer(); 
	        for (SWRLRule rule : ontology.getAxioms(AxiomType.SWRL_RULE)) { 
	        	String temp = renderer.render(rule);
	        	System.out.println(temp);
//	        	System.out.println(rule.getClassAtomPredicates());
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


