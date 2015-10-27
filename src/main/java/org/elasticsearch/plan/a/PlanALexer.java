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
    ADD=26, SUB=27, LSH=28, RSH=29, USH=30, CAT=31, LT=32, LTE=33, GT=34, 
    GTE=35, EQ=36, EQR=37, NE=38, NER=39, BWAND=40, BWXOR=41, BWOR=42, BOOLAND=43, 
    BOOLOR=44, COND=45, COLON=46, INCR=47, DECR=48, ASSIGN=49, AADD=50, ASUB=51, 
    AMUL=52, ADIV=53, AREM=54, AAND=55, AXOR=56, AOR=57, ALSH=58, ARSH=59, 
    AUSH=60, ACAT=61, OCTAL=62, HEX=63, INTEGER=64, DECIMAL=65, STRING=66, 
    CHAR=67, TRUE=68, FALSE=69, NULL=70, ID=71, GENERIC=72;
  public static String[] modeNames = {
    "DEFAULT_MODE"
  };

  public static final String[] ruleNames = {
    "WS", "COMMENT", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", "DOT", 
    "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
    "BREAK", "RETURN", "NEW", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", 
    "SUB", "LSH", "RSH", "USH", "CAT", "LT", "LTE", "GT", "GTE", "EQ", "EQR", 
    "NE", "NER", "BWAND", "BWXOR", "BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", 
    "INCR", "DECR", "ASSIGN", "AADD", "ASUB", "AMUL", "ADIV", "AREM", "AAND", 
    "AXOR", "AOR", "ALSH", "ARSH", "AUSH", "ACAT", "OCTAL", "HEX", "INTEGER", 
    "DECIMAL", "STRING", "CHAR", "TRUE", "FALSE", "NULL", "ID", "GENERIC"
  };

  private static final String[] _LITERAL_NAMES = {
    null, null, null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'.'", "','", 
    "';'", "'if'", "'else'", "'while'", "'do'", "'for'", "'continue'", "'break'", 
    "'return'", "'new'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", 
    "'<<'", "'>>'", "'>>>'", "'..'", "'<'", "'<='", "'>'", "'>='", "'=='", 
    "'==='", "'!='", "'!=='", "'&'", "'^'", "'|'", "'&&'", "'||'", "'?'", 
    "':'", "'++'", "'--'", "'='", "'+='", "'-='", "'*='", "'/='", "'%='", 
    "'&='", "'^='", "'|='", "'<<='", "'>>='", "'>>>='", "'..='", null, null, 
    null, null, null, null, "'true'", "'false'", "'null'"
  };
  private static final String[] _SYMBOLIC_NAMES = {
    null, "WS", "COMMENT", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", 
    "DOT", "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
    "BREAK", "RETURN", "NEW", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", 
    "SUB", "LSH", "RSH", "USH", "CAT", "LT", "LTE", "GT", "GTE", "EQ", "EQR", 
    "NE", "NER", "BWAND", "BWXOR", "BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", 
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
    case 65:
      STRING_action((RuleContext)_localctx, actionIndex);
      break;
    case 66:
      CHAR_action((RuleContext)_localctx, actionIndex);
      break;
    case 71:
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
    "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2J\u01fa\b\1\4\2\t"+
    "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
    "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
    "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
    ",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
    "\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
    "\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
    "\tI\3\2\6\2\u0095\n\2\r\2\16\2\u0096\3\2\3\2\3\3\3\3\3\3\3\3\7\3\u009f"+
    "\n\3\f\3\16\3\u00a2\13\3\3\3\3\3\3\3\3\3\3\3\7\3\u00a9\n\3\f\3\16\3\u00ac"+
    "\13\3\3\3\3\3\5\3\u00b0\n\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
    "\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3"+
    "\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3"+
    "\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3"+
    "\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\26\3"+
    "\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3"+
    "\35\3\35\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3 \3 \3 \3!\3!\3\"\3\"\3\""+
    "\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3&\3&\3\'\3\'\3\'\3(\3(\3(\3(\3)\3)\3*"+
    "\3*\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\3/\3/\3\60\3\60\3\60\3\61\3\61\3\61"+
    "\3\62\3\62\3\63\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\65\3\66\3\66\3\66"+
    "\3\67\3\67\3\67\38\38\38\39\39\39\3:\3:\3:\3;\3;\3;\3;\3<\3<\3<\3<\3="+
    "\3=\3=\3=\3=\3>\3>\3>\3>\3?\3?\6?\u016b\n?\r?\16?\u016c\3?\5?\u0170\n"+
    "?\3@\3@\3@\6@\u0175\n@\r@\16@\u0176\3@\5@\u017a\n@\3A\3A\3A\7A\u017f\n"+
    "A\fA\16A\u0182\13A\5A\u0184\nA\3A\5A\u0187\nA\3B\3B\3B\7B\u018c\nB\fB"+
    "\16B\u018f\13B\5B\u0191\nB\3B\3B\7B\u0195\nB\fB\16B\u0198\13B\5B\u019a"+
    "\nB\3B\3B\6B\u019e\nB\rB\16B\u019f\5B\u01a2\nB\3B\3B\5B\u01a6\nB\3B\6"+
    "B\u01a9\nB\rB\16B\u01aa\5B\u01ad\nB\3B\5B\u01b0\nB\3C\3C\3C\3C\3C\3C\7"+
    "C\u01b8\nC\fC\16C\u01bb\13C\3C\3C\3C\3D\3D\3D\3D\3D\3E\3E\3E\3E\3E\3F"+
    "\3F\3F\3F\3F\3F\3G\3G\3G\3G\3G\3H\3H\7H\u01d7\nH\fH\16H\u01da\13H\3I\3"+
    "I\3I\5I\u01df\nI\3I\3I\5I\u01e3\nI\3I\5I\u01e6\nI\3I\3I\5I\u01ea\nI\3"+
    "I\3I\5I\u01ee\nI\7I\u01f0\nI\fI\16I\u01f3\13I\3I\5I\u01f6\nI\3I\3I\3I"+
    "\5\u00a0\u00aa\u01b9\2J\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
    "\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63"+
    "\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62"+
    "c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{?}@\177A\u0081B\u0083C\u0085D\u0087"+
    "E\u0089F\u008bG\u008dH\u008fI\u0091J\3\2\17\5\2\13\f\17\17\"\"\3\2\62"+
    "9\4\2NNnn\4\2ZZzz\5\2\62;CHch\3\2\63;\3\2\62;\4\2GGgg\4\2--//\4\2HHhh"+
    "\4\2$$^^\5\2C\\aac|\6\2\62;C\\aac|\u0219\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
    "\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
    "\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
    "\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)"+
    "\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2"+
    "\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2"+
    "A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3"+
    "\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2"+
    "\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2"+
    "g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3"+
    "\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3"+
    "\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2"+
    "\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\2\u008f\3\2\2\2\2\u0091"+
    "\3\2\2\2\3\u0094\3\2\2\2\5\u00af\3\2\2\2\7\u00b3\3\2\2\2\t\u00b5\3\2\2"+
    "\2\13\u00b7\3\2\2\2\r\u00b9\3\2\2\2\17\u00bb\3\2\2\2\21\u00bd\3\2\2\2"+
    "\23\u00bf\3\2\2\2\25\u00c1\3\2\2\2\27\u00c3\3\2\2\2\31\u00c5\3\2\2\2\33"+
    "\u00c8\3\2\2\2\35\u00cd\3\2\2\2\37\u00d3\3\2\2\2!\u00d6\3\2\2\2#\u00da"+
    "\3\2\2\2%\u00e3\3\2\2\2\'\u00e9\3\2\2\2)\u00f0\3\2\2\2+\u00f4\3\2\2\2"+
    "-\u00f6\3\2\2\2/\u00f8\3\2\2\2\61\u00fa\3\2\2\2\63\u00fc\3\2\2\2\65\u00fe"+
    "\3\2\2\2\67\u0100\3\2\2\29\u0102\3\2\2\2;\u0105\3\2\2\2=\u0108\3\2\2\2"+
    "?\u010c\3\2\2\2A\u010f\3\2\2\2C\u0111\3\2\2\2E\u0114\3\2\2\2G\u0116\3"+
    "\2\2\2I\u0119\3\2\2\2K\u011c\3\2\2\2M\u0120\3\2\2\2O\u0123\3\2\2\2Q\u0127"+
    "\3\2\2\2S\u0129\3\2\2\2U\u012b\3\2\2\2W\u012d\3\2\2\2Y\u0130\3\2\2\2["+
    "\u0133\3\2\2\2]\u0135\3\2\2\2_\u0137\3\2\2\2a\u013a\3\2\2\2c\u013d\3\2"+
    "\2\2e\u013f\3\2\2\2g\u0142\3\2\2\2i\u0145\3\2\2\2k\u0148\3\2\2\2m\u014b"+
    "\3\2\2\2o\u014e\3\2\2\2q\u0151\3\2\2\2s\u0154\3\2\2\2u\u0157\3\2\2\2w"+
    "\u015b\3\2\2\2y\u015f\3\2\2\2{\u0164\3\2\2\2}\u0168\3\2\2\2\177\u0171"+
    "\3\2\2\2\u0081\u0183\3\2\2\2\u0083\u01a1\3\2\2\2\u0085\u01b1\3\2\2\2\u0087"+
    "\u01bf\3\2\2\2\u0089\u01c4\3\2\2\2\u008b\u01c9\3\2\2\2\u008d\u01cf\3\2"+
    "\2\2\u008f\u01d4\3\2\2\2\u0091\u01db\3\2\2\2\u0093\u0095\t\2\2\2\u0094"+
    "\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2"+
    "\2\2\u0097\u0098\3\2\2\2\u0098\u0099\b\2\2\2\u0099\4\3\2\2\2\u009a\u009b"+
    "\7\61\2\2\u009b\u009c\7\61\2\2\u009c\u00a0\3\2\2\2\u009d\u009f\13\2\2"+
    "\2\u009e\u009d\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a0\u009e"+
    "\3\2\2\2\u00a1\u00a3\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a3\u00b0\7\f\2\2\u00a4"+
    "\u00a5\7\61\2\2\u00a5\u00a6\7,\2\2\u00a6\u00aa\3\2\2\2\u00a7\u00a9\13"+
    "\2\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00ac\3\2\2\2\u00aa\u00ab\3\2\2\2\u00aa"+
    "\u00a8\3\2\2\2\u00ab\u00ad\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ad\u00ae\7,"+
    "\2\2\u00ae\u00b0\7\61\2\2\u00af\u009a\3\2\2\2\u00af\u00a4\3\2\2\2\u00b0"+
    "\u00b1\3\2\2\2\u00b1\u00b2\b\3\2\2\u00b2\6\3\2\2\2\u00b3\u00b4\7}\2\2"+
    "\u00b4\b\3\2\2\2\u00b5\u00b6\7\177\2\2\u00b6\n\3\2\2\2\u00b7\u00b8\7]"+
    "\2\2\u00b8\f\3\2\2\2\u00b9\u00ba\7_\2\2\u00ba\16\3\2\2\2\u00bb\u00bc\7"+
    "*\2\2\u00bc\20\3\2\2\2\u00bd\u00be\7+\2\2\u00be\22\3\2\2\2\u00bf\u00c0"+
    "\7\60\2\2\u00c0\24\3\2\2\2\u00c1\u00c2\7.\2\2\u00c2\26\3\2\2\2\u00c3\u00c4"+
    "\7=\2\2\u00c4\30\3\2\2\2\u00c5\u00c6\7k\2\2\u00c6\u00c7\7h\2\2\u00c7\32"+
    "\3\2\2\2\u00c8\u00c9\7g\2\2\u00c9\u00ca\7n\2\2\u00ca\u00cb\7u\2\2\u00cb"+
    "\u00cc\7g\2\2\u00cc\34\3\2\2\2\u00cd\u00ce\7y\2\2\u00ce\u00cf\7j\2\2\u00cf"+
    "\u00d0\7k\2\2\u00d0\u00d1\7n\2\2\u00d1\u00d2\7g\2\2\u00d2\36\3\2\2\2\u00d3"+
    "\u00d4\7f\2\2\u00d4\u00d5\7q\2\2\u00d5 \3\2\2\2\u00d6\u00d7\7h\2\2\u00d7"+
    "\u00d8\7q\2\2\u00d8\u00d9\7t\2\2\u00d9\"\3\2\2\2\u00da\u00db\7e\2\2\u00db"+
    "\u00dc\7q\2\2\u00dc\u00dd\7p\2\2\u00dd\u00de\7v\2\2\u00de\u00df\7k\2\2"+
    "\u00df\u00e0\7p\2\2\u00e0\u00e1\7w\2\2\u00e1\u00e2\7g\2\2\u00e2$\3\2\2"+
    "\2\u00e3\u00e4\7d\2\2\u00e4\u00e5\7t\2\2\u00e5\u00e6\7g\2\2\u00e6\u00e7"+
    "\7c\2\2\u00e7\u00e8\7m\2\2\u00e8&\3\2\2\2\u00e9\u00ea\7t\2\2\u00ea\u00eb"+
    "\7g\2\2\u00eb\u00ec\7v\2\2\u00ec\u00ed\7w\2\2\u00ed\u00ee\7t\2\2\u00ee"+
    "\u00ef\7p\2\2\u00ef(\3\2\2\2\u00f0\u00f1\7p\2\2\u00f1\u00f2\7g\2\2\u00f2"+
    "\u00f3\7y\2\2\u00f3*\3\2\2\2\u00f4\u00f5\7#\2\2\u00f5,\3\2\2\2\u00f6\u00f7"+
    "\7\u0080\2\2\u00f7.\3\2\2\2\u00f8\u00f9\7,\2\2\u00f9\60\3\2\2\2\u00fa"+
    "\u00fb\7\61\2\2\u00fb\62\3\2\2\2\u00fc\u00fd\7\'\2\2\u00fd\64\3\2\2\2"+
    "\u00fe\u00ff\7-\2\2\u00ff\66\3\2\2\2\u0100\u0101\7/\2\2\u01018\3\2\2\2"+
    "\u0102\u0103\7>\2\2\u0103\u0104\7>\2\2\u0104:\3\2\2\2\u0105\u0106\7@\2"+
    "\2\u0106\u0107\7@\2\2\u0107<\3\2\2\2\u0108\u0109\7@\2\2\u0109\u010a\7"+
    "@\2\2\u010a\u010b\7@\2\2\u010b>\3\2\2\2\u010c\u010d\7\60\2\2\u010d\u010e"+
    "\7\60\2\2\u010e@\3\2\2\2\u010f\u0110\7>\2\2\u0110B\3\2\2\2\u0111\u0112"+
    "\7>\2\2\u0112\u0113\7?\2\2\u0113D\3\2\2\2\u0114\u0115\7@\2\2\u0115F\3"+
    "\2\2\2\u0116\u0117\7@\2\2\u0117\u0118\7?\2\2\u0118H\3\2\2\2\u0119\u011a"+
    "\7?\2\2\u011a\u011b\7?\2\2\u011bJ\3\2\2\2\u011c\u011d\7?\2\2\u011d\u011e"+
    "\7?\2\2\u011e\u011f\7?\2\2\u011fL\3\2\2\2\u0120\u0121\7#\2\2\u0121\u0122"+
    "\7?\2\2\u0122N\3\2\2\2\u0123\u0124\7#\2\2\u0124\u0125\7?\2\2\u0125\u0126"+
    "\7?\2\2\u0126P\3\2\2\2\u0127\u0128\7(\2\2\u0128R\3\2\2\2\u0129\u012a\7"+
    "`\2\2\u012aT\3\2\2\2\u012b\u012c\7~\2\2\u012cV\3\2\2\2\u012d\u012e\7("+
    "\2\2\u012e\u012f\7(\2\2\u012fX\3\2\2\2\u0130\u0131\7~\2\2\u0131\u0132"+
    "\7~\2\2\u0132Z\3\2\2\2\u0133\u0134\7A\2\2\u0134\\\3\2\2\2\u0135\u0136"+
    "\7<\2\2\u0136^\3\2\2\2\u0137\u0138\7-\2\2\u0138\u0139\7-\2\2\u0139`\3"+
    "\2\2\2\u013a\u013b\7/\2\2\u013b\u013c\7/\2\2\u013cb\3\2\2\2\u013d\u013e"+
    "\7?\2\2\u013ed\3\2\2\2\u013f\u0140\7-\2\2\u0140\u0141\7?\2\2\u0141f\3"+
    "\2\2\2\u0142\u0143\7/\2\2\u0143\u0144\7?\2\2\u0144h\3\2\2\2\u0145\u0146"+
    "\7,\2\2\u0146\u0147\7?\2\2\u0147j\3\2\2\2\u0148\u0149\7\61\2\2\u0149\u014a"+
    "\7?\2\2\u014al\3\2\2\2\u014b\u014c\7\'\2\2\u014c\u014d\7?\2\2\u014dn\3"+
    "\2\2\2\u014e\u014f\7(\2\2\u014f\u0150\7?\2\2\u0150p\3\2\2\2\u0151\u0152"+
    "\7`\2\2\u0152\u0153\7?\2\2\u0153r\3\2\2\2\u0154\u0155\7~\2\2\u0155\u0156"+
    "\7?\2\2\u0156t\3\2\2\2\u0157\u0158\7>\2\2\u0158\u0159\7>\2\2\u0159\u015a"+
    "\7?\2\2\u015av\3\2\2\2\u015b\u015c\7@\2\2\u015c\u015d\7@\2\2\u015d\u015e"+
    "\7?\2\2\u015ex\3\2\2\2\u015f\u0160\7@\2\2\u0160\u0161\7@\2\2\u0161\u0162"+
    "\7@\2\2\u0162\u0163\7?\2\2\u0163z\3\2\2\2\u0164\u0165\7\60\2\2\u0165\u0166"+
    "\7\60\2\2\u0166\u0167\7?\2\2\u0167|\3\2\2\2\u0168\u016a\7\62\2\2\u0169"+
    "\u016b\t\3\2\2\u016a\u0169\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u016a\3\2"+
    "\2\2\u016c\u016d\3\2\2\2\u016d\u016f\3\2\2\2\u016e\u0170\t\4\2\2\u016f"+
    "\u016e\3\2\2\2\u016f\u0170\3\2\2\2\u0170~\3\2\2\2\u0171\u0172\7\62\2\2"+
    "\u0172\u0174\t\5\2\2\u0173\u0175\t\6\2\2\u0174\u0173\3\2\2\2\u0175\u0176"+
    "\3\2\2\2\u0176\u0174\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0179\3\2\2\2\u0178"+
    "\u017a\t\4\2\2\u0179\u0178\3\2\2\2\u0179\u017a\3\2\2\2\u017a\u0080\3\2"+
    "\2\2\u017b\u0184\7\62\2\2\u017c\u0180\t\7\2\2\u017d\u017f\t\b\2\2\u017e"+
    "\u017d\3\2\2\2\u017f\u0182\3\2\2\2\u0180\u017e\3\2\2\2\u0180\u0181\3\2"+
    "\2\2\u0181\u0184\3\2\2\2\u0182\u0180\3\2\2\2\u0183\u017b\3\2\2\2\u0183"+
    "\u017c\3\2\2\2\u0184\u0186\3\2\2\2\u0185\u0187\t\4\2\2\u0186\u0185\3\2"+
    "\2\2\u0186\u0187\3\2\2\2\u0187\u0082\3\2\2\2\u0188\u0191\7\62\2\2\u0189"+
    "\u018d\t\7\2\2\u018a\u018c\t\b\2\2\u018b\u018a\3\2\2\2\u018c\u018f\3\2"+
    "\2\2\u018d\u018b\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u0191\3\2\2\2\u018f"+
    "\u018d\3\2\2\2\u0190\u0188\3\2\2\2\u0190\u0189\3\2\2\2\u0191\u0199\3\2"+
    "\2\2\u0192\u0196\7\60\2\2\u0193\u0195\t\b\2\2\u0194\u0193\3\2\2\2\u0195"+
    "\u0198\3\2\2\2\u0196\u0194\3\2\2\2\u0196\u0197\3\2\2\2\u0197\u019a\3\2"+
    "\2\2\u0198\u0196\3\2\2\2\u0199\u0192\3\2\2\2\u0199\u019a\3\2\2\2\u019a"+
    "\u01a2\3\2\2\2\u019b\u019d\7\60\2\2\u019c\u019e\t\b\2\2\u019d\u019c\3"+
    "\2\2\2\u019e\u019f\3\2\2\2\u019f\u019d\3\2\2\2\u019f\u01a0\3\2\2\2\u01a0"+
    "\u01a2\3\2\2\2\u01a1\u0190\3\2\2\2\u01a1\u019b\3\2\2\2\u01a2\u01ac\3\2"+
    "\2\2\u01a3\u01a5\t\t\2\2\u01a4\u01a6\t\n\2\2\u01a5\u01a4\3\2\2\2\u01a5"+
    "\u01a6\3\2\2\2\u01a6\u01a8\3\2\2\2\u01a7\u01a9\t\b\2\2\u01a8\u01a7\3\2"+
    "\2\2\u01a9\u01aa\3\2\2\2\u01aa\u01a8\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab"+
    "\u01ad\3\2\2\2\u01ac\u01a3\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01af\3\2"+
    "\2\2\u01ae\u01b0\t\13\2\2\u01af\u01ae\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0"+
    "\u0084\3\2\2\2\u01b1\u01b9\7$\2\2\u01b2\u01b3\7^\2\2\u01b3\u01b8\7$\2"+
    "\2\u01b4\u01b5\7^\2\2\u01b5\u01b8\7^\2\2\u01b6\u01b8\n\f\2\2\u01b7\u01b2"+
    "\3\2\2\2\u01b7\u01b4\3\2\2\2\u01b7\u01b6\3\2\2\2\u01b8\u01bb\3\2\2\2\u01b9"+
    "\u01ba\3\2\2\2\u01b9\u01b7\3\2\2\2\u01ba\u01bc\3\2\2\2\u01bb\u01b9\3\2"+
    "\2\2\u01bc\u01bd\7$\2\2\u01bd\u01be\bC\3\2\u01be\u0086\3\2\2\2\u01bf\u01c0"+
    "\7)\2\2\u01c0\u01c1\13\2\2\2\u01c1\u01c2\7)\2\2\u01c2\u01c3\bD\4\2\u01c3"+
    "\u0088\3\2\2\2\u01c4\u01c5\7v\2\2\u01c5\u01c6\7t\2\2\u01c6\u01c7\7w\2"+
    "\2\u01c7\u01c8\7g\2\2\u01c8\u008a\3\2\2\2\u01c9\u01ca\7h\2\2\u01ca\u01cb"+
    "\7c\2\2\u01cb\u01cc\7n\2\2\u01cc\u01cd\7u\2\2\u01cd\u01ce\7g\2\2\u01ce"+
    "\u008c\3\2\2\2\u01cf\u01d0\7p\2\2\u01d0\u01d1\7w\2\2\u01d1\u01d2\7n\2"+
    "\2\u01d2\u01d3\7n\2\2\u01d3\u008e\3\2\2\2\u01d4\u01d8\t\r\2\2\u01d5\u01d7"+
    "\t\16\2\2\u01d6\u01d5\3\2\2\2\u01d7\u01da\3\2\2\2\u01d8\u01d6\3\2\2\2"+
    "\u01d8\u01d9\3\2\2\2\u01d9\u0090\3\2\2\2\u01da\u01d8\3\2\2\2\u01db\u01dc"+
    "\5\u008fH\2\u01dc\u01de\7>\2\2\u01dd\u01df\5\3\2\2\u01de\u01dd\3\2\2\2"+
    "\u01de\u01df\3\2\2\2\u01df\u01e2\3\2\2\2\u01e0\u01e3\5\u008fH\2\u01e1"+
    "\u01e3\5\u0091I\2\u01e2\u01e0\3\2\2\2\u01e2\u01e1\3\2\2\2\u01e3\u01e5"+
    "\3\2\2\2\u01e4\u01e6\5\3\2\2\u01e5\u01e4\3\2\2\2\u01e5\u01e6\3\2\2\2\u01e6"+
    "\u01f1\3\2\2\2\u01e7\u01e9\5\25\13\2\u01e8\u01ea\5\3\2\2\u01e9\u01e8\3"+
    "\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01ed\3\2\2\2\u01eb\u01ee\5\u008fH\2"+
    "\u01ec\u01ee\5\u0091I\2\u01ed\u01eb\3\2\2\2\u01ed\u01ec\3\2\2\2\u01ee"+
    "\u01f0\3\2\2\2\u01ef\u01e7\3\2\2\2\u01f0\u01f3\3\2\2\2\u01f1\u01ef\3\2"+
    "\2\2\u01f1\u01f2\3\2\2\2\u01f2\u01f5\3\2\2\2\u01f3\u01f1\3\2\2\2\u01f4"+
    "\u01f6\5\3\2\2\u01f5\u01f4\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f7\3\2"+
    "\2\2\u01f7\u01f8\7@\2\2\u01f8\u01f9\bI\5\2\u01f9\u0092\3\2\2\2\"\2\u0096"+
    "\u00a0\u00aa\u00af\u016c\u016f\u0176\u0179\u0180\u0183\u0186\u018d\u0190"+
    "\u0196\u0199\u019f\u01a1\u01a5\u01aa\u01ac\u01af\u01b7\u01b9\u01d8\u01de"+
    "\u01e2\u01e5\u01e9\u01ed\u01f1\u01f5\6\b\2\2\3C\2\3D\3\3I\4";
  public static final ATN _ATN =
    new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
