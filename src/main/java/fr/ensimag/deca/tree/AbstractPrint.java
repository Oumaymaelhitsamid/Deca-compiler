package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl44
 * @date 01/01/2022
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();

    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType, EnvironmentExp envTypes)
            throws ContextualError {

        // Nothing to check besides the arguments
        for (AbstractExpr a : getArguments().getList()) {
            Type argType = a.verifyExpr(compiler, localEnv, currentClass, envTypes);
            
            if (argType.isClass()) {
                throw new ContextualError("[ContextualError 3.31.1] Cannot print ClassType", getLocation());
            }
            if (argType.isBoolean()) {
                throw new ContextualError("[ContextualError 3.31.2] Cannot print a boolean", getLocation());
            }
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if (getPrintHex()){
            for (AbstractExpr a : getArguments().getList()) {
                a.codeGenPrintX(compiler);
            }
        } else {
            for (AbstractExpr a : getArguments().getList()) {
                a.codeGenPrint(compiler);
            }    
        }
    }

    private boolean getPrintHex() {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
      if(!this.printHex){
        s.print("print"+this.getSuffix()+"(");
      } else {
        s.print("print"+this.getSuffix()+"x(");
      }
        getArguments().decompile(s);
        s.print(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
