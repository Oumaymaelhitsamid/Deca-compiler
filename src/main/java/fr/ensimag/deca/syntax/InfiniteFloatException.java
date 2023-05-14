package fr.ensimag.deca.syntax;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Exception raised when a float is rounded until infinite
 *
 * @author gl44
 * @date 10/01/2022
 */

public class InfiniteFloatException extends DecaRecognitionException {
    private final String float_value;

    public InfiniteFloatException(DecaParser recognizer, ParserRuleContext ctx, String float_value) {
        super(recognizer, ctx);
        this.float_value = float_value;
    }

    public String getValue() {
        return float_value;
    }

    @Override
    public String getMessage() {
        return float_value + ":[Syntax Error] The rounding of the float tends towards infinity ";
    }

    private static final long serialVersionUID = 1L;

}
