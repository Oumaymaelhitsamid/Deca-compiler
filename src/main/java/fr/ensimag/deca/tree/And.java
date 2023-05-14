package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;

import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.ImmediateInteger;



/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    protected void codeGenBranch(DecacCompiler compiler, Label lb){
        this.getLeftOperand().codeGenBranch(compiler, lb);
        this.getRightOperand().codeGenBranch(compiler, lb);
    }

    protected void codeGenBool(DecacCompiler compiler, GPRegister rDest){
        int ID = compiler.getUniqueID();
        Label lbFalse = new Label("and."+ID+".false");
        Label lbEnd = new Label("and."+ID+".end");
        this.getLeftOperand().codeGenBranch(compiler, lbFalse);
        this.getRightOperand().codeGenBool(compiler, rDest);
        compiler.addInstruction(new BRA(lbEnd));
        compiler.addLabel(lbFalse);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), rDest));
        compiler.addLabel(lbEnd);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }


}
