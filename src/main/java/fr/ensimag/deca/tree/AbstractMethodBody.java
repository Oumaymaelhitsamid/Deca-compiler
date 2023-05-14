package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.MethodDefinition;


/**
 * Method Body declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */

 public abstract class AbstractMethodBody extends Tree{
   //A remplir pendant la partie B & C avec des abstract method

   abstract void codeGenBody(DecacCompiler compiler, MethodDefinition methodDefinition);

   protected abstract void verifyListDeclVariable(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass,
           EnvironmentExp envTypes) throws ContextualError;

   protected abstract void verifyListInst(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass,
           EnvironmentExp envTypes, Type returnType) throws ContextualError;

 }
