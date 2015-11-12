grammar PlanA;

@parser::header {
    import java.util.Set;
}

@parser::members {
    private Set<String> types = null;

    void setTypes(Set<String> types) {
        this.types = types;
    }

    boolean isType() {
        if (types == null) {
            throw new IllegalStateException();
        }

        return types.contains(getCurrentToken().getText());
    }
}

source
    : statement+ EOF
    ;

statement
    : IF LP expression RP block (ELSE block)?                                                # if
    | WHILE LP expression RP ( block | empty )                                               # while
    | DO block WHILE LP expression RP SEMICOLON?                                             # do
    | FOR LP initializer? SEMICOLON expression? SEMICOLON afterthought? RP ( block | empty ) # for
    | declaration SEMICOLON?                                                                 # decl
    | CONTINUE SEMICOLON?                                                                    # continue
    | BREAK SEMICOLON?                                                                       # break
    | RETURN expression SEMICOLON?                                                           # return
    | expression SEMICOLON?                                                                  # expr
    ;

block
    : LBRACK statement* RBRACK                 # multiple
    | statement                                # single
    ;

empty
    : SEMICOLON
    ;

initializer
    : declaration
    | expression
    ;

afterthought
    : expression
    ;

declaration
    : decltype declvar ( COMMA declvar )*
    ;

decltype
    : type (LBRACE RBRACE)*
    ;

declvar
    : id ( ASSIGN expression )?
    ;

type
    : {isType()}? ID
    | {isType()}? GENERIC
    ;

id
    : {!isType()}? ID
    ;

expression
    :               LP expression RP                                    # precedence
    |               ( OCTAL | HEX | INTEGER | DECIMAL )                 # numeric
    |               STRING                                              # string
    |               CHAR                                                # char
    |               TRUE                                                # true
    |               FALSE                                               # false
    |               NULL                                                # null
    |               extstart                                            # external
    |               extstart increment                                  # postinc
    |               increment extstart                                  # preinc
    |               ( BOOLNOT | BWNOT | ADD | SUB ) expression          # unary
    |               LP decltype RP expression                           # cast
    |               expression ( MUL | DIV | REM ) expression           # binary
    |               expression ( ADD | SUB ) expression                 # binary
    |               expression ( LSH | RSH | USH ) expression           # binary
    |               expression ( LT | LTE | GT | GTE ) expression       # comp
    |               expression ( EQ | EQR | NE | NER ) expression       # comp
    |               expression BWAND expression                         # binary
    |               expression BWXOR expression                         # binary
    |               expression BWOR expression                          # binary
    |               expression BOOLAND expression                       # bool
    |               expression BOOLOR expression                        # bool
    | <assoc=right> expression COND expression COLON expression         # conditional
    |               extstart ( ASSIGN | AADD | ASUB | AMUL | ADIV
                                      | AREM | AAND | AXOR | AOR
                                      | ALSH | ARSH | AUSH ) expression # assignment
    ;

extstart
   : extprec
   | extcast
   | exttype
   | extmember
   | extnew
   ;

extprec:   LP ( extprec | extcast | exttype | extmember | extnew ) RP ( extdot | extbrace )?;
extcast:   LP decltype RP ( extprec | extcast | exttype | extmember | extnew );
extbrace:  LBRACE expression RBRACE ( extdot | extbrace )?;
extdot:    DOT ( extcall | extmember );
exttype:   type extdot;
extcall:   id arguments ( extdot | extbrace )?;
extmember: id (extdot | extbrace )?;
extnew:    NEW type ( ( arguments ( extdot | extbrace)? ) | ( ( LBRACE expression RBRACE )+ extdot? ) );

arguments
    : ( LP ( expression ( COMMA expression )* )? RP )
    ;

increment
    : INCR
    | DECR
    ;

WS: [ \t\n\r]+ -> skip;
COMMENT: ( '//' .*? '\n' | '/*' .*? '*/' ) -> skip;

LBRACK:    '{';
RBRACK:    '}';
LBRACE:    '[';
RBRACE:    ']';
LP:        '(';
RP:        ')';
DOT:       '.';
COMMA:     ',';
SEMICOLON: ';';
IF:        'if';
ELSE:      'else';
WHILE:     'while';
DO:        'do';
FOR:       'for';
CONTINUE:  'continue';
BREAK:     'break';
RETURN:    'return';
NEW:       'new';

BOOLNOT: '!';
BWNOT:   '~';
MUL:     '*';
DIV:     '/';
REM:     '%';
ADD:     '+';
SUB:     '-';
LSH:     '<<';
RSH:     '>>';
USH:     '>>>';
LT:      '<';
LTE:     '<=';
GT:      '>';
GTE:     '>=';
EQ:      '==';
EQR:     '===';
NE:      '!=';
NER:     '!==';
BWAND:   '&';
BWXOR:   '^';
BWOR:    '|';
BOOLAND: '&&';
BOOLOR:  '||';
COND:    '?';
COLON:   ':';
INCR:    '++';
DECR:    '--';

ASSIGN:    '=';
AADD:      '+=';
ASUB:      '-=';
AMUL:      '*=';
ADIV:      '/=';
AREM:      '%=';
AAND:      '&=';
AXOR:      '^=';
AOR:       '|=';
ALSH:      '<<=';
ARSH:      '>>=';
AUSH:      '>>>=';
ACAT:      '..=';

OCTAL: '0' [0-7]+ [lL]?;
HEX: '0' [xX] [0-9a-fA-F]+ [lL]?;
INTEGER: ( '0' | [1-9] [0-9]* ) [lL]?;
DECIMAL: ( ( '0' | [1-9] [0-9]* ) ( '.' [0-9]* )? | '.' [0-9]+ ) ( [eE] [+\-]? [0-9]+ )? [fF]?;

STRING: '"' ( '\\"' | '\\\\' | ~[\\"] )*? '"' {setText(getText().substring(1, getText().length() - 1));};
CHAR: '\'' . '\''                             {setText(getText().substring(1, getText().length() - 1));};

TRUE:  'true';
FALSE: 'false';

NULL: 'null';

ID: [_a-zA-Z] [_a-zA-Z0-9]*;
GENERIC: ID '<' WS? ( ID | GENERIC ) WS? (COMMA WS? ( ID | GENERIC ) )* WS? '>' {setText(getText().replace(" ", ""));};
