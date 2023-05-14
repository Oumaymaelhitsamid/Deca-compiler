package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Register;

import fr.ensimag.deca.codegen.StackTracker;

/**
 * Binary expressions.
 *
 * @author gl44
 * @date 01/01/2022
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {

    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;

    public AbstractBinaryExpr(AbstractExpr leftOperand,
            AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
        s.print(")");
    }

    abstract protected String getOperatorName();

    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }

    /**
     * Calcule les arguments et les mets dans les registre d'indices return[0] et return[1]
     * Si un indice est nul, l'élément est dans la pile.
     */
    protected int[] codeGenRecupArgs(DecacCompiler compiler) {
        int ri1 = this.getLeftOperand().codeGenStock(compiler);
        int ri2 = this.getRightOperand().codeGenStock(compiler);

        if (ri2 < 0){
            compiler.addInstruction(new POP(Register.R0));
            compiler.stackTracker.pop();
            ri2 = 0;
        }

        if (ri1 < 0){
            compiler.addInstruction(new POP(Register.R1));
            compiler.stackTracker.pop();
            ri1 = 1;
        }
        return new int[]{ri2, ri1}; // On échange l'ordre car ADD 1, 2 réalise 2+1, par exemple
    }
}
