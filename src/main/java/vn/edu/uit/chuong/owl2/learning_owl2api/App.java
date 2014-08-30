package vn.edu.uit.chuong.owl2.learning_owl2api;
import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.util.OneToManyMap.Entry;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
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
	private static final String FILE_PATH = "/home/Downloads/transport.owl";
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
		OWLDataFactory factory = manager.getOWLDataFactory();
		//
		PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		// Set default prefix URI
		pm.setDefaultPrefix(BASE_URL + "#");
		
		Scanner input = new Scanner(System.in);
		// Get DataProperty named 'logicQuiz'
		OWLDataProperty logicProp = factory.getOWLDataProperty(":logicQuiz", pm);
		// Get DataProperty named 'functionalQuiz'
		OWLDataProperty functionalProp = factory.getOWLDataProperty(":functionalQuiz",pm);
		// Get all individuals whose class is Transportation
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
		// Map the each subClass of Transportation class to an integer ,just for input 
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
			reasoner.flush(); // reload reasoner to see immediately result
			/*
			 * With the new subClass you've defined for the individual, now get its classes  	
			 */
			Set<OWLClass> classesOfIndividual = reasoner.getTypes(individual,false).getFlattened();//false mean include asserted classes
			/*
			 * Making questions based on Domains of DataProperties, which are mentioned in SWRL Rules
			 */
			for (OWLClass cOfInd  : classesOfIndividual) {
				if(cOfInd.isOWLClass()) {
					
					for(OWLDataProperty dataProp : dataProperties) {
						// Get domains of this DataProperty and check if its Domains contained the Class of the individual 
						if(dataProp.getDomains(ontology).contains(cOfInd)) { 
							
							if(dataProp.getSuperProperties(ontology).contains(functionalProp)) {
								// Just get meaning words from dataProperty's name. E.g., hasNumberOfWheels -> Wheels  
								String objPropShortname = renderer.render(dataProp).substring(11);
								System.out.println("How many '"+objPropShortname+"' does the individual named '"+renderer.render(individual)+"' have ? [Type '0' if it has none]");
								// Reading input
								int answer2 = input.nextInt();
								while(answer2 < 0) {
									System.err.println("Please type only positive number and '0'!");
									answer2 = input.nextInt();
								};
								/*
								 * if the answer is valid, then add new DataProperty for the individual
								 */
								if(answer2 > 0) {
									OWLAxiom propAxiom = factory.getOWLDataPropertyAssertionAxiom(dataProp, individual, answer2);
									manager.applyChange(new AddAxiom(ontology, propAxiom));
									manager.saveOntology(ontology);
									reasoner.flush();// reload reasoner to see immediately result
									
//									System.out.println("With the given data property you've just defined. Now the individuals named '"
//														+renderer.render(individual)+"' belongs to a new class -> "
//														+renderer.render(reasoner.getTypes(individual,true).getFlattened().iterator().next()));
									
									//Print new classes for the individual
//									System.out.println(reasoner.getTypes(individual,true).getFlattened());
									printClassesOfIndividuals(reasoner, individual);
								
								} else  { // answer == 0 -> nothing happened
									System.out.println("None was defined! ");
								}
							}
						}
					}					
				}				
			}
			/*
			 * Making questions based on Ranges of ObjectProperties which are mentions in SWRL Rules
			 */
			for(OWLObjectProperty objProp: objProperties) {
				Set<OWLClassExpression> ranges = objProp.getRanges(ontology);
				
				for (OWLClassExpression ce : ranges) {
					Map<Integer, String> rangeMap = new HashMap<Integer, String>();
					int index2 = 0;
					// I suppose to get all classes in Ranges but I just get first  ClassExpression
					Set<OWLClassExpression> subExOfobjPropRange = ce.getClassesInSignature().iterator().next().getSubClasses(ontology);
					rangeMap.put(++index2, renderer.render(ce.getClassesInSignature().iterator().next()));
					for (OWLClassExpression ce2 : subExOfobjPropRange) {	
						++index2;
						rangeMap.put(index2,renderer.render(ce2));
					}
					System.out.println("'"+renderer.render(individual)+"' '"+renderer.render(objProp)+"' something in options below! [Type 0 if none are corrected]");
					for(int currentKey : rangeMap.keySet() ) {
						System.out.println(currentKey+". "+rangeMap.get(currentKey));
					}
					int answer3 = input.nextInt();
					while(!(answer3 >=0  && answer3 <= index2)) {
						System.out.println("Please choose from "+index2+" options above only !");
						answer3 = input.nextInt();
					}
					if(answer3 != 0) {
						Set<OWLNamedIndividual> individualsInRange = reasoner.getInstances(factory.getOWLClass(":"+rangeMap.get(answer3),pm), true).getFlattened();
						if(!individualsInRange.isEmpty()) {
							Map<Integer, OWLNamedIndividual> individualsMap = new HashMap<Integer, OWLNamedIndividual>();
							int index3 = 0;
							for (OWLNamedIndividual ind2 : individualsInRange) {	
								++index3;
								individualsMap.put(index3,ind2);
							}
							System.out.println("'"+renderer.render(individual)+"' "+renderer.render(objProp)+" with which individuals of "+rangeMap.get(answer3) +"in options below! [Type 0 if none are corrected]");
							for(int currentKey : individualsMap.keySet() ) {
								System.out.println(currentKey+". "+renderer.render(individualsMap.get(currentKey)));
							}
							int answer4 = input.nextInt();
							while(!(answer4 >=0  && answer4 <= index3)) {
								System.out.println("Please choose from "+index3+" options above only !");
								answer4 = input.nextInt();
							}
							if(answer4 != 0) {
								/*
								 * This step is still incomplete because it need some individuals who already belong to classes (in the Range) 
								 * to make an ObjectProperty relationship between 2 individuals
								 * 
								 * E.g.,  Let say we have a SWRL like 'A ship can carry a Boat,but a Boat cannot carry a ship'
								 * 		  SWRL : Ship&Boat(?s), (canCarry some Boat) (?s) -> Ship(?s)
								 * 		  In this 'transport.owl' already has 2 individuals named 'Titanic' which is Transportation Class and 'LifeBoat' which is Boat Class
								 * 		  To state Titanic is a ship, it has to carry some individuals from Boat Class [LifeBoat]       
								 */
									
									OWLObjectPropertyAssertionAxiom propAxiomOfIndividuals = factory.getOWLObjectPropertyAssertionAxiom(objProp,individual,individualsMap.get(answer4));
									manager.applyChange(new AddAxiom(ontology, propAxiomOfIndividuals));
									manager.saveOntology(ontology);
									reasoner.flush();// reload reasoner to see immediately result
									// View result
//									System.out.println(reasoner.getTypes(individual,true).getFlattened());
									printClassesOfIndividuals(reasoner, individual);
							} else {
								System.err.println("No answers, so we cannot make any object Property relationship for '"+individual+"'");
							}
						} else {
							System.err.println("There is no individuals in this class, so we cannot make any object Property relationship for '"+renderer.render(individual)+"'");
						}
					} else {
						break;
					}
				}
			}
		}
		System.out.println("End!");
	}
	public static void printClassesOfIndividuals(OWLReasoner reasoner,OWLNamedIndividual individual) {
		OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
		System.out.println("The individuals named '"+renderer.render(individual)+"' belong to the following classes: ");
		for(OWLClass c : reasoner.getTypes(individual,true).getFlattened()) {
			System.out.println(renderer.render(c));
		}
		System.out.println("----------------------");
	}
}
