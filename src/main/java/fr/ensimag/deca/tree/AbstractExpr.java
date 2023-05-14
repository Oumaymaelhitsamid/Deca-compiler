package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.context.ClassType;

import fr.ensimag.deca.codegen.StackTracker;
import fr.ensimag.deca.codegen.RegisterManager;

import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl44
 * @date 01/01/2022
 */
public abstract class AbstractExpr extends AbstractInst {

    /**
    * Variable usefull for this decompilation
    */
    protected boolean thisCase;

    protected boolean getThisCase(){
      return thisCase;
    }

    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     *
     * implements non-terminals "expr" and "lvalue"
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments
     *
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass,
            Type expectedType)
            throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType, EnvironmentExp envTypes)
            throws ContextualError {

        if (this instanceof Return) {
            Return ret = (Return) this;

            if (returnType.isVoid()) {
                throw new ContextualError("[ContextualError 3.24.1] Cannot have a return in a void type function", getLocation());
            }

            Type typeReturn = verifyExpr(compiler, localEnv, currentClass, envTypes);

            if (!(returnType.sameType(typeReturn))) {
                if (!(returnType.isFloat() && typeReturn.isInt())) {
                    if ((typeReturn.isClassOrNull() && returnType.isClass()) && (typeReturn.isNull() || ((ClassType) typeReturn).isSubClassOf((ClassType) returnType))) {

                    } else {
                        throw new ContextualError("[ContextualError 3.28.1] Incompatible return type (expected " + returnType + " got " + typeReturn + ")", getLocation());
                    }
                } else {

                    // Ici on cast l'expression retournée dans un convFloat
                    ret.setExpr(new ConvFloat(ret.getExpr()));
                }
            }
        }

        verifyExpr(compiler, localEnv, currentClass, envTypes);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        Type typeCond = verifyExpr(compiler, localEnv, currentClass, envTypes);

        if (!typeCond.isBoolean()) {
            throw new ContextualError("[ContextualError 3.29.1] Condition type must be boolean (got " + getType() +")", getLocation());
        }
    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    protected void codeGenPrintX(DecacCompiler compiler){
        this.codeGenPrint(compiler);
    }

    protected void codeGenPrintBool(DecacCompiler compiler){
        int ID = compiler.getUniqueID();
        Label lbEnd = new Label("boolprint."+ID+".end");
        Label lbFalse = new Label("boolprint."+ID+".false");
        this.codeGenBool(compiler, Register.R1);
        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.R1)); // Cette instruction est très souvent superflue, c'est une ligne de défense.
        compiler.addInstruction(new BEQ(lbFalse));
        compiler.addInstruction(new WSTR("true"));
        compiler.addInstruction(new BRA(lbEnd));
        compiler.addLabel(lbFalse);
        compiler.addInstruction(new WSTR("false"));
        compiler.addLabel(lbEnd);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Generate the code to stock the value in the pile
     *
     * @param compiler
     *
     * @return the indice of the register. Negative mean in the stack.
     */
     protected int codeGenStock(DecacCompiler compiler){
         this.codeGenInst(compiler);
         int newR = RegisterManager.allocateRegister();
         if (newR < 0){
             compiler.addInstruction(new PUSH(Register.R1));
             compiler.stackTracker.push();
             return -1;
         } else {
             compiler.addInstruction(new LOAD(Register.R1, Register.getR(newR)));
             return newR;
         }
     }

     protected void codeGenBranch(DecacCompiler compiler, Label lb) {
         throw new UnsupportedOperationException("should not be implemented");
     }

     protected void codeGenBool(DecacCompiler compiler, GPRegister rDest) {
         throw new UnsupportedOperationException("should not be implemented");
     }


    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}
