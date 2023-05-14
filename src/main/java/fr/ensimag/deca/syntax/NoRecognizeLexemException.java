package fr.ensimag.deca.syntax;
import org.antlr.v4.runtime.IntStream;


/**
 * Exception raised when a expression isn't in the lexer
 *
 * @author gl44
 * @date 10/01/2022
 */

public class NoRecognizeLexemException extends DecaRecognitionException {
    private final String expr_value;

    //public DecaRecognitionException(AbstractDecaLexer recognizer,IntStream input) : Super constructor for lexer
    public NoRecognizeLexemException(DecaLexer recognizer, IntStream stream, String expr_value) {
        super(recognizer, stream);
        this.expr_value = expr_value;
    }

    public String getValue() {
        return expr_value;
    }

    @Override
    public String getMessage() {
        return expr_value + ":[Syntax Error] This expression is not recognized by the compiler ";
    }

    private static final long serialVersionUID = 1L;

}
