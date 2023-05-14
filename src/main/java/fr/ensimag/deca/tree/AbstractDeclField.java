package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;

/**
 * Field declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */

 public abstract class AbstractDeclField extends Tree{
   //A remplir pendant la partie B & C avec des abstract method

   protected abstract void codeGenInitAttribut(DecacCompiler compiler);

   protected abstract void verifyDeclField(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
           throws ContextualError;

  protected abstract void verifyInitField(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes) 
           throws ContextualError;
 }
