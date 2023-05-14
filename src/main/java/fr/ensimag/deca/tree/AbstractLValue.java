package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.DAddr;

/**
 * Left-hand side value of an assignment.
 *
 * @author gl44
 * @date 01/01/2022
 */
public abstract class AbstractLValue extends AbstractExpr {

    protected abstract DAddr getLValueOperand(DecacCompiler compiler);

    
}
