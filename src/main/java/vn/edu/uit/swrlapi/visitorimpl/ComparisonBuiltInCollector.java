package vn.edu.uit.swrlapi.visitorimpl;
/**
 * @author Chuong Dang, University of Information and Technology, 
 * 		   Faculty of Computer Network and Telecommunication, Date: Oct 14, 2014
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
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
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
/**
 * Collect all comparison SWRL Built-in and associated Classes
 */
public class ComparisonBuiltInCollector implements SWRLObjectVisitor 	{
	
//	private final Map<OWLDataProperty,Map<Integer,Integer>> dataRangeInRules;
	private final Map<SWRLVariable, Set<SWRLLiteralArgument>> variablesMap;
	private final Set<OWLOntology> onts;
	
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
	
	public ComparisonBuiltInCollector(Set<OWLOntology> onts) {
//		dataRangeInRules = new HashMap<OWLDataProperty,Map<Integer,Integer>>();
		variablesMap = new HashMap<SWRLVariable, Set<SWRLLiteralArgument>>();
		this.onts = onts;
	}
	/**
	 * Let {@link org.semanticweb.owlapi.model.SWRLAtom} accepts 
	 * 	   {@link vn.edu.uit.swrlapi.visitorimpl.ComparisonBuiltInCollector}
	 */
	@Override
	public void visit(SWRLRule node) {
		for(SWRLAtom atom : node.getBody()) {
			atom.accept(this);
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
	public void visit(SWRLDataPropertyAtom node) {
		// TODO Auto-generated method stub
		System.out.println("The first argument is "+node.getFirstArgument()+
						" ,the second argument is "+node.getSecondArgument());
		if(node.getSecondArgument() instanceof SWRLVariable) {
			System.out.println("Hey there are variables here");
		} else if(node.getSecondArgument() instanceof SWRLLiteralArgument) {
			System.out.println("Hey there literal argument here");
			SWRLLiteralArgument lit = (SWRLLiteralArgument)node.getSecondArgument();
			System.out.println("And the literal is "+lit.getLiteral());
			
		}
		
	}

	@Override
	public void visit(SWRLBuiltInAtom node)	{
		SWRLBuiltInsVocabulary builtInVoc = 
				SWRLBuiltInsVocabulary.getBuiltIn(node.getPredicate());
		if(node.isCoreBuiltIn() && compareVocab.contains(builtInVoc)) {
			System.out.println("Yo!");
		}
	}

	@Override
	public void visit(SWRLVariable node) {
		// TODO Auto-generated method stub
		System.out.println("SWRLVariable here");
	}

	@Override
	public void visit(SWRLIndividualArgument node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SWRLLiteralArgument node) {
		// TODO Auto-generated method stub
		System.out.println("Literal Argument here");
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
