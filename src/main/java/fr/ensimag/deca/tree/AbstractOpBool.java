package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Register;

/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    private static int amountOf = 0;

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type leftType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);
        Type rightType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

        if (!(leftType.isBoolean() && rightType.isBoolean())) {
            throw new ContextualError(("[ContextualError 3.33.1] Uncompatible types for " + getOperatorName() + " (got " + leftType + " and " + rightType + ")"), getLocation());
        }

        this.setType(leftType);
        return leftType;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        this.codeGenBool(compiler, Register.R1);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler){
        this.codeGenPrintBool(compiler);
    }



}
