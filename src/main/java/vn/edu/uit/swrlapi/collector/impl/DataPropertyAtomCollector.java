package vn.edu.uit.swrlapi.collector.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
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
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgumentVisitorEx;
import org.swrlapi.builtins.arguments.SWRLLiteralBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLVariableBuiltInArgument;
import org.swrlapi.core.SWRLAPIBuiltInAtom;
import org.swrlapi.core.visitors.SWRLAPIBuiltInAtomVisitorEx;


public class DataPropertyAtomCollector implements SWRLObjectVisitor {
		
	private final OWLClass namedClass;
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
	/* mapping DataProperty and their Arguments */
	
	
	
	private final Map<SWRLVariable,OWLDataProperty> currentMapper;
	/* May use Multi Map for this one */
	private Map<SWRLBuiltInsVocabulary, OWLLiteral> currentRange;
	/* recommended answers */
	private final HashMap<OWLDataProperty,Object> recommendedAnswers;
	
	private SWRLVariable currentVar; 
	private OWLDataProperty currentDprop;
	public DataPropertyAtomCollector(OWLClass cls) 
	{
		namedClass = cls;
		currentMapper =
				new HashMap<SWRLVariable, OWLDataProperty>();
		currentRange =
				new HashMap<SWRLBuiltInsVocabulary, OWLLiteral>();
		recommendedAnswers = 
				new HashMap<OWLDataProperty,Object>();
	}
	public Map<OWLDataProperty,Object> getRecommendedAnswers() 
	{
		return recommendedAnswers;
	}
	@Override
	public void visit(SWRLRule node) 
	{
		Set<SWRLAtom> atoms = node.getBody();
		boolean hasFound = false;
		for(SWRLAtom atom : atoms) {
			if(atom.getClassesInSignature().contains(this.namedClass)) {
				hasFound = true;
				break;
			}
		}
		if(hasFound) {
			for(SWRLAtom atom : node.getBody()) {
				atom.accept(this);
			}
		} else {
//			System.out.println("Class " 
//					+ this.namedClass + " not found in Rule body");
		}
		
	}
	@Override
	public void visit(SWRLClassAtom node) {
		// TODO Auto-generated method stub
	}
	@Override
	public void visit(SWRLDataRangeAtom node) {
		// TODO Auto-generated method stub
	}
	@Override
	public void visit(SWRLObjectPropertyAtom node) {
		// TODO Auto-generated method stub
	}
	@Override
	public void visit(SWRLDataPropertyAtom node) 
	{
		OWLDataProperty dProp = node
				.getDataPropertiesInSignature().iterator().next();
		currentDprop = dProp;
		
			SWRLDArgument secondArg = node.getSecondArgument();
			
			if(secondArg instanceof SWRLLiteralArgument) {
				
				SWRLLiteralArgument literalArg = (SWRLLiteralArgument)secondArg;
				
				recommendedAnswers
				.put(dProp, Collections.singleton(literalArg.getLiteral()));
			} else if (secondArg instanceof SWRLVariable) {
				currentVar = (SWRLVariable)secondArg;
				currentMapper
				.put(currentVar, currentDprop);
			}
		
	}
	@Override
	public void visit(SWRLBuiltInAtom node) 
	{		
		

		SWRLBuiltInsVocabulary vocab = SWRLBuiltInsVocabulary
				.getBuiltIn(node.getPredicate());
		
		for(SWRLDArgument arg :node.getArguments()) {
			
			if (arg instanceof SWRLLiteralBuiltInArgument) {
				
				SWRLBuiltInArgument builtInArgument = (SWRLBuiltInArgument)arg;
				CompareBuiltInRangeCollector collector = new CompareBuiltInRangeCollector(vocab);
				currentRange.put(vocab,builtInArgument.accept(collector));
				System.out.println(recommendedAnswers);
				recommendedAnswers.put(currentDprop, currentRange);
			} else if (arg instanceof SWRLVariableBuiltInArgument) {
				
			}
		}
	}
	@Override
	public void visit(SWRLVariable node) {
		
	}
	@Override
	public void visit(SWRLIndividualArgument node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(SWRLLiteralArgument node) {
		
	}
	@Override
	public void visit(SWRLSameIndividualAtom node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(SWRLDifferentIndividualsAtom node) {
		// TODO Auto-generated method stub
		
	}
	
	
}

