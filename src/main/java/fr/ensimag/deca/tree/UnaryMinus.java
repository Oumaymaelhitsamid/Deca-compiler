package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.OPP;

import fr.ensimag.deca.codegen.RegisterManager;

/**
 * @author gl44
 * @date 01/01/2022
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type exprType = this.getOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

        if ((!exprType.isInt()) && (!exprType.isFloat())) {
            throw new ContextualError("[ContextualError 3.33.1] Uncompatible type for unary minus (got " + exprType + ")", getLocation());
        }

        this.setType(exprType);
        return exprType;
        // throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        int ri = this.codeGenRecupArg(compiler);
        compiler.addInstruction(new OPP(Register.getR(ri), Register.R1));
        RegisterManager.freeRegister(ri);
    }


    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        if (this.getType().isFloat()){
            compiler.addInstruction(new WFLOAT());
        } else if (this.getType().isInt()) {
            compiler.addInstruction(new WINT());
        }
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler){
        this.codeGenInst(compiler);
        compiler.addInstruction(new WFLOATX());
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

}
