package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;


import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.NullOperand;

import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;


import fr.ensimag.deca.codegen.StackTracker;
import fr.ensimag.deca.codegen.RegisterManager;


/**
 *
 * @author gl44
 * @date 01/01/2022
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler, EnvironmentType env_types) throws ContextualError {
        LOG.debug("verify listClass: start");

        for (AbstractDeclClass c : getList()) {
            c.verifyClass(compiler, env_types);
        }

        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler, EnvironmentType env_types) throws ContextualError {

        for (AbstractDeclClass c : getList()) {
            c.verifyClassMembers(compiler, env_types);
        }
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler, EnvironmentType env_types) throws ContextualError {

        for (AbstractDeclClass c : getList()) {
            c.verifyClassBody(compiler, env_types);
        }
    }

    protected void codeGenListDeclClass(DecacCompiler compiler){
        // On génère la table pour Object
        compiler.addInstruction(new ADDSP(2));
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        DAddr regOff = new RegisterOffset(compiler.stackTracker.push(), Register.GB);
        compiler.addInstruction(new STORE(Register.R0, regOff));

        Label eq = new Label("Object.1.equals.2");
        compiler.addInstruction(new LOAD(new LabelOperand(eq), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(compiler.stackTracker.push(), Register.GB)));
        // On génère la table des autres classes
        for (AbstractDeclClass dc : getList()){
            dc.codeGenDeclClass(compiler);
        }
    }

    protected void codeGenListInitClass(DecacCompiler compiler){

        compiler.addComment("Initialisation de Object");
        compiler.addLabel(new Label("init.Object.1"));

        RegisterManager.saveRegisters(compiler);

        compiler.addInstruction(new LEA(new RegisterOffset(1, Register.GB), Register.R1));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(2)));
        compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(0, Register.getR(2))));

        RegisterManager.restoreRegisters(compiler);
        compiler.addInstruction(new RTS());

        for (AbstractDeclClass dc : getList()){
            dc.codeGenInitClass(compiler);
        }
    }

    protected void codeGenListMethodes(DecacCompiler compiler){

        compiler.addComment("~~~~~~~~~~~~~~~~~");
        compiler.addComment("Code des methodes");
        compiler.addComment("~~~~~~~~~~~~~~~~~");

        for (AbstractDeclClass dc : getList()){
            dc.codeGenMethodes(compiler);
        }

        compiler.addLabel(new Label("Object.1.equals.2"));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        compiler.addInstruction(new CMP(new RegisterOffset(-3, Register.LB), Register.R0));
        compiler.addInstruction(new SEQ(Register.R0));
        compiler.addInstruction(new RTS());
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addComment("Fin du code des methodes");
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~");

    }




}
