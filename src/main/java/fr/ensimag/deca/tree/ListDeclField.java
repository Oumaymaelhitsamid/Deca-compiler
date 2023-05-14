package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of declarations (e.g. int x; float y,z).
 *
 * @author gl44
 * @date 01/01/2022
 */
public class ListDeclField extends TreeList<AbstractDeclField> {

    @Override
    public void decompile(IndentPrintStream s) {
      for (AbstractDeclField i : getList()) {
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

        for (AbstractDeclField d : getList()) {
            // Check every field decalration in the list
            d.verifyDeclField(compiler, localEnv, currentClass, envTypes);
        }
    }

    void codeGenInitAttributList(DecacCompiler compiler){
        for (AbstractDeclField d : getList()){
            d.codeGenInitAttribut(compiler);
        }
      }


    void verifyListInitVariable(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        for (AbstractDeclField d : getList()) {
            // Check every field initialization in the list
            d.verifyInitField(compiler, localEnv, currentClass, envTypes);
        }

    }


}
