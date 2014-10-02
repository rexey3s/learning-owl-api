package vn.edu.uit.chuong.owl2.learning_owl2api;
import java.io.File;
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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String FILE_PATH = "/home/r2/Downloads/transport_custom.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
	
	public static void main(String[] args)  throws OWLOntologyCreationException, OWLOntologyStorageException, InconsistentOntologyException {
		//
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(new File(FILE_PATH)));
		//
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		//
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology,new SimpleConfiguration());
		//
		OWLDebugger debugger = new BlackBoxOWLDebugger(manager, ontology, reasonerFactory);
		
		OWLDataFactory factory = manager.getOWLDataFactory();
		//
		DefaultExplanationGenerator explanator = new DefaultExplanationGenerator(manager, reasonerFactory, ontology, null);
		//
		DefaultPrefixManager pm = new DefaultPrefixManager(null, null,BASE_URL);
		// Set default prefix URI
		pm.setDefaultPrefix(BASE_URL + "#");
		
//		Scanner input = new Scanner(System.in);
		OWLClass hovercraft = factory.getOWLClass("HoverCraft",pm);
		Set<Set<OWLAxiom>> explanations = explanator.getExplanations(hovercraft);
		
		System.out.println("End!");
	}
	public static void printClassesOfIndividuals(OWLReasoner reasoner,OWLNamedIndividual individual) {
		OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
		reasoner.flush();
		System.out.println("The individuals named '"+renderer.render(individual)+"' belong to the following classes: ");
		for(OWLClass c : reasoner.getTypes(individual,false).getFlattened()) {
			if(!c.isOWLThing()) {
				System.out.println(renderer.render(c));
			}
		}
		System.out.println("----------------------");
	}
}
