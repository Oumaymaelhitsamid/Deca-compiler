package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.SNE;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;

import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class NotEquals extends AbstractOpExactCmp {

    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "!=";
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, Label lb) {
        this.codeGenInst(compiler);
        compiler.addInstruction(new BEQ(lb));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, GPRegister rDest){
        this.codeGenInst(compiler);
        compiler.addInstruction(new SNE(rDest));
    }

}
