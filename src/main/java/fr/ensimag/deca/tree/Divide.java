package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.QUO;

import fr.ensimag.deca.codegen.ErrorManager;


/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenOp(DecacCompiler compiler, GPRegister rSource, GPRegister rDest){
        if (this.getType().isFloat()){
            compiler.addInstruction(new DIV(rSource,
                                            rDest));
        } else {
            compiler.addInstruction(new QUO(rSource,
                                            rDest));
        }
        ErrorManager.codeGenTestArithError(compiler);
    }

    @Override
    protected String getOperatorName() {
        return "/";
    }

}
