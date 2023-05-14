lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@header {
  import org.apache.log4j.Logger;
  import fr.ensimag.deca.tools.DecacWarning;
}

@members {
    private static Logger logger = Logger.getLogger(DecaLexer.class);
}

// Deca lexer rules.

//Revserved Words

ASM : 'asm' ;
CLASS : 'class';
EXTENDS : 'extends';
ELSE : 'else';
FALSE : 'false';
IF : 'if';
INSTANCEOF : 'instanceof';
NEW : 'new';
NULL : 'null';
READINT : 'readInt';
READFLOAT : 'readFloat';
PRINT : 'print';
PRINTLN : 'println';
PRINTLNX : 'printlnx';
PRINTX : 'printx';
PROTECTED : 'protected';
RETURN : 'return';
THIS : 'this';
TRUE : 'true';
WHILE : 'while';
ELSEIF : 'elseif'{
  if(true) throw new ElseIfException(this,getInputStream(),getText());
};


//Identifiers

fragment DIGIT : '0' .. '9';
fragment LETTER : 'a' .. 'z' | 'A' .. 'Z';
IDENT : (LETTER | '$' + '_') ((LETTER | DIGIT | '$' | '_')*){
  try{
      if(getText().length() > 128 && getText().length() <512 && getDecacCompiler().getCompilerOptions().getWarning()){
        throw new DecaRecognitionException(this,getInputStream());
      }
  } catch (DecaRecognitionException e) {
    DecacWarning warn = new DecacWarning(e.getLocation(),"Identifier anormaly long", getText());
    warn.display();
  }
  if(getText().length() >= 512){
    throw new TooLongIdentException(this,getInputStream(),getText());
  }
} ;

//Special Symbol
LT : '<';
GT : '>';
EQUALS : '=';
PLUS : '+';
MINUS : '-';
TIMES : '*';
SLASH : '/';
PERCENT : '%';
DOT : '.';
COMMA : ',';
OPARENT : '(';
CPARENT : ')';
OBRACE : '{';
CBRACE : '}';
EXCLAM : '!';
SEMI : ';';
EQEQ : '==';
NEQ : '!=';
LEQ : '<=';
GEQ : '>=';
AND : '&&';
OR : '||';

//Integer

fragment POSITIVE_DIGIT : '1' .. '9';
INT : '0' | POSITIVE_DIGIT DIGIT*;
//Verif codage a faire : ICI ou ailleurs ?


//Flottants
NUM : DIGIT+;
fragment SIGN : '+' | '-' | ;
fragment EXP : ('E' | 'e') SIGN NUM;
fragment DEC : NUM '.' NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f' | );
fragment DIGITHEX : DIGIT | 'A' .. 'F' | 'a' .. 'f';
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f' |);
FLOAT : FLOATDEC | FLOATHEX;

//De même, gérer l'exception

//String
fragment STRING_CAR : ~( '"' | '\\' | '\n' );
STRING : '"' ( STRING_CAR | '\\"' | '\\\\' )* '"';
MULTI_LINE_STRING : '"'  (STRING_CAR | '\n' | '\\"' |  '\\\\')*  '"' ;

//To skip
EOL : '\n' {skip();};
SPACE : ' ' {skip();};
TAB : '\t' {skip();};
DOUBLE_DOT : ':' {skip();};
COMMENT : (  '/*' .*? '*/' ) {skip();} ;
BS_R : '\r' {skip();};
DOUBLE_COMMENT : '//' .*?'\n' {skip();};

//Include file
fragment FILENAME : (LETTER | DIGIT | '.' | '_')+;
INCLUDE : '#include' (' ')* '"' FILENAME '"'{
  //logger.info("Sortie getText" + getText());
  doInclude(getText());
  skip();};

INCORRECT_INCLUDE : ('#include' (' ')* FILENAME '"' | '#include' (' ')* '"' FILENAME | '#include' (' ')* FILENAME) {
  for(int i=0; i<2;i++){ //Eviter l'erreur java : unreachable statement : Demander si il y'a mieux
  throw new IncludeIncorrectException(this,getInputStream(),getText());
}
};


OTHER :.{
  for(int i=0; i<2;i++){ //Eviter l'erreur java : unreachable statement : Demander si il y'a mieux
  throw new NoRecognizeLexemException(this,getInputStream(),getText());
}
};
