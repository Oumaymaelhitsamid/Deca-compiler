package fr.ensimag.deca.tree;

import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;



/**
 *
 * @author gl44
 * @date 13/01/2022
 */

public class Return extends AbstractExpr{

  private AbstractExpr expr;

  public Return(AbstractExpr expr){
    Validate.notNull(expr);
    this.expr = expr;
  }

  public void setExpr(AbstractExpr expr) {
    this.expr = expr;
  }

  public AbstractExpr getExpr() {
    return this.expr;
  } 

  @Override
  public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
          ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

		Type typeExpr = expr.verifyExpr(compiler, localEnv, currentClass, envTypes);
		setType(typeExpr);
		return typeExpr;
  }

  @Override
  public void decompile(IndentPrintStream s) {
    s.print("return ");
    expr.decompile(s);
  }

  @Override
  protected void iterChildren(TreeFunction f) {
      // leaf node => nothing to do
  }

  @Override
  protected void codeGenMethodInst(DecacCompiler compiler, MethodDefinition methodDefinition){
      compiler.addComment("~~~~~~~~~~~~~~~~~~");
      compiler.addComment("Return instruction");
      compiler.addComment("~~~~~~~~~~~~~~~~~~");
      expr.codeGenInst(compiler);
      compiler.addInstruction(new LOAD(Register.R1, Register.R0));
      Label lbFin = new Label("fin."+methodDefinition.getLabel().getName());
      compiler.addInstruction(new BRA(lbFin));
  }

  @Override
	protected void prettyPrintChildren(PrintStream s, String prefix) {
    expr.prettyPrint(s,prefix,true);
	}



}
