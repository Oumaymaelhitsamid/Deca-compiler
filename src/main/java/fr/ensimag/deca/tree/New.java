package fr.ensimag.deca.tree;

import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.tools.SymbolTable;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import fr.ensimag.deca.codegen.StackTracker;
import fr.ensimag.deca.codegen.ErrorManager;

/**
 *
 * @author gl44
 * @date 13/01/2022
 */

public class New extends AbstractExpr{

  private AbstractInst obj;

  public New(AbstractInst obj){
    this.obj = obj;
  }

  @Override
  public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
          ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

		if (!(obj instanceof Identifier)) {
			// Unreachable error in principle
			throw new ContextualError("[UnreachableError] Wanted new with no Instance", getLocation());
		}

		Identifier objIdent = (Identifier) obj;
    Type objType = objIdent.verifyExpr(compiler, envTypes, currentClass, null);

    if (!(objType.isClass())) {
        throw new ContextualError("[ContextualError 3.42.1] Uncompatible new object type", getLocation());
    }


		setType(objType);
		return objType;
  	}

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        Identifier ident = (Identifier) obj; // Le cast réussi grâce à la partie B
        compiler.addInstruction(new NEW(ident.getClassDefinition().getNumberOfFields()+1, Register.R1));
        ErrorManager.codeGenTestAllocError(compiler);
        compiler.addInstruction(new PUSH(Register.R1));
        compiler.stackTracker.push();
        Label lb = new Label("init."+ident.getClassDefinition().getLabel());
        compiler.addInstruction(new BSR(lb));
        compiler.addInstruction(new POP(Register.R1));
        compiler.stackTracker.pop();

    }

  @Override
  public void decompile(IndentPrintStream s) {
    s.print("new ");
    obj.decompile(s);
    s.print("()");
  }

  @Override
  protected void iterChildren(TreeFunction f) {
    obj.iterChildren(f);
  }

  @Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
    obj.prettyPrint(s,prefix,true);
        // leaf node => nothing to do
	}

}
