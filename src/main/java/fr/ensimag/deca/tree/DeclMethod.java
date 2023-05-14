package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.ERROR;


import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.codegen.ErrorManager;

/**
 * Class declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */

public class DeclMethod extends AbstractDeclMethod{
	private AbstractIdentifier returnType;
	private AbstractIdentifier methodName;
	private ListDeclParam param;
	private AbstractMethodBody methodBody;

    public DeclMethod(AbstractIdentifier returnType, AbstractIdentifier methodName, ListDeclParam param, AbstractMethodBody methodBody){
		Validate.notNull(returnType);
		Validate.notNull(methodName);
		Validate.notNull(param);
		Validate.notNull(methodBody);
		this.returnType = returnType;
		this.methodName = methodName;
		this.param = param;
		this.methodBody = methodBody;
	}

     @Override
   public void decompile(IndentPrintStream s) {
     returnType.decompile(s);
     s.print(" ");
     methodName.decompile(s);
     s.print("(");
     param.decompile(s);
     s.print(")");
     if(methodBody instanceof MethodAsmBody){
       s.print(" asm (");
       methodBody.decompile(s);
       s.print(");");
     } else {
       s.println("{");
       methodBody.decompile(s);
       s.print("}");
     }
   }

   @Override
   protected void iterChildren(TreeFunction f) {
       returnType.iter(f);
       methodName.iter(f);
       param.iter(f);
       methodBody.iter(f);
   }

	 @Override
	 void codeGenDeclMethod(DecacCompiler compiler, int offset){

		 compiler.addInstruction(new LOAD(new LabelOperand(methodName.getMethodDefinition().getLabel()), Register.R0));
		 compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(methodName.getMethodDefinition().getIndex()-offset, Register.SP)));
		 param.initParams();
	 }

   @Override
   protected void prettyPrintChildren(PrintStream s, String prefix) {
      returnType.prettyPrint(s, prefix, false);
      methodName.prettyPrint(s, prefix, false);
      param.prettyPrint(s, prefix, false);
      methodBody.prettyPrint(s, prefix, false);
   }

   @Override
   protected void verifyDeclMethod(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
           throws ContextualError{

        // Récupérer le type associé à l'identifier "type"
        Type returnT = returnType.verifyType(compiler, envTypes);

        // Récupérer l'environnement du super
        EnvironmentExp superEnv = currentClass.getSuperClass().getEnvironment();

        if (superEnv.get(methodName.getName()) != null && !(superEnv.get(methodName.getName()).isMethod())) {
          throw new ContextualError("[ContextualError 2.7.1] Method cannot be defined as not method in super class environment", getLocation());
        }

        Signature sign = param.getSign(compiler, envTypes);
        int index;

        if (superEnv.get(methodName.getName()) != null) {

          // La méthode est redéfinie après héritage
          MethodDefinition methodHerite = (MethodDefinition) superEnv.get(methodName.getName());
          if (!sign.isSame(methodHerite.getSignature())) {
            throw new ContextualError("[ContextualError 2.7.2] Uncompatible redefined method signature", getLocation());
          }
          if (!methodHerite.getType().sameType(returnT) && !(methodHerite.getType().isClass() && returnT.isClass() && ((ClassType) returnT).isSubClassOf((ClassType) methodHerite.getType()))) {
            throw new ContextualError("[ContextualError 2.7.3] Uncompatible redefined method output type", getLocation());
          }
          index = ((MethodDefinition) superEnv.get(methodName.getName())).getIndex();
        } else {
          index = currentClass.incNumberOfMethods();
        }


        // Ajouter la déclaration de methode dans l'environement de la classe
        try {
            MethodDefinition defMeth = new MethodDefinition(returnT, getLocation(), sign, index);
            methodName.setDefinition(defMeth);
            currentClass.getEnvironment().declare(methodName.getName(), methodName.getDefinition());
            methodName.getDefinition().setLabel(new Label(currentClass.getLabel().getName() + "." + methodName.getName().toString() + "." + compiler.getUniqueID()));
        } catch (DoubleDefException e) {
            throw new ContextualError("[ContextualError 2.6.1] Method already defined", getLocation());
        }
    }

    @Override
    void codeGenMethod(DecacCompiler compiler){
        if (!methodName.getMethodDefinition().isWritten()){
			compiler.addComment("");
            compiler.addComment("Methode de "+methodName.getName()+" :");
			compiler.addComment("");
            compiler.addLabel(methodName.getMethodDefinition().getLabel());

			compiler.addComment("");
            compiler.addComment("Sauvegarde de registre");
			compiler.addComment("");
            RegisterManager.saveRegisters(compiler);

			compiler.addComment("");
            compiler.addComment("Corps de la methode");
			compiler.addComment("");
            methodBody.codeGenBody(compiler, methodName.getMethodDefinition());
            if (returnType.getName().getName() == "void"){
                ErrorManager.codeGenTestVoidError(compiler);
            }
            Label lbFin = new Label("fin."+methodName.getMethodDefinition().getLabel().getName());
            compiler.addLabel(lbFin);

			compiler.addComment("");
            compiler.addComment("Restauration de registre");
			compiler.addComment("");
            RegisterManager.restoreRegisters(compiler);

			compiler.addComment("");
			compiler.addComment("Procedure de fin");
			compiler.addComment("");
            compiler.addInstruction(new RTS());

			compiler.addComment("");
			compiler.addComment("Fin de la methode");
			compiler.addComment("");
            methodName.getMethodDefinition().write();
        }
    }

	protected void verifyMethodBody(DecacCompiler compiler,
           EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
           throws ContextualError {

		// Récupérer le type associé à l'identifier "type"
    Type returnT = returnType.verifyType(compiler, envTypes);

		// Construire l'environnement local de la méthode (avec juste les paramètres au début)
		EnvironmentExp envMethod = new EnvironmentExp(((ClassDefinition) envTypes.get(currentClass.getType().getName())).getEnvironment());
    param.verifyListDeclVariable(compiler, envMethod, currentClass, envTypes);

		// Ajouter dans cet environnement les variables dans methodBody.varList
		methodBody.verifyListDeclVariable(compiler, envMethod, currentClass, envTypes);

		// Verifier methodBody.instList avec cet environnement, le type de retour...
		methodBody.verifyListInst(compiler, envMethod, currentClass, envTypes, returnT);

	}

}
