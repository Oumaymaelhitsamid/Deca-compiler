package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.And;
import fr.ensimag.deca.tree.Not;
/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class Or extends AbstractOpBool {

    private AbstractExpr notAnd;

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
        this.notAnd = new Not(new And(new Not(this.getLeftOperand()), new Not(this.getRightOperand())));

    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, Label lb){
        this.notAnd.codeGenBranch(compiler, lb);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, GPRegister rDest){
        this.notAnd.codeGenBool(compiler, rDest);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }


}
