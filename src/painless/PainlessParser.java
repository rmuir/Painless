// Generated from src/painless/Painless.g4 by ANTLR 4.5
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
		T__0=1, WS=2, LBRACK=3, RBRACK=4, LBRACE=5, RBRACE=6, LP=7, RP=8, ASSIGN=9, 
		COMMA=10, SEMICOLON=11, IF=12, ELSE=13, WHILE=14, DO=15, FOR=16, CONTINUE=17, 
		BREAK=18, RETURN=19, BOOLNOT=20, BWNOT=21, MUL=22, DIV=23, REM=24, ADD=25, 
		SUB=26, LSH=27, RSH=28, USH=29, LT=30, LTE=31, GT=32, GTE=33, EQ=34, NE=35, 
		BWAND=36, BWXOR=37, BWOR=38, BOOLAND=39, BOOLOR=40, COND=41, COLON=42, 
		OCTAL=43, HEX=44, INTEGER=45, DECIMAL=46, STRING=47, CHAR=48, TRUE=49, 
		FALSE=50, NULL=51, TYPE=52, ID=53;
	public static final int
		RULE_source = 0, RULE_statement = 1, RULE_block = 2, RULE_declaration = 3, 
		RULE_decltype = 4, RULE_expression = 5, RULE_external = 6, RULE_arguments = 7;
	public static final String[] ruleNames = {
		"source", "statement", "block", "declaration", "decltype", "expression", 
		"external", "arguments"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'.'", null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'='", "','", 
		"';'", "'if'", "'else'", "'while'", "'do'", "'for'", "'continue'", "'break'", 
		"'return'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", "'<<'", "'>>'", 
		"'>>>'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&'", "'^'", "'|'", 
		"'&&'", "'||'", "'?'", "':'", null, null, null, null, null, null, "'true'", 
		"'false'", "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "WS", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", 
		"ASSIGN", "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
		"BREAK", "RETURN", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", "SUB", 
		"LSH", "RSH", "USH", "LT", "LTE", "GT", "GTE", "EQ", "NE", "BWAND", "BWXOR", 
		"BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", "OCTAL", "HEX", "INTEGER", 
		"DECIMAL", "STRING", "CHAR", "TRUE", "FALSE", "NULL", "TYPE", "ID"
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
			setState(17); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(16);
				statement();
				}
				}
				setState(19); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << IF) | (1L << WHILE) | (1L << DO) | (1L << FOR) | (1L << CONTINUE) | (1L << BREAK) | (1L << RETURN) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << TYPE) | (1L << ID))) != 0) );
			setState(21);
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
			setState(74);
			switch (_input.LA(1)) {
			case IF:
				_localctx = new IfContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(23);
				match(IF);
				setState(24);
				match(LP);
				setState(25);
				expression(0);
				setState(26);
				match(RP);
				setState(27);
				block();
				setState(30);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(28);
					match(ELSE);
					setState(29);
					block();
					}
					break;
				}
				}
				break;
			case WHILE:
				_localctx = new WhileContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(32);
				match(WHILE);
				setState(33);
				match(LP);
				setState(34);
				expression(0);
				setState(35);
				match(RP);
				setState(36);
				block();
				}
				break;
			case DO:
				_localctx = new DoContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(38);
				match(DO);
				setState(39);
				block();
				setState(40);
				match(WHILE);
				setState(41);
				match(LP);
				setState(42);
				expression(0);
				setState(43);
				match(RP);
				}
				break;
			case FOR:
				_localctx = new ForContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(45);
				match(FOR);
				setState(46);
				match(LP);
				setState(48);
				_la = _input.LA(1);
				if (_la==TYPE) {
					{
					setState(47);
					declaration();
					}
				}

				setState(50);
				match(SEMICOLON);
				setState(52);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << ID))) != 0)) {
					{
					setState(51);
					expression(0);
					}
				}

				setState(54);
				match(SEMICOLON);
				setState(56);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << ID))) != 0)) {
					{
					setState(55);
					expression(0);
					}
				}

				setState(58);
				match(RP);
				setState(59);
				block();
				}
				break;
			case TYPE:
				_localctx = new DeclContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(60);
				declaration();
				setState(61);
				match(SEMICOLON);
				}
				break;
			case CONTINUE:
				_localctx = new ContinueContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(63);
				match(CONTINUE);
				setState(64);
				match(SEMICOLON);
				}
				break;
			case BREAK:
				_localctx = new BreakContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(65);
				match(BREAK);
				setState(66);
				match(SEMICOLON);
				}
				break;
			case RETURN:
				_localctx = new ReturnContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(67);
				match(RETURN);
				setState(68);
				expression(0);
				setState(69);
				match(SEMICOLON);
				}
				break;
			case LBRACE:
			case LP:
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
			case ID:
				_localctx = new ExprContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(71);
				expression(0);
				setState(72);
				match(SEMICOLON);
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
	public static class EmptyContext extends BlockContext {
		public TerminalNode SEMICOLON() { return getToken(PainlessParser.SEMICOLON, 0); }
		public EmptyContext(BlockContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitEmpty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_block);
		int _la;
		try {
			setState(86);
			switch (_input.LA(1)) {
			case LBRACK:
				_localctx = new MultipleContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(76);
				match(LBRACK);
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << IF) | (1L << WHILE) | (1L << DO) | (1L << FOR) | (1L << CONTINUE) | (1L << BREAK) | (1L << RETURN) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << TYPE) | (1L << ID))) != 0)) {
					{
					{
					setState(77);
					statement();
					}
					}
					setState(82);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(83);
				match(RBRACK);
				}
				break;
			case LBRACE:
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
				setState(84);
				statement();
				}
				break;
			case SEMICOLON:
				_localctx = new EmptyContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(85);
				match(SEMICOLON);
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
		enterRule(_localctx, 6, RULE_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			decltype();
			setState(89);
			match(ID);
			setState(92);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(90);
				match(ASSIGN);
				setState(91);
				expression(0);
				}
			}

			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(94);
				match(COMMA);
				setState(95);
				match(ID);
				setState(98);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(96);
					match(ASSIGN);
					setState(97);
					expression(0);
					}
				}

				}
				}
				setState(104);
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
		enterRule(_localctx, 8, RULE_decltype);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			match(TYPE);
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACE) {
				{
				{
				setState(106);
				match(LBRACE);
				setState(107);
				match(RBRACE);
				}
				}
				setState(112);
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
		public ExternalContext external() {
			return getRuleContext(ExternalContext.class,0);
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
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode ASSIGN() { return getToken(PainlessParser.ASSIGN, 0); }
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
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				_localctx = new UnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(114);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(115);
				expression(14);
				}
				break;
			case 2:
				{
				_localctx = new CastContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(116);
				match(LP);
				setState(117);
				decltype();
				setState(118);
				match(RP);
				setState(119);
				expression(13);
				}
				break;
			case 3:
				{
				_localctx = new PrecedenceContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(121);
				match(LP);
				setState(122);
				expression(0);
				setState(123);
				match(RP);
				}
				break;
			case 4:
				{
				_localctx = new NumericContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(125);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			case 5:
				{
				_localctx = new StringContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(126);
				match(STRING);
				}
				break;
			case 6:
				{
				_localctx = new CharContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(127);
				match(CHAR);
				}
				break;
			case 7:
				{
				_localctx = new TrueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(128);
				match(TRUE);
				}
				break;
			case 8:
				{
				_localctx = new FalseContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(129);
				match(FALSE);
				}
				break;
			case 9:
				{
				_localctx = new NullContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(130);
				match(NULL);
				}
				break;
			case 10:
				{
				_localctx = new ExtContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(131);
				external(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(175);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(173);
					switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(134);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(135);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << REM))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(136);
						expression(13);
						}
						break;
					case 2:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(137);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(138);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(139);
						expression(12);
						}
						break;
					case 3:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(140);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(141);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSH) | (1L << RSH) | (1L << USH))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(142);
						expression(11);
						}
						break;
					case 4:
						{
						_localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(143);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(144);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LTE) | (1L << GT) | (1L << GTE))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(145);
						expression(10);
						}
						break;
					case 5:
						{
						_localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(146);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(147);
						_la = _input.LA(1);
						if ( !(_la==EQ || _la==NE) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(148);
						expression(9);
						}
						break;
					case 6:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(149);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(150);
						match(BWAND);
						setState(151);
						expression(8);
						}
						break;
					case 7:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(152);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(153);
						match(BWXOR);
						setState(154);
						expression(7);
						}
						break;
					case 8:
						{
						_localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(155);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(156);
						match(BWOR);
						setState(157);
						expression(6);
						}
						break;
					case 9:
						{
						_localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(158);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(159);
						match(BOOLAND);
						setState(160);
						expression(5);
						}
						break;
					case 10:
						{
						_localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(161);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(162);
						match(BOOLOR);
						setState(163);
						expression(4);
						}
						break;
					case 11:
						{
						_localctx = new ConditionalContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(164);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(165);
						match(COND);
						setState(166);
						expression(0);
						setState(167);
						match(COLON);
						setState(168);
						expression(2);
						}
						break;
					case 12:
						{
						_localctx = new AssignmentContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(170);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(171);
						match(ASSIGN);
						setState(172);
						expression(1);
						}
						break;
					}
					} 
				}
				setState(177);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
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

	public static class ExternalContext extends ParserRuleContext {
		public ExternalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_external; }
	 
		public ExternalContext() { }
		public void copyFrom(ExternalContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExtdotContext extends ExternalContext {
		public List<ExternalContext> external() {
			return getRuleContexts(ExternalContext.class);
		}
		public ExternalContext external(int i) {
			return getRuleContext(ExternalContext.class,i);
		}
		public ExtdotContext(ExternalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtdot(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExtprecContext extends ExternalContext {
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public List<ExternalContext> external() {
			return getRuleContexts(ExternalContext.class);
		}
		public ExternalContext external(int i) {
			return getRuleContext(ExternalContext.class,i);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public ExtprecContext(ExternalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtprec(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExtargsContext extends ExternalContext {
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public ExternalContext external() {
			return getRuleContext(ExternalContext.class,0);
		}
		public ExtargsContext(ExternalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtargs(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExtarrayContext extends ExternalContext {
		public TerminalNode LBRACE() { return getToken(PainlessParser.LBRACE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(PainlessParser.RBRACE, 0); }
		public ExternalContext external() {
			return getRuleContext(ExternalContext.class,0);
		}
		public ExtarrayContext(ExternalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtarray(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExtcastContext extends ExternalContext {
		public TerminalNode LP() { return getToken(PainlessParser.LP, 0); }
		public DecltypeContext decltype() {
			return getRuleContext(DecltypeContext.class,0);
		}
		public TerminalNode RP() { return getToken(PainlessParser.RP, 0); }
		public ExternalContext external() {
			return getRuleContext(ExternalContext.class,0);
		}
		public ExtcastContext(ExternalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtcast(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExtvarContext extends ExternalContext {
		public TerminalNode ID() { return getToken(PainlessParser.ID, 0); }
		public ExternalContext external() {
			return getRuleContext(ExternalContext.class,0);
		}
		public ExtvarContext(ExternalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainlessVisitor ) return ((PainlessVisitor<? extends T>)visitor).visitExtvar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExternalContext external() throws RecognitionException {
		return external(0);
	}

	private ExternalContext external(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExternalContext _localctx = new ExternalContext(_ctx, _parentState);
		ExternalContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_external, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				_localctx = new ExtcastContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(179);
				match(LP);
				setState(180);
				decltype();
				setState(181);
				match(RP);
				setState(182);
				external(5);
				}
				break;
			case 2:
				{
				_localctx = new ExtprecContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(184);
				match(LP);
				setState(185);
				external(0);
				setState(186);
				match(RP);
				setState(188);
				switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
				case 1:
					{
					setState(187);
					external(0);
					}
					break;
				}
				}
				break;
			case 3:
				{
				_localctx = new ExtarrayContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(190);
				match(LBRACE);
				setState(191);
				expression(0);
				setState(192);
				match(RBRACE);
				setState(194);
				switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
				case 1:
					{
					setState(193);
					external(0);
					}
					break;
				}
				}
				break;
			case 4:
				{
				_localctx = new ExtargsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(196);
				arguments();
				setState(198);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(197);
					external(0);
					}
					break;
				}
				}
				break;
			case 5:
				{
				_localctx = new ExtvarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(200);
				match(ID);
				setState(202);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(201);
					external(0);
					}
					break;
				}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(211);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExtdotContext(new ExternalContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_external);
					setState(206);
					if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
					setState(207);
					match(T__0);
					setState(208);
					external(5);
					}
					} 
				}
				setState(213);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
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
		enterRule(_localctx, 14, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(214);
			match(LP);
			setState(223);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACE) | (1L << LP) | (1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB) | (1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL) | (1L << STRING) | (1L << CHAR) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << ID))) != 0)) {
				{
				setState(215);
				expression(0);
				setState(220);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(216);
					match(COMMA);
					setState(217);
					expression(0);
					}
					}
					setState(222);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(225);
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
		case 5:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 6:
			return external_sempred((ExternalContext)_localctx, predIndex);
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
		case 11:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean external_sempred(ExternalContext _localctx, int predIndex) {
		switch (predIndex) {
		case 12:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\67\u00e6\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\6\2\24\n"+
		"\2\r\2\16\2\25\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3!\n\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\63\n\3\3\3"+
		"\3\3\5\3\67\n\3\3\3\3\3\5\3;\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3M\n\3\3\4\3\4\7\4Q\n\4\f\4\16\4T\13\4\3"+
		"\4\3\4\3\4\5\4Y\n\4\3\5\3\5\3\5\3\5\5\5_\n\5\3\5\3\5\3\5\3\5\5\5e\n\5"+
		"\7\5g\n\5\f\5\16\5j\13\5\3\6\3\6\3\6\7\6o\n\6\f\6\16\6r\13\6\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7"+
		"\u0087\n\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7\u00b0\n\7\f\7\16\7\u00b3\13\7\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u00bf\n\b\3\b\3\b\3\b\3\b\5\b\u00c5"+
		"\n\b\3\b\3\b\5\b\u00c9\n\b\3\b\3\b\5\b\u00cd\n\b\5\b\u00cf\n\b\3\b\3\b"+
		"\3\b\7\b\u00d4\n\b\f\b\16\b\u00d7\13\b\3\t\3\t\3\t\3\t\7\t\u00dd\n\t\f"+
		"\t\16\t\u00e0\13\t\5\t\u00e2\n\t\3\t\3\t\3\t\2\4\f\16\n\2\4\6\b\n\f\16"+
		"\20\2\t\4\2\26\27\33\34\3\2-\60\3\2\30\32\3\2\33\34\3\2\35\37\3\2 #\3"+
		"\2$%\u0111\2\23\3\2\2\2\4L\3\2\2\2\6X\3\2\2\2\bZ\3\2\2\2\nk\3\2\2\2\f"+
		"\u0086\3\2\2\2\16\u00ce\3\2\2\2\20\u00d8\3\2\2\2\22\24\5\4\3\2\23\22\3"+
		"\2\2\2\24\25\3\2\2\2\25\23\3\2\2\2\25\26\3\2\2\2\26\27\3\2\2\2\27\30\7"+
		"\2\2\3\30\3\3\2\2\2\31\32\7\16\2\2\32\33\7\t\2\2\33\34\5\f\7\2\34\35\7"+
		"\n\2\2\35 \5\6\4\2\36\37\7\17\2\2\37!\5\6\4\2 \36\3\2\2\2 !\3\2\2\2!M"+
		"\3\2\2\2\"#\7\20\2\2#$\7\t\2\2$%\5\f\7\2%&\7\n\2\2&\'\5\6\4\2\'M\3\2\2"+
		"\2()\7\21\2\2)*\5\6\4\2*+\7\20\2\2+,\7\t\2\2,-\5\f\7\2-.\7\n\2\2.M\3\2"+
		"\2\2/\60\7\22\2\2\60\62\7\t\2\2\61\63\5\b\5\2\62\61\3\2\2\2\62\63\3\2"+
		"\2\2\63\64\3\2\2\2\64\66\7\r\2\2\65\67\5\f\7\2\66\65\3\2\2\2\66\67\3\2"+
		"\2\2\678\3\2\2\28:\7\r\2\29;\5\f\7\2:9\3\2\2\2:;\3\2\2\2;<\3\2\2\2<=\7"+
		"\n\2\2=M\5\6\4\2>?\5\b\5\2?@\7\r\2\2@M\3\2\2\2AB\7\23\2\2BM\7\r\2\2CD"+
		"\7\24\2\2DM\7\r\2\2EF\7\25\2\2FG\5\f\7\2GH\7\r\2\2HM\3\2\2\2IJ\5\f\7\2"+
		"JK\7\r\2\2KM\3\2\2\2L\31\3\2\2\2L\"\3\2\2\2L(\3\2\2\2L/\3\2\2\2L>\3\2"+
		"\2\2LA\3\2\2\2LC\3\2\2\2LE\3\2\2\2LI\3\2\2\2M\5\3\2\2\2NR\7\5\2\2OQ\5"+
		"\4\3\2PO\3\2\2\2QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2SU\3\2\2\2TR\3\2\2\2UY\7"+
		"\6\2\2VY\5\4\3\2WY\7\r\2\2XN\3\2\2\2XV\3\2\2\2XW\3\2\2\2Y\7\3\2\2\2Z["+
		"\5\n\6\2[^\7\67\2\2\\]\7\13\2\2]_\5\f\7\2^\\\3\2\2\2^_\3\2\2\2_h\3\2\2"+
		"\2`a\7\f\2\2ad\7\67\2\2bc\7\13\2\2ce\5\f\7\2db\3\2\2\2de\3\2\2\2eg\3\2"+
		"\2\2f`\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2i\t\3\2\2\2jh\3\2\2\2kp\7"+
		"\66\2\2lm\7\7\2\2mo\7\b\2\2nl\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2q\13"+
		"\3\2\2\2rp\3\2\2\2st\b\7\1\2tu\t\2\2\2u\u0087\5\f\7\20vw\7\t\2\2wx\5\n"+
		"\6\2xy\7\n\2\2yz\5\f\7\17z\u0087\3\2\2\2{|\7\t\2\2|}\5\f\7\2}~\7\n\2\2"+
		"~\u0087\3\2\2\2\177\u0087\t\3\2\2\u0080\u0087\7\61\2\2\u0081\u0087\7\62"+
		"\2\2\u0082\u0087\7\63\2\2\u0083\u0087\7\64\2\2\u0084\u0087\7\65\2\2\u0085"+
		"\u0087\5\16\b\2\u0086s\3\2\2\2\u0086v\3\2\2\2\u0086{\3\2\2\2\u0086\177"+
		"\3\2\2\2\u0086\u0080\3\2\2\2\u0086\u0081\3\2\2\2\u0086\u0082\3\2\2\2\u0086"+
		"\u0083\3\2\2\2\u0086\u0084\3\2\2\2\u0086\u0085\3\2\2\2\u0087\u00b1\3\2"+
		"\2\2\u0088\u0089\f\16\2\2\u0089\u008a\t\4\2\2\u008a\u00b0\5\f\7\17\u008b"+
		"\u008c\f\r\2\2\u008c\u008d\t\5\2\2\u008d\u00b0\5\f\7\16\u008e\u008f\f"+
		"\f\2\2\u008f\u0090\t\6\2\2\u0090\u00b0\5\f\7\r\u0091\u0092\f\13\2\2\u0092"+
		"\u0093\t\7\2\2\u0093\u00b0\5\f\7\f\u0094\u0095\f\n\2\2\u0095\u0096\t\b"+
		"\2\2\u0096\u00b0\5\f\7\13\u0097\u0098\f\t\2\2\u0098\u0099\7&\2\2\u0099"+
		"\u00b0\5\f\7\n\u009a\u009b\f\b\2\2\u009b\u009c\7\'\2\2\u009c\u00b0\5\f"+
		"\7\t\u009d\u009e\f\7\2\2\u009e\u009f\7(\2\2\u009f\u00b0\5\f\7\b\u00a0"+
		"\u00a1\f\6\2\2\u00a1\u00a2\7)\2\2\u00a2\u00b0\5\f\7\7\u00a3\u00a4\f\5"+
		"\2\2\u00a4\u00a5\7*\2\2\u00a5\u00b0\5\f\7\6\u00a6\u00a7\f\4\2\2\u00a7"+
		"\u00a8\7+\2\2\u00a8\u00a9\5\f\7\2\u00a9\u00aa\7,\2\2\u00aa\u00ab\5\f\7"+
		"\4\u00ab\u00b0\3\2\2\2\u00ac\u00ad\f\3\2\2\u00ad\u00ae\7\13\2\2\u00ae"+
		"\u00b0\5\f\7\3\u00af\u0088\3\2\2\2\u00af\u008b\3\2\2\2\u00af\u008e\3\2"+
		"\2\2\u00af\u0091\3\2\2\2\u00af\u0094\3\2\2\2\u00af\u0097\3\2\2\2\u00af"+
		"\u009a\3\2\2\2\u00af\u009d\3\2\2\2\u00af\u00a0\3\2\2\2\u00af\u00a3\3\2"+
		"\2\2\u00af\u00a6\3\2\2\2\u00af\u00ac\3\2\2\2\u00b0\u00b3\3\2\2\2\u00b1"+
		"\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\r\3\2\2\2\u00b3\u00b1\3\2\2\2"+
		"\u00b4\u00b5\b\b\1\2\u00b5\u00b6\7\t\2\2\u00b6\u00b7\5\n\6\2\u00b7\u00b8"+
		"\7\n\2\2\u00b8\u00b9\5\16\b\7\u00b9\u00cf\3\2\2\2\u00ba\u00bb\7\t\2\2"+
		"\u00bb\u00bc\5\16\b\2\u00bc\u00be\7\n\2\2\u00bd\u00bf\5\16\b\2\u00be\u00bd"+
		"\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\u00cf\3\2\2\2\u00c0\u00c1\7\7\2\2\u00c1"+
		"\u00c2\5\f\7\2\u00c2\u00c4\7\b\2\2\u00c3\u00c5\5\16\b\2\u00c4\u00c3\3"+
		"\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00cf\3\2\2\2\u00c6\u00c8\5\20\t\2\u00c7"+
		"\u00c9\5\16\b\2\u00c8\u00c7\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00cf\3"+
		"\2\2\2\u00ca\u00cc\7\67\2\2\u00cb\u00cd\5\16\b\2\u00cc\u00cb\3\2\2\2\u00cc"+
		"\u00cd\3\2\2\2\u00cd\u00cf\3\2\2\2\u00ce\u00b4\3\2\2\2\u00ce\u00ba\3\2"+
		"\2\2\u00ce\u00c0\3\2\2\2\u00ce\u00c6\3\2\2\2\u00ce\u00ca\3\2\2\2\u00cf"+
		"\u00d5\3\2\2\2\u00d0\u00d1\f\6\2\2\u00d1\u00d2\7\3\2\2\u00d2\u00d4\5\16"+
		"\b\7\u00d3\u00d0\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d5"+
		"\u00d6\3\2\2\2\u00d6\17\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d8\u00e1\7\t\2"+
		"\2\u00d9\u00de\5\f\7\2\u00da\u00db\7\f\2\2\u00db\u00dd\5\f\7\2\u00dc\u00da"+
		"\3\2\2\2\u00dd\u00e0\3\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df"+
		"\u00e2\3\2\2\2\u00e0\u00de\3\2\2\2\u00e1\u00d9\3\2\2\2\u00e1\u00e2\3\2"+
		"\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e4\7\n\2\2\u00e4\21\3\2\2\2\31\25 \62"+
		"\66:LRX^dhp\u0086\u00af\u00b1\u00be\u00c4\u00c8\u00cc\u00ce\u00d5\u00de"+
		"\u00e1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}