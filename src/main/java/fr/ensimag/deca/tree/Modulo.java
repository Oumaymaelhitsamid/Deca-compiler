package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.REM;

import fr.ensimag.deca.codegen.ErrorManager;


/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type leftType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);
        Type rightType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

        if ((!leftType.isInt()) || (!rightType.isInt())) {
            throw new ContextualError(("[ContextualError 3.33.1] Uncompatible types for " + getOperatorName() + "(got " + leftType + " and " + rightType + ")"), getLocation());
        }

        this.setType(leftType);
        return leftType;
    }

    @Override
    protected void codeGenOp(DecacCompiler compiler, GPRegister rSource, GPRegister rDest){
        compiler.addInstruction(new REM(rSource, rDest));
        ErrorManager.codeGenTestArithError(compiler);
    }

    @Override
    protected String getOperatorName() {
        return "%";
    }

}
