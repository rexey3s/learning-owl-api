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
	private static final String FILE_PATH = "/home/r2/transport.owl";
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
		
		OWLDataProperty logicProp = factory.getOWLDataProperty(":logicQuiz", pm);
		OWLDataProperty functionalProp = factory.getOWLDataProperty(":functionalQuiz",pm);
		Set<OWLNamedIndividual> namedIndividuals = reasoner.getInstances(factory.getOWLClass(":Transportation",pm),true).getFlattened();
		Set<OWLClassExpression> subClassesOfTransportation = factory.getOWLClass(":Transportation",pm).getSubClasses(ontology);
		Set<OWLDataProperty> dataProperties = new HashSet<OWLDataProperty>();
		Set<OWLObjectProperty> objProperties = new HashSet<OWLObjectProperty>();
		
		for (SWRLRule rule : ontology.getAxioms(AxiomType.SWRL_RULE)) {
			for(OWLDataProperty property : rule.getDataPropertiesInSignature()) {
				if(!dataProperties.contains(property)) {
					dataProperties.add(property);
				}
			}
			for(OWLObjectProperty objProperty : rule.getObjectPropertiesInSignature()) {
				if(!objProperties.contains(objProperty)) {
					objProperties.add(objProperty);
				}
			}
		}
		System.out.println(objProperties);
		Map<Integer, String> subClassesOfTransportationMap = new HashMap<Integer, String>();
		int index = 0;
		for (OWLClassExpression ce : subClassesOfTransportation) {
			++index;
			subClassesOfTransportationMap.put(index,renderer.render(ce));
		}
		for (OWLNamedIndividual individual : namedIndividuals) {
			
			System.out.println("Which classes does '"+renderer.render(individual)+"' belong to ? Choose one from options below");
			for(int currentKey : subClassesOfTransportationMap.keySet() ) {
				System.out.println(currentKey+". "+subClassesOfTransportationMap.get(currentKey));
			}
			int answer = input.nextInt();
			while(!(answer >0  && answer <= index)) {
				System.out.println("Please choose from "+index+" options above only !");
				answer = input.nextInt();
			}
			System.out.println("Kq: "+subClassesOfTransportationMap.get(answer));
			OWLClass c = factory.getOWLClass(":"+subClassesOfTransportationMap.get(answer),pm);
			
			OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(c, individual);
			manager.applyChange(new AddAxiom(ontology,classAssertionAxiom));
			manager.saveOntology(ontology);
			reasoner.flush();
				
			Set<OWLClass> classesOfIndividual = reasoner.getTypes(individual,false).getFlattened();//false mean include asserted classes
			for (OWLClass cOfInd  : classesOfIndividual) {
				if(cOfInd.isOWLClass()) {					
					for(OWLDataProperty dataProp : dataProperties) {
						if(dataProp.getDomains(ontology).contains(cOfInd)) {
							if(dataProp.getSuperProperties(ontology).contains(functionalProp)) {
								String objPropShortname = renderer.render(dataProp).substring(11);
								System.out.println("How many '"+objPropShortname+"' does the individual named '"+renderer.render(individual)+"' have ?");
								int answer2 = input.nextInt();
								while(answer < 0) {
									System.err.println("Please type only positive number and zero !");
									answer = input.nextInt();
								};
								if(answer > 0) {
									OWLAxiom propAxiom = factory.getOWLDataPropertyAssertionAxiom(dataProp, individual, answer2);
									manager.applyChange(new AddAxiom(ontology, propAxiom));
									manager.saveOntology(ontology);
									reasoner.flush();
									
//									System.out.println("With the given data property you've just defined. Now the individuals named '"
//														+renderer.render(individual)+"' belongs to a new class -> "
//														+renderer.render(reasoner.getTypes(individual,true).getFlattened().iterator().next()));
									System.out.println(reasoner.getTypes(individual,true).getFlattened());
								} else  {
									System.out.println("None was defined! ");
								}
							}
						}
					}
				}				
			}
		}
	}
}
