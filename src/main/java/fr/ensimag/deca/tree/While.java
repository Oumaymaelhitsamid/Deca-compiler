package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class While extends AbstractInst {

    private AbstractExpr condition;
    private ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int ID = compiler.getUniqueID();
        Label lbWhile = new Label("While."+ID+".while");
        Label lbEnd = new Label("While."+ID+".end");
        compiler.addLabel(lbWhile);

        condition.codeGenBranch(compiler, lbEnd);
        body.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(lbWhile));
        compiler.addLabel(lbEnd);
    }

    @Override
    protected void codeGenMethodInst(DecacCompiler compiler, MethodDefinition methodDefinition) {
        int ID = compiler.getUniqueID();
        Label lbWhile = new Label("While."+ID+".while");
        Label lbEnd = new Label("While."+ID+".end");
        compiler.addLabel(lbWhile);

        condition.codeGenBranch(compiler, lbEnd);
        body.codeGenListMethodInst(compiler, methodDefinition);
        compiler.addInstruction(new BRA(lbWhile));
        compiler.addLabel(lbEnd);
    }


    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType, EnvironmentExp envTypes)
            throws ContextualError {

        // Verify the condition
        this.condition.verifyCondition(compiler, localEnv, currentClass, envTypes);

        // Verify the body
        this.body.verifyListInst(compiler, localEnv, currentClass, returnType, envTypes);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
