package vn.edu.uit.chuong.owl2.learning_owl2api;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Hello world!
 *
 */
public class App 
{
	static final String http = "http://acrab.ics.muni.cz/ontologies/tutorial.owl";
	static final String local = "./animal.owl";
	static final JsonArray sets = new JsonArray();
	static final JsonArray overlaps = new JsonArray();
	static final Set<int[]> intSet = new HashSet<>();
	private static final String http1 = "http://chuongdang.com/transport.owl";
	private static final JsonObject thingObject = new JsonObject();
	private static final JsonArray thingArray = new JsonArray();
	private static final JsonArray nodes = new JsonArray();
	private static final JsonArray edges = new JsonArray();
	private static final Set<OWLClass> visited = new HashSet<>();
	static int SIZE = 350;
	static List<OWLClass> owlClasses;
	static int classCount = 0;
	static OWLClass thing = OWLManager.getOWLDataFactory().getOWLThing();
	private static OWLObjectRenderer renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();

	public static void main(String[] args)  throws InconsistentOntologyException, UnsupportedOperationException, OWLException, IOException {
		//
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(http1));
		//
		OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();

		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, new SimpleConfiguration());
		reasoner.precomputeInferences();

		OWLDataFactory factory = manager.getOWLDataFactory();

		manager.setOntologyDocumentIRI(ontology, ontology.getOntologyID().getDefaultDocumentIRI().get());
		PrefixManager pm = new DefaultPrefixManager(null, null, manager.getOntologyDocumentIRI(ontology).toString() + "#");
		/* Venn Diagram
		owlClasses = ontology
				.getClassesInSignature()
				.stream()
				.filter(c-> !c.isOWLThing())
				.map(c -> (c))
				.collect(Collectors.toCollection(ArrayList::new));
		classCount = owlClasses.size();
		int[] arr = new int[classCount];
		for(int i=0; i < classCount;  i++) {
			arr[i] = i;
		}

		comb2(arr);
		owlClasses.forEach(clz -> {
			int size = reasoner.getInstances(clz, false).getFlattened().size();
			sets.add(buildSet(clz, size + 1));
		});
		intSet.forEach(tempArr -> overlaps.add(buildOverlap(tempArr)));

		for(int i=0; i < classCount; i++) {
			r(i, -1, reasoner);

		}

		System.out.println(intSet.size());
		System.out.println(sets.toString());
		System.out.println(overlaps.toString());
		*/
		/* Directed - graph */
		owlClasses = ontology
				.getClassesInSignature()
				.stream()
				.collect(Collectors.toCollection(ArrayList::new));
		classCount = owlClasses.size();

		owlClasses.forEach(clz -> {
			nodes.add(buildNode(clz, owlClasses.indexOf(clz)));
		});

		for (int i = 0; i < classCount; i++) {
			recursive2(ontology, -1, i);

		}

		System.out.println(nodes.toString());
		System.out.println(edges.toString());

	}

	static JsonObject buildNode(OWLClass owlClass, int id) {
		final JsonObject jsObject = new JsonObject();
		jsObject.addProperty("id", id);
		jsObject.addProperty("label", renderer.render(owlClass));
		return jsObject;
	}

	static JsonObject buildEdge(int source, int target, String label) {
		final JsonObject jsObject = new JsonObject();
		jsObject.addProperty("source", source);
		jsObject.addProperty("target", target);
		jsObject.addProperty("label", label);
		return jsObject;
	}

	static JsonObject buildOverlap(int[] arr) {
		final JsonObject jsObject = new JsonObject();
		final JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonPrimitive(arr[0]));
		jsonArray.add(new JsonPrimitive(arr[1]));

		jsObject.add("sets", jsonArray);
		jsObject.addProperty("size", 0);
		return jsObject;
	}

	static JsonObject buildSet(OWLClass owlClass, int numberOfIndividuals) {
		final JsonObject jsObject = new JsonObject();
		jsObject.addProperty("label", renderer.render(owlClass));
		jsObject.addProperty("size", numberOfIndividuals);
		return jsObject;
	}

	static Boolean checkIndex(int index, int size) {
		return (index >= 0 && index < size);
	}

	static void r(int indexChild, int indexParent, OWLReasoner reasoner) {
		if (checkIndex(indexChild, classCount) && checkIndex(indexParent, classCount)) {

			JsonObject parentJson = sets.get(indexParent).getAsJsonObject();

			int childSize = sets.get(indexChild).getAsJsonObject().get("size").getAsInt();
			int parentSize = parentJson.get("size").getAsInt();

			OWLClass parent = owlClasses.get(indexParent);
//			parentSize += (childSize * 2);
			parentSize += childSize;
			parentJson.addProperty("size", parentSize);
			for (JsonElement e : overlaps) {
				JsonObject temp = e.getAsJsonObject();
				JsonArray tmpArr = temp.getAsJsonArray("sets");
				if ((tmpArr.get(0).getAsInt() == indexParent && tmpArr.get(1).getAsInt() == indexChild)
						|| (tmpArr.get(0).getAsInt() == indexChild && tmpArr.get(1).getAsInt() == indexParent)) {
					int currentSize = temp.get("size").getAsInt();
					currentSize += childSize;
					temp.addProperty("size", currentSize);
				}
			}
			Set<OWLClass> superClasses = reasoner.getSuperClasses(parent, false).getFlattened();
			superClasses.remove(thing);
			for (OWLClass superClass : superClasses) {
				r(indexParent, owlClasses.indexOf(superClass), reasoner);					
			}

		} else if (checkIndex(indexChild, classCount)) {
			OWLClass child = owlClasses.get(indexChild);

			Set<OWLClass> superClasses = reasoner.getSuperClasses(child, false).getFlattened();
			superClasses.remove(thing);
			for (OWLClass superClass : superClasses) {
				r(indexChild, owlClasses.indexOf(superClass), reasoner);
			}
		}
	}

//	static void r2(int indexChild, int indexParent, OWLReasoner reasoner) {
//		if(checkIndex(indexChild, classCount) && checkIndex(indexParent, classCount)) {
//			JsonObject childJson
//		} else if(checkIndex(indexParent, classCount)) {
//
//		}
//
//	}

	private static void recursive2(OWLOntology ontology, int indexChild, int indexParent) {
		if (checkIndex(indexChild, classCount) && checkIndex(indexParent, classCount)) {

			OWLClass child = owlClasses.get(indexChild);
			edges.add(buildEdge(indexParent, indexChild, " has SubClass"));
			Collection<OWLClassExpression> subClasses = EntitySearcher.getSubClasses(child, ontology);
			for (OWLClassExpression subClass : subClasses) {
				if (subClass instanceof OWLClass) {
					recursive2(ontology, owlClasses.indexOf(subClass), indexChild);
				}
			}

		} else if (checkIndex(indexParent, classCount)) {
			OWLClass parent = owlClasses.get(indexParent);

			Collection<OWLClassExpression> subClasses = EntitySearcher.getSubClasses(parent, ontology);
			for (OWLClassExpression subClass : subClasses) {
				if (subClass instanceof OWLClass) {
					recursive2(ontology, owlClasses.indexOf(subClass), indexParent);
				}
			}
		}
	}

	
	static void comb2(int[] items) {
		Arrays.sort(items);
		kcomb(items, 0, 2, new int[2]);
		
	}

	static void kcomb(int[] items, int n, int k, int[] arr) {
		if (k == 0) {
			int[] temp = Arrays.copyOf(arr, 2);
			intSet.add(temp);
		} else {
			for (int i = n; i <= items.length - k; i++) {
				arr[arr.length - k] = items[i];
				kcomb(items, i + 1, k - 1, arr);
			}
		}
	}




}
