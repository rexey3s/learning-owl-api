package vn.edu.uit.swrlapi.collector.impl;

import org.semanticweb.owlapi.model.*;
import org.swrlapi.core.SWRLAPIOWLOntology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Chuong Dang, University of Information and Technology, HCMC Vietnam,
 *         Faculty of Computer Network and Telecomunication created on 12/25/14.
 */
public class PropertyAtomSearcher {
    private final Map<OWLObjectProperty, Set<OWLClass>> objectPropertiesSuggestion = new HashMap<>();
    private final Map<OWLDataProperty, Set<OWLLiteral>> dataPropertySuggestions = new HashMap<>();
    private final SWRLAPIOWLOntology ontology;
    private final Set<SWRLRule> rules = new HashSet<>();

    public PropertyAtomSearcher(SWRLAPIOWLOntology swrlapiowlOntology) {
        ontology = swrlapiowlOntology;
        ontology.getSWRLAPIRules().stream()
                .filter(rule -> !rule.isSQWRLQuery())
                .forEach(rule -> rules.add(rule.getSimplified()));

    }

    public Map<OWLObjectProperty, Set<OWLClass>> getObjectPropertySuggestionByClass(OWLClass clzz) {

        objectPropertiesSuggestion.clear();
        ObjectPropertyFillerAtomCollector fillerAtomCollector = new ObjectPropertyFillerAtomCollector();
        Set<SWRLRule> affectedRules = new HashSet<>();

        for (SWRLRule rule : rules) {
            for (SWRLAtom atom : rule.getBody()) {

                if (atom.containsEntityInSignature(clzz)) {
//                    System.out.println(rule);
                    affectedRules.add(rule);

                }
            }
        }
        ;
        affectedRules.forEach(rule -> rule.getBody().forEach(atom -> {
            if (atom instanceof SWRLObjectPropertyAtom) {
                SWRLObjectPropertyAtom atom1 = (SWRLObjectPropertyAtom) atom;
                objectPropertiesSuggestion.put(atom1.getPredicate().asOWLObjectProperty(), fillerAtomCollector.getFillerClasses(rule, atom1));
                System.out.println(objectPropertiesSuggestion);
            }

        }));
        return objectPropertiesSuggestion;
    }

    public static class ObjectPropertyFillerAtomCollector {

        public Set<OWLClass> getFillerClasses(SWRLRule rule, SWRLObjectPropertyAtom propertyAtom) {
            final Set<OWLClass> classesSuggestion = new HashSet<>();
            rule.accept(new SWRLObjectVisitorAdapter() {
                @Override
                public void visit(SWRLRule rule) {
                    rule.getBody().forEach(atom -> atom.accept(this));

                }

                @Override
                public void visit(SWRLClassAtom classAtom) {
                    if (propertyAtom.getSecondArgument().equals(classAtom.getArgument()) && !classAtom.getPredicate().isAnonymous()) {
                        classesSuggestion.add(classAtom.getPredicate().asOWLClass());

                    }
                }
            });
            return classesSuggestion;
        }

    }
}
