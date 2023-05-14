package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;


/**
 * @author gl44
 * @date 01/01/2022
 */
public class DeclVar extends AbstractDeclVar {


    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    /*
    * Detect if there is an init
    * @return boolean
    */
    public boolean initialized(){
      return this.initialization instanceof Initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
            throws ContextualError {

        // Récupérer le type associé à l'identifier "type"
        Type varType = type.verifyType(compiler, envTypes);

        if (varType.isVoid()) {
            throw new ContextualError("[ContextualError 3.17.1] Variable '" + varName.getName() + "' cannot be of type void", getLocation());
        }

        // Vérifier que l'initialisation est valide
        initialization.verifyInitialization(compiler, varType, localEnv, currentClass, envTypes);


        // Ajouter la déclaration de variable dans "localEnv"
        try {
            varName.setDefinition(new VariableDefinition(varType, getLocation()));
            localEnv.declare(varName.getName(), varName.getDefinition());
        } catch (DoubleDefException e) {
            throw new ContextualError("[ContextualError 3.17.2] Variable '" + varName.getName() + "' already declared", getLocation());
        }
    }

    /**
     * Code generation for variable declaration
     *
     * @param compiler
     * @param i i-eme variable declaration
     */
    @Override
    protected void codeGenDeclVar(DecacCompiler compiler, int i) {
        compiler.addComment("Declaration de la variable "+varName.getName());
        varName.getVariableDefinition().setOperand(new RegisterOffset(i, Register.LB));
        initialization.codeGenInitialization(compiler, varName.getVariableDefinition().getOperand());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        if(initialized()) s.print(" = ");
        initialization.decompile(s);
        s.print(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
