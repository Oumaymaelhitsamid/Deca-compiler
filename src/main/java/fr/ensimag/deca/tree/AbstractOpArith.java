package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;

import fr.ensimag.deca.codegen.RegisterManager;


/**
 * Arithmetic binary operations (+, -, /, ...)
 *
 * @author gl44
 * @date 01/01/2022
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type leftType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);
        Type rightType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

        // Il faut que les deux cotés du signe soient soit int soit float
        if (!(leftType.isInt() || leftType.isFloat()) || !(rightType.isInt() || rightType.isFloat())) {
            throw new ContextualError(("[ContextualError 3.33.1] Uncompatible types for " + getOperatorName() + " (got " + leftType + " and " + rightType + ")"), getLocation());
        }

        // Si ils ont également le même type, rien à faire de spécial
        if (leftType.sameType(rightType)) {
            this.setType(leftType);
            return leftType;
        }

        // Si les types sont différents, on a un int et un float, donc un ConvFloat à ajouter sur le int
        if (leftType.isInt()) {
            ConvFloat tmp = new ConvFloat(getLeftOperand());
            tmp.setType(rightType);
            setLeftOperand(tmp);

            this.setType(rightType);
            return rightType;
        } else { // C'est donc le rightType qui est int
            ConvFloat tmp = new ConvFloat(getRightOperand());
            tmp.setType(leftType);
            setRightOperand(tmp);

            this.setType(leftType);
            return leftType;
        }

    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        int[] regs = this.codeGenRecupArgs(compiler);
        this.codeGenOp(compiler, Register.getR(regs[0]), Register.getR(regs[1]));
        RegisterManager.freeRegister(regs[0]);
        compiler.addInstruction(new LOAD(Register.getR(regs[1]), Register.R1));
        RegisterManager.freeRegister(regs[1]);
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
    protected void codeGenPrintX(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        compiler.addInstruction(new WFLOATX());
    }

    protected void codeGenOp(DecacCompiler compiler, GPRegister rSource, GPRegister rDest){
        throw new UnsupportedOperationException("not yet implemented");
    }



}
