package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 * Full if/else if/else statement.
 *
 * @author gl44
 * @date 01/01/2022
 */
public class IfThenElse extends AbstractInst {

    private AbstractExpr condition; //Suppression du final
    private ListInst thenBranch;    //Suppression du final
    private ListInst elseBranch;
    private boolean else_present;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.else_present = true;
    }

    //Début Implémentation par Tristan : Si problème, peut être faire une classe IfThen.

    public IfThenElse(){
      this.condition = null;
      this.thenBranch = null;
      this.elseBranch = new ListInst();
      this.else_present = false;
    }

    public void setIf(AbstractExpr condition){
      Validate.notNull(condition);
      this.condition = condition;
    }

    public void setThen(ListInst thenBranch){
      Validate.notNull(thenBranch);
      this.thenBranch = thenBranch;
    }

    public void setElse(ListInst elseBranch){
      Validate.notNull(thenBranch);
      this.elseBranch = elseBranch;
      this.else_present = true;

    }

    //Fin



    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType, EnvironmentExp envTypes)
            throws ContextualError {

		// Verify condition
		this.condition.verifyCondition(compiler, localEnv, currentClass, envTypes);

		// Verify then and else branch
		this.thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType, envTypes);
		this.elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType, envTypes);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int ID = compiler.getUniqueID();
        Label lbElse = new Label("IfThenElse."+ID+".else");
        Label lbEnd = new Label("IfThenElse."+ID+".end");

        condition.codeGenBranch(compiler, lbElse);

        thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(lbEnd));

        compiler.addLabel(lbElse);
        elseBranch.codeGenListInst(compiler);
        compiler.addLabel(lbEnd);
    }

    @Override
    protected void codeGenMethodInst(DecacCompiler compiler, MethodDefinition methodDefinition) {
        int ID = compiler.getUniqueID();
        Label lbElse = new Label("IfThenElse."+ID+".else");
        Label lbEnd = new Label("IfThenElse."+ID+".end");

        condition.codeGenBranch(compiler, lbElse);

        thenBranch.codeGenListMethodInst(compiler, methodDefinition);
        compiler.addInstruction(new BRA(lbEnd));

        compiler.addLabel(lbElse);
        elseBranch.codeGenListMethodInst(compiler, methodDefinition);
        compiler.addLabel(lbEnd);
    }


    @Override
    public void decompile(IndentPrintStream s) {

      s.print("if(");
          condition.decompile();
          condition.decompile(s);
          s.println("){");
          thenBranch.decompile();
          thenBranch.decompile(s);
          s.print("}");
          if(else_present){
            s.println("else {");
            elseBranch.decompile();
            elseBranch.decompile(s);
            s.print("}");
          }
        }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
