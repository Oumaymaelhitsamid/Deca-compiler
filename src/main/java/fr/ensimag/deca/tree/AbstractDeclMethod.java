package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;

/**
 * Method declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */

 public abstract class AbstractDeclMethod extends Tree{
   //A remplir pendant la partie B & C avec des abstract method
   abstract void codeGenDeclMethod(DecacCompiler compiler, int offset);

   abstract void codeGenMethod(DecacCompiler compiler);

   protected void verifyDeclMethod(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass)
           throws ContextualError{
             //A implémenter
           }

   protected abstract void verifyDeclMethod(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
           throws ContextualError;


  protected abstract void verifyMethodBody(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
           throws ContextualError;

 }
