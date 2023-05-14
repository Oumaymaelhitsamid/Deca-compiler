package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.context.FloatType;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;

import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.codegen.RegisterManager;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 *
 * @author gl44
 * @date 01/01/2022
 */
public class ConvFloat extends AbstractUnaryExpr {

    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type exprType = this.getOperand().verifyExpr(compiler, localEnv, currentClass, envTypes);

        if ((!exprType.isInt()) && (!exprType.isFloat())) {
            throw new ContextualError("[ContextualError 3.33.2] Uncompatible type for float conversion (expected int or float got " + exprType +")", getLocation());
        }

        Type type = envTypes.get(compiler.getSymbolTable().create("float")).getType();
        setType(type);
        return type;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        int ri = this.codeGenRecupArg(compiler);
        compiler.addInstruction(new FLOAT(Register.getR(ri),
                                        Register.R1));
        RegisterManager.freeRegister(ri);
        ErrorManager.codeGenTestArithError(compiler);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        compiler.addInstruction(new WFLOAT());
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        compiler.addInstruction(new WFLOATX());
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

}
