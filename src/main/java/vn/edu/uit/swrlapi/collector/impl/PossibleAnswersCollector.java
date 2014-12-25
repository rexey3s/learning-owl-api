package vn.edu.uit.swrlapi.collector.impl;
/**
 * @author Chuong Dang, University of Information and Technology, 
 * 		   Faculty of Computer Network and Telecommunication, Date: Oct 14, 2014
 */

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * Collect all comparison SWRL Built-in and associated Classes
 */
public class PossibleAnswersCollector implements SWRLObjectVisitor 	{

	private static final Set<SWRLBuiltInsVocabulary> compareVocab =
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
	private final Set<SWRLRule> processedRules;
	private final Map<OWLDataProperty,Set<SWRLLiteralArgument>> dpArgs;
	private final Set<SWRLDataPropertyAtom> processedProps;
	private final Set<SWRLLiteralArgument> processedLiteralArgs;
	private final Map<OWLDataProperty,Set<SWRLDArgument>> dpDArgs;
	private final Set<SWRLVariable> processedVars;
	/* Class to find suggestion */
	private final OWLClass  cls;
	/* IArgument */
	private SWRLIArgument individual;
	
	public PossibleAnswersCollector(OWLClass cls) {
		
		processedRules = new HashSet<SWRLRule>();
		dpArgs = new HashMap<OWLDataProperty,Set<SWRLLiteralArgument>>();
		processedProps = new HashSet<SWRLDataPropertyAtom>();
		processedLiteralArgs = new HashSet<SWRLLiteralArgument>();
		
		dpDArgs = new HashMap<OWLDataProperty,Set<SWRLDArgument>>();
		processedVars = new HashSet<SWRLVariable>();
		
		
		this.cls = cls;
	}
	public Map<OWLDataProperty,Set<SWRLLiteralArgument>> getPossibleAnswers() {
		return dpArgs;
	}
	/**
	 * Let {@link org.semanticweb.owlapi.model.SWRLAtom} accepts 
	 * 	   {@link vn.edu.uit.swrlapi.collector.impl.PossibleAnswersCollector}
	 */
	@Override
	public void visit(SWRLRule node) {
		Set<SWRLAtom> atoms = node.getBody();
		boolean hasFound = false;
		for(SWRLAtom atom : atoms) {
			if(atom.getClassesInSignature().contains(this.cls)) {
				hasFound = true;
				break;
			}
		}
		if(hasFound) {

			for(SWRLAtom atom : node.getBody()) {
				atom.accept(this);
			}
		} else {
			System.out.println("Class " + this.cls + " not found in Rule body");
		}
	}
	/**
	 * 		  visit ClassAtoms in rule body
	 */
	@Override
	public void visit(SWRLClassAtom node) {
		if(node.getClassesInSignature().contains(this.cls)) {
			this.individual = node.getArgument();
		}
	}
	/**
	 * 		  visit DataRangeAtom in rule body
	 */
	@Override
	public void visit(SWRLDataRangeAtom node) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 		  visit SWLObjectPropertyAtom in rule body
	 */
	@Override
	public void visit(SWRLObjectPropertyAtom node) {
		for(SWRLArgument arg : node.getAllArguments()) {
			arg.accept(this);
		}
 	}

	@Override
	public void visit(SWRLDataPropertyAtom node) {
		if(!processedProps.contains(node)) {
			System.out.println("The first argument is "+node.getFirstArgument()+
							" ,the second argument is "+node.getSecondArgument());
			if(node.getFirstArgument() instanceof SWRLVariable) {
				
				if(node.getSecondArgument() instanceof SWRLVariable) {
					node.getSecondArgument().accept(this);
					
				} else if(node.getSecondArgument() instanceof SWRLLiteralArgument) {
					node.getSecondArgument().accept(this);
					dpArgs.put(node.getDataPropertiesInSignature().iterator().next(),
							processedLiteralArgs);
				}
			}
			processedProps.add(node);
		}
	}

	@Override
	public void visit(SWRLBuiltInAtom node)	{
		SWRLBuiltInsVocabulary builtInVoc = 
				SWRLBuiltInsVocabulary.getBuiltIn(node.getPredicate());
		if(node.isCoreBuiltIn() && compareVocab.contains(builtInVoc)) {
			System.out.println("BuiltIn here!");
			
		}
		
	}

	@Override
	public void visit(SWRLVariable node) {
		System.out.println("SWRLVariable here");
		
	}

	@Override
	public void visit(SWRLIndividualArgument node) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(SWRLLiteralArgument node) {
		System.out.println("Literal Argument here");
		if(!processedLiteralArgs.contains(node)) {
			processedLiteralArgs.add(node);
		}
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
