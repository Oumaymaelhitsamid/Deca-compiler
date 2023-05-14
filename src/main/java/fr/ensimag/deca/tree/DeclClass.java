package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.EnvironmentType;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.deca.context.Definition;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

import fr.ensimag.deca.codegen.StackTracker;
import fr.ensimag.deca.codegen.RegisterManager;


/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 *
 * @author gl44
 * @date 01/01/2022
 */
public class DeclClass extends AbstractDeclClass {

    private AbstractIdentifier nameClass;
    private AbstractIdentifier type; //Obj souvent
    private ListDeclField attributList;
    private ListDeclMethod methodList;


    public DeclClass(AbstractIdentifier nameClass,AbstractIdentifier type,ListDeclField attributList,ListDeclMethod methodList){
      Validate.notNull(nameClass);
      Validate.notNull(type);
      Validate.notNull(attributList);
      Validate.notNull(methodList);
      this.nameClass = nameClass;
      this.type = type;
      this.attributList = attributList;
      this.methodList = methodList;
    }

    /*
    * If the class extends Object
    * @return boolean
    */
    public boolean extendsObject(){
      return type.getName().toString() == "Object";
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        nameClass.decompile(s);
        if(!extendsObject()){
        s.print(" extends ");
        type.decompile(s);
        }
        s.println("{");
        attributList.decompile(s);
        methodList.decompile(s);
        s.print("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler, EnvironmentType envTypes) throws ContextualError {

        Symbol object = nameClass.getName();

        Definition superDef = envTypes.get(type.getName());

        if (superDef == null) {
            throw new ContextualError("[ContextualError 1.3.1] Super class identifier '" + type.getName() + "' unknown", getLocation());
        }

        // Lien entre la définition du superType et son identifiant lors du premier passage
        if ((type.getDefinition() == null)) {
            type.setDefinition(superDef);
        }

        if (!superDef.isClass()) {
            throw new ContextualError("[ContextualError 1.3.2] Identifier '" + type.getName() + "' must be class identifier", getLocation());
        }

        // Needed verifications for this to work are made before
        ClassDefinition superClassDef = type.getClassDefinition();
        ClassDefinition objectDef = new ClassDefinition(new ClassType(object, getLocation(), type.getClassDefinition()), getLocation(), superClassDef);

        try {
            nameClass.setDefinition(objectDef);
            envTypes.declare(nameClass.getName(), nameClass.getDefinition());
            nameClass.getDefinition().setLabel(new Label(nameClass.getName().toString() + "." + compiler.getUniqueID()));
        } catch (DoubleDefException e) {
            throw new ContextualError("[ContextualError 1.3.3] Type or class '" + type.getName() +"' already defined", getLocation());
        }
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler, EnvironmentType envTypes)
            throws ContextualError {

        ClassDefinition superClassDef = type.getClassDefinition();
        ClassDefinition objectDef = nameClass.getClassDefinition();

        objectDef.setNumberOfFields(superClassDef.getNumberOfFields());
        objectDef.setNumberOfMethods(superClassDef.getNumberOfMethods());

        attributList.verifyListDeclVariable(compiler, envTypes, nameClass.getClassDefinition(), envTypes);
        methodList.verifyListDeclVariable(compiler, envTypes, nameClass.getClassDefinition(), envTypes);
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler, EnvironmentType envTypes) throws ContextualError {

        attributList.verifyListInitVariable(compiler, envTypes, nameClass.getClassDefinition(), envTypes);
        methodList.verifyListMethodBody(compiler, envTypes, nameClass.getClassDefinition(), envTypes);

        // throw new UnsupportedOperationException("not yet implemented");
    }

    void codeGenDeclClass(DecacCompiler compiler){
        compiler.addComment("Declaration de la table de "+nameClass.getName()+" :");
        compiler.addInstruction(new ADDSP(new ImmediateInteger(nameClass.getClassDefinition().getNumberOfMethods()+1)));
        // On regarde le parent de l'objet, et on LOAD en conséquence
        if (extendsObject()){
            compiler.addInstruction(new LEA(new RegisterOffset(1, Register.GB), Register.R0)); // 1(GB) contient forcément Object
        } else {
            compiler.addInstruction(new LEA(nameClass.getClassDefinition().getSuperClass().getOperand(), Register.R0));
        }
        DAddr op = new RegisterOffset(compiler.stackTracker.push(), Register.GB);
        nameClass.getClassDefinition().setOperand(op);
        compiler.addInstruction(new STORE(Register.R0, op));
        for (int i = 0; i < nameClass.getClassDefinition().getNumberOfMethods(); i++){compiler.stackTracker.push();} // On track la pile.

        // Code de declaration des méthodes héritées
        for (int i = 0; i < nameClass.getClassDefinition().getSuperClass().getNumberOfMethods(); i++){
            compiler.addInstruction(new LOAD(new RegisterOffset(i+1, Register.R0), Register.R1));
            compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(i+1 - nameClass.getClassDefinition().getNumberOfMethods(), Register.SP)));
        }
        for (AbstractDeclMethod dm : methodList.getList()){
            dm.codeGenDeclMethod(compiler, nameClass.getClassDefinition().getNumberOfMethods()); // Cette liste ne contient pas les méthodes héritées, mais contient les méthodes redéfinies
        }

    }

    void codeGenInitClass(DecacCompiler compiler){
        compiler.addComment("Initialisation de "+nameClass.getName()+" :");
        compiler.addLabel(new Label("init."+nameClass.getClassDefinition().getLabel()));
        RegisterManager.saveRegisters(compiler);

        if (!extendsObject()){
            compiler.addComment("Appel du constructeur parent");
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(2)));
            compiler.addInstruction(new PUSH(Register.getR(2)));
            compiler.stackTracker.push();
            compiler.addInstruction(new BSR(new Label("init."+nameClass.getClassDefinition().getSuperClass().getLabel())));
        }

        compiler.addInstruction(new LEA(nameClass.getClassDefinition().getOperand(), Register.R1));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(2)));
        compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(0, Register.getR(2))));
        attributList.codeGenInitAttributList(compiler);
        RegisterManager.restoreRegisters(compiler);
        compiler.addInstruction(new RTS());
    }

    void codeGenMethodes(DecacCompiler compiler){
        for (AbstractDeclMethod dm : methodList.getList()){
            dm.codeGenMethod(compiler);
        }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
       nameClass.prettyPrint(s, prefix, false);
       type.prettyPrint(s, prefix, false);
       attributList.prettyPrint(s, prefix, false);
       methodList.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        nameClass.iter(f);
        if(!extendsObject()){
        type.iter(f);
        }
        attributList.iter(f);
        methodList.iter(f);
    }

}
