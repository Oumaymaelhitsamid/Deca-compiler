package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl44
 * @date 01/01/2022
 */
public class CompilerOptions {

    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    private boolean disableChecks;
    private boolean help = false;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean stdin = false;
    private boolean stopAfterCheck;
    private boolean stopAfterParse;
    private boolean enableWarnings = false;
    private int debug = 0;
    private int registersAmount = 4;
    private int threadAmount = 1;

    private List<File> sourceFiles = new ArrayList<File>();

    private Options options = new Options();

    public boolean shouldDisableChecks() {
        return disableChecks;
    }

    public boolean getWarning() {
        return enableWarnings;
    }

    public boolean shouldStopAfterStepA() {
        return stopAfterParse;
    }

    public boolean shouldStopAfterStepB() {
        return stopAfterCheck;
    }

    public int getDebug() {
        return debug;
    }

    public boolean needHelp() {
        return help;
    }

    public boolean getParallel() {
        return parallel;
    }

    public int getThreadAmount() {
        return threadAmount;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }

    public int getRegistersAmount() {
        return registersAmount;
    }

    public boolean getStdin() {
        return stdin;
    }

    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }
    
    public void parseArgs(String[] args) throws CLIException {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;

        try {
            registerOptions();
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            throw new CLIException(e.getMessage());
        }

        if (commandLine.hasOption("p") && commandLine.hasOption("v")) {
            throw new CLIException("-p and -v can not be used together");
        }

        this.printBanner = commandLine.hasOption("b");
        this.help = commandLine.hasOption("h");
        this.stdin = commandLine.hasOption("i");
        this.stopAfterParse = commandLine.hasOption("p");
        this.stopAfterCheck = commandLine.hasOption("v");
        this.disableChecks = commandLine.hasOption("n");
        this.enableWarnings = commandLine.hasOption("w");

        Option parsedOptions[] = commandLine.getOptions();
        for (Option option : parsedOptions) { // incrémente le niveau de debug a chaque répetition de -d
            if (option.getOpt().equals("d")) {
                debug++;
            }
        }

        if (commandLine.hasOption("r")) {
            Integer value = Integer.parseInt(commandLine.getOptionValue("r"));
            if (value < 4 || value > 16) {
                throw new CLIException("register amount must be between 4 and 16");
            }
            this.registersAmount = value;
        }

        if (commandLine.hasOption("P")) {
            String value = commandLine.getOptionValue("P");
            if (value != null) {
                Integer threads;
                try {
                    threads = Integer.parseInt(value);
                    if (threads > 0) {
                        this.threadAmount = threads;
                    } else {
                        throw new CLIException("threads number must be strictly positive");
                    }
                } catch (NumberFormatException e) {
                    throw new CLIException("thread number incorrect (use -- to disambiguate options and paths)");
                }
            } else {
                this.threadAmount = 4;
            }
            this.parallel = true;
        }

        // les arguments restants sont les fichiers source
        for (String filename : commandLine.getArgs()) {

            int extensionIndex = filename.lastIndexOf(".");
            String extension = filename.substring(extensionIndex + 1); //+1 pour ignorer le .

            if (! extension.toLowerCase().equals("deca")) {
                throw new CLIException(filename + ": not a .deca file");
            }

            File file = new File(filename);

            if (! file.exists()) {
                throw new CLIException(filename + ": file not found");
            }

            if (file.isDirectory()) {
                throw new CLIException(filename + ": not a file");
            }

            if (! sourceFiles.contains(file)) {
                sourceFiles.add(file);
            }
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
            case QUIET: break; // keep default
            case INFO:
                logger.setLevel(Level.INFO); break;
            case DEBUG:
                logger.setLevel(Level.DEBUG); break;
            case TRACE:
                logger.setLevel(Level.TRACE); break;
            default:
                logger.setLevel(Level.ALL); break;
        }

        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    private void registerOptions() {
        this.options.addOption(Option.builder("b").desc("display banner").build());
        this.options.addOption(Option.builder("d").desc("enable debug messages (repeat to increase level)").build());
        this.options.addOption(Option.builder("h").desc("print this message").build());
        this.options.addOption(Option.builder("i").desc("use stdin as program input").build());
        this.options.addOption(Option.builder("n").desc("disable runtime checks").build());
        this.options.addOption(Option.builder("P").desc("enable multithreading (default: 4 threads)").hasArg().argName("threads").optionalArg(true).build());
        this.options.addOption(Option.builder("p").desc("print program decompilation and stop").build());
        this.options.addOption(Option.builder("r").desc("set number of registers to use (between 4 and 16)").hasArg().argName("registers").build());
        this.options.addOption(Option.builder("v").desc("verify then stop (does not produce executable)").build());
        this.options.addOption(Option.builder("w").desc("enable warnings").build());
    }

    protected void displayUsage() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("decac", this.options);
    }
}
