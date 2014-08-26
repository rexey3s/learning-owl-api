package vn.edu.uit.chuong.owl2.learning_owl2api;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.clarkparsia.sparqlowl.parser.antlr.SparqlOwlParser.builtInCall_return;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationOrdererImpl;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.File;


public class ImportDemo {
	private static final String FILE_PATH_A = "/home/pseudo/empty_transport.owl";
	private static final String FILE_PATH_B = "/home/pseudo/transportIndividuals.owl";
//	private static final String BASE_URL_A = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport";
//	private static final String BASE_URL_B = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport-ind";
	private static OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

	public static void main (String args[]) throws OWLOntologyCreationException {
		//
//		try {
//			shouldMergeOntologies();
			//
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			//
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create("file:/home/pseudo/empty_transport.owl"));
			//
			OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
			//
			OWLReasoner reasoner = reasonerFactory.createReasoner(ontology,new SimpleConfiguration());
			//
			OWLDataFactory factory = manager.getOWLDataFactory();
				//
			PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
			// Set default prefix URI
			pm.setDefaultPrefix("http://www.semanticweb.org/pseudo/ontologies/2014/7/transport#");
	
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
    public static void shouldMergeOntologies() throws OWLOntologyCreationException,
	    OWLOntologyStorageException {
	// Just load two arbitrary ontologies for the purposes of this example
	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	man.loadOntologyFromOntologyDocument(IRI.create(new File(FILE_PATH_A)));
	man.loadOntologyFromOntologyDocument(IRI.create(new File(FILE_PATH_B)));
	// Create our ontology merger
	OWLOntologyMerger merger = new OWLOntologyMerger(man);
	// We merge all of the loaded ontologies. Since an OWLOntologyManager is
	// an OWLOntologySetProvider we just pass this in. We also need to
	// specify the URI of the new ontology that will be created.
	IRI mergedOntologyIRI = IRI
	        .create("http://www.semanticweb.org/pseudo/ontologies/2014/7/transport");
	OWLOntology merged = merger
	        .createMergedOntology(man, mergedOntologyIRI);
	// Print out the axioms in the merged ontology.
	for (OWLAxiom ax : merged.getAxioms()) {
	    System.out.println(ax);
	}
	// Save to RDF/XML
	man.saveOntology(merged, new RDFXMLOntologyFormat(),
	        IRI.create("file:/home/pseudo/mergedont.owlapi"));
	}

}
