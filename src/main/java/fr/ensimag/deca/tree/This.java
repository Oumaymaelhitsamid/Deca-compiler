package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;



/**
 *
 * @author gl44
 * @date 13/01/2022
 */

public class This extends AbstractExpr{

  public This(boolean contains){
    this.thisCase = contains;
  }

  public boolean thisPresent(){
    return thisCase;
  }

  @Override
  public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
          ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {
        if (currentClass == null) {
            throw new ContextualError("[ContextualError 3.43.1] Cannot use 'this' outside class nor call a method without intancied object in main", getLocation());
        }
        Type type = currentClass.getType();
        setType(type);
        return type;
  }

  @Override
  protected void codeGenInst(DecacCompiler compiler){
      compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
  }

  @Override
  public void decompile(IndentPrintStream s) {
    if(thisCase) s.print("this");
  }

  @Override
  protected void iterChildren(TreeFunction f) {
      // leaf node => nothing to do
  }

  @Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
	}



}
