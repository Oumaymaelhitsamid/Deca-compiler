package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.Register;

import fr.ensimag.deca.codegen.StackTracker;


/**
 * Unary expression.
 *
 * @author gl44
 * @date 01/01/2022
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

    public AbstractExpr getOperand() {
        return operand;
    }

    private AbstractExpr operand;

    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }


    protected abstract String getOperatorName();

    @Override
    public void decompile(IndentPrintStream s) {

        s.print("(" + getOperatorName());
        operand.decompile(s);
        s.print(")");

        // throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }

    protected int codeGenRecupArg(DecacCompiler compiler) {
        int ri = this.getOperand().codeGenStock(compiler);
        if (ri < 0){
            compiler.addInstruction(new POP(Register.R1));
            compiler.stackTracker.pop();
            ri = 1;
        }
        return ri;
    }

}
