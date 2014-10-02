package vn.edu.uit.chuong.owl2.learning_owl2api;

import java.io.File;
import java.util.Collection;
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
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class RecursiveApp {
	private static final String FILE_PATH = "/home/r2/Downloads/transport.owl";
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
		OWLDocumentFormat owlFormat = manager.getOntologyFormat(ontology);
		DefaultPrefixManager pm = new DefaultPrefixManager(null, null,BASE_URL);
		// Set default prefix URI
		pm.setDefaultPrefix(BASE_URL + "#");
		OWLClass tranportation = factory.getOWLClass(":Transportation",pm);
		Set<OWLNamedIndividual> namedIndividuals = reasoner.getInstances(tranportation,true).getFlattened();
		// Get all subClasses of Transportation class
		Set<OWLClass> subClassesOfTransportation = reasoner.getSubClasses(tranportation, true).getFlattened();
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
		for (OWLClass ce : subClassesOfTransportation) {
			++index;
			subClassesOfTransportationMap.put(index,renderer.render(ce));
		}
		/*
		 * Making questions to all individuals from Transportation class 
		 */
		for (OWLNamedIndividual individual : namedIndividuals) {
			for (OWLDataProperty dataProp : dataProperties) {
				if(EntitySearcher.getDomains(dataProp, ontology).contains(tranportation)) {
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
						printClassesOfIndividuals(reasoner, individual);
					}
				}
			}
			for(OWLObjectProperty objProp: objProperties) {
				if(EntitySearcher.getDomains(objProp, ontology).contains(tranportation)) {
					Collection<OWLClassExpression> ranges = EntitySearcher.getRanges(objProp, ontology);
					System.out.println("'"+renderer.render(individual)+"' '"+renderer.render(objProp)+"' something in options below! [Type 0 if none are corrected]");
					//A new recursive loop here

					for(OWLClassExpression range : ranges) {
						for (OWLClass c : range.getClassesInSignature()) {
							rObjectPropertiesRange rObjRange = new rObjectPropertiesRange();
							rObjRange.print_r(ontology, c, 1);
							int answer = input.nextInt();
							while(!(answer >=0  && answer <= rObjRange.count)) {
								System.out.println("Please choose from "+rObjRange.count+" options above only !");
								answer = input.nextInt();
							}
							if(answer != 0) {
								Set<OWLNamedIndividual> indiInRange = reasoner.getInstances(rObjRange.map.get(answer),true).getFlattened();
								Map<Integer,OWLNamedIndividual> indiMap = new HashMap<Integer,OWLNamedIndividual>();
								int index2 = 0;
								for(OWLNamedIndividual ind: indiInRange) {
									index2++;
									indiMap.put(index2, ind);
								}
								System.out.println(indiMap);
								System.out.println("'"+renderer.render(individual)+"' "+renderer.render(objProp)+" with which individuals of '"+renderer.render(rObjRange.map.get(answer)) +"' in options below! [Type 0 if none are corrected]");
								for(int currentKey : indiMap.keySet() ) {
									System.out.println(currentKey+". "+renderer.render(indiMap.get(currentKey)));
								}
								int answer2 = input.nextInt();
								while(!(answer2 >=0  && answer2 <= index2)) {
									System.out.println("Please choose from "+index2+" options above only !");
									answer2 = input.nextInt();
								}
								if(answer2 != 0) {
									/*
									 * This step is still incomplete because it need some individuals who already belong to classes (in the Range) 
									 * to make an ObjectProperty relationship between 2 individuals
									 * 
									 * E.g.,  Let say we have a SWRL like 'A ship can carry a Boat,but a Boat cannot carry a ship'
									 * 		  SWRL : Ship&Boat(?s), (canCarry some Boat) (?s) -> Ship(?s)
									 * 		  In this 'transport.owl' already has 2 individuals named 'Titanic' which is Transportation Class and 'LifeBoat' which is Boat Class
									 * 		  To state Titanic is a ship, it has to carry some individuals from Boat Class [LifeBoat]       
									 */
										
										OWLObjectPropertyAssertionAxiom propAxiomOfIndividuals = factory.getOWLObjectPropertyAssertionAxiom(objProp,individual,indiMap.get(answer2));
										manager.applyChange(new AddAxiom(ontology, propAxiomOfIndividuals));
										manager.saveOntology(ontology);
										reasoner.flush();// reload reasoner to see immediately result
										// View result
//										System.out.println(reasoner.getTypes(individual,true).getFlattened());
										printClassesOfIndividuals(reasoner, individual);
								} else {
									System.err.println("No answers, so we cannot make any object Property relationship for '"+renderer.render(individual)+"'");
								}
							} else {
								System.err.println("No answer! Nothing happened.");
							}
						}
					}
				}
			}
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
			printClassesOfIndividuals(reasoner, individual);
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
			for(OWLClassExpression ce : EntitySearcher.getSubClasses(owlClass, ont)) {
				for(OWLClass c : ce.getClassesInSignature()){
					OWLClassPrint_r(ont,c,level);
				}
			};
		}
	}
	/**
	 * 
	 * @param manager 		the ontology manager
	 * @param ontology 		current working ontology
	 * @param factory 		data factory
	 * @param reasoner 		The reasoner
	 * @param individual	The individual which is being worked with
	 * @param visisted		A set of Classes that were visited in recursive loop
	 * @param dataProps 	A set of OWLDataProperties 
	 * @param objProps 		A set of OWLObjectProperties
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
			
			if(!visited.contains(type) && type.isOWLClass() && ! EntitySearcher.getSubClasses(type,ontology).isEmpty()) {
				for (OWLDataProperty dataProp : dataProps) {
//					dataProp.getRanges(ontology);
//					Maybe using Data Range in the future version
					if(EntitySearcher.getDomains(dataProp,ontology).contains(type) ) {
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
						}
					}
				}
				for(OWLObjectProperty objProp: objProps) {
					if(EntitySearcher.getDomains(objProp, ontology).contains(type)) {
						Collection<OWLClassExpression> ranges = EntitySearcher.getRanges(objProp,ontology);
						System.out.println("'"+renderer.render(individual)+"' '"+renderer.render(objProp)+"' something in options below! [Type 0 if none are corrected]");
						//A new recursive loop here
						for(OWLClassExpression range : ranges) {
							for (OWLClass c : range.getClassesInSignature()) {
								rObjectPropertiesRange rObjRange = new rObjectPropertiesRange();
								rObjRange.print_r(ontology, c, 1);
								int answer = input.nextInt();
								while(!(answer >=0  && answer <= rObjRange.count)) {
									System.out.println("Please choose from "+rObjRange.count+" options above only !");
									answer = input.nextInt();
								}
								if(answer != 0) {
									Set<OWLNamedIndividual> indiInRange = reasoner.getInstances(rObjRange.map.get(answer),true).getFlattened();
									Map<Integer,OWLNamedIndividual> indiMap = new HashMap<Integer,OWLNamedIndividual>();
									int index2 = 0;
									for(OWLNamedIndividual ind: indiInRange) {
										index2++;
										indiMap.put(index2, ind);
									}
									System.out.println(indiMap);
									System.out.println("'"+renderer.render(individual)+"' "+renderer.render(objProp)+" with which individuals of '"+renderer.render(rObjRange.map.get(answer)) +"' in options below! [Type 0 if none are corrected]");
									for(int currentKey : indiMap.keySet() ) {
										System.out.println(currentKey+". "+renderer.render(indiMap.get(currentKey)));
									}
									int answer2 = input.nextInt();
									while(!(answer2 >=0  && answer2 <= index2)) {
										System.out.println("Please choose from "+index2+" options above only !");
										answer2 = input.nextInt();
									}
									if(answer2 != 0) {	
										OWLObjectPropertyAssertionAxiom propAxiomOfIndividuals = factory.getOWLObjectPropertyAssertionAxiom(objProp,individual,indiMap.get(answer2));
										manager.applyChange(new AddAxiom(ontology, propAxiomOfIndividuals));
										manager.saveOntology(ontology);
										reasoner.flush();// reload reasoner to see immediately result
										// View result
//										System.out.println(reasoner.getTypes(individual,true).getFlattened());
										printClassesOfIndividuals(reasoner, individual);
									} else {
										System.err.println("No answers, so we cannot make any object Property relationship for '"+renderer.render(individual)+"'");
									}
								} else {
									System.err.println("No answer! Nothing happened.");
								}
							}
						}
					}
				}
				reasoner.flush();
				for(OWLClass newType : reasoner.getTypes(individual,true).getFlattened()) {
					boolean alreadyThere = visited.add(newType);
//					System.out.println("Log: duplicate class ["+alreadyThere+"]");
				}
				printClassesOfIndividuals(reasoner, individual);
				rAsk(manager, ontology, factory, reasoner, individual, visited, dataProps, objProps);
			}
		}
	}
	/**
	 * 
	 */
	public static class rObjectPropertiesRange {
		public Map<Integer,OWLClass> map;
		public rObjectPropertiesRange() {
			map = new HashMap<Integer,OWLClass>();
		}
		public int count = 1;
		public void print_r(OWLOntology ont, OWLClass owlClass,int level) {
			if(owlClass.isOWLClass()) {
				if(!EntitySearcher.getSubClasses(owlClass, ont).isEmpty()) {
					System.out.print(count+". ");
					for (int i = 0; i < level; i++) {
						System.out.printf("\t");
					}
					System.out.println(renderer.render(owlClass)+"("+EntitySearcher.getIndividuals(owlClass,ont).size()+")");
					level++;
					map.put(count, owlClass);
					
					for(OWLClassExpression ce : EntitySearcher.getSubClasses(owlClass, ont)) {
						for(OWLClass c : ce.getClassesInSignature()){
							count++;
							print_r(ont,c,level);
						}
					};
				} else {
					System.out.print(count+". ");
					for (int i = 0; i < level; i++) {
						System.out.printf("\t");
					}
					System.out.println(renderer.render(owlClass)+"("+EntitySearcher.getIndividuals(owlClass,ont).size()+")");
					map.put(count, owlClass);
				}
			}
		}
	}
	
	/**
	 * 
	 */
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
