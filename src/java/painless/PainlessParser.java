// Generated from src/java/painless/Painless.g4 by ANTLR 4.5
package painless;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PainlessParser extends Parser {
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
		OCTAL=43, HEX=44, INTEGER=45, DECIMAL=46, STRING=47, CHAR=48, TRUE=49, 
		FALSE=50, NULL=51, VOID=52, TYPE=53, ID=54;
	public static final int
		RULE_source = 0, RULE_statement = 1, RULE_block = 2, RULE_empty = 3, RULE_declaration = 4, 
		RULE_decltype = 5, RULE_expression = 6, RULE_extstart = 7, RULE_extprec = 8, 
		RULE_extcast = 9, RULE_extarray = 10, RULE_extdot = 11, RULE_exttype = 12, 
		RULE_extcall = 13, RULE_extmember = 14, RULE_arguments = 15;
	public static final String[] ruleNames = {
		"source", "statement", "block", "empty", "declaration", "decltype", "expression", 
		"extstart", "extprec", "extcast", "extarray", "extdot", "exttype", "extcall", 
		"extmember", "arguments"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'='", "'.'", "','", 
		"';'", "'if'", "'else'", "'while'", "'do'", "'for'", "'continue'", "'break'", 
		"'return'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", "'<<'", "'>>'", 
		"'>>>'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&'", "'^'", "'|'", 
		"'&&'", "'||'", "'?'", "':'", null, null, null, null, null, null, "'true'", 
		"'false'", "'null'", "'void'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "WS", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", "ASSIGN", 
		"DOT", "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
		"BREAK", "RETURN", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", "SUB", 
		"LSH", "RSH", "USH", "LT", "LTE", "GT", "GTE", "EQ", "NE", "BWAND", "BWXOR", 
		"BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", "OCTAL", "HEX", "INTEGER", 
		"DECIMAL", "STRING", "CHAR", "TRUE", "FALSE", "NULL", "VOID", "TYPE", 
		"ID"
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

	@Override
	public String getGrammarFileName() { return "Painless.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PainlessParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SourceContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(PainlessParser.EOF, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public SourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_source; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitSource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SourceContext source() throws RecognitionException {
		SourceContext _localctx = new SourceContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_source);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(32);
				statement();
				}
				}
				setState(35); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LP) | (1L << IF) | (1L << WHILE) | (1L << DO) | (1L << FOR) | (1L << CONTINUE) | (1L << BREAK) | (1L << RETURN) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << TYPE) | (1L << ID))) != 0) );
			setState(37);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DeclContext extends StatementContext {
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
		public DeclContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitDecl(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BreakContext extends StatementContext {
		public TerminalNode BREAK() { return getToken(PainlessParser.BREAK, 0); }
		public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
		public BreakContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitBreak(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ContinueContext extends StatementContext {
		public TerminalNode CONTINUE() { return getToken(PainlessParser.CONTINUE, 0); }
		public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
		public ContinueContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitContinue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ForContext extends StatementContext {
		public TerminalNode FOR() { return getToken(PainlessParser.FOR, 0); }
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public List<TerminalNode> SEMICOLON() { return getTokens(PainlessParser.SEMICOLON); }
		public TerminalNode SEMICOLON(int i) {
			return getToken(PainlessParser.SEMICOLON, i);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public EmptyContext empty() {
			return getRuleContext(EmptyContext.class,0);
		}
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ForContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitFor(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprContext extends StatementContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
		public ExprContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DoContext extends StatementContext {
		public TerminalNode DO() { return getToken(PainlessParser.DO, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode WHILE() { return getToken(PainlessParser.WHILE, 0); }
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public DoContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitDo(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhileContext extends StatementContext {
		public TerminalNode WHILE() { return getToken(PainlessParser.WHILE, 0); }
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public EmptyContext empty() {
			return getRuleContext(EmptyContext.class,0);
		}
		public WhileContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitWhile(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfContext extends StatementContext {
		public TerminalNode IF() { return getToken(PainlessParser.IF, 0); }
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(PainlessParser.ELSE, 0); }
		public IfContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitIf(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReturnContext extends StatementContext {
		public TerminalNode RETURN() { return getToken(PainlessParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
		public ReturnContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitReturn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		int _la;
		try {
			setState(95);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new IfContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(39);
				match(IF);
				setState(40);
				match(LP);
				setState(41);
				expression(0);
				setState(42);
				match(RP);
				setState(43);
				block();
				setState(46);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(44);
					match(ELSE);
					setState(45);
					block();
					}
					break;
				}
				}
				break;
			case 2:
				_localctx = new WhileContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(48);
				match(WHILE);
				setState(49);
				match(LP);
				setState(50);
				expression(0);
				setState(51);
				match(RP);
				setState(54);
				switch (_input.LA(1)) {
				case LBRACK:
				case LP:
				case IF:
				case WHILE:
				case DO:
				case FOR:
				case CONTINUE:
				case BREAK:
				case RETURN:
				case BOOLNOT:
				case BWNOT:
				case ADD:
				case SUB:
				case OCTAL:
				case HEX:
				case INTEGER:
				case DECIMAL:
				case STRING:
				case CHAR:
				case TRUE:
				case FALSE:
				case NULL:
				case TYPE:
				case ID:
					{
					setState(52);
					block();
					}
					break;
				case SEMICOLON:
					{
					setState(53);
					empty();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 3:
				_localctx = new DoContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(56);
				match(DO);
				setState(57);
				block();
				setState(58);
				match(WHILE);
				setState(59);
				match(LP);
				setState(60);
				expression(0);
				setState(61);
				match(RP);
				}
				break;
			case 4:
				_localctx = new ForContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(63);
				match(FOR);
				setState(64);
				match(LP);
				setState(66);
				_la = _input.LA(1);
				if (_la==TYPE) {
					{
					setState(65);
					declaration();
					}
				}

				setState(68);
				match(SEMICOLON);
				setState(70);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LP) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << TYPE) | (1L << ID))) != 0)) {
					{
					setState(69);
					expression(0);
					}
				}

				setState(72);
				match(SEMICOLON);
				setState(74);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LP) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << TYPE) | (1L << ID))) != 0)) {
					{
					setState(73);
					expression(0);
					}
				}

				setState(76);
				match(RP);
				setState(79);
				switch (_input.LA(1)) {
				case LBRACK:
				case LP:
				case IF:
				case WHILE:
				case DO:
				case FOR:
				case CONTINUE:
				case BREAK:
				case RETURN:
				case BOOLNOT:
				case BWNOT:
				case ADD:
				case SUB:
				case OCTAL:
				case HEX:
				case INTEGER:
				case DECIMAL:
				case STRING:
				case CHAR:
				case TRUE:
				case FALSE:
				case NULL:
				case TYPE:
				case ID:
					{
					setState(77);
					block();
					}
					break;
				case SEMICOLON:
					{
					setState(78);
					empty();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 5:
				_localctx = new DeclContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(81);
				declaration();
				setState(82);
				match(SEMICOLON);
				}
				break;
			case 6:
				_localctx = new ContinueContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(84);
				match(CONTINUE);
				setState(85);
				match(SEMICOLON);
				}
				break;
			case 7:
				_localctx = new BreakContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(86);
				match(BREAK);
				setState(87);
				match(SEMICOLON);
				}
				break;
			case 8:
				_localctx = new ReturnContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(88);
				match(RETURN);
				setState(89);
				expression(0);
				setState(90);
				match(SEMICOLON);
				}
				break;
			case 9:
				_localctx = new ExprContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(92);
				expression(0);
				setState(93);
				match(SEMICOLON);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
	 
		public BlockContext() { }
		public void copyFrom(BlockContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SingleContext extends BlockContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public SingleContext(BlockContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitSingle(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MultipleContext extends BlockContext {
		public TerminalNode LBRACK() { return getToken(PainlessParser.LBRACK, 0); }
		public TerminalNode RBRACK() { return getToken(PainlessParser.RBRACK, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public MultipleContext(BlockContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitMultiple(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_block);
		int _la;
		try {
			setState(106);
			switch (_input.LA(1)) {
			case LBRACK:
				_localctx = new MultipleContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(97);
				match(LBRACK);
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LP) | (1L << IF) | (1L << WHILE) | (1L << DO) | (1L << FOR) | (1L << CONTINUE) | (1L << BREAK) | (1L << RETURN) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << TYPE) | (1L << ID))) != 0)) {
					{
					{
					setState(98);
					statement();
					}
					}
					setState(103);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(104);
				match(RBRACK);
				}
				break;
			case LP:
			case IF:
			case WHILE:
			case DO:
			case FOR:
			case CONTINUE:
			case BREAK:
			case RETURN:
			case BOOLNOT:
			case BWNOT:
			case ADD:
			case SUB:
			case OCTAL:
			case HEX:
			case INTEGER:
			case DECIMAL:
			case STRING:
			case CHAR:
			case TRUE:
			case FALSE:
			case NULL:
			case TYPE:
			case ID:
				_localctx = new SingleContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(105);
				statement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EmptyContext extends ParserRuleContext {
		public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
		public EmptyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_empty; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitEmpty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EmptyContext empty() throws RecognitionException {
		EmptyContext _localctx = new EmptyContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_empty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclarationContext extends ParserRuleContext {
		public DecltypeContext decltype() {
			return getRuleContext(DecltypeContext.class,0);
		}
		public List<TerminalNode> ID() { return getTokens(PainlessParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(PainlessParser.ID, i);
		}
		public List<TerminalNode> ASSIGN() { return getTokens(PainlessParser.ASSIGN); }
		public TerminalNode ASSIGN(int i) {
			return getToken(PainlessParser.ASSIGN, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PainlessParser.COMMA, i);
		}
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			decltype();
			setState(111);
			match(ID);
			setState(114);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(112);
				match(ASSIGN);
				setState(113);
				expression(0);
				}
			}

			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(116);
				match(COMMA);
				setState(117);
				match(ID);
				setState(120);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(118);
					match(ASSIGN);
					setState(119);
					expression(0);
					}
				}

				}
				}
				setState(126);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DecltypeContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(PainlessParser.TYPE, 0); }
		public List<TerminalNode> LBRACE() { return getTokens(PainlessParser.LBRACE); }
		public TerminalNode LBRACE(int i) {
			return getToken(PainlessParser.LBRACE, i);
		}
		public List<TerminalNode> RBRACE() { return getTokens(PainlessParser.RBRACE); }
		public TerminalNode RBRACE(int i) {
			return getToken(PainlessParser.RBRACE, i);
		}
		public DecltypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decltype; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitDecltype(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DecltypeContext decltype() throws RecognitionException {
		DecltypeContext _localctx = new DecltypeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_decltype);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(TYPE);
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACE) {
				{
				{
				setState(128);
				match(LBRACE);
				setState(129);
				match(RBRACE);
				}
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExtContext extends ExpressionContext {
		public ExtstartContext extstart() {
			return getRuleContext(ExtstartContext.class,0);
		}
		public ExtContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CompContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode LT() { return getToken(PainlessParser.LT, 0); }
		public TerminalNode LTE() { return getToken(PainlessParser.LTE, 0); }
		public TerminalNode GT() { return getToken(PainlessParser.GT, 0); }
		public TerminalNode GTE() { return getToken(PainlessParser.GTE, 0); }
		public TerminalNode EQ() { return getToken(PainlessParser.EQ, 0); }
		public TerminalNode NE() { return getToken(PainlessParser.NE, 0); }
		public CompContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitComp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringContext extends ExpressionContext {
		public TerminalNode STRING() { return getToken(PainlessParser.STRING, 0); }
		public StringContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitString(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BoolContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode BOOLAND() { return getToken(PainlessParser.BOOLAND, 0); }
		public TerminalNode BOOLOR() { return getToken(PainlessParser.BOOLOR, 0); }
		public BoolContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitBool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConditionalContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode COND() { return getToken(PainlessParser.COND, 0); }
		public TerminalNode COLON() { return getToken(PainlessParser.COLON, 0); }
		public ConditionalContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitConditional(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignmentContext extends ExpressionContext {
		public ExtstartContext extstart() {
			return getRuleContext(ExtstartContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(PainlessParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FalseContext extends ExpressionContext {
		public TerminalNode FALSE() { return getToken(PainlessParser.FALSE, 0); }
		public FalseContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitFalse(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumericContext extends ExpressionContext {
		public TerminalNode OCTAL() { return getToken(PainlessParser.OCTAL, 0); }
		public TerminalNode HEX() { return getToken(PainlessParser.HEX, 0); }
		public TerminalNode INTEGER() { return getToken(PainlessParser.INTEGER, 0); }
		public TerminalNode DECIMAL() { return getToken(PainlessParser.DECIMAL, 0); }
		public NumericContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitNumeric(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnaryContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode BOOLNOT() { return getToken(PainlessParser.BOOLNOT, 0); }
		public TerminalNode BWNOT() { return getToken(PainlessParser.BWNOT, 0); }
		public TerminalNode ADD() { return getToken(PainlessParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(PainlessParser.SUB, 0); }
		public UnaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitUnary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrecedenceContext extends ExpressionContext {
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public PrecedenceContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitPrecedence(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CastContext extends ExpressionContext {
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public DecltypeContext decltype() {
			return getRuleContext(DecltypeContext.class,0);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public CastContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitCast(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullContext extends ExpressionContext {
		public TerminalNode NULL() { return getToken(PainlessParser.NULL, 0); }
		public NullContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitNull(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode MUL() { return getToken(PainlessParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(PainlessParser.DIV, 0); }
		public TerminalNode REM() { return getToken(PainlessParser.REM, 0); }
		public TerminalNode ADD() { return getToken(PainlessParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(PainlessParser.SUB, 0); }
		public TerminalNode LSH() { return getToken(PainlessParser.LSH, 0); }
		public TerminalNode RSH() { return getToken(PainlessParser.RSH, 0); }
		public TerminalNode USH() { return getToken(PainlessParser.USH, 0); }
		public TerminalNode BWAND() { return getToken(PainlessParser.BWAND, 0); }
		public TerminalNode BWXOR() { return getToken(PainlessParser.BWXOR, 0); }
		public TerminalNode BWOR() { return getToken(PainlessParser.BWOR, 0); }
		public BinaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitBinary(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CharContext extends ExpressionContext {
		public TerminalNode CHAR() { return getToken(PainlessParser.CHAR, 0); }
		public CharContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitChar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TrueContext extends ExpressionContext {
		public TerminalNode TRUE() { return getToken(PainlessParser.TRUE, 0); }
		public TrueContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitTrue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				_localctx = new UnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(136);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(137);
				expression(14);
				}
				break;
			case 2:
				{
				_localctx = new CastContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(138);
				match(LP);
				setState(139);
				decltype();
				setState(140);
				match(RP);
				setState(141);
				expression(13);
				}
				break;
			case 3:
				{
				_localctx = new AssignmentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(143);
				extstart();
				setState(144);
				match(ASSIGN);
				setState(145);
				expression(1);
				}
				break;
			case 4:
				{
				_localctx = new PrecedenceContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(147);
				match(LP);
				setState(148);
				expression(0);
				setState(149);
				match(RP);
				}
				break;
			case 5:
				{
				_localctx = new NumericContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(151);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case 6:
				{
				_localctx = new StringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(152);
				match(STRING);
				}
				break;
			case 7:
				{
				_localctx = new CharContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(153);
				match(CHAR);
				}
				break;
			case 8:
				{
				_localctx = new TrueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(154);
				match(TRUE);
				}
				break;
			case 9:
				{
				_localctx = new FalseContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(155);
				match(FALSE);
				}
				break;
			case 10:
				{
				_localctx = new NullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(156);
				match(NULL);
				}
				break;
			case 11:
				{
				_localctx = new ExtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(157);
				extstart();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(198);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(196);
					switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(160);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(161);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << REM))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(162);
						expression(13);
						}
						break;
					case 2:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(163);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(164);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(165);
						expression(12);
						}
						break;
					case 3:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(166);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(167);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSH) | (1L << RSH) | (1L << USH))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(168);
						expression(11);
						}
						break;
					case 4:
						{
						_localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(169);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(170);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LTE) | (1L << GT) | (1L << GTE))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(171);
						expression(10);
						}
						break;
					case 5:
						{
						_localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(172);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(173);
						_la = _input.LA(1);
						if ( !(_la==EQ || _la==NE) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(174);
						expression(9);
						}
						break;
					case 6:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(175);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(176);
						match(BWAND);
						setState(177);
						expression(8);
						}
						break;
					case 7:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(178);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(179);
						match(BWXOR);
						setState(180);
						expression(7);
						}
						break;
					case 8:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(181);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(182);
						match(BWOR);
						setState(183);
						expression(6);
						}
						break;
					case 9:
						{
						_localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(184);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(185);
						match(BOOLAND);
						setState(186);
						expression(5);
						}
						break;
					case 10:
						{
						_localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(187);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(188);
						match(BOOLOR);
						setState(189);
						expression(4);
						}
						break;
					case 11:
						{
						_localctx = new ConditionalContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(190);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(191);
						match(COND);
						setState(192);
						expression(0);
						setState(193);
						match(COLON);
						setState(194);
						expression(2);
						}
						break;
					}
					} 
				}
				setState(200);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExtstartContext extends ParserRuleContext {
		public ExtprecContext extprec() {
			return getRuleContext(ExtprecContext.class,0);
		}
		public ExtcastContext extcast() {
			return getRuleContext(ExtcastContext.class,0);
		}
		public ExttypeContext exttype() {
			return getRuleContext(ExttypeContext.class,0);
		}
		public ExtmemberContext extmember() {
			return getRuleContext(ExtmemberContext.class,0);
		}
		public ExtstartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extstart; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtstart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtstartContext extstart() throws RecognitionException {
		ExtstartContext _localctx = new ExtstartContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_extstart);
		try {
			setState(205);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(201);
				extprec();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(202);
				extcast();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(203);
				exttype();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(204);
				extmember();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExtprecContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public ExtprecContext extprec() {
			return getRuleContext(ExtprecContext.class,0);
		}
		public ExtcastContext extcast() {
			return getRuleContext(ExtcastContext.class,0);
		}
		public ExttypeContext exttype() {
			return getRuleContext(ExttypeContext.class,0);
		}
		public ExtmemberContext extmember() {
			return getRuleContext(ExtmemberContext.class,0);
		}
		public ExtdotContext extdot() {
			return getRuleContext(ExtdotContext.class,0);
		}
		public ExtarrayContext extarray() {
			return getRuleContext(ExtarrayContext.class,0);
		}
		public ExtprecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extprec; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtprec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtprecContext extprec() throws RecognitionException {
		ExtprecContext _localctx = new ExtprecContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_extprec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(LP);
			setState(212);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(208);
				extprec();
				}
				break;
			case 2:
				{
				setState(209);
				extcast();
				}
				break;
			case 3:
				{
				setState(210);
				exttype();
				}
				break;
			case 4:
				{
				setState(211);
				extmember();
				}
				break;
			}
			setState(214);
			match(RP);
			setState(217);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(215);
				extdot();
				}
				break;
			case 2:
				{
				setState(216);
				extarray();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExtcastContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public DecltypeContext decltype() {
			return getRuleContext(DecltypeContext.class,0);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public ExtprecContext extprec() {
			return getRuleContext(ExtprecContext.class,0);
		}
		public ExtcastContext extcast() {
			return getRuleContext(ExtcastContext.class,0);
		}
		public ExttypeContext exttype() {
			return getRuleContext(ExttypeContext.class,0);
		}
		public ExtmemberContext extmember() {
			return getRuleContext(ExtmemberContext.class,0);
		}
		public ExtcastContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extcast; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtcast(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtcastContext extcast() throws RecognitionException {
		ExtcastContext _localctx = new ExtcastContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_extcast);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(LP);
			setState(220);
			decltype();
			setState(221);
			match(RP);
			setState(226);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(222);
				extprec();
				}
				break;
			case 2:
				{
				setState(223);
				extcast();
				}
				break;
			case 3:
				{
				setState(224);
				exttype();
				}
				break;
			case 4:
				{
				setState(225);
				extmember();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExtarrayContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(PainlessParser.LBRACE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(PainlessParser.RBRACE, 0); }
		public ExtdotContext extdot() {
			return getRuleContext(ExtdotContext.class,0);
		}
		public ExtarrayContext extarray() {
			return getRuleContext(ExtarrayContext.class,0);
		}
		public ExtarrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extarray; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtarray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtarrayContext extarray() throws RecognitionException {
		ExtarrayContext _localctx = new ExtarrayContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_extarray);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(LBRACE);
			setState(229);
			expression(0);
			setState(230);
			match(RBRACE);
			setState(233);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(231);
				extdot();
				}
				break;
			case 2:
				{
				setState(232);
				extarray();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExtdotContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(PainlessParser.DOT, 0); }
		public ExtcallContext extcall() {
			return getRuleContext(ExtcallContext.class,0);
		}
		public ExtmemberContext extmember() {
			return getRuleContext(ExtmemberContext.class,0);
		}
		public ExtdotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extdot; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtdot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtdotContext extdot() throws RecognitionException {
		ExtdotContext _localctx = new ExtdotContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_extdot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(DOT);
			setState(238);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(236);
				extcall();
				}
				break;
			case 2:
				{
				setState(237);
				extmember();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExttypeContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(PainlessParser.TYPE, 0); }
		public ExtdotContext extdot() {
			return getRuleContext(ExtdotContext.class,0);
		}
		public ExttypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exttype; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExttype(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExttypeContext exttype() throws RecognitionException {
		ExttypeContext _localctx = new ExttypeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_exttype);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			match(TYPE);
			setState(241);
			extdot();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExtcallContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public ExtdotContext extdot() {
			return getRuleContext(ExtdotContext.class,0);
		}
		public ExtarrayContext extarray() {
			return getRuleContext(ExtarrayContext.class,0);
		}
		public ExtcallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extcall; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtcall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtcallContext extcall() throws RecognitionException {
		ExtcallContext _localctx = new ExtcallContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_extcall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			match(ID);
			setState(244);
			arguments();
			setState(247);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(245);
				extdot();
				}
				break;
			case 2:
				{
				setState(246);
				extarray();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExtmemberContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
		public ExtdotContext extdot() {
			return getRuleContext(ExtdotContext.class,0);
		}
		public ExtarrayContext extarray() {
			return getRuleContext(ExtarrayContext.class,0);
		}
		public ExtmemberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extmember; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtmember(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExtmemberContext extmember() throws RecognitionException {
		ExtmemberContext _localctx = new ExtmemberContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_extmember);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			match(ID);
			setState(252);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(250);
				extdot();
				}
				break;
			case 2:
				{
				setState(251);
				extarray();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentsContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PainlessParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PainlessParser.COMMA, i);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitArguments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(254);
			match(LP);
			setState(263);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LP) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << TYPE) | (1L << ID))) != 0)) {
				{
				setState(255);
				expression(0);
				setState(260);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(256);
					match(COMMA);
					setState(257);
					expression(0);
					}
					}
					setState(262);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(265);
			match(RP);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 6:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 12);
		case 1:
			return precpred(_ctx, 11);
		case 2:
			return precpred(_ctx, 10);
		case 3:
			return precpred(_ctx, 9);
		case 4:
			return precpred(_ctx, 8);
		case 5:
			return precpred(_ctx, 7);
		case 6:
			return precpred(_ctx, 6);
		case 7:
			return precpred(_ctx, 5);
		case 8:
			return precpred(_ctx, 4);
		case 9:
			return precpred(_ctx, 3);
		case 10:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\38\u010e\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\6\2$\n"+
		"\2\r\2\16\2%\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\61\n\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\5\39\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3E\n"+
		"\3\3\3\3\3\5\3I\n\3\3\3\3\3\5\3M\n\3\3\3\3\3\3\3\5\3R\n\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3b\n\3\3\4\3\4\7\4f\n\4"+
		"\f\4\16\4i\13\4\3\4\3\4\5\4m\n\4\3\5\3\5\3\6\3\6\3\6\3\6\5\6u\n\6\3\6"+
		"\3\6\3\6\3\6\5\6{\n\6\7\6}\n\6\f\6\16\6\u0080\13\6\3\7\3\7\3\7\7\7\u0085"+
		"\n\7\f\7\16\7\u0088\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u00a1\n\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7\b\u00c7"+
		"\n\b\f\b\16\b\u00ca\13\b\3\t\3\t\3\t\3\t\5\t\u00d0\n\t\3\n\3\n\3\n\3\n"+
		"\3\n\5\n\u00d7\n\n\3\n\3\n\3\n\5\n\u00dc\n\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\5\13\u00e5\n\13\3\f\3\f\3\f\3\f\3\f\5\f\u00ec\n\f\3\r\3\r\3"+
		"\r\5\r\u00f1\n\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\5\17\u00fa\n\17\3"+
		"\20\3\20\3\20\5\20\u00ff\n\20\3\21\3\21\3\21\3\21\7\21\u0105\n\21\f\21"+
		"\16\21\u0108\13\21\5\21\u010a\n\21\3\21\3\21\3\21\2\3\16\22\2\4\6\b\n"+
		"\f\16\20\22\24\26\30\32\34\36 \2\t\4\2\26\27\33\34\3\2-\60\3\2\30\32\3"+
		"\2\33\34\3\2\35\37\3\2 #\3\2$%\u013b\2#\3\2\2\2\4a\3\2\2\2\6l\3\2\2\2"+
		"\bn\3\2\2\2\np\3\2\2\2\f\u0081\3\2\2\2\16\u00a0\3\2\2\2\20\u00cf\3\2\2"+
		"\2\22\u00d1\3\2\2\2\24\u00dd\3\2\2\2\26\u00e6\3\2\2\2\30\u00ed\3\2\2\2"+
		"\32\u00f2\3\2\2\2\34\u00f5\3\2\2\2\36\u00fb\3\2\2\2 \u0100\3\2\2\2\"$"+
		"\5\4\3\2#\"\3\2\2\2$%\3\2\2\2%#\3\2\2\2%&\3\2\2\2&\'\3\2\2\2\'(\7\2\2"+
		"\3(\3\3\2\2\2)*\7\16\2\2*+\7\b\2\2+,\5\16\b\2,-\7\t\2\2-\60\5\6\4\2./"+
		"\7\17\2\2/\61\5\6\4\2\60.\3\2\2\2\60\61\3\2\2\2\61b\3\2\2\2\62\63\7\20"+
		"\2\2\63\64\7\b\2\2\64\65\5\16\b\2\658\7\t\2\2\669\5\6\4\2\679\5\b\5\2"+
		"8\66\3\2\2\28\67\3\2\2\29b\3\2\2\2:;\7\21\2\2;<\5\6\4\2<=\7\20\2\2=>\7"+
		"\b\2\2>?\5\16\b\2?@\7\t\2\2@b\3\2\2\2AB\7\22\2\2BD\7\b\2\2CE\5\n\6\2D"+
		"C\3\2\2\2DE\3\2\2\2EF\3\2\2\2FH\7\r\2\2GI\5\16\b\2HG\3\2\2\2HI\3\2\2\2"+
		"IJ\3\2\2\2JL\7\r\2\2KM\5\16\b\2LK\3\2\2\2LM\3\2\2\2MN\3\2\2\2NQ\7\t\2"+
		"\2OR\5\6\4\2PR\5\b\5\2QO\3\2\2\2QP\3\2\2\2Rb\3\2\2\2ST\5\n\6\2TU\7\r\2"+
		"\2Ub\3\2\2\2VW\7\23\2\2Wb\7\r\2\2XY\7\24\2\2Yb\7\r\2\2Z[\7\25\2\2[\\\5"+
		"\16\b\2\\]\7\r\2\2]b\3\2\2\2^_\5\16\b\2_`\7\r\2\2`b\3\2\2\2a)\3\2\2\2"+
		"a\62\3\2\2\2a:\3\2\2\2aA\3\2\2\2aS\3\2\2\2aV\3\2\2\2aX\3\2\2\2aZ\3\2\2"+
		"\2a^\3\2\2\2b\5\3\2\2\2cg\7\4\2\2df\5\4\3\2ed\3\2\2\2fi\3\2\2\2ge\3\2"+
		"\2\2gh\3\2\2\2hj\3\2\2\2ig\3\2\2\2jm\7\5\2\2km\5\4\3\2lc\3\2\2\2lk\3\2"+
		"\2\2m\7\3\2\2\2no\7\r\2\2o\t\3\2\2\2pq\5\f\7\2qt\78\2\2rs\7\n\2\2su\5"+
		"\16\b\2tr\3\2\2\2tu\3\2\2\2u~\3\2\2\2vw\7\f\2\2wz\78\2\2xy\7\n\2\2y{\5"+
		"\16\b\2zx\3\2\2\2z{\3\2\2\2{}\3\2\2\2|v\3\2\2\2}\u0080\3\2\2\2~|\3\2\2"+
		"\2~\177\3\2\2\2\177\13\3\2\2\2\u0080~\3\2\2\2\u0081\u0086\7\67\2\2\u0082"+
		"\u0083\7\6\2\2\u0083\u0085\7\7\2\2\u0084\u0082\3\2\2\2\u0085\u0088\3\2"+
		"\2\2\u0086\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\r\3\2\2\2\u0088\u0086"+
		"\3\2\2\2\u0089\u008a\b\b\1\2\u008a\u008b\t\2\2\2\u008b\u00a1\5\16\b\20"+
		"\u008c\u008d\7\b\2\2\u008d\u008e\5\f\7\2\u008e\u008f\7\t\2\2\u008f\u0090"+
		"\5\16\b\17\u0090\u00a1\3\2\2\2\u0091\u0092\5\20\t\2\u0092\u0093\7\n\2"+
		"\2\u0093\u0094\5\16\b\3\u0094\u00a1\3\2\2\2\u0095\u0096\7\b\2\2\u0096"+
		"\u0097\5\16\b\2\u0097\u0098\7\t\2\2\u0098\u00a1\3\2\2\2\u0099\u00a1\t"+
		"\3\2\2\u009a\u00a1\7\61\2\2\u009b\u00a1\7\62\2\2\u009c\u00a1\7\63\2\2"+
		"\u009d\u00a1\7\64\2\2\u009e\u00a1\7\65\2\2\u009f\u00a1\5\20\t\2\u00a0"+
		"\u0089\3\2\2\2\u00a0\u008c\3\2\2\2\u00a0\u0091\3\2\2\2\u00a0\u0095\3\2"+
		"\2\2\u00a0\u0099\3\2\2\2\u00a0\u009a\3\2\2\2\u00a0\u009b\3\2\2\2\u00a0"+
		"\u009c\3\2\2\2\u00a0\u009d\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0\u009f\3\2"+
		"\2\2\u00a1\u00c8\3\2\2\2\u00a2\u00a3\f\16\2\2\u00a3\u00a4\t\4\2\2\u00a4"+
		"\u00c7\5\16\b\17\u00a5\u00a6\f\r\2\2\u00a6\u00a7\t\5\2\2\u00a7\u00c7\5"+
		"\16\b\16\u00a8\u00a9\f\f\2\2\u00a9\u00aa\t\6\2\2\u00aa\u00c7\5\16\b\r"+
		"\u00ab\u00ac\f\13\2\2\u00ac\u00ad\t\7\2\2\u00ad\u00c7\5\16\b\f\u00ae\u00af"+
		"\f\n\2\2\u00af\u00b0\t\b\2\2\u00b0\u00c7\5\16\b\13\u00b1\u00b2\f\t\2\2"+
		"\u00b2\u00b3\7&\2\2\u00b3\u00c7\5\16\b\n\u00b4\u00b5\f\b\2\2\u00b5\u00b6"+
		"\7\'\2\2\u00b6\u00c7\5\16\b\t\u00b7\u00b8\f\7\2\2\u00b8\u00b9\7(\2\2\u00b9"+
		"\u00c7\5\16\b\b\u00ba\u00bb\f\6\2\2\u00bb\u00bc\7)\2\2\u00bc\u00c7\5\16"+
		"\b\7\u00bd\u00be\f\5\2\2\u00be\u00bf\7*\2\2\u00bf\u00c7\5\16\b\6\u00c0"+
		"\u00c1\f\4\2\2\u00c1\u00c2\7+\2\2\u00c2\u00c3\5\16\b\2\u00c3\u00c4\7,"+
		"\2\2\u00c4\u00c5\5\16\b\4\u00c5\u00c7\3\2\2\2\u00c6\u00a2\3\2\2\2\u00c6"+
		"\u00a5\3\2\2\2\u00c6\u00a8\3\2\2\2\u00c6\u00ab\3\2\2\2\u00c6\u00ae\3\2"+
		"\2\2\u00c6\u00b1\3\2\2\2\u00c6\u00b4\3\2\2\2\u00c6\u00b7\3\2\2\2\u00c6"+
		"\u00ba\3\2\2\2\u00c6\u00bd\3\2\2\2\u00c6\u00c0\3\2\2\2\u00c7\u00ca\3\2"+
		"\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\17\3\2\2\2\u00ca\u00c8"+
		"\3\2\2\2\u00cb\u00d0\5\22\n\2\u00cc\u00d0\5\24\13\2\u00cd\u00d0\5\32\16"+
		"\2\u00ce\u00d0\5\36\20\2\u00cf\u00cb\3\2\2\2\u00cf\u00cc\3\2\2\2\u00cf"+
		"\u00cd\3\2\2\2\u00cf\u00ce\3\2\2\2\u00d0\21\3\2\2\2\u00d1\u00d6\7\b\2"+
		"\2\u00d2\u00d7\5\22\n\2\u00d3\u00d7\5\24\13\2\u00d4\u00d7\5\32\16\2\u00d5"+
		"\u00d7\5\36\20\2\u00d6\u00d2\3\2\2\2\u00d6\u00d3\3\2\2\2\u00d6\u00d4\3"+
		"\2\2\2\u00d6\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00db\7\t\2\2\u00d9"+
		"\u00dc\5\30\r\2\u00da\u00dc\5\26\f\2\u00db\u00d9\3\2\2\2\u00db\u00da\3"+
		"\2\2\2\u00db\u00dc\3\2\2\2\u00dc\23\3\2\2\2\u00dd\u00de\7\b\2\2\u00de"+
		"\u00df\5\f\7\2\u00df\u00e4\7\t\2\2\u00e0\u00e5\5\22\n\2\u00e1\u00e5\5"+
		"\24\13\2\u00e2\u00e5\5\32\16\2\u00e3\u00e5\5\36\20\2\u00e4\u00e0\3\2\2"+
		"\2\u00e4\u00e1\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e3\3\2\2\2\u00e5\25"+
		"\3\2\2\2\u00e6\u00e7\7\6\2\2\u00e7\u00e8\5\16\b\2\u00e8\u00eb\7\7\2\2"+
		"\u00e9\u00ec\5\30\r\2\u00ea\u00ec\5\26\f\2\u00eb\u00e9\3\2\2\2\u00eb\u00ea"+
		"\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\27\3\2\2\2\u00ed\u00f0\7\13\2\2\u00ee"+
		"\u00f1\5\34\17\2\u00ef\u00f1\5\36\20\2\u00f0\u00ee\3\2\2\2\u00f0\u00ef"+
		"\3\2\2\2\u00f1\31\3\2\2\2\u00f2\u00f3\7\67\2\2\u00f3\u00f4\5\30\r\2\u00f4"+
		"\33\3\2\2\2\u00f5\u00f6\78\2\2\u00f6\u00f9\5 \21\2\u00f7\u00fa\5\30\r"+
		"\2\u00f8\u00fa\5\26\f\2\u00f9\u00f7\3\2\2\2\u00f9\u00f8\3\2\2\2\u00f9"+
		"\u00fa\3\2\2\2\u00fa\35\3\2\2\2\u00fb\u00fe\78\2\2\u00fc\u00ff\5\30\r"+
		"\2\u00fd\u00ff\5\26\f\2\u00fe\u00fc\3\2\2\2\u00fe\u00fd\3\2\2\2\u00fe"+
		"\u00ff\3\2\2\2\u00ff\37\3\2\2\2\u0100\u0109\7\b\2\2\u0101\u0106\5\16\b"+
		"\2\u0102\u0103\7\f\2\2\u0103\u0105\5\16\b\2\u0104\u0102\3\2\2\2\u0105"+
		"\u0108\3\2\2\2\u0106\u0104\3\2\2\2\u0106\u0107\3\2\2\2\u0107\u010a\3\2"+
		"\2\2\u0108\u0106\3\2\2\2\u0109\u0101\3\2\2\2\u0109\u010a\3\2\2\2\u010a"+
		"\u010b\3\2\2\2\u010b\u010c\7\t\2\2\u010c!\3\2\2\2\35%\608DHLQagltz~\u0086"+
		"\u00a0\u00c6\u00c8\u00cf\u00d6\u00db\u00e4\u00eb\u00f0\u00f9\u00fe\u0106"+
		"\u0109";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}