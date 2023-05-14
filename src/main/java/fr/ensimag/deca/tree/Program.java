package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.codegen.RegisterManager;

import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.ClassDefinition;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl44
 * @date 01/01/2022
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);

    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }

    public ListDeclClass getClasses() {
        return classes;
    }

    public AbstractMain getMain() {
        return main;
    }

    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");

        // Creates types environment
        EnvironmentType envTypes = new EnvironmentType(compiler);

        // Verify classes (passes 1, 2 & 3 pour les classes)
        classes.verifyListClass(compiler, envTypes);           // passe 1
        classes.verifyListClassMembers(compiler, envTypes);    // passe 2
        classes.verifyListClassBody(compiler, envTypes);       // passe 3

        // Verify main (passe 3 pour le main)
        main.verifyMain(compiler, envTypes);

        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        RegisterManager.setNbRegisters(compiler.getCompilerOptions().getRegistersAmount());
        RegisterManager.init();
        compiler.addComment("Construction of the method Table");
        classes.codeGenListDeclClass(compiler);
        // A FAIRE: compléter ce squelette très rudimentaire de code
        compiler.addComment("Main program");
        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());
        ErrorManager.codeGenError(compiler);
        classes.codeGenListInitClass(compiler);
        classes.codeGenListMethodes(compiler);

    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
