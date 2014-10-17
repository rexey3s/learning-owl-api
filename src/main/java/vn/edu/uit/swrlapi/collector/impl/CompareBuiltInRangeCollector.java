package vn.edu.uit.swrlapi.collector.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLLiteral;
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
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
import org.semanticweb.owlapi.vocab.SWRLVocabulary;
import org.swrlapi.builtins.arguments.SQWRLCollectionVariableBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLAnnotationPropertyBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgumentVisitorEx;
import org.swrlapi.builtins.arguments.SWRLClassBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLDataPropertyBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLDatatypeBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLLiteralBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLMultiValueVariableBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLNamedIndividualBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLObjectPropertyBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLVariableBuiltInArgument;
import org.swrlapi.core.SWRLAPIBuiltInAtom;
import org.swrlapi.core.visitors.SWRLAPIBuiltInAtomVisitorEx;
import org.swrlapi.core.visitors.SWRLArgumentVisitorEx;

public class CompareBuiltInRangeCollector implements SWRLBuiltInArgumentVisitorEx<OWLLiteral> {
	
	private static final Set<SWRLBuiltInsVocabulary> allowedSWRLVocab = 
			new HashSet<SWRLBuiltInsVocabulary>() { 
		private static final long serialVersionUID = 1L;
		{
			add(SWRLBuiltInsVocabulary.GREATER_THAN);
			add(SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL);
			add(SWRLBuiltInsVocabulary.EQUAL);
			add(SWRLBuiltInsVocabulary.NOT_EQUAL);
			add(SWRLBuiltInsVocabulary.LESS_THAN);
			add(SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL);
		}
	};
	private final SWRLBuiltInsVocabulary builtInVocab;
	private final Map<SWRLBuiltInsVocabulary,OWLLiteral> pop;
	
	public CompareBuiltInRangeCollector(SWRLBuiltInsVocabulary vocab) {
		this.builtInVocab = vocab;
		pop = new HashMap<SWRLBuiltInsVocabulary,OWLLiteral>();
	}
	
	public Map<SWRLBuiltInsVocabulary,OWLLiteral> getLiterals() {
		return pop;
	}
	
	@Override
	public OWLLiteral visit(SWRLClassBuiltInArgument argument) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public OWLLiteral visit(SWRLNamedIndividualBuiltInArgument argument) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public OWLLiteral visit(SWRLObjectPropertyBuiltInArgument argument) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public OWLLiteral visit(SWRLDataPropertyBuiltInArgument argument) {
		return null;
	}
	@Override
	public OWLLiteral visit(SWRLAnnotationPropertyBuiltInArgument argument) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public OWLLiteral visit(SWRLDatatypeBuiltInArgument argument) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public OWLLiteral visit(SWRLLiteralBuiltInArgument argument) {
		System.out.println(argument);
		return argument.getLiteral();
	}
	
	@Override
	public OWLLiteral visit(SWRLVariableBuiltInArgument argument) {
		System.out.println(argument.getIRI());
		return null;
	}
	
	@Override
	public OWLLiteral visit(SWRLMultiValueVariableBuiltInArgument argument) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public OWLLiteral visit(SQWRLCollectionVariableBuiltInArgument argument) {
		// TODO Auto-generated method stub
		return null;
	}	
}
