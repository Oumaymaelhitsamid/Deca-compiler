package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tree.Visibility;

import fr.ensimag.ima.pseudocode.instructions.POP;

import fr.ensimag.deca.codegen.StackTracker;
import fr.ensimag.deca.codegen.ErrorManager;

/**
 *
 * @author gl44
 * @date 13/01/2022
 */

 public class Selection extends AbstractLValue{

   private AbstractExpr expr;
   private AbstractIdentifier ident;

   public Selection(AbstractExpr expr, AbstractIdentifier ident){
     this.expr = expr;
     this.ident = ident;
   }

   @Override
   public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
           ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass, envTypes);

        if (! exprType.isClass()) {
          throw new ContextualError("[ContextualError 3.65.1] Cannot call an identifier on a not class Type object", getLocation());
        }

        ClassType castExpr = (ClassType) exprType;

        Definition def = ((ClassDefinition) envTypes.get(castExpr.getName())).getEnvironment().get(ident.getName());

        if (def == null) {
          throw new ContextualError("[ContextualError 3.65.2] Cannot find field " + ident.getName() + " in class " + castExpr, getLocation());
        }
        if (!def.isField()) {
          throw new ContextualError("[ContextualError 3.65.3] Identifier " + ident.getName() + " does not define a field", getLocation());
        }

        FieldDefinition fieldDef = (FieldDefinition) def;
        ident.setDefinition(fieldDef);

        if (fieldDef.getVisibility() == Visibility.PROTECTED && ((currentClass == null) || !(castExpr.isSubClassOf(currentClass.getType())))) {
          throw new ContextualError("[ContextualError 3.66.1] Cannot call parent protected field from child class", getLocation());
        }

        if (fieldDef.getVisibility() == Visibility.PROTECTED && !(currentClass.getType().isSubClassOf(fieldDef.getContainingClass().getType()))) {
          throw new ContextualError("[ContextualError 3.66.2] Cannot access protected field of child class", getLocation());
        }

        setType(fieldDef.getType());
        return fieldDef.getType();
   }

   @Override
   protected void codeGenInst(DecacCompiler compiler){
       expr.codeGenInst(compiler);
       ErrorManager.codeGenTestNULLError(compiler, Register.R1);
       compiler.addInstruction(new LOAD(new RegisterOffset(ident.getFieldDefinition().getIndex(), Register.R1), Register.R1));
   }

   @Override
   protected void codeGenBranch(DecacCompiler compiler, Label lb){
       this.codeGenInst(compiler);
       compiler.addInstruction(new BEQ(lb));
   }

   @Override
   protected void codeGenPrint(DecacCompiler compiler){
       this.codeGenInst(compiler);
       Type type = ident.getDefinition().getType();
       if (type.isInt()){
           compiler.addInstruction(new WINT());
       } else if (type.isFloat()){
           compiler.addInstruction(new WFLOAT());
       } else if (type.isBoolean()){
           int ID = compiler.getUniqueID();
           Label lbElse = new Label("ident."+ID+".else");
           Label lbEnd = new Label("ident."+ID+".end");
           compiler.addInstruction(new BNE(lbElse));
           compiler.addInstruction(new WSTR(new ImmediateString("false")));
           compiler.addInstruction(new BRA(lbEnd));
           compiler.addLabel(lbElse);
           compiler.addInstruction(new WSTR(new ImmediateString("true")));
           compiler.addLabel(lbEnd);
       } else {
           throw new UnsupportedOperationException("not yet implemented");
       }
   }

   @Override
   protected void codeGenPrintX(DecacCompiler compiler){
       if (ident.getDefinition().getType().isFloat()){
           this.codeGenInst(compiler);
           compiler.addInstruction(new WFLOATX());
       } else {
           this.codeGenInst(compiler);
       }
   }

   @Override
   protected DAddr getLValueOperand(DecacCompiler compiler){
       int ri = expr.codeGenStock(compiler);
       if (ri < 0){
           compiler.addInstruction(new POP(Register.R0));
           compiler.stackTracker.pop();
           ri = 0;
       }
       ErrorManager.codeGenTestNULLError(compiler, Register.getR(ri));
       return new RegisterOffset(ident.getFieldDefinition().getIndex(), Register.getR(ri));
   }

   @Override
   public void decompile(IndentPrintStream s) {
     expr.decompile(s);
     s.print(".");
     ident.decompile(s);
   }

   @Override
   protected void iterChildren(TreeFunction f) {
       // leaf node => nothing to do
   }

   @Override
   protected void prettyPrintChildren(PrintStream s, String prefix) {
     expr.prettyPrint(s,prefix,false);
     ident.prettyPrint(s,prefix,true);
   }
 }
