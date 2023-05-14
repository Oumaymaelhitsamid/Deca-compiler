package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import fr.ensimag.ima.pseudocode.ImmediateString;

import fr.ensimag.deca.codegen.StackTracker;
import fr.ensimag.deca.codegen.RegisterManager;



/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

        if (!(leftType.isBoolean() && rightType.isBoolean())) {
            if ((!(leftType.isInt() || leftType.isFloat()) || !(rightType.isInt() || rightType.isFloat()))) {
                throw new ContextualError(("[ContextualError 3.33.1] Uncompatible types for " + getOperatorName() + " (got " + leftType + " and " + rightType + ")"), getLocation());
            }

            if (!leftType.sameType(rightType)) {
                if (leftType.isInt()) {
                    setLeftOperand(new ConvFloat(getLeftOperand()));
                    getLeftOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);
                } else { // C'est donc le rightType qui est int
                    setRightOperand(new ConvFloat(getRightOperand()));
                    getRightOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);
                }
            }
        }

        SymbolTable symbolTable = compiler.getSymbolTable();
        BooleanType type = new BooleanType(symbolTable.create("boolean"));
        this.setType(type);

        return type;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int[] regs = this.codeGenRecupArgs(compiler);
        compiler.addInstruction(new CMP(Register.getR(regs[0]), Register.getR(regs[1])));
    }

    @Override
    protected int codeGenStock(DecacCompiler compiler){
        int newR = RegisterManager.allocateRegister();
        this.codeGenBool(compiler, Register.R1);
        if (newR < 0){
            compiler.addInstruction(new PUSH(Register.R1));
            compiler.stackTracker.push();
            return -1;
        } else {
            compiler.addInstruction(new LOAD(Register.R1, Register.getR(newR)));
            return newR;
        }

    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler){
        int ID = compiler.getUniqueID();
        int ri = this.codeGenStock(compiler);
        if (ri < 0){
            compiler.addInstruction(new POP(Register.R1));
            compiler.stackTracker.pop();
            ri = 1;
        } else {
            compiler.addInstruction(new LOAD(Register.getR(ri), Register.R1));
        }
        Label lbElse = new Label("cmp."+ID+".else");
        Label lbEnd = new Label("cmp."+ID+".end");
        compiler.addInstruction(new BNE(lbElse));
        compiler.addInstruction(new WSTR(new ImmediateString("false")));
        compiler.addInstruction(new BRA(lbEnd));
        compiler.addLabel(lbElse);
        compiler.addInstruction(new WSTR(new ImmediateString("true")));
        compiler.addLabel(lbEnd);
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, Label lb) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    protected void codeGenBool(DecacCompiler compiler, GPRegister rDest) {
        throw new UnsupportedOperationException("not yet implemented");
    }


}
