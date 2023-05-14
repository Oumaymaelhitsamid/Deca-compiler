package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.context.NullType;
import fr.ensimag.deca.tools.SymbolTable;

import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;


/**
 *
 * @author gl44
 * @date 13/01/2022
 */

public class Null extends AbstractExpr{

  public Null(){
  }

  @Override
  public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
          ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

              Type tmp = new NullType(compiler.getSymbolTable().create("null"));
              setType(tmp);
	          return tmp;
  }

  @Override
  protected void codeGenInst(DecacCompiler compiler){
      compiler.addInstruction(new LOAD(new NullOperand(), Register.R1));
  }

  @Override
  public void decompile(IndentPrintStream s) {
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
