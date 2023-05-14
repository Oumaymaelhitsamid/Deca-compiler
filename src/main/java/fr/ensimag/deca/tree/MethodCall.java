package fr.ensimag.deca.tree;

import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;

import java.util.ArrayList;
import java.util.List;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;

import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.CMP;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import fr.ensimag.deca.codegen.StackTracker;
import fr.ensimag.deca.codegen.ErrorManager;


/**
 *
 * @author gl44
 * @date 13/01/2022
 */

public class MethodCall extends AbstractExpr {

  private AbstractExpr e1;
  private AbstractIdentifier ident;
  private ListExpr args;

  public MethodCall(AbstractExpr e1, AbstractIdentifier ident, ListExpr args){
    Validate.notNull(e1);
    Validate.notNull(ident);
    Validate.notNull(args);
    this.e1 = e1;
    this.ident = ident;
    this.args = args;
  }

  @Override
  public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
          ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

      // On verifie l'espression à droite du point
      Type typeExpr = e1.verifyExpr(compiler, localEnv, currentClass, envTypes);

      if (!typeExpr.isClass()) {
        throw new ContextualError("[ContextualError 3.71.1] Cannot call a method on a not class Type object", getLocation());
      }
      // Cannot fail thanks to upper verification
      ClassType castExpr = (ClassType) typeExpr;


      // On verifie le nom de la méthode
      Definition def = ((ClassDefinition) envTypes.get(castExpr.getName())).getEnvironment().get(ident.getName());

      if (def == null) {
        throw new ContextualError("[ContextualError 3.72.2] Cannot find method " + ident.getName() + " on class " + typeExpr, getLocation());
      }
      if (!def.isMethod()) {
        throw new ContextualError("[ContextualError 3.72.3] Identifier " + ident.getName() + " does not define a method", getLocation());
      }
      // Cannot fail thanks to upper verification
      MethodDefinition methodDef = (MethodDefinition) def;
      ident.setDefinition(methodDef);


      // On vérifie que la signature correspond avec les types donnés
      Signature sign = methodDef.getSignature();

      // Get the types which are used for the methodCall
      int i = 0;
      for (AbstractExpr a : args.getList()) {
        if (i >= sign.size()) {
          throw new ContextualError("[ContextualError 3.72.1] Too much arguments (expected " + sign.size() + ")", getLocation());
        }

          Type gotType = a.verifyExpr(compiler, localEnv, currentClass, envTypes);
          Type expecType = sign.paramNumber(i);

          if (!gotType.sameType(expecType)) {
              if (!(gotType.isInt() && expecType.isFloat())) {
                if (!((gotType.isClassOrNull() && expecType.isClass()) && (gotType.isNull() || ((ClassType) gotType).isSubClassOf((ClassType) expecType)))) { 
                  throw new ContextualError("[ContextualError 3.74.1] Uncompatible types for methodCall (expected " + expecType + " got " + gotType + ")", getLocation());
                }
              } else {
                ConvFloat tmp = new ConvFloat(a);
                tmp.setType(expecType);
                args.set(i, tmp);
              }
          }

          i++;
      }

      if (i < sign.size()) {
          throw new ContextualError("[ContextualError 3.72.2] Not enough arguments (expected " + sign.size() + " got " + i + ")", getLocation());
      }

      setType(methodDef.getType());
      return methodDef.getType();
  }

  @Override
  protected void codeGenInst(DecacCompiler compiler){
      compiler.addComment("Method call to "+ident.getMethodDefinition().getLabel().getName());
      compiler.addInstruction(new ADDSP(args.size()+1));
      int i = -1;
      for (AbstractExpr exp : args.getList()){
          exp.codeGenInst(compiler);
          compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(i--, Register.SP)));
          compiler.stackTracker.push();
      }
      e1.codeGenInst(compiler);
      compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(0, Register.SP))); // R1 contient l'adresse de l'objet
      compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R1), Register.R1)); // R1 contient l'adresse '0' de la table des méthodes de l'objets
      ErrorManager.codeGenTestNULLError(compiler, Register.R1);
      compiler.addInstruction(new BSR(new RegisterOffset(ident.getMethodDefinition().getIndex(), Register.R1)));
      compiler.addInstruction(new LOAD(Register.R0, Register.R1));
      compiler.addInstruction(new SUBSP(args.size()+1));
      for (int j = 0; j < args.size(); j++){compiler.stackTracker.pop();}
      compiler.addComment("End of method call to "+ident.getMethodDefinition().getLabel().getName());
  }

  @Override
  protected void codeGenBranch(DecacCompiler compiler, Label lb){
      this.codeGenInst(compiler);
      compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.R1));
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
  public void decompile(IndentPrintStream s) {
    if(e1.getThisCase()){
    e1.decompile(s);
    s.print(".");
    }
    ident.decompile(s);
    s.print("(");
    args.decompile(s);
    s.print(")");
  }

  @Override
  protected void iterChildren(TreeFunction f) {
      if(e1.getThisCase()) e1.iter(f);
      ident.iter(f);
      args.iter(f);
  }

  @Override
  protected void prettyPrintChildren(PrintStream s, String prefix) {
        e1.prettyPrint(s,prefix,false);
        ident.prettyPrint(s,prefix,false);
        args.prettyPrint(s,prefix,true);
  }
}
