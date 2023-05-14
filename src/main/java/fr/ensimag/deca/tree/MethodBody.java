package fr.ensimag.deca.tree;

import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.MethodDefinition;

import fr.ensimag.ima.pseudocode.instructions.SUBSP;

/**
 * Class declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */

 public class MethodBody extends AbstractMethodBody{
   private ListDeclVar varList;
   private ListInst instList;

   public ListDeclVar getVarList(){
       return varList;
   }


   public MethodBody(ListDeclVar varList,ListInst instList){
     Validate.notNull(varList);
     Validate.notNull(instList);
     this.varList = varList;
     this.instList = instList;
   }

     @Override
   public void decompile(IndentPrintStream s) {
     varList.decompile(s);
     instList.decompile(s);
   }

   @Override
   protected void iterChildren(TreeFunction f) {
     varList.iter(f);
     instList.iter(f);
   }

   @Override
   protected void prettyPrintChildren(PrintStream s, String prefix) {
         varList.prettyPrint(s,prefix,false);
         instList.prettyPrint(s,prefix,true);
   }

   @Override
   void codeGenBody(DecacCompiler compiler, MethodDefinition methodDefinition){
       varList.codeGenListDeclVar(compiler);
       instList.codeGenListMethodInst(compiler, methodDefinition);
   }

   protected void verifyListDeclVariable(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass,
           EnvironmentExp envTypes) throws ContextualError {

		varList.verifyListDeclVariable(compiler, localEnv, currentClass, envTypes);
    }

   @Override
   protected void verifyListInst(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass,
           EnvironmentExp envTypes, Type returnType) throws ContextualError {

    instList.verifyListInst(compiler, localEnv, currentClass, returnType, envTypes);
    }
 }
