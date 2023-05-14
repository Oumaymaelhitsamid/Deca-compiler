package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.ima.pseudocode.instructions.SGE;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;

import fr.ensimag.deca.DecacCompiler;

/**
 * Operator "x >= y"
 *
 * @author gl44
 * @date 01/01/2022
 */
public class GreaterOrEqual extends AbstractOpIneq {

    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">=";
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, Label lb) {
        this.codeGenInst(compiler);
        compiler.addInstruction(new BLT(lb));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, GPRegister rDest){
        this.codeGenInst(compiler);
        compiler.addInstruction(new SGE(rDest));
    }


}
