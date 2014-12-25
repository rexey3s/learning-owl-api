package vn.edu.uit.swrlapi.collector.impl;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
import org.swrlapi.builtins.arguments.SWRLBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLLiteralBuiltInArgument;
import org.swrlapi.builtins.arguments.SWRLVariableBuiltInArgument;

import java.util.*;


public class DataPropertyAtomCollector implements SWRLObjectVisitor {

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
	private final OWLClass namedClass;
	/* mapping DataProperty and their Arguments */
	private final Map<SWRLVariable,OWLDataProperty> currentMapper;
	/* recommended answers */
	private final HashMap<OWLDataProperty, Object> recommendedAnswers;
	/* May use Multi Map for this one */
	private Map<SWRLBuiltInsVocabulary, OWLLiteral> currentRange;
	private SWRLVariable currentVar;
	private OWLDataProperty currentDprop;
	public DataPropertyAtomCollector(OWLClass cls) 
	{
		namedClass = cls;
		currentMapper =
				new HashMap<>();
		currentRange =
				new HashMap<>();
		recommendedAnswers =
				new HashMap<>();
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

