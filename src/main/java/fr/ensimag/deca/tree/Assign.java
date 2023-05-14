package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassType;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.DAddr;

import fr.ensimag.deca.codegen.StackTracker;
import fr.ensimag.deca.codegen.RegisterManager;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl44
 * @date 01/01/2022
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

        if (!(leftType.sameType(rightType))) {
            if (!(leftType.isFloat() && rightType.isInt())) {
                if ((rightType.isClassOrNull() && leftType.isClass()) && (rightType.isNull() || ((ClassType) rightType).isSubClassOf((ClassType) leftType))) {
                    setType(leftType);
                    return (leftType);
                } else {
                    throw new ContextualError("[ContextualError 3.32.1] Incompatible types for assignation (got " + leftType + " and " + rightType + ")", getLocation());
                }
            } else {

                // Ici on assogne un entier dans un flottant
                setRightOperand(new ConvFloat(getRightOperand()));
                getRightOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

                this.setType(rightType);
                return rightType;
            }
        } else { // Les types sont les mêmes, rien à faire
            setType(leftType);
            return leftType;
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        compiler.addComment("Assign instruction");
        int ri = this.getRightOperand().codeGenStock(compiler);
        DAddr dest = getLeftOperand().getLValueOperand(compiler);
        if (ri < 0){
            compiler.addInstruction(new POP(Register.R1));
            compiler.stackTracker.pop();
            ri = 1;
        }
        compiler.addInstruction(new STORE(Register.getR(ri), dest));
        compiler.addComment("End of assign instruction");
        RegisterManager.freeRegister(ri);
    }


    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        this.getRightOperand().codeGenPrint(compiler);
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        this.getRightOperand().codeGenPrintX(compiler);
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

}
