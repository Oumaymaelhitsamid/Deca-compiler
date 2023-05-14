package fr.ensimag.deca.tree;

import org.apache.commons.lang.Validate;
import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.InlinePortion;

import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;

import fr.ensimag.deca.DecacCompiler;
/**
 * Class declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */

 public class MethodAsmBody extends AbstractMethodBody{
   private StringLiteral s_method;


   public MethodAsmBody(StringLiteral s){
     Validate.notNull(s);
     this.s_method = s;
   }

     @Override
   public void decompile(IndentPrintStream s) {
     s_method.decompile(s);
   }

   @Override
   protected void codeGenBody(DecacCompiler compiler, MethodDefinition methodDefinition){
       InlinePortion p = new InlinePortion(s_method.getValue());
       compiler.add(p);
   }

   @Override
   protected void iterChildren(TreeFunction f) {
       // leaf node => nothing to do
   }

   @Override
   protected void prettyPrintChildren(PrintStream s, String prefix) {
         // leaf node => nothing to do
   }

   protected void verifyListDeclVariable(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass,
           EnvironmentExp envMethod) throws ContextualError {
    }

   @Override
   protected void verifyListInst(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass,
           EnvironmentExp envMethod, Type returnType) throws ContextualError {
    }
 }
