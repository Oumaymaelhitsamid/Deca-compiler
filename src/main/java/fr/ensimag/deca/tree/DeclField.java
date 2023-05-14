package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

/**
 * Class declaration.
 *
 * @author gl44
 * @date 13/01/2022
 */

public class DeclField extends AbstractDeclField{
    private AbstractIdentifier varName;
    private AbstractIdentifier typeName;
    private AbstractInitialization init;
    private Visibility visibility;

    public Visibility getVisibility(){
      return visibility;
    }


    public DeclField(AbstractIdentifier varName,AbstractIdentifier typeName,AbstractInitialization init,Visibility visibility){
		Validate.notNull(varName);
		Validate.notNull(typeName);
		Validate.notNull(init);
		Validate.notNull(visibility);
		this.varName = varName;
		this.typeName = typeName;
		this.init = init;
		this.visibility = visibility;
    }

    public boolean initialized(){
      return this.init instanceof Initialization;
    }

	@Override
    public void decompile(IndentPrintStream s) {
        if(visibility == Visibility.PROTECTED) s.print("protected");
        s.print(" ");
        typeName.decompile(s);
        s.print(" ");
        varName.decompile(s);
        if(initialized()) s.print(" = ");
        init.decompile(s);
        s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
      typeName.prettyPrint(s, prefix, false);
      varName.prettyPrint(s,prefix,false);
      init.prettyPrint(s,prefix,true);
    }

    @Override
    protected void verifyDeclField(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes)
            throws ContextualError{

		    // Récupérer le type associé à l'identifier "type"
        Type varType = typeName.verifyType(compiler, localEnv);

        if (varType.isVoid()) {
            throw new ContextualError("[ContextualError 2.5.1] Field '" + varName.getName() + "' cannot be of type void", getLocation());
        }

        // Récupérer l'environnement du super
        EnvironmentExp superEnv = currentClass.getSuperClass().getEnvironment();
        if (superEnv.get(varName.getName()) != null && !(superEnv.get(varName.getName()).isField())) {
          throw new ContextualError("[ContextualError 2.5.2] Field '" + varName.getName() + "' cannot be defined as not field in super class environment", getLocation());
        }

		    // Ajouter la déclaration de variable dans l'environement de la classe
        try {
            varName.setDefinition(new FieldDefinition(varType, getLocation(), visibility, currentClass, currentClass.incNumberOfFields()));
            currentClass.getEnvironment().declare(varName.getName(), varName.getDefinition());
        } catch (DoubleDefException e) {
            throw new ContextualError("[ContextualError 2.4.1] Field '" + varName.getName() + "' already defined", getLocation());
        }
    }

    protected void verifyInitField(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, EnvironmentExp envTypes) throws ContextualError {

            // Récupérer le type associé à l'identifier "type"
            Type varType = typeName.verifyType(compiler, envTypes);
              init.verifyInitialization(compiler, varType, localEnv, currentClass, envTypes);

            }


   protected void codeGenInitAttribut(DecacCompiler compiler){
       varName.getExpDefinition().setOperand(new RegisterOffset(varName.getFieldDefinition().getIndex(), Register.R0));
       init.codeGenClassInitialization(compiler, new RegisterOffset(varName.getFieldDefinition().getIndex(), Register.getR(2)));
   }
 }
