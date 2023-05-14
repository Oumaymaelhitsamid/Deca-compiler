package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;

import fr.ensimag.deca.context.Type;

/**
 * Parameter declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */



public abstract class AbstractDeclParam extends Tree{
   //A remplir pendant la partie B & C avec des abstract method

    public abstract void setOperand(int i);

    public abstract Type getType(DecacCompiler compiler, EnvironmentExp envTypes) throws ContextualError;

    protected abstract void verifyDeclParam(DecacCompiler compiler,
        EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
        throws ContextualError;
 }
