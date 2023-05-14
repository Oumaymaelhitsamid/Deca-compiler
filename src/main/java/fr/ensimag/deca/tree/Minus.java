package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;

import fr.ensimag.deca.codegen.ErrorManager;


/**
 * @author gl44
 * @date 01/01/2022
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenOp(DecacCompiler compiler, GPRegister rSource, GPRegister rDest){
        compiler.addInstruction(new SUB(rSource, rDest));
        if (this.getType().isFloat()){
            ErrorManager.codeGenTestArithError(compiler);
        }
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

}
