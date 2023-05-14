package fr.ensimag.deca;

import java.io.File;

import java.lang.Runnable;
public class DecacThreadRunnable implements Runnable {

    private CompilerOptions options;
    private File sourceFile;

    public DecacThreadRunnable(CompilerOptions options, File sourceFile) {
        this.options = options;
        this.sourceFile = sourceFile;
    }

    @Override
    public void run() {
        DecacCompiler compiler = new DecacCompiler(this.options, this.sourceFile);
        compiler.compile();
    }
}
