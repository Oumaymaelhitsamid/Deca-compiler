package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

/**
 * Class declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */

 public class DeclParam extends AbstractDeclParam {
   private AbstractIdentifier typeName;
   private AbstractIdentifier varName;


   public DeclParam(AbstractIdentifier typeName,AbstractIdentifier varName){
     Validate.notNull(varName);
     Validate.notNull(typeName);
     this.varName = varName;
     this.typeName = typeName;
   }

     @Override
   public void decompile(IndentPrintStream s) {
     typeName.decompile(s);
     s.print(" ");
     varName.decompile(s);
   }

   @Override
   public void setOperand(int i){
       varName.getExpDefinition().setOperand(new RegisterOffset(i, Register.LB));
   }

   @Override
   protected void iterChildren(TreeFunction f) {
     typeName.iter(f);
     varName.iter(f);
   }

   @Override
   protected void prettyPrintChildren(PrintStream s, String prefix) {
     typeName.prettyPrint(s,prefix,false);
     varName.prettyPrint(s,prefix,true);
   }

   	public Type getType(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError {
		return typeName.verifyType(compiler, localEnv);
	}

   @Override
   protected void verifyDeclParam(DecacCompiler compiler,
           EnvironmentExp envMethod, ClassDefinition currentClass, EnvironmentExp envTypes)
           throws ContextualError{

        // Récupérer le type associé à l'identifier "typeName"
        Type paramType = typeName.verifyType(compiler, envTypes);

        // Ajouter la déclaration de variable dans "envMethod"
        try {
            varName.setDefinition(new ParamDefinition(paramType, getLocation()));
            envMethod.declare(varName.getName(), varName.getDefinition());
        } catch (DoubleDefException e) {
            throw new ContextualError("[ContextualError 3.12.1] Parameter '" + varName.getName() + "' already declared", getLocation());
        }
    }


}
