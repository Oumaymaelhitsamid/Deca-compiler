package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;


import fr.ensimag.deca.tools.SymbolTable;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.DAddr;


/**
 * Deca Identifier
 *
 * @author gl44
 * @date 01/01/2022
 */
public class Identifier extends AbstractIdentifier {

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Definition def = localEnv.get(getName());

        if (def == null) {
            throw new ContextualError("[ContextualError 0.1.1] Identifier '" + getName() + "' not defined", getLocation());
        }

        setDefinition(def);
        return getDefinition().getType();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if (getDefinition().isField()){
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            //temp
            compiler.addInstruction(new LOAD(new RegisterOffset(getFieldDefinition().getIndex(), Register.R1), Register.R1));
        } else {
            compiler.addInstruction(new LOAD(getExpDefinition().getOperand(), Register.R1));
        }
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, Label lb){
        this.codeGenInst(compiler);
        compiler.addInstruction(new BEQ(lb));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, GPRegister rDest){
        compiler.addInstruction(new LOAD(getExpDefinition().getOperand(), rDest));
    }


    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        this.codeGenInst(compiler);
        Type type = definition.getType();
        if (type.isInt()){
            compiler.addInstruction(new WINT());
        } else if (type.isFloat()){
            compiler.addInstruction(new WFLOAT());
        } else if (type.isBoolean()){
            int ID = compiler.getUniqueID();
            Label lbElse = new Label("ident."+ID+".else");
            Label lbEnd = new Label("ident."+ID+".end");
            compiler.addInstruction(new BNE(lbElse));
            compiler.addInstruction(new WSTR(new ImmediateString("false")));
            compiler.addInstruction(new BRA(lbEnd));
            compiler.addLabel(lbElse);
            compiler.addInstruction(new WSTR(new ImmediateString("true")));
            compiler.addLabel(lbEnd);
        } else {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }

    @Override
    protected void codeGenPrintX(DecacCompiler compiler){
        if (definition.getType().isFloat()){
            this.codeGenInst(compiler);
            compiler.addInstruction(new WFLOATX());
        } else {
            this.codeGenPrint(compiler);
        }
    }

    @Override
    protected DAddr getLValueOperand(DecacCompiler compiler){
        if (getDefinition().isField()){
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        }
        return this.getExpDefinition().getOperand();
    }
    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError {

        SymbolTable table = compiler.getSymbolTable();
        this.setDefinition(localEnv.get(name));

        Definition def = this.getDefinition();

        if (def == null) {
            throw new ContextualError("[ContextualError 0.2.1] Type indentifier '" + getName() + "' not declared", getLocation());
        }
        if (def.getType().isString()) {
            throw new ContextualError("[ContextualError 0.2.2] Cannot declare string variable, field nor parameter", getLocation());
        }

        return def.getType();
    }


    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

}
