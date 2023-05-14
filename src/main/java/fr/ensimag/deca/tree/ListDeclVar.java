package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.codegen.StackTracker;

import fr.ensimag.ima.pseudocode.instructions.ADDSP;

/**
 * List of declarations (e.g. int x; float y,z).
 *
 * @author gl44
 * @date 01/01/2022
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
      for (AbstractDeclVar i : getList()) {
          i.decompile(s);
          s.println();
      }
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to
     *      the "env_exp_r" attribute
     * @param currentClass
     *          corresponds to "class" attribute (null in the main bloc).
     */
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {
        for (AbstractDeclVar d : getList()) {
            // Check every variable decalration in the list
            d.verifyDeclVar(compiler, localEnv, currentClass, envTypes);
        }
    }

    protected void codeGenListDeclVar(DecacCompiler compiler) {
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addComment("Declarations de variables");
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addInstruction(new ADDSP(size())); //On track le stack juste après
        int i = 0;
        for (AbstractDeclVar dv: getList()){
            dv.codeGenDeclVar(compiler, ++i);
        }
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addComment("Fin des declarations de variables");
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    protected void codeGenListGlobalDeclVar(DecacCompiler compiler){
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addComment("Declarations de variables globales");
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addInstruction(new ADDSP(size())); //On track le stack juste après
        int i = -1 * size();
        for (AbstractDeclVar dv: getList()){
            int val = compiler.stackTracker.push();
            dv.codeGenDeclVar(compiler, val);
        }
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addComment("Fin des declarations de variables globales");
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }


}
