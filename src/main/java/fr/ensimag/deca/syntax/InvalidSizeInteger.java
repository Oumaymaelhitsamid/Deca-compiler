package fr.ensimag.deca.syntax;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Exception raised when a an intger is not codable on 32 bits as a signed positfive integer
 *
 * @author gl44
 * @date 10/01/2022
 */

public class InvalidSizeInteger extends DecaRecognitionException {
    private final String int_value;

    public InvalidSizeInteger(DecaParser recognizer, ParserRuleContext ctx, String int_value) {
        super(recognizer, ctx);
        this.int_value = int_value;
    }

    public String getValue() {
        return int_value;
    }

    @Override
    public String getMessage() {
        return int_value + ":[Syntax Error] This integer cannot be encoded as a 32-bit signed integer ";
    }

    private static final long serialVersionUID = 1L;

}
