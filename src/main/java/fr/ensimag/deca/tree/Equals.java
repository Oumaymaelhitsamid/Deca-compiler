package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.SEQ;

import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, Label lb) {
        this.codeGenInst(compiler);
        compiler.addInstruction(new BNE(lb));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, GPRegister rDest){
        this.codeGenInst(compiler);
        compiler.addInstruction(new SEQ(rDest));
    }

}
