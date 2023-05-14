package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * @author gl44
 * @date 01/01/2022
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
            throws ContextualError {

        Type exprType = expression.verifyExpr(compiler, localEnv, currentClass, envTypes);

        if (!exprType.sameType(t)) {
            if (!(t.isFloat() && exprType.isInt())) {
                if ((exprType.isClassOrNull() && t.isClass()) && (exprType.isNull() || ((ClassType) exprType).isSubClassOf((ClassType) t))) {

                } else {
                    throw new ContextualError("[ContextualError 3.28.2] Incompatible initialization types (got " + t + " and " + exprType +")", getLocation());
                }
            } else {
                expression = new ConvFloat(expression);
                expression.setType(t);
            }
        }
    }

    @Override
    protected void codeGenInitialization(DecacCompiler compiler, DAddr dest){
        expression.codeGenInst(compiler);
        compiler.addInstruction(new STORE(Register.R1, dest));
    }

    protected void codeGenClassInitialization(DecacCompiler compiler, DAddr dest){
        expression.codeGenInst(compiler);
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(2)));
        compiler.addInstruction(new STORE(Register.R1, dest));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        expression.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
}
