package fr.ensimag.deca.syntax;
import org.antlr.v4.runtime.IntStream;


/**
 * Exception raised when one of the string delimiter is missing
 *
 * @author gl44
 * @date 10/01/2022
 */

public class TooLongIdentException extends DecaRecognitionException {
    private final String expr_value;

    //public DecaRecognitionException(AbstractDecaLexer recognizer,IntStream input) : Super constructor for lexer
    public TooLongIdentException(DecaLexer recognizer, IntStream stream, String expr_value) {
        super(recognizer, stream);
        this.expr_value = expr_value;
    }

    public String getValue() {
        return expr_value;
    }

    @Override
    public String getMessage() {
        return expr_value + ":[Syntax Error] The identifier is too long (max 512 char) ";
    }

    private static final long serialVersionUID = 1L;

}
