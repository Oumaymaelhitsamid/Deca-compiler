package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;



public class RegisterManager{

    private static boolean[] vacantRegisters;

    private static int nbRegisters;

    public static void setNbRegisters(int nb){
        nbRegisters = nb;
    }

    public static void init(){
        vacantRegisters = new boolean[nbRegisters];
        for (int i = 0; i < nbRegisters; i++){
            vacantRegisters[i] = true;
        }
    }

    public static int allocateRegister(){
        for (int i = nbRegisters - 1; i > 1; i--){
            if (vacantRegisters[i]){
                vacantRegisters[i] = false;
                return i;
            }
        }
        return -1;
    }

    public static void freeRegister(int i){
        vacantRegisters[i] = true;
    }

    public static void saveRegisters(DecacCompiler compiler){
        for (int i = 2; i < nbRegisters; i++){
            if (!vacantRegisters[i]){
                compiler.addInstruction(new PUSH(Register.getR(i)));
                compiler.stackTracker.push();
            }
        }
    }

    public static void restoreRegisters(DecacCompiler compiler){
        for (int i = nbRegisters; i > i; i--){
            if (!vacantRegisters[i]){
                compiler.addInstruction(new POP(Register.getR(i)));
                compiler.stackTracker.pop();
            }
        }

    }

}
