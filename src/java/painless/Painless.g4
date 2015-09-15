grammar Painless;

source
    : statement+ EOF
    ;

statement
    : IF LP expression RP block (ELSE block)?                                  # if
    | WHILE LP expression RP block                                             # while
    | DO block WHILE LP expression RP                                          # do
    | FOR LP declaration? SEMICOLON expression? SEMICOLON expression? RP block # for
    | declaration SEMICOLON                                                    # decl
    | CONTINUE SEMICOLON                                                       # continue
    | BREAK SEMICOLON                                                          # break
    | RETURN expression SEMICOLON                                              # return
    | expression SEMICOLON                                                     # expr
    ;

block
    : LBRACK statement* RBRACK                 # multiple
    | statement                                # single
    | SEMICOLON                                # empty
    ;

declaration
    : decltype ID ( ASSIGN expression )? ( COMMA ID ( ASSIGN expression )? )*
    ;

decltype
    : TYPE (LBRACE RBRACE)*
    ;

expression
    :               LP expression RP                                          # precedence
    |               ( OCTAL | HEX | INTEGER | DECIMAL )                       # numeric
    |               STRING                                                    # string
    |               CHAR                                                      # char
    |               TRUE                                                      # true
    |               FALSE                                                     # false
    |               NULL                                                      # null
    |               extstart                                                  # ext
    | <assoc=right> ( BOOLNOT | BWNOT | ADD | SUB ) expression                # unary
    | <assoc=right> LP decltype RP expression                                 # cast
    |               expression ( MUL | DIV | REM ) expression                 # binary
    |               expression ( ADD | SUB ) expression                       # binary
    |               expression ( LSH | RSH | USH ) expression                 # binary
    |               expression ( LT | LTE | GT | GTE ) expression             # comp
    |               expression ( EQ | NE ) expression                         # comp
    |               expression BWAND expression                               # binary
    |               expression BWXOR expression                               # binary
    |               expression BWOR expression                                # binary
    |               expression BOOLAND expression                             # bool
    |               expression BOOLOR expression                              # bool
    | <assoc=right> expression COND expression COLON expression               # conditional
    | <assoc=right> extstart ASSIGN expression                                # assignment
    ;

extstart
   : extprec
   | extcast
   | extfunc
   | extstatic
   | extmember
   ;

extprec:   LP ( extprec | extcast | extfunc | extstatic | extcall | extmember) RP ( extdot | extarray )?;
extcast:   LP decltype RP ( extprec | extcast | extfunc | extstatic | extcall | extmember );
extarray:  LBRACE expression RBRACE ( extdot | extarray )?;
extdot:    DOT ( extcall | extmember );
extfunc:   TYPE DOT ID arguments ( extdot | extarray )?;
extstatic: TYPE DOT ID ( extdot | extarray )?;
extcall:   ID arguments ( extdot | extarray )?;
extmember: ID (extdot | extarray )?;

arguments
    : ( LP ( expression ( COMMA expression )* )? RP )
    ;

WS: [ \t\n\r]+ -> skip;

LBRACK:    '{';
RBRACK:    '}';
LBRACE:    '[';
RBRACE:    ']';
LP:        '(';
RP:        ')';
ASSIGN:    '=';
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
NE:      '!=';
BWAND:   '&';
BWXOR:   '^';
BWOR:    '|';
BOOLAND: '&&';
BOOLOR:  '||';
COND:    '?';
COLON:   ':';

OCTAL: '0' [0-7]+ [lL]?;
HEX: '0' [xX] [0-9a-fA-F]+ [lL]?;
INTEGER: ( '0' | [1-9] [0-9]* ) [lL]?;
DECIMAL: ( ( '0' | [1-9] [0-9]* ) ( '.' [0-9]* )? | '.' [0-9]+ ) ( [eE] [+\-]? [0-9]+ )? [fF]?;

STRING: '"' ( '\\"' | '\\\\' | ~[\\"] )*? '"';
CHAR: '\'' . '\'';

TRUE:  'true';
FALSE: 'false';

NULL: 'null';

VOID: 'void';

TYPE
    : 'bool'
    | 'byte'
    | 'short'
    | 'int'
    | 'long'
    | 'float'
    | 'double'
    | 'char'
    | 'string'
    | 'object'
    | 'list'
    | 'map'
    | '$' [a-z]*
    ;

ID: [_a-zA-Z] [_a-zA-Z0-9]*;