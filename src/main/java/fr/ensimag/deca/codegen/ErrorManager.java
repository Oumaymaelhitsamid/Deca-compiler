package fr.ensimag.deca.codegen;

import fr.ensimag.deca.codegen.StackTracker;

import fr.ensimag.deca.DecacCompiler;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;

public class ErrorManager{

    private static Label IOErrorLabel = new Label("ioError");

    public static Label getIOErrorLabel(){
        return IOErrorLabel;
    }

    public static void codeGenTestIOError(DecacCompiler compiler){
        compiler.addInstruction(new BOV(IOErrorLabel));
    }

    private static void codeGenIOError(DecacCompiler compiler){
        compiler.addLabel(IOErrorLabel);
        compiler.addInstruction(new WSTR("[ExecutionError 0.1]: Input error"));
        compiler.addInstruction(new ERROR());
    }


    private static Label arithErrorLabel = new Label("arithError");

    public static Label getArithErrorLabel(){
        return arithErrorLabel;
    }

    public static void codeGenTestArithError(DecacCompiler compiler){
        if(! compiler.getCompilerOptions().shouldDisableChecks()) {
            compiler.addInstruction(new BOV(arithErrorLabel));
        }
    }

    private static void codeGenArithError(DecacCompiler compiler){
        compiler.addLabel(arithErrorLabel);
        compiler.addInstruction(new WSTR("[ExecutionError 1.1]: Arithmetic error"));
        compiler.addInstruction(new ERROR());
    }

    private static Label NULLErrorLabel = new Label("NULLError");

    private static void codeGenNULLError(DecacCompiler compiler){
        compiler.addLabel(NULLErrorLabel);
        compiler.addInstruction(new WSTR("[ExecutionError 1.2]: Dereferencement error"));
        compiler.addInstruction(new ERROR());
    }

    public static void codeGenTestNULLError(DecacCompiler compiler, GPRegister rcmp){
        if (!compiler.getCompilerOptions().shouldDisableChecks()){
            compiler.addInstruction(new CMP(new NullOperand(), rcmp));
            compiler.addInstruction(new BOV(NULLErrorLabel));
        }
    }

    private static Label voidErrorLabel = new Label("voidError");

    private static void codeGenVoidError(DecacCompiler compiler){
        compiler.addLabel(voidErrorLabel);
        compiler.addInstruction(new WSTR("[ExecutionError 1.3]: VoidError, no return instruction in non-void method"));
        compiler.addInstruction(new ERROR());
    }

    public static void codeGenTestVoidError(DecacCompiler compiler){
        if (!compiler.getCompilerOptions().shouldDisableChecks()){
            compiler.addInstruction(new BRA(voidErrorLabel));
        }
    }

    private static Label allocErrorLabel = new Label("AllocError");

    private static void codeGenAllocError(DecacCompiler compiler){
        compiler.addLabel(allocErrorLabel);
        compiler.addInstruction(new WSTR("[ExecutionError 1.4]: Cannot allocate memory"));
        compiler.addInstruction(new ERROR());
    }

    public static void codeGenTestAllocError(DecacCompiler compiler){
        if (!compiler.getCompilerOptions().shouldDisableChecks()){
            compiler.addInstruction(new BOV(allocErrorLabel));
        }
    }

    public static void codeGenError(DecacCompiler compiler){
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addComment("Code de gestion des erreurs");
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        codeGenIOError(compiler);
        if (! compiler.getCompilerOptions().shouldDisableChecks()) {
            compiler.stackTracker.codeGenStackError(compiler);
            codeGenArithError(compiler);
            codeGenVoidError(compiler);
            codeGenNULLError(compiler);
            codeGenAllocError(compiler);
        }
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        compiler.addComment(" Fin du code de gestion des erreurs");
        compiler.addComment("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

}
