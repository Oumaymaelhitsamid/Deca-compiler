package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.POP;

/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type exprType = this.getOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

        if (!exprType.isBoolean()) {
            throw new ContextualError("[ContextualError 3.33.1] Uncompatible type for not operator (got " + exprType + ")", getLocation());
        }

        this.setType(exprType);
        return exprType;
        // throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, Label lb){
        int ID = compiler.getUniqueID();
        Label noLb = new Label(lb.getName()+".not."+ID);
        this.getOperand().codeGenBranch(compiler, noLb);
        compiler.addInstruction(new BRA(lb));
        compiler.addLabel(noLb);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        this.codeGenBool(compiler, Register.R1);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, GPRegister rDest){
        this.getOperand().codeGenBool(compiler, rDest);
        compiler.addInstruction(new ADD(new ImmediateInteger(1), rDest));
        compiler.addInstruction(new REM(new ImmediateInteger(2), rDest));
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler){
        this.codeGenPrintBool(compiler);

    }


    @Override
    protected String getOperatorName() {
        return "!";
    }
}
