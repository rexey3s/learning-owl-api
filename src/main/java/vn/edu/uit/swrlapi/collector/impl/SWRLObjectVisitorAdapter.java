package vn.edu.uit.swrlapi.collector.impl;

import org.semanticweb.owlapi.model.*;

/**
 * @author Chuong Dang, University of Information and Technology, HCMC Vietnam,
 *         Faculty of Computer Network and Telecomunication created on 12/25/14.
 */
public class SWRLObjectVisitorAdapter implements SWRLObjectVisitor {
    @SuppressWarnings({"unused"})
    protected void handleDefault(
            @SuppressWarnings("unused") SWRLObject node) {
    }

    @Override
    public void visit(SWRLRule node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLClassAtom node) {
        handleDefault(node);
    }

    @Override
    public void visit(SWRLDataRangeAtom node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLObjectPropertyAtom node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLDataPropertyAtom node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLBuiltInAtom node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLVariable node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLIndividualArgument node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLLiteralArgument node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLSameIndividualAtom node) {
        handleDefault(node);

    }

    @Override
    public void visit(SWRLDifferentIndividualsAtom node) {
        handleDefault(node);

    }
}
