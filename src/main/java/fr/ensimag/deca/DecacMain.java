package fr.ensimag.deca;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl44
 * @date 01/01/2022
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        boolean error = false;
    
        final CompilerOptions options = new CompilerOptions();

        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println(e.getMessage());
            System.err.println("use -h for help");
            System.exit(1);
        }

        if (options.needHelp()) {
            options.displayUsage();
            System.exit(0);
        }

        if (options.getPrintBanner()) {
            System.out.println("Projet GL 2022 | g8 gl44");
            System.exit(0);
        }

        List<File> sourceFiles = options.getSourceFiles();
        if (sourceFiles.isEmpty() && ! options.getStdin()) {
            System.err.println("error: no input files");
            System.exit(1);
        }

        if (options.getStdin()) {
            System.out.println("write DECA program, Ctrl+D for EOF");

            String programText = "";
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                programText += scanner.nextLine()+"\n";
            }
            scanner.close();
            LOG.debug("program from stdin:\n"+ programText);

            try {
                File tempFile = Files.createTempFile("decac", ".deca").toFile();
                FileWriter writer = new FileWriter(tempFile);
                LOG.debug("writing stdin program to "+ tempFile.getPath());
                writer.write(programText);
                writer.close();
                sourceFiles = new ArrayList<File>();
                sourceFiles.add(tempFile);
            } catch (Exception e) {
                System.err.println("failed to create program file "+ e.getMessage());
                System.exit(1);
            }
        }

        if (options.getParallel()) {
            int threadAmount = options.getThreadAmount();
            LOG.info("compiling concurrently using "+ threadAmount +" threads");

            ExecutorService executor = Executors.newFixedThreadPool(threadAmount);

            for (File sourceFile : sourceFiles) {
                Runnable routine = new DecacThreadRunnable(options, sourceFile);
                executor.execute(routine);
            }

            executor.shutdown();

            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                LOG.warn(e.getMessage());
            }

        } else {
            for (File source : sourceFiles) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }

        if (options.getStdin() && sourceFiles.size() == 1) {
            File f = sourceFiles.get(0);
            LOG.info("removing temp file "+ f.getAbsolutePath());
            f.delete();
        }

        System.exit(error ? 1 : 0);
    }
}
