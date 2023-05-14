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
public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    @Override
    public void decompile(IndentPrintStream s) {
      for (AbstractDeclMethod i : getList()) {
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

        for (AbstractDeclMethod d : getList()) {
            // Check every variable decalaration in the list
            d.verifyDeclMethod(compiler, localEnv, currentClass, envTypes);
        }
    }

    void verifyListMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

        for (AbstractDeclMethod d : getList()) {
            // Check every method body in the list
            d.verifyMethodBody(compiler, localEnv, currentClass, envTypes);
        }
    }


}
