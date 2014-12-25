package vn.edu.uit.chuong.owl2.learning_owl2api;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;
import org.swrlapi.core.SWRLAPIOWLOntology;
import org.swrlapi.core.SWRLAPIRenderer;
import org.swrlapi.core.impl.DefaultSWRLAPIRenderer;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String FILE_PATH = "/home/r1/workspace/git/transport.owl";
	private static final String BASE_URL = "http://www.semanticweb.org/pseudo/ontologies/2014/7/transport.owl";
	private static final JsonObject thingObject = new JsonObject();
	private static final JsonArray thingArray = new JsonArray();
	private static final JsonArray nodes = new JsonArray();
	private static final JsonArray edges = new JsonArray();
	private static final Set<OWLClass> visited = new HashSet<>();
	static int SIZE = 350;
	private static OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();

	public static void main(String[] args)  throws InconsistentOntologyException, UnsupportedOperationException, OWLException, IOException {
		//
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(FILE_PATH));
		//
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();

		OWLDataFactory factory = manager.getOWLDataFactory();
		//
		//
		manager.setOntologyDocumentIRI(ontology, ontology.getOntologyID().getDefaultDocumentIRI().get());
		PrefixManager pm = new DefaultPrefixManager(null, null, manager.getOntologyDocumentIRI(ontology).toString() + "#");
//		pm.setDefaultPrefix(BASE_URL + "#");
//		thingObject.addProperty("name", "Thing");
//		thingObject.add("children", thingArray);
//		ontology.accept(initPopulationEngine(ontology, thingObject));
		thingObject.addProperty("id", "Thing");
		thingObject.addProperty("label", "Thing");

		nodes.add(thingObject);
		ontology.accept(initPopulationEngine2(ontology));
		System.out.println(nodes.toString());
		System.out.println(edges.toString());

	}

	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	private static void recursive2(OWLOntology ontology, OWLClass child, OWLClass parent) {
		visited.add(child);
		final JsonObject childObject = new JsonObject();
		childObject.addProperty("id", renderer.render(child));
		childObject.addProperty("label", renderer.render(child));
		nodes.add(childObject);
		if (parent != null) {
			JsonObject edge = new JsonObject();
			edge.addProperty(renderer.render(parent), renderer.render(child));
			edges.add(edge);
		} else {
			if (EntitySearcher.getSuperClasses(child, ontology).size() == 0) {
				JsonObject edge = new JsonObject();
				edge.addProperty("Thing", renderer.render(child));
				edges.add(edge);
			}

		}
		Collection<OWLClassExpression> childs = EntitySearcher.getSubClasses(child, ontology);
		if (childs.size() > 0) {

			childs.forEach(
					ce -> ce.accept(new OWLClassExpressionVisitorAdapter() {
						public void visit(OWLClass owlClass) {
							recursive2(ontology, owlClass, child);
						}
					})
			);
		}
	}

	private static OWLObjectVisitorAdapter initPopulationEngine2(OWLOntology activeOntology) {
		return new OWLObjectVisitorAdapter() {
			@Override
			public void visit(@Nonnull OWLOntology ontology) {
				ontology.getClassesInSignature()
						.stream().filter(c -> !c.isOWLThing())
						.forEach(c -> c.accept(this));
			}

			@Override
			public void visit(@Nonnull OWLClass owlClass) {
				if (!visited.contains(owlClass)) {
					recursive2(activeOntology, owlClass, null);
				}
			}
		};
	}

	private static void recursive(OWLOntology ontology, OWLClass child, OWLClass parent, JsonObject parentObject) {
		visited.add(child);
		final JsonObject childObject = new JsonObject();
		childObject.addProperty("name", renderer.render(child));
		childObject.addProperty("size", randInt(2, 10) * SIZE);

		if (parent != null && parentObject.has("children")) {

			parentObject.get("children").getAsJsonArray().add(childObject);

		} else if (parentObject.has("children")) {
			if (EntitySearcher.getSuperClasses(child, ontology).size() == 0)
				parentObject.get("children").getAsJsonArray().add(childObject);

		}
		Collection<OWLClassExpression> childs = EntitySearcher.getSubClasses(child, ontology);
		if (childs.size() > 0) {
			childObject.remove("size");
			final JsonArray childArray = new JsonArray();
			childObject.add("children", childArray);
			childs.forEach(
					ce -> ce.accept(new OWLClassExpressionVisitorAdapter() {
						public void visit(OWLClass owlClass) {
							recursive(ontology, owlClass, child, childObject);
						}
					})
			);
		}
	}

	private static OWLObjectVisitorAdapter initPopulationEngine(OWLOntology activeOntology, JsonObject thingObject) {
		return new OWLObjectVisitorAdapter() {
			@Override
			public void visit(@Nonnull OWLOntology ontology) {
				ontology.getClassesInSignature()
						.stream().filter(c -> !c.isOWLThing())
						.forEach(c -> c.accept(this));
			}

			@Override
			public void visit(@Nonnull OWLClass owlClass) {
				if (!visited.contains(owlClass)) {
					recursive(activeOntology, owlClass, null, thingObject);
				}
			}
		};
	}
	public static void printClassesOfIndividuals(OWLReasoner reasoner,OWLNamedIndividual individual) {
		OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();
		reasoner.flush();
		System.out.println("The individuals named '"+renderer.render(individual)+"' belong to the following classes: ");
		for(OWLClass c : reasoner.getTypes(individual,false).getFlattened()) {
			if(!c.isOWLThing()) {
				System.out.println(renderer.render(c));
			}
		}
		System.out.println("----------------------");
	}

	public static Set<OWLObjectPropertyExpression> findObjectPropertiesByDomain(SWRLAPIOWLOntology ontology, OWLClass domain) {
		final Set<OWLObjectPropertyExpression> retOEs = new HashSet<>();
		final SWRLAPIRenderer renderer = new DefaultSWRLAPIRenderer(ontology);
		ontology.getSWRLAPIRules().forEach(rule -> {
			if (!rule.isSQWRLQuery()) {
				if (rule.getClassesInSignature().contains(domain)) {
					retOEs.addAll(rule.getObjectPropertiesInSignature());
//					System.out.println(renderer.renderSWRLRule(rule));
				}
			}
		});

		return retOEs;

	}

	public static Set<OWLDataPropertyExpression> findDataPropertiesByDomain(SWRLAPIOWLOntology ontology, OWLClass domain) {
		final Set<OWLDataPropertyExpression> retDEs = new HashSet<>();
		final SWRLAPIRenderer renderer = new DefaultSWRLAPIRenderer(ontology);
		ontology.getSWRLAPIRules().forEach(rule -> {
			if (!rule.isSQWRLQuery()) {
				rule.getBodyAtoms().forEach(atom -> {
					if (atom.getClassesInSignature().contains(domain))
						retDEs.addAll(rule.getDataPropertiesInSignature());
				});
			}
		});

		return retDEs;

	}

}
