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
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import java.util.*;
import java.io.File;
import java.lang.reflect.Array;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String FILE_PATH = "/home/r2/transport.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
	
	public static void main(String[] args)  throws OWLOntologyCreationException, OWLOntologyStorageException {
		//
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(new File(FILE_PATH)));
		//
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
		//
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology,new SimpleConfiguration());
		//
		OWLDataFactory factory = manager.getOWLDataFactory();
		//
		PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		// Set default prefix URI
		pm.setDefaultPrefix(BASE_URL + "#");
		
		Scanner input = new Scanner(System.in);
		
		OWLDataProperty logicProp = factory.getOWLDataProperty(":logicQuiz", pm);
		OWLDataProperty functionalProp = factory.getOWLDataProperty(":functionalQuiz",pm);
		Set<OWLNamedIndividual> namedIndividuals = reasoner.getInstances(factory.getOWLClass(":Transportation",pm),true).getFlattened();
		Set<OWLDataProperty> dataProperties = new HashSet<OWLDataProperty>();
		
		for (SWRLRule rule : ontology.getAxioms(AxiomType.SWRL_RULE)) {
			for(OWLDataProperty property : rule.getDataPropertiesInSignature()) {
				if(!dataProperties.contains(property)) {
					dataProperties.add(property);
				}
			}
		}
//		for (OWLNamedIndividual individual : namedIndividuals) {
//			for (OWLDataProperty prop : dataProperties) {
//				if(prop.getSuperProperties(ontology).contains(logicProp)) {
//					if(renderer.render(prop).contains("can")) {
//						String propShortName = renderer.render(prop).substring(3);
//						System.out.println("Can the named individual '"+renderer.render(individual)+"'"+propShortName+"? [Yes/No]");
//						String answer = input.next().toLowerCase();
//						while(!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("no") && !answer.equalsIgnoreCase("none")) {
//							System.out.println("Please type only 'yes' or 'no' !");
//							answer = input.next().toString().toLowerCase();
//						};
//						if(answer.equalsIgnoreCase("yes")) {
//							OWLAxiom propAxiom = factory.getOWLDataPropertyAssertionAxiom(prop, individual, true);
//							manager.applyChange(new AddAxiom(ontology, propAxiom));
//							manager.saveOntology(ontology);
//							reasoner.flush(); 
//							// Get first OWLClass in the Set<OWLClass> which is inferred by the reasoner 
//							System.out.println("With the given data property you've just defined. Now the individuals named '"+renderer.render(individual)+"' belongs to a new class -> "+renderer.render(reasoner.getTypes(individual,true).getFlattened().iterator().next()));
//							
//						} else if(answer.equalsIgnoreCase("no")) {
//							OWLAxiom propAxiom = factory.getOWLDataPropertyAssertionAxiom(prop, individual, false);
//							manager.applyChange(new AddAxiom(ontology, propAxiom));
//							manager.saveOntology(ontology);
//							reasoner.flush(); 
//							System.out.println("With the given data property you've just defined. Now the individuals named '"+renderer.render(individual)+"' belongs to a new class -> "+renderer.render(reasoner.getTypes(individual,true).getFlattened().iterator().next()));
//						}
//						else {
//							System.out.println("None was defined! ");
//						}
//					} // Maybe more data properties if need
//				}
//			}
		OWLClass transportationClass = factory.getOWLClass(":Transportation",pm);
		
		// Get all individuals who have type "Transportation"
		for(OWLNamedIndividual t : reasoner.getInstances(transportationClass,false).getFlattened()) {
			System.out.println("Transportation : "+renderer.render(t));
		}
		for (OWLNamedIndividual individual : namedIndividuals) {
			for (OWLDataProperty prop : dataProperties) {
				Set<OWLClassExpression> assertedClasses = individual.getTypes(ontology);
				for (OWLClass c : reasoner.getTypes(individual, false).getFlattened()) {
					boolean asserted = assertedClasses.contains(c);
					System.out.println((asserted ? "asserted" : "inferred") + " class for individual: "+renderer.render(c)); 
					for(OWLClassExpression ce : c.getSuperClasses(ontology)) {
						System.out.println("\t\t\t"+ renderer.render(c) +" has superClass -> "+ renderer.render(ce));
					}				
				}
//				for (OWLClass c : reasoner.getTypes(individual,true).getFlattened()) {
//					System.out.println(c);
//					if(prop.getDomains(ontology).contains(c)) {
//						System.out.println(prop+" ->"+c);
//					}
//				}
			}
		}
		
		
    }
	
}
