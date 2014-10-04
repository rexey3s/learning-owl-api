package vn.edu.uit.chuong.owl2.learning_owl2api;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.debugging.BlackBoxOWLDebugger;
import org.semanticweb.owlapi.debugging.OWLDebugger;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLObjectVisitor;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import com.clarkparsia.owlapi.explanation.TransactionAwareSingleExpGen;
import com.clarkparsia.owlapi.explanation.io.manchester.BlockWriter;
import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;
import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxObjectRenderer;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import uk.ac.manchester.owl.owlapi.tutorialowled2011.*;
/**
 * Hello world!
 *
 */
public class App 
{
	private static final String FILE_PATH = "/home/r2/Downloads/transport_custom.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
	
	public static void main(String[] args)  throws InconsistentOntologyException, UnsupportedOperationException, OWLException, IOException {
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
		//
		OWLDataFactory factory = manager.getOWLDataFactory();
		//
		DefaultExplanationGenerator explanator = new DefaultExplanationGenerator(manager, reasonerFactory, ontology, null);
		//
		
		DefaultPrefixManager pm = new DefaultPrefixManager(null, null,BASE_URL);
		// Set default prefix URI
		pm.setDefaultPrefix(BASE_URL + "#");
		
//		Scanner input = new Scanner(System.in);
//		OWLClass hovercraft = factory.getOWLClass("HoverCraft",pm);
//		Set<Set<OWLAxiom>> explanations = explanator.getExplanations(hovercraft);
		PrintWriter out = new PrintWriter(System.out);
		ManchesterSyntaxExplanationRenderer mexprenderer = new ManchesterSyntaxExplanationRenderer();
//		mexprenderer.startRendering(out);
//		mexprenderer.render(explanations);
//		mexprenderer.endRendering();
        OWLClass bottom = manager.getOWLDataFactory().getOWLNothing();
		Set<OWLClass> unsatisfiables = new HashSet<>();
	    for (OWLClass clazz : ontology.getClassesInSignature()) {
	        assert clazz != null;
	    /* Collect the unsatisfiable classes that aren't bottom. */
	        if (!reasoner.isSatisfiable(clazz) && !clazz.equals(bottom)) {
	                unsatisfiables.add(clazz);
	        }
	    }

//	    for (OWLClass unsatisfiable: unsatisfiables) {
//	    	 Set<OWLAxiom> sos = debugger.getSOSForInconsistentClass(unsatisfiable);
//	    	Set<Set<OWLAxiom>> soss =  debugger.getAllSOSForInconsistentClass(unsatisfiable);
//	 		mexprenderer.startRendering(out);
//	 		mexprenderer.render(soss);
//	 		mexprenderer.endRendering();
//	 		int setCount = 0;
//	 		int axiomCount = 0;
//	 		for(Set<OWLAxiom> AxiomSet : soss) {
//	 			setCount++;
//	 			for(OWLAxiom axiom : sos) {
//	 				axiomCount++;
//	 				System.out.println(axiom);
//	 				System.out.println(axiom.getAxiomType());
//	 				System.out.println(axiom.getClassesInSignature());
//	 			}
//	 		}
//	    }
	    SWRLObjectVisitor visitor = new SWRLObjectVisitor() {
			
			@Override
			public void visit(SWRLDifferentIndividualsAtom node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLSameIndividualAtom node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLLiteralArgument node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLIndividualArgument node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLVariable node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLBuiltInAtom node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLDataPropertyAtom node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLObjectPropertyAtom node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLDataRangeAtom node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLClassAtom node) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SWRLRule node) {
				// TODO Auto-generated method stub
				
			}
		};
	    for(SWRLRule swrl: ontology.getAxioms(AxiomType.SWRL_RULE)) {
	    	Set<SWRLAtom> head = swrl.getHead();
	    	Set<SWRLAtom> body = swrl.getBody();
	    	// Lay phan body de giam vun`g tim kiem
	    	for(SWRLAtom ruleAtom: body) {
//	    		Set<OWLClassExpression> owlClsExpSet = ruleAtom.getNestedClassExpressions();
//	    		for(OWLClassExpression clsExp: owlClsExpSet) {
//	    			System.out.println(ruleAtom.());
//	    			
//	    		}
	    	}
	    	System.out.println("<!------- seperator -------->");
	    }
	    	
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
