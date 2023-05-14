package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.ERROR;

/**
 * Keep track of the state of the stack.
 */
public class StackTracker{

    private int currentStack = 0;
    private int maxStack = 0;

    public int push(){
        if (++currentStack > maxStack){
            maxStack = currentStack;
        }
        return currentStack;
    }

    public int pop(){
        currentStack--;
        return currentStack;
    }

    public int getMaxStack(){
        return maxStack;
    }

    void codeGenStackError(DecacCompiler compiler){
        Label lb = new Label("stackOverflow");
        compiler.getProgram().addFirst(new BOV(lb));
        compiler.getProgram().addFirst(new TSTO(maxStack));
        compiler.addLabel(lb);
        compiler.addInstruction(new WSTR("[ExecutionError 1.5]: StackOverflow"));
        compiler.addInstruction(new ERROR());
    }
}
