package vn.edu.uit.chuong.owl2.learning_owl2api;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class RecursiveApp {
	private static final String FILE_PATH = "/home/r2/transport.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
	protected static Scanner input = new Scanner(System.in);
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
		OWLDataFactory factory = manager.getOWLDataFactory();
		//
		PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		// Set default prefix URI
		pm.setDefaultPrefix(BASE_URL + "#");
		OWLClass tranportation = factory.getOWLClass(":Transportation",pm);
		Set<OWLNamedIndividual> namedIndividuals = reasoner.getInstances(factory.getOWLClass(":Transportation",pm),true).getFlattened();
		// Get all subClasses of Transportation class
		Set<OWLClassExpression> subClassesOfTransportation = factory.getOWLClass(":Transportation",pm).getSubClasses(ontology);
		// Create a set of DataProperties and set of ObjectProperties which will be extracted from SWRL_RULE 
		Set<OWLDataProperty> dataProperties = new HashSet<OWLDataProperty>();
		Set<OWLObjectProperty> objProperties = new HashSet<OWLObjectProperty>();
		// Extract DataProperties and ObjectProperties which are mentioned in SWRL Rules
		for (SWRLRule rule : ontology.getAxioms(AxiomType.SWRL_RULE)) {
			for(OWLDataProperty property : rule.getDataPropertiesInSignature()) {
				if(!dataProperties.contains(property)) { // Make sure they don't duplicate
					dataProperties.add(property);
				}
			}
			for(OWLObjectProperty objProperty : rule.getObjectPropertiesInSignature()) {
				if(!objProperties.contains(objProperty)) {// Make sure they don't duplicate
					objProperties.add(objProperty);
				}
			}
		}
		Map<Integer, String> subClassesOfTransportationMap = new HashMap<Integer, String>();
		int index = 0;
		for (OWLClassExpression ce : subClassesOfTransportation) {
			++index;
			subClassesOfTransportationMap.put(index,renderer.render(ce));
		}
		/*
		 * Making questions to all individuals from Transportation class 
		 */
		for (OWLNamedIndividual individual : namedIndividuals) {
			// Ask them which subClasses of Transportation do they belong to
			System.out.println("Which classes does '"+renderer.render(individual)+"' belong to ? Choose one from options below");
			for(int currentKey : subClassesOfTransportationMap.keySet() ) {
				System.out.println(currentKey+". "+subClassesOfTransportationMap.get(currentKey));
			}
			// Reading input
			int answer = input.nextInt();
			while(!(answer >0  && answer <= index)) {
				System.out.println("Please choose from "+index+" options above only !");
				answer = input.nextInt();
			}
			/*
			 * if the answer is valid, then add the individual to the corresponding class
			 */
			System.out.println("Kq: "+subClassesOfTransportationMap.get(answer));
			OWLClass c = factory.getOWLClass(":"+subClassesOfTransportationMap.get(answer),pm);
			OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(c, individual);
			manager.applyChange(new AddAxiom(ontology,classAssertionAxiom));
			manager.saveOntology(ontology);
			reasoner.flush();
			rAsk(manager, ontology, factory, reasoner, individual, new HashSet<OWLClass>(), dataProperties, objProperties);
		}
		
	}
	private static void OWLClassPrint_r(OWLOntology ont, OWLClass owlClass,int level) {
		if(owlClass.isOWLClass() && !owlClass.isBottomEntity()) {
			System.out.printf("%1$"+(level*10)+"s\n", renderer.render(owlClass));
			level++;
			for(OWLClassExpression ce : owlClass.getSubClasses(ont)) {
				for(OWLClass c : ce.getClassesInSignature()){
					OWLClassPrint_r(ont,c,level);
				}
			};
		}
	}
	/**
	 * 
	 * @param manager the ontology manager
	 * @param ontology current working ontology
	 * @param factory data factory
	 * @param reasoner The reasoner
	 * @param individual The individual which is being worked with
	 * 
	 * @param dataProps A set of OWLDataProperties 
	 * @param objProps A set of OWLObjectProperties
	 * @throws OWLOntologyStorageException 
	 * 
	 */
	private static void rAsk(
			OWLOntologyManager manager,
			OWLOntology ontology,
			OWLDataFactory factory,
			OWLReasoner reasoner,
			OWLNamedIndividual individual,
			Set<OWLClass> visited,
			Set<OWLDataProperty> dataProps,
			Set<OWLObjectProperty> objProps) throws OWLOntologyStorageException {
		
		Set<OWLClass> types = reasoner.getTypes(individual,true).getFlattened();
		
		for (OWLClass type : types) {
			
			if(!visited.contains(type) && type.isOWLClass() && !type.isBottomEntity()) {
				for (OWLDataProperty dataProp : dataProps) {
					if(dataProp.getDomains(ontology).contains(type)) {
						String shortnameOfProp = renderer.render(dataProp).substring(11);
						System.out.println("How many '"+shortnameOfProp+"' does the individual named '"+renderer.render(individual)+"' have ? [Type '0' if it has none]");
						int answer = input.nextInt();
						while(answer < 0) {
							System.err.println("Please type only positive number and '0'!");
							answer = input.nextInt();
						}
						if( answer > 0 ) {
							OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(dataProp, individual, answer);
							manager.applyChange(new AddAxiom(ontology, axiom));
							manager.saveOntology(ontology);
							reasoner.flush();
						}
						for(OWLClass newType : reasoner.getTypes(individual,true).getFlattened()) {
							visited.add(newType);
						}
						printClassesOfIndividuals(reasoner, individual);
						rAsk(manager, ontology, factory, reasoner, individual, visited, dataProps, objProps);
					}
				}
			}
		}
	}
	/**
	 * 
	 */
	public static void printClassesOfIndividuals(OWLReasoner reasoner,OWLNamedIndividual individual) {
		OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
		System.out.println("The individuals named '"+renderer.render(individual)+"' belong to the following classes: ");
		for(OWLClass c : reasoner.getTypes(individual,true).getFlattened()) {
			System.out.println(renderer.render(c));
		}
		System.out.println("----------------------");
	}
}
