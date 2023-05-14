package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;

/**
 * List of declarations (e.g. int x; float y,z).
 *
 * @author gl44
 * @date 01/01/2022
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {


    void initParams(){
        int i = -3;
        for (AbstractDeclParam dp : getList()){
            dp.setOperand(i--);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
      int length = size();
      int iter = 0;
      for (AbstractDeclParam i : getList()) {
          iter++;
          i.decompile(s);
          if(iter == length) break;
          s.print(",");
      }
    }

    Signature getSign(DecacCompiler compiler, EnvironmentExp envTypes) throws ContextualError {
        Signature tmp = new Signature();

        for (AbstractDeclParam d : getList()) {
            // Check every variable decalration in the list
            Type tmp2 = d.getType(compiler, envTypes);
            if (tmp2.isVoid()) {
                throw new ContextualError("[ContextualError 2.9.1 Parameter cannot be of type void", getLocation());
            }
            tmp.add(tmp2);
        }

        return tmp;
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
            ClassDefinition currentClass, EnvironmentExp envMethod) throws ContextualError {

        for (AbstractDeclParam d : getList()) {
            // Check every variable decalration in the list
            d.verifyDeclParam(compiler, localEnv, currentClass, envMethod);
        }
    }




}
