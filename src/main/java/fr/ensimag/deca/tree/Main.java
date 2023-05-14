package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.context.TypeDefinition;

import fr.ensimag.deca.context.EnvironmentType;


/**
 * @author gl44
 * @date 01/01/2022
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);

    private ListDeclVar declVariables;
    private ListInst insts;

    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler, EnvironmentType envTypes) throws ContextualError {
        LOG.debug("verify Main: start");

        // A FAIRE: Appeler méthodes "verify*" de ListDeclVar et ListInst.
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).

        SymbolTable table = compiler.getSymbolTable();

        // Creates localEnv for main variables
        EnvironmentExp localEnv = new EnvironmentExp(null);

        // Verify ListDeclVar (not necessary for hello_world)
        this.declVariables.verifyListDeclVariable(compiler, localEnv, null, envTypes);

        // Verify ListInst
        this.insts.verifyListInst(compiler, localEnv, null, envTypes.get(table.create("void")).getType(), envTypes);

        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        declVariables.codeGenListGlobalDeclVar(compiler);
        compiler.addComment("Beginning of main instructions:");
        insts.codeGenListInst(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
