// Generated from src/java/painless/Painless.g4 by ANTLR 4.5
package painless;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PainlessLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, LBRACK=2, RBRACK=3, LBRACE=4, RBRACE=5, LP=6, RP=7, ASSIGN=8, DOT=9, 
		COMMA=10, SEMICOLON=11, IF=12, ELSE=13, WHILE=14, DO=15, FOR=16, CONTINUE=17, 
		BREAK=18, RETURN=19, BOOLNOT=20, BWNOT=21, MUL=22, DIV=23, REM=24, ADD=25, 
		SUB=26, LSH=27, RSH=28, USH=29, LT=30, LTE=31, GT=32, GTE=33, EQ=34, NE=35, 
		BWAND=36, BWXOR=37, BWOR=38, BOOLAND=39, BOOLOR=40, COND=41, COLON=42, 
		INCR=43, DECR=44, OCTAL=45, HEX=46, INTEGER=47, DECIMAL=48, STRING=49, 
		CHAR=50, TRUE=51, FALSE=52, NULL=53, VOID=54, ID=55;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"WS", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", "ASSIGN", "DOT", 
		"COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
		"BREAK", "RETURN", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", "SUB", 
		"LSH", "RSH", "USH", "LT", "LTE", "GT", "GTE", "EQ", "NE", "BWAND", "BWXOR", 
		"BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", "INCR", "DECR", "OCTAL", 
		"HEX", "INTEGER", "DECIMAL", "STRING", "CHAR", "TRUE", "FALSE", "NULL", 
		"VOID", "ID"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'='", "'.'", "','", 
		"';'", "'if'", "'else'", "'while'", "'do'", "'for'", "'continue'", "'break'", 
		"'return'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", "'<<'", "'>>'", 
		"'>>>'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&'", "'^'", "'|'", 
		"'&&'", "'||'", "'?'", "':'", "'++'", "'--'", null, null, null, null, 
		null, null, "'true'", "'false'", "'null'", "'void'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "WS", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", "ASSIGN", 
		"DOT", "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
		"BREAK", "RETURN", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", "SUB", 
		"LSH", "RSH", "USH", "LT", "LTE", "GT", "GTE", "EQ", "NE", "BWAND", "BWXOR", 
		"BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", "INCR", "DECR", "OCTAL", 
		"HEX", "INTEGER", "DECIMAL", "STRING", "CHAR", "TRUE", "FALSE", "NULL", 
		"VOID", "ID"
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


	public PainlessLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Painless.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\29\u016b\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\3\2\6\2s\n\2\r\2\16\2t\3\2\3\2"+
		"\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32"+
		"\3\32\3\33\3\33\3\34\3\34\3\34\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\37"+
		"\3\37\3 \3 \3 \3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3&\3&\3\'\3\'"+
		"\3(\3(\3(\3)\3)\3)\3*\3*\3+\3+\3,\3,\3,\3-\3-\3-\3.\3.\6.\u00f8\n.\r."+
		"\16.\u00f9\3.\5.\u00fd\n.\3/\3/\3/\6/\u0102\n/\r/\16/\u0103\3/\5/\u0107"+
		"\n/\3\60\3\60\3\60\7\60\u010c\n\60\f\60\16\60\u010f\13\60\5\60\u0111\n"+
		"\60\3\60\5\60\u0114\n\60\3\61\3\61\3\61\7\61\u0119\n\61\f\61\16\61\u011c"+
		"\13\61\5\61\u011e\n\61\3\61\3\61\7\61\u0122\n\61\f\61\16\61\u0125\13\61"+
		"\5\61\u0127\n\61\3\61\3\61\6\61\u012b\n\61\r\61\16\61\u012c\5\61\u012f"+
		"\n\61\3\61\3\61\5\61\u0133\n\61\3\61\6\61\u0136\n\61\r\61\16\61\u0137"+
		"\5\61\u013a\n\61\3\61\5\61\u013d\n\61\3\62\3\62\3\62\3\62\3\62\3\62\7"+
		"\62\u0145\n\62\f\62\16\62\u0148\13\62\3\62\3\62\3\63\3\63\3\63\3\63\3"+
		"\64\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3"+
		"\66\3\66\3\67\3\67\3\67\3\67\3\67\38\38\78\u0167\n8\f8\168\u016a\138\3"+
		"\u0146\29\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67"+
		"\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65"+
		"i\66k\67m8o9\3\2\17\5\2\13\f\17\17\"\"\3\2\629\4\2NNnn\4\2ZZzz\5\2\62"+
		";CHch\3\2\63;\3\2\62;\4\2GGgg\4\2--//\4\2HHhh\4\2$$^^\5\2C\\aac|\6\2\62"+
		";C\\aac|\u0180\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3"+
		"\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2"+
		"\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2"+
		"_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3"+
		"\2\2\2\2m\3\2\2\2\2o\3\2\2\2\3r\3\2\2\2\5x\3\2\2\2\7z\3\2\2\2\t|\3\2\2"+
		"\2\13~\3\2\2\2\r\u0080\3\2\2\2\17\u0082\3\2\2\2\21\u0084\3\2\2\2\23\u0086"+
		"\3\2\2\2\25\u0088\3\2\2\2\27\u008a\3\2\2\2\31\u008c\3\2\2\2\33\u008f\3"+
		"\2\2\2\35\u0094\3\2\2\2\37\u009a\3\2\2\2!\u009d\3\2\2\2#\u00a1\3\2\2\2"+
		"%\u00aa\3\2\2\2\'\u00b0\3\2\2\2)\u00b7\3\2\2\2+\u00b9\3\2\2\2-\u00bb\3"+
		"\2\2\2/\u00bd\3\2\2\2\61\u00bf\3\2\2\2\63\u00c1\3\2\2\2\65\u00c3\3\2\2"+
		"\2\67\u00c5\3\2\2\29\u00c8\3\2\2\2;\u00cb\3\2\2\2=\u00cf\3\2\2\2?\u00d1"+
		"\3\2\2\2A\u00d4\3\2\2\2C\u00d6\3\2\2\2E\u00d9\3\2\2\2G\u00dc\3\2\2\2I"+
		"\u00df\3\2\2\2K\u00e1\3\2\2\2M\u00e3\3\2\2\2O\u00e5\3\2\2\2Q\u00e8\3\2"+
		"\2\2S\u00eb\3\2\2\2U\u00ed\3\2\2\2W\u00ef\3\2\2\2Y\u00f2\3\2\2\2[\u00f5"+
		"\3\2\2\2]\u00fe\3\2\2\2_\u0110\3\2\2\2a\u012e\3\2\2\2c\u013e\3\2\2\2e"+
		"\u014b\3\2\2\2g\u014f\3\2\2\2i\u0154\3\2\2\2k\u015a\3\2\2\2m\u015f\3\2"+
		"\2\2o\u0164\3\2\2\2qs\t\2\2\2rq\3\2\2\2st\3\2\2\2tr\3\2\2\2tu\3\2\2\2"+
		"uv\3\2\2\2vw\b\2\2\2w\4\3\2\2\2xy\7}\2\2y\6\3\2\2\2z{\7\177\2\2{\b\3\2"+
		"\2\2|}\7]\2\2}\n\3\2\2\2~\177\7_\2\2\177\f\3\2\2\2\u0080\u0081\7*\2\2"+
		"\u0081\16\3\2\2\2\u0082\u0083\7+\2\2\u0083\20\3\2\2\2\u0084\u0085\7?\2"+
		"\2\u0085\22\3\2\2\2\u0086\u0087\7\60\2\2\u0087\24\3\2\2\2\u0088\u0089"+
		"\7.\2\2\u0089\26\3\2\2\2\u008a\u008b\7=\2\2\u008b\30\3\2\2\2\u008c\u008d"+
		"\7k\2\2\u008d\u008e\7h\2\2\u008e\32\3\2\2\2\u008f\u0090\7g\2\2\u0090\u0091"+
		"\7n\2\2\u0091\u0092\7u\2\2\u0092\u0093\7g\2\2\u0093\34\3\2\2\2\u0094\u0095"+
		"\7y\2\2\u0095\u0096\7j\2\2\u0096\u0097\7k\2\2\u0097\u0098\7n\2\2\u0098"+
		"\u0099\7g\2\2\u0099\36\3\2\2\2\u009a\u009b\7f\2\2\u009b\u009c\7q\2\2\u009c"+
		" \3\2\2\2\u009d\u009e\7h\2\2\u009e\u009f\7q\2\2\u009f\u00a0\7t\2\2\u00a0"+
		"\"\3\2\2\2\u00a1\u00a2\7e\2\2\u00a2\u00a3\7q\2\2\u00a3\u00a4\7p\2\2\u00a4"+
		"\u00a5\7v\2\2\u00a5\u00a6\7k\2\2\u00a6\u00a7\7p\2\2\u00a7\u00a8\7w\2\2"+
		"\u00a8\u00a9\7g\2\2\u00a9$\3\2\2\2\u00aa\u00ab\7d\2\2\u00ab\u00ac\7t\2"+
		"\2\u00ac\u00ad\7g\2\2\u00ad\u00ae\7c\2\2\u00ae\u00af\7m\2\2\u00af&\3\2"+
		"\2\2\u00b0\u00b1\7t\2\2\u00b1\u00b2\7g\2\2\u00b2\u00b3\7v\2\2\u00b3\u00b4"+
		"\7w\2\2\u00b4\u00b5\7t\2\2\u00b5\u00b6\7p\2\2\u00b6(\3\2\2\2\u00b7\u00b8"+
		"\7#\2\2\u00b8*\3\2\2\2\u00b9\u00ba\7\u0080\2\2\u00ba,\3\2\2\2\u00bb\u00bc"+
		"\7,\2\2\u00bc.\3\2\2\2\u00bd\u00be\7\61\2\2\u00be\60\3\2\2\2\u00bf\u00c0"+
		"\7\'\2\2\u00c0\62\3\2\2\2\u00c1\u00c2\7-\2\2\u00c2\64\3\2\2\2\u00c3\u00c4"+
		"\7/\2\2\u00c4\66\3\2\2\2\u00c5\u00c6\7>\2\2\u00c6\u00c7\7>\2\2\u00c78"+
		"\3\2\2\2\u00c8\u00c9\7@\2\2\u00c9\u00ca\7@\2\2\u00ca:\3\2\2\2\u00cb\u00cc"+
		"\7@\2\2\u00cc\u00cd\7@\2\2\u00cd\u00ce\7@\2\2\u00ce<\3\2\2\2\u00cf\u00d0"+
		"\7>\2\2\u00d0>\3\2\2\2\u00d1\u00d2\7>\2\2\u00d2\u00d3\7?\2\2\u00d3@\3"+
		"\2\2\2\u00d4\u00d5\7@\2\2\u00d5B\3\2\2\2\u00d6\u00d7\7@\2\2\u00d7\u00d8"+
		"\7?\2\2\u00d8D\3\2\2\2\u00d9\u00da\7?\2\2\u00da\u00db\7?\2\2\u00dbF\3"+
		"\2\2\2\u00dc\u00dd\7#\2\2\u00dd\u00de\7?\2\2\u00deH\3\2\2\2\u00df\u00e0"+
		"\7(\2\2\u00e0J\3\2\2\2\u00e1\u00e2\7`\2\2\u00e2L\3\2\2\2\u00e3\u00e4\7"+
		"~\2\2\u00e4N\3\2\2\2\u00e5\u00e6\7(\2\2\u00e6\u00e7\7(\2\2\u00e7P\3\2"+
		"\2\2\u00e8\u00e9\7~\2\2\u00e9\u00ea\7~\2\2\u00eaR\3\2\2\2\u00eb\u00ec"+
		"\7A\2\2\u00ecT\3\2\2\2\u00ed\u00ee\7<\2\2\u00eeV\3\2\2\2\u00ef\u00f0\7"+
		"-\2\2\u00f0\u00f1\7-\2\2\u00f1X\3\2\2\2\u00f2\u00f3\7/\2\2\u00f3\u00f4"+
		"\7/\2\2\u00f4Z\3\2\2\2\u00f5\u00f7\7\62\2\2\u00f6\u00f8\t\3\2\2\u00f7"+
		"\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2"+
		"\2\2\u00fa\u00fc\3\2\2\2\u00fb\u00fd\t\4\2\2\u00fc\u00fb\3\2\2\2\u00fc"+
		"\u00fd\3\2\2\2\u00fd\\\3\2\2\2\u00fe\u00ff\7\62\2\2\u00ff\u0101\t\5\2"+
		"\2\u0100\u0102\t\6\2\2\u0101\u0100\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0101"+
		"\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0106\3\2\2\2\u0105\u0107\t\4\2\2\u0106"+
		"\u0105\3\2\2\2\u0106\u0107\3\2\2\2\u0107^\3\2\2\2\u0108\u0111\7\62\2\2"+
		"\u0109\u010d\t\7\2\2\u010a\u010c\t\b\2\2\u010b\u010a\3\2\2\2\u010c\u010f"+
		"\3\2\2\2\u010d\u010b\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u0111\3\2\2\2\u010f"+
		"\u010d\3\2\2\2\u0110\u0108\3\2\2\2\u0110\u0109\3\2\2\2\u0111\u0113\3\2"+
		"\2\2\u0112\u0114\t\4\2\2\u0113\u0112\3\2\2\2\u0113\u0114\3\2\2\2\u0114"+
		"`\3\2\2\2\u0115\u011e\7\62\2\2\u0116\u011a\t\7\2\2\u0117\u0119\t\b\2\2"+
		"\u0118\u0117\3\2\2\2\u0119\u011c\3\2\2\2\u011a\u0118\3\2\2\2\u011a\u011b"+
		"\3\2\2\2\u011b\u011e\3\2\2\2\u011c\u011a\3\2\2\2\u011d\u0115\3\2\2\2\u011d"+
		"\u0116\3\2\2\2\u011e\u0126\3\2\2\2\u011f\u0123\7\60\2\2\u0120\u0122\t"+
		"\b\2\2\u0121\u0120\3\2\2\2\u0122\u0125\3\2\2\2\u0123\u0121\3\2\2\2\u0123"+
		"\u0124\3\2\2\2\u0124\u0127\3\2\2\2\u0125\u0123\3\2\2\2\u0126\u011f\3\2"+
		"\2\2\u0126\u0127\3\2\2\2\u0127\u012f\3\2\2\2\u0128\u012a\7\60\2\2\u0129"+
		"\u012b\t\b\2\2\u012a\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012a\3\2"+
		"\2\2\u012c\u012d\3\2\2\2\u012d\u012f\3\2\2\2\u012e\u011d\3\2\2\2\u012e"+
		"\u0128\3\2\2\2\u012f\u0139\3\2\2\2\u0130\u0132\t\t\2\2\u0131\u0133\t\n"+
		"\2\2\u0132\u0131\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0135\3\2\2\2\u0134"+
		"\u0136\t\b\2\2\u0135\u0134\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0135\3\2"+
		"\2\2\u0137\u0138\3\2\2\2\u0138\u013a\3\2\2\2\u0139\u0130\3\2\2\2\u0139"+
		"\u013a\3\2\2\2\u013a\u013c\3\2\2\2\u013b\u013d\t\13\2\2\u013c\u013b\3"+
		"\2\2\2\u013c\u013d\3\2\2\2\u013db\3\2\2\2\u013e\u0146\7$\2\2\u013f\u0140"+
		"\7^\2\2\u0140\u0145\7$\2\2\u0141\u0142\7^\2\2\u0142\u0145\7^\2\2\u0143"+
		"\u0145\n\f\2\2\u0144\u013f\3\2\2\2\u0144\u0141\3\2\2\2\u0144\u0143\3\2"+
		"\2\2\u0145\u0148\3\2\2\2\u0146\u0147\3\2\2\2\u0146\u0144\3\2\2\2\u0147"+
		"\u0149\3\2\2\2\u0148\u0146\3\2\2\2\u0149\u014a\7$\2\2\u014ad\3\2\2\2\u014b"+
		"\u014c\7)\2\2\u014c\u014d\13\2\2\2\u014d\u014e\7)\2\2\u014ef\3\2\2\2\u014f"+
		"\u0150\7v\2\2\u0150\u0151\7t\2\2\u0151\u0152\7w\2\2\u0152\u0153\7g\2\2"+
		"\u0153h\3\2\2\2\u0154\u0155\7h\2\2\u0155\u0156\7c\2\2\u0156\u0157\7n\2"+
		"\2\u0157\u0158\7u\2\2\u0158\u0159\7g\2\2\u0159j\3\2\2\2\u015a\u015b\7"+
		"p\2\2\u015b\u015c\7w\2\2\u015c\u015d\7n\2\2\u015d\u015e\7n\2\2\u015el"+
		"\3\2\2\2\u015f\u0160\7x\2\2\u0160\u0161\7q\2\2\u0161\u0162\7k\2\2\u0162"+
		"\u0163\7f\2\2\u0163n\3\2\2\2\u0164\u0168\t\r\2\2\u0165\u0167\t\16\2\2"+
		"\u0166\u0165\3\2\2\2\u0167\u016a\3\2\2\2\u0168\u0166\3\2\2\2\u0168\u0169"+
		"\3\2\2\2\u0169p\3\2\2\2\u016a\u0168\3\2\2\2\30\2t\u00f9\u00fc\u0103\u0106"+
		"\u010d\u0110\u0113\u011a\u011d\u0123\u0126\u012c\u012e\u0132\u0137\u0139"+
		"\u013c\u0144\u0146\u0168\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}