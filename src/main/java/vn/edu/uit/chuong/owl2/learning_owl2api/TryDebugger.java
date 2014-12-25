package vn.edu.uit.chuong.owl2.learning_owl2api;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.io.manchester.ManchesterSyntaxExplanationRenderer;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.debugging.BlackBoxOWLDebugger;
import org.semanticweb.owlapi.debugging.OWLDebugger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class TryDebugger {
	private static final String FILE_PATH = 
			"./inconsistent_transport.owl";
	private static final String BASE_URL = 
			"http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	public static ManchesterSyntaxExplanationRenderer render = 
			new ManchesterSyntaxExplanationRenderer();

	public static void main(String[] args)  
			throws InconsistentOntologyException, 
				   UnsupportedOperationException, 
				   OWLException, IOException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(
										IRI.create(new File(FILE_PATH)));
		OWLReasonerFactory rf = PelletReasonerFactory.getInstance();
		
		OWLReasoner reasoner = rf.createReasoner(ont, new SimpleConfiguration());
		
		OWLDebugger debugger = new BlackBoxOWLDebugger(manager, ont, rf);
		
//		OWLDataFactory df = manager.getOWLDataFactory();
		
		DefaultExplanationGenerator explanator = new DefaultExplanationGenerator(manager, rf, ont, null);
		
		DefaultPrefixManager pm = new DefaultPrefixManager(BASE_URL);

		pm.setDefaultPrefix(BASE_URL + "#");
		
		// Prepare stdout
		PrintWriter out = new PrintWriter(System.out);

        OWLClass bottom = manager.getOWLDataFactory().getOWLNothing();
		Set<OWLClass> unsatisfiables = new HashSet<>();
	    for (OWLClass clazz : ont.getClassesInSignature()) {
	        assert clazz != null;
	    /* Collect the unsatisfiable classes that aren't bottom. */
	        if (!reasoner.isSatisfiable(clazz) && !clazz.equals(bottom)) {
	                unsatisfiables.add(clazz);
	        }
	    }
	    /* render explanation for each class supposed to cause inconsistency */
	    for (OWLClass unsatisfiable: unsatisfiables) {
			Set<OWLAxiom> sos = debugger.getSOSForInconsistentClass(unsatisfiable);
			Set<Set<OWLAxiom>> soss = debugger.getAllSOSForInconsistentClass(unsatisfiable);

	 		render.startRendering(out);
	 		render.render(soss);
	 		render.endRendering();
	    }	    		
	}
}
