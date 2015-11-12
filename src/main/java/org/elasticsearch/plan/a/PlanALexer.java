// ANTLR GENERATED CODE: DO NOT EDIT
package org.elasticsearch.plan.a;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class PlanALexer extends Lexer {
  static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache =
    new PredictionContextCache();
  public static final int
    WS=1, COMMENT=2, LBRACK=3, RBRACK=4, LBRACE=5, RBRACE=6, LP=7, RP=8, DOT=9, 
    COMMA=10, SEMICOLON=11, IF=12, ELSE=13, WHILE=14, DO=15, FOR=16, CONTINUE=17, 
    BREAK=18, RETURN=19, NEW=20, BOOLNOT=21, BWNOT=22, MUL=23, DIV=24, REM=25, 
    ADD=26, SUB=27, LSH=28, RSH=29, USH=30, LT=31, LTE=32, GT=33, GTE=34, 
    EQ=35, EQR=36, NE=37, NER=38, BWAND=39, BWXOR=40, BWOR=41, BOOLAND=42, 
    BOOLOR=43, COND=44, COLON=45, INCR=46, DECR=47, ASSIGN=48, AADD=49, ASUB=50, 
    AMUL=51, ADIV=52, AREM=53, AAND=54, AXOR=55, AOR=56, ALSH=57, ARSH=58, 
    AUSH=59, ACAT=60, OCTAL=61, HEX=62, INTEGER=63, DECIMAL=64, STRING=65, 
    CHAR=66, TRUE=67, FALSE=68, NULL=69, ID=70, GENERIC=71;
  public static String[] modeNames = {
    "DEFAULT_MODE"
  };

  public static final String[] ruleNames = {
    "WS", "COMMENT", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", "DOT", 
    "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
    "BREAK", "RETURN", "NEW", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", 
    "SUB", "LSH", "RSH", "USH", "LT", "LTE", "GT", "GTE", "EQ", "EQR", "NE", 
    "NER", "BWAND", "BWXOR", "BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", 
    "INCR", "DECR", "ASSIGN", "AADD", "ASUB", "AMUL", "ADIV", "AREM", "AAND", 
    "AXOR", "AOR", "ALSH", "ARSH", "AUSH", "ACAT", "OCTAL", "HEX", "INTEGER", 
    "DECIMAL", "STRING", "CHAR", "TRUE", "FALSE", "NULL", "ID", "GENERIC"
  };

  private static final String[] _LITERAL_NAMES = {
    null, null, null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'.'", "','", 
    "';'", "'if'", "'else'", "'while'", "'do'", "'for'", "'continue'", "'break'", 
    "'return'", "'new'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", 
    "'<<'", "'>>'", "'>>>'", "'<'", "'<='", "'>'", "'>='", "'=='", "'==='", 
    "'!='", "'!=='", "'&'", "'^'", "'|'", "'&&'", "'||'", "'?'", "':'", "'++'", 
    "'--'", "'='", "'+='", "'-='", "'*='", "'/='", "'%='", "'&='", "'^='", 
    "'|='", "'<<='", "'>>='", "'>>>='", "'..='", null, null, null, null, null, 
    null, "'true'", "'false'", "'null'"
  };
  private static final String[] _SYMBOLIC_NAMES = {
    null, "WS", "COMMENT", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", 
    "DOT", "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
    "BREAK", "RETURN", "NEW", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", 
    "SUB", "LSH", "RSH", "USH", "LT", "LTE", "GT", "GTE", "EQ", "EQR", "NE", 
    "NER", "BWAND", "BWXOR", "BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", 
    "INCR", "DECR", "ASSIGN", "AADD", "ASUB", "AMUL", "ADIV", "AREM", "AAND", 
    "AXOR", "AOR", "ALSH", "ARSH", "AUSH", "ACAT", "OCTAL", "HEX", "INTEGER", 
    "DECIMAL", "STRING", "CHAR", "TRUE", "FALSE", "NULL", "ID", "GENERIC"
  };
  public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

  /**
   * @deprecated Use {@link #VOCABULARY} instead.
   */
  @Deprecated
  public static final String[] tokenNames;
  static {
    tokenNames = new String[_SYMBOLIC_NAMES.length];
    for (int i = 0; i < tokenNames.length; i++) {
      tokenNames[i] = VOCABULARY.getLiteralName(i);
      if (tokenNames[i] == null) {
        tokenNames[i] = VOCABULARY.getSymbolicName(i);
      }

      if (tokenNames[i] == null) {
        tokenNames[i] = "<INVALID>";
      }
    }
  }

  @Override
  @Deprecated
  public String[] getTokenNames() {
    return tokenNames;
  }

  @Override

  public Vocabulary getVocabulary() {
    return VOCABULARY;
  }


  public PlanALexer(CharStream input) {
    super(input);
    _interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
  }

  @Override
  public String getGrammarFileName() { return "PlanA.g4"; }

  @Override
  public String[] getRuleNames() { return ruleNames; }

  @Override
  public String getSerializedATN() { return _serializedATN; }

  @Override
  public String[] getModeNames() { return modeNames; }

  @Override
  public ATN getATN() { return _ATN; }

  @Override
  public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
    switch (ruleIndex) {
    case 64:
      STRING_action((RuleContext)_localctx, actionIndex);
      break;
    case 65:
      CHAR_action((RuleContext)_localctx, actionIndex);
      break;
    case 70:
      GENERIC_action((RuleContext)_localctx, actionIndex);
      break;
    }
  }
  private void STRING_action(RuleContext _localctx, int actionIndex) {
    switch (actionIndex) {
    case 0:
      setText(getText().substring(1, getText().length() - 1));
      break;
    }
  }
  private void CHAR_action(RuleContext _localctx, int actionIndex) {
    switch (actionIndex) {
    case 1:
      setText(getText().substring(1, getText().length() - 1));
      break;
    }
  }
  private void GENERIC_action(RuleContext _localctx, int actionIndex) {
    switch (actionIndex) {
    case 2:
      setText(getText().replace(" ", ""));
      break;
    }
  }

  public static final String _serializedATN =
    "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2I\u01f5\b\1\4\2\t"+
    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
    ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
    "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
    "\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\3\2"+
    "\6\2\u0093\n\2\r\2\16\2\u0094\3\2\3\2\3\3\3\3\3\3\3\3\7\3\u009d\n\3\f"+
    "\3\16\3\u00a0\13\3\3\3\3\3\3\3\3\3\3\3\7\3\u00a7\n\3\f\3\16\3\u00aa\13"+
    "\3\3\3\3\3\5\3\u00ae\n\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3"+
    "\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3"+
    "\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3"+
    "\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3"+
    "\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\26\3\26\3"+
    "\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3"+
    "\35\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3#\3#\3"+
    "#\3$\3$\3$\3%\3%\3%\3%\3&\3&\3&\3\'\3\'\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3"+
    "+\3+\3,\3,\3,\3-\3-\3.\3.\3/\3/\3/\3\60\3\60\3\60\3\61\3\61\3\62\3\62"+
    "\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\65\3\66\3\66\3\66\3\67"+
    "\3\67\3\67\38\38\38\39\39\39\3:\3:\3:\3:\3;\3;\3;\3;\3<\3<\3<\3<\3<\3"+
    "=\3=\3=\3=\3>\3>\6>\u0166\n>\r>\16>\u0167\3>\5>\u016b\n>\3?\3?\3?\6?\u0170"+
    "\n?\r?\16?\u0171\3?\5?\u0175\n?\3@\3@\3@\7@\u017a\n@\f@\16@\u017d\13@"+
    "\5@\u017f\n@\3@\5@\u0182\n@\3A\3A\3A\7A\u0187\nA\fA\16A\u018a\13A\5A\u018c"+
    "\nA\3A\3A\7A\u0190\nA\fA\16A\u0193\13A\5A\u0195\nA\3A\3A\6A\u0199\nA\r"+
    "A\16A\u019a\5A\u019d\nA\3A\3A\5A\u01a1\nA\3A\6A\u01a4\nA\rA\16A\u01a5"+
    "\5A\u01a8\nA\3A\5A\u01ab\nA\3B\3B\3B\3B\3B\3B\7B\u01b3\nB\fB\16B\u01b6"+
    "\13B\3B\3B\3B\3C\3C\3C\3C\3C\3D\3D\3D\3D\3D\3E\3E\3E\3E\3E\3E\3F\3F\3"+
    "F\3F\3F\3G\3G\7G\u01d2\nG\fG\16G\u01d5\13G\3H\3H\3H\5H\u01da\nH\3H\3H"+
    "\5H\u01de\nH\3H\5H\u01e1\nH\3H\3H\5H\u01e5\nH\3H\3H\5H\u01e9\nH\7H\u01eb"+
    "\nH\fH\16H\u01ee\13H\3H\5H\u01f1\nH\3H\3H\3H\5\u009e\u00a8\u01b4\2I\3"+
    "\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
    "\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37="+
    " ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9"+
    "q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087E\u0089F\u008bG\u008dH\u008f"+
    "I\3\2\17\5\2\13\f\17\17\"\"\3\2\629\4\2NNnn\4\2ZZzz\5\2\62;CHch\3\2\63"+
    ";\3\2\62;\4\2GGgg\4\2--//\4\2HHhh\4\2$$^^\5\2C\\aac|\6\2\62;C\\aac|\u0214"+
    "\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
    "\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
    "\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
    "\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
    "\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3"+
    "\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2"+
    "\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2"+
    "U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3"+
    "\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2"+
    "\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2"+
    "{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085"+
    "\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2"+
    "\2\2\u008f\3\2\2\2\3\u0092\3\2\2\2\5\u00ad\3\2\2\2\7\u00b1\3\2\2\2\t\u00b3"+
    "\3\2\2\2\13\u00b5\3\2\2\2\r\u00b7\3\2\2\2\17\u00b9\3\2\2\2\21\u00bb\3"+
    "\2\2\2\23\u00bd\3\2\2\2\25\u00bf\3\2\2\2\27\u00c1\3\2\2\2\31\u00c3\3\2"+
    "\2\2\33\u00c6\3\2\2\2\35\u00cb\3\2\2\2\37\u00d1\3\2\2\2!\u00d4\3\2\2\2"+
    "#\u00d8\3\2\2\2%\u00e1\3\2\2\2\'\u00e7\3\2\2\2)\u00ee\3\2\2\2+\u00f2\3"+
    "\2\2\2-\u00f4\3\2\2\2/\u00f6\3\2\2\2\61\u00f8\3\2\2\2\63\u00fa\3\2\2\2"+
    "\65\u00fc\3\2\2\2\67\u00fe\3\2\2\29\u0100\3\2\2\2;\u0103\3\2\2\2=\u0106"+
    "\3\2\2\2?\u010a\3\2\2\2A\u010c\3\2\2\2C\u010f\3\2\2\2E\u0111\3\2\2\2G"+
    "\u0114\3\2\2\2I\u0117\3\2\2\2K\u011b\3\2\2\2M\u011e\3\2\2\2O\u0122\3\2"+
    "\2\2Q\u0124\3\2\2\2S\u0126\3\2\2\2U\u0128\3\2\2\2W\u012b\3\2\2\2Y\u012e"+
    "\3\2\2\2[\u0130\3\2\2\2]\u0132\3\2\2\2_\u0135\3\2\2\2a\u0138\3\2\2\2c"+
    "\u013a\3\2\2\2e\u013d\3\2\2\2g\u0140\3\2\2\2i\u0143\3\2\2\2k\u0146\3\2"+
    "\2\2m\u0149\3\2\2\2o\u014c\3\2\2\2q\u014f\3\2\2\2s\u0152\3\2\2\2u\u0156"+
    "\3\2\2\2w\u015a\3\2\2\2y\u015f\3\2\2\2{\u0163\3\2\2\2}\u016c\3\2\2\2\177"+
    "\u017e\3\2\2\2\u0081\u019c\3\2\2\2\u0083\u01ac\3\2\2\2\u0085\u01ba\3\2"+
    "\2\2\u0087\u01bf\3\2\2\2\u0089\u01c4\3\2\2\2\u008b\u01ca\3\2\2\2\u008d"+
    "\u01cf\3\2\2\2\u008f\u01d6\3\2\2\2\u0091\u0093\t\2\2\2\u0092\u0091\3\2"+
    "\2\2\u0093\u0094\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095"+
    "\u0096\3\2\2\2\u0096\u0097\b\2\2\2\u0097\4\3\2\2\2\u0098\u0099\7\61\2"+
    "\2\u0099\u009a\7\61\2\2\u009a\u009e\3\2\2\2\u009b\u009d\13\2\2\2\u009c"+
    "\u009b\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009f\3\2\2\2\u009e\u009c\3\2"+
    "\2\2\u009f\u00a1\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00ae\7\f\2\2\u00a2"+
    "\u00a3\7\61\2\2\u00a3\u00a4\7,\2\2\u00a4\u00a8\3\2\2\2\u00a5\u00a7\13"+
    "\2\2\2\u00a6\u00a5\3\2\2\2\u00a7\u00aa\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a8"+
    "\u00a6\3\2\2\2\u00a9\u00ab\3\2\2\2\u00aa\u00a8\3\2\2\2\u00ab\u00ac\7,"+
    "\2\2\u00ac\u00ae\7\61\2\2\u00ad\u0098\3\2\2\2\u00ad\u00a2\3\2\2\2\u00ae"+
    "\u00af\3\2\2\2\u00af\u00b0\b\3\2\2\u00b0\6\3\2\2\2\u00b1\u00b2\7}\2\2"+
    "\u00b2\b\3\2\2\2\u00b3\u00b4\7\177\2\2\u00b4\n\3\2\2\2\u00b5\u00b6\7]"+
    "\2\2\u00b6\f\3\2\2\2\u00b7\u00b8\7_\2\2\u00b8\16\3\2\2\2\u00b9\u00ba\7"+
    "*\2\2\u00ba\20\3\2\2\2\u00bb\u00bc\7+\2\2\u00bc\22\3\2\2\2\u00bd\u00be"+
    "\7\60\2\2\u00be\24\3\2\2\2\u00bf\u00c0\7.\2\2\u00c0\26\3\2\2\2\u00c1\u00c2"+
    "\7=\2\2\u00c2\30\3\2\2\2\u00c3\u00c4\7k\2\2\u00c4\u00c5\7h\2\2\u00c5\32"+
    "\3\2\2\2\u00c6\u00c7\7g\2\2\u00c7\u00c8\7n\2\2\u00c8\u00c9\7u\2\2\u00c9"+
    "\u00ca\7g\2\2\u00ca\34\3\2\2\2\u00cb\u00cc\7y\2\2\u00cc\u00cd\7j\2\2\u00cd"+
    "\u00ce\7k\2\2\u00ce\u00cf\7n\2\2\u00cf\u00d0\7g\2\2\u00d0\36\3\2\2\2\u00d1"+
    "\u00d2\7f\2\2\u00d2\u00d3\7q\2\2\u00d3 \3\2\2\2\u00d4\u00d5\7h\2\2\u00d5"+
    "\u00d6\7q\2\2\u00d6\u00d7\7t\2\2\u00d7\"\3\2\2\2\u00d8\u00d9\7e\2\2\u00d9"+
    "\u00da\7q\2\2\u00da\u00db\7p\2\2\u00db\u00dc\7v\2\2\u00dc\u00dd\7k\2\2"+
    "\u00dd\u00de\7p\2\2\u00de\u00df\7w\2\2\u00df\u00e0\7g\2\2\u00e0$\3\2\2"+
    "\2\u00e1\u00e2\7d\2\2\u00e2\u00e3\7t\2\2\u00e3\u00e4\7g\2\2\u00e4\u00e5"+
    "\7c\2\2\u00e5\u00e6\7m\2\2\u00e6&\3\2\2\2\u00e7\u00e8\7t\2\2\u00e8\u00e9"+
    "\7g\2\2\u00e9\u00ea\7v\2\2\u00ea\u00eb\7w\2\2\u00eb\u00ec\7t\2\2\u00ec"+
    "\u00ed\7p\2\2\u00ed(\3\2\2\2\u00ee\u00ef\7p\2\2\u00ef\u00f0\7g\2\2\u00f0"+
    "\u00f1\7y\2\2\u00f1*\3\2\2\2\u00f2\u00f3\7#\2\2\u00f3,\3\2\2\2\u00f4\u00f5"+
    "\7\u0080\2\2\u00f5.\3\2\2\2\u00f6\u00f7\7,\2\2\u00f7\60\3\2\2\2\u00f8"+
    "\u00f9\7\61\2\2\u00f9\62\3\2\2\2\u00fa\u00fb\7\'\2\2\u00fb\64\3\2\2\2"+
    "\u00fc\u00fd\7-\2\2\u00fd\66\3\2\2\2\u00fe\u00ff\7/\2\2\u00ff8\3\2\2\2"+
    "\u0100\u0101\7>\2\2\u0101\u0102\7>\2\2\u0102:\3\2\2\2\u0103\u0104\7@\2"+
    "\2\u0104\u0105\7@\2\2\u0105<\3\2\2\2\u0106\u0107\7@\2\2\u0107\u0108\7"+
    "@\2\2\u0108\u0109\7@\2\2\u0109>\3\2\2\2\u010a\u010b\7>\2\2\u010b@\3\2"+
    "\2\2\u010c\u010d\7>\2\2\u010d\u010e\7?\2\2\u010eB\3\2\2\2\u010f\u0110"+
    "\7@\2\2\u0110D\3\2\2\2\u0111\u0112\7@\2\2\u0112\u0113\7?\2\2\u0113F\3"+
    "\2\2\2\u0114\u0115\7?\2\2\u0115\u0116\7?\2\2\u0116H\3\2\2\2\u0117\u0118"+
    "\7?\2\2\u0118\u0119\7?\2\2\u0119\u011a\7?\2\2\u011aJ\3\2\2\2\u011b\u011c"+
    "\7#\2\2\u011c\u011d\7?\2\2\u011dL\3\2\2\2\u011e\u011f\7#\2\2\u011f\u0120"+
    "\7?\2\2\u0120\u0121\7?\2\2\u0121N\3\2\2\2\u0122\u0123\7(\2\2\u0123P\3"+
    "\2\2\2\u0124\u0125\7`\2\2\u0125R\3\2\2\2\u0126\u0127\7~\2\2\u0127T\3\2"+
    "\2\2\u0128\u0129\7(\2\2\u0129\u012a\7(\2\2\u012aV\3\2\2\2\u012b\u012c"+
    "\7~\2\2\u012c\u012d\7~\2\2\u012dX\3\2\2\2\u012e\u012f\7A\2\2\u012fZ\3"+
    "\2\2\2\u0130\u0131\7<\2\2\u0131\\\3\2\2\2\u0132\u0133\7-\2\2\u0133\u0134"+
    "\7-\2\2\u0134^\3\2\2\2\u0135\u0136\7/\2\2\u0136\u0137\7/\2\2\u0137`\3"+
    "\2\2\2\u0138\u0139\7?\2\2\u0139b\3\2\2\2\u013a\u013b\7-\2\2\u013b\u013c"+
    "\7?\2\2\u013cd\3\2\2\2\u013d\u013e\7/\2\2\u013e\u013f\7?\2\2\u013ff\3"+
    "\2\2\2\u0140\u0141\7,\2\2\u0141\u0142\7?\2\2\u0142h\3\2\2\2\u0143\u0144"+
    "\7\61\2\2\u0144\u0145\7?\2\2\u0145j\3\2\2\2\u0146\u0147\7\'\2\2\u0147"+
    "\u0148\7?\2\2\u0148l\3\2\2\2\u0149\u014a\7(\2\2\u014a\u014b\7?\2\2\u014b"+
    "n\3\2\2\2\u014c\u014d\7`\2\2\u014d\u014e\7?\2\2\u014ep\3\2\2\2\u014f\u0150"+
    "\7~\2\2\u0150\u0151\7?\2\2\u0151r\3\2\2\2\u0152\u0153\7>\2\2\u0153\u0154"+
    "\7>\2\2\u0154\u0155\7?\2\2\u0155t\3\2\2\2\u0156\u0157\7@\2\2\u0157\u0158"+
    "\7@\2\2\u0158\u0159\7?\2\2\u0159v\3\2\2\2\u015a\u015b\7@\2\2\u015b\u015c"+
    "\7@\2\2\u015c\u015d\7@\2\2\u015d\u015e\7?\2\2\u015ex\3\2\2\2\u015f\u0160"+
    "\7\60\2\2\u0160\u0161\7\60\2\2\u0161\u0162\7?\2\2\u0162z\3\2\2\2\u0163"+
    "\u0165\7\62\2\2\u0164\u0166\t\3\2\2\u0165\u0164\3\2\2\2\u0166\u0167\3"+
    "\2\2\2\u0167\u0165\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u016a\3\2\2\2\u0169"+
    "\u016b\t\4\2\2\u016a\u0169\3\2\2\2\u016a\u016b\3\2\2\2\u016b|\3\2\2\2"+
    "\u016c\u016d\7\62\2\2\u016d\u016f\t\5\2\2\u016e\u0170\t\6\2\2\u016f\u016e"+
    "\3\2\2\2\u0170\u0171\3\2\2\2\u0171\u016f\3\2\2\2\u0171\u0172\3\2\2\2\u0172"+
    "\u0174\3\2\2\2\u0173\u0175\t\4\2\2\u0174\u0173\3\2\2\2\u0174\u0175\3\2"+
    "\2\2\u0175~\3\2\2\2\u0176\u017f\7\62\2\2\u0177\u017b\t\7\2\2\u0178\u017a"+
    "\t\b\2\2\u0179\u0178\3\2\2\2\u017a\u017d\3\2\2\2\u017b\u0179\3\2\2\2\u017b"+
    "\u017c\3\2\2\2\u017c\u017f\3\2\2\2\u017d\u017b\3\2\2\2\u017e\u0176\3\2"+
    "\2\2\u017e\u0177\3\2\2\2\u017f\u0181\3\2\2\2\u0180\u0182\t\4\2\2\u0181"+
    "\u0180\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0080\3\2\2\2\u0183\u018c\7\62"+
    "\2\2\u0184\u0188\t\7\2\2\u0185\u0187\t\b\2\2\u0186\u0185\3\2\2\2\u0187"+
    "\u018a\3\2\2\2\u0188\u0186\3\2\2\2\u0188\u0189\3\2\2\2\u0189\u018c\3\2"+
    "\2\2\u018a\u0188\3\2\2\2\u018b\u0183\3\2\2\2\u018b\u0184\3\2\2\2\u018c"+
    "\u0194\3\2\2\2\u018d\u0191\7\60\2\2\u018e\u0190\t\b\2\2\u018f\u018e\3"+
    "\2\2\2\u0190\u0193\3\2\2\2\u0191\u018f\3\2\2\2\u0191\u0192\3\2\2\2\u0192"+
    "\u0195\3\2\2\2\u0193\u0191\3\2\2\2\u0194\u018d\3\2\2\2\u0194\u0195\3\2"+
    "\2\2\u0195\u019d\3\2\2\2\u0196\u0198\7\60\2\2\u0197\u0199\t\b\2\2\u0198"+
    "\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u0198\3\2\2\2\u019a\u019b\3\2"+
    "\2\2\u019b\u019d\3\2\2\2\u019c\u018b\3\2\2\2\u019c\u0196\3\2\2\2\u019d"+
    "\u01a7\3\2\2\2\u019e\u01a0\t\t\2\2\u019f\u01a1\t\n\2\2\u01a0\u019f\3\2"+
    "\2\2\u01a0\u01a1\3\2\2\2\u01a1\u01a3\3\2\2\2\u01a2\u01a4\t\b\2\2\u01a3"+
    "\u01a2\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a3\3\2\2\2\u01a5\u01a6\3\2"+
    "\2\2\u01a6\u01a8\3\2\2\2\u01a7\u019e\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8"+
    "\u01aa\3\2\2\2\u01a9\u01ab\t\13\2\2\u01aa\u01a9\3\2\2\2\u01aa\u01ab\3"+
    "\2\2\2\u01ab\u0082\3\2\2\2\u01ac\u01b4\7$\2\2\u01ad\u01ae\7^\2\2\u01ae"+
    "\u01b3\7$\2\2\u01af\u01b0\7^\2\2\u01b0\u01b3\7^\2\2\u01b1\u01b3\n\f\2"+
    "\2\u01b2\u01ad\3\2\2\2\u01b2\u01af\3\2\2\2\u01b2\u01b1\3\2\2\2\u01b3\u01b6"+
    "\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b4\u01b2\3\2\2\2\u01b5\u01b7\3\2\2\2\u01b6"+
    "\u01b4\3\2\2\2\u01b7\u01b8\7$\2\2\u01b8\u01b9\bB\3\2\u01b9\u0084\3\2\2"+
    "\2\u01ba\u01bb\7)\2\2\u01bb\u01bc\13\2\2\2\u01bc\u01bd\7)\2\2\u01bd\u01be"+
    "\bC\4\2\u01be\u0086\3\2\2\2\u01bf\u01c0\7v\2\2\u01c0\u01c1\7t\2\2\u01c1"+
    "\u01c2\7w\2\2\u01c2\u01c3\7g\2\2\u01c3\u0088\3\2\2\2\u01c4\u01c5\7h\2"+
    "\2\u01c5\u01c6\7c\2\2\u01c6\u01c7\7n\2\2\u01c7\u01c8\7u\2\2\u01c8\u01c9"+
    "\7g\2\2\u01c9\u008a\3\2\2\2\u01ca\u01cb\7p\2\2\u01cb\u01cc\7w\2\2\u01cc"+
    "\u01cd\7n\2\2\u01cd\u01ce\7n\2\2\u01ce\u008c\3\2\2\2\u01cf\u01d3\t\r\2"+
    "\2\u01d0\u01d2\t\16\2\2\u01d1\u01d0\3\2\2\2\u01d2\u01d5\3\2\2\2\u01d3"+
    "\u01d1\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4\u008e\3\2\2\2\u01d5\u01d3\3\2"+
    "\2\2\u01d6\u01d7\5\u008dG\2\u01d7\u01d9\7>\2\2\u01d8\u01da\5\3\2\2\u01d9"+
    "\u01d8\3\2\2\2\u01d9\u01da\3\2\2\2\u01da\u01dd\3\2\2\2\u01db\u01de\5\u008d"+
    "G\2\u01dc\u01de\5\u008fH\2\u01dd\u01db\3\2\2\2\u01dd\u01dc\3\2\2\2\u01de"+
    "\u01e0\3\2\2\2\u01df\u01e1\5\3\2\2\u01e0\u01df\3\2\2\2\u01e0\u01e1\3\2"+
    "\2\2\u01e1\u01ec\3\2\2\2\u01e2\u01e4\5\25\13\2\u01e3\u01e5\5\3\2\2\u01e4"+
    "\u01e3\3\2\2\2\u01e4\u01e5\3\2\2\2\u01e5\u01e8\3\2\2\2\u01e6\u01e9\5\u008d"+
    "G\2\u01e7\u01e9\5\u008fH\2\u01e8\u01e6\3\2\2\2\u01e8\u01e7\3\2\2\2\u01e9"+
    "\u01eb\3\2\2\2\u01ea\u01e2\3\2\2\2\u01eb\u01ee\3\2\2\2\u01ec\u01ea\3\2"+
    "\2\2\u01ec\u01ed\3\2\2\2\u01ed\u01f0\3\2\2\2\u01ee\u01ec\3\2\2\2\u01ef"+
    "\u01f1\5\3\2\2\u01f0\u01ef\3\2\2\2\u01f0\u01f1\3\2\2\2\u01f1\u01f2\3\2"+
    "\2\2\u01f2\u01f3\7@\2\2\u01f3\u01f4\bH\5\2\u01f4\u0090\3\2\2\2\"\2\u0094"+
    "\u009e\u00a8\u00ad\u0167\u016a\u0171\u0174\u017b\u017e\u0181\u0188\u018b"+
    "\u0191\u0194\u019a\u019c\u01a0\u01a5\u01a7\u01aa\u01b2\u01b4\u01d3\u01d9"+
    "\u01dd\u01e0\u01e4\u01e8\u01ec\u01f0\6\b\2\2\3B\2\3C\3\3H\4";
  public static final ATN _ATN =
    new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
