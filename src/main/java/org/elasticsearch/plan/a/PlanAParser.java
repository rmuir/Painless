// ANTLR GENERATED CODE: DO NOT EDIT
package org.elasticsearch.plan.a;

    import java.util.Set;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class PlanAParser extends Parser {
  static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache =
    new PredictionContextCache();
  public static final int
    WS=1, LBRACK=2, RBRACK=3, LBRACE=4, RBRACE=5, LP=6, RP=7, DOT=8, COMMA=9, 
    SEMICOLON=10, IF=11, ELSE=12, WHILE=13, DO=14, FOR=15, CONTINUE=16, BREAK=17, 
    RETURN=18, BOOLNOT=19, BWNOT=20, MUL=21, DIV=22, REM=23, ADD=24, SUB=25, 
    LSH=26, RSH=27, USH=28, CAT=29, LT=30, LTE=31, GT=32, GTE=33, EQ=34, EQR=35, 
    NE=36, NER=37, BWAND=38, BWXOR=39, BWOR=40, BOOLAND=41, BOOLOR=42, COND=43, 
    COLON=44, INCR=45, DECR=46, ASSIGN=47, AADD=48, ASUB=49, AMUL=50, ADIV=51, 
    AREM=52, AAND=53, AXOR=54, AOR=55, ALSH=56, ARSH=57, AUSH=58, ACAT=59, 
    OCTAL=60, HEX=61, INTEGER=62, DECIMAL=63, STRING=64, CHAR=65, TRUE=66, 
    FALSE=67, NULL=68, VOID=69, ID=70;
  public static final int
    RULE_source = 0, RULE_statement = 1, RULE_block = 2, RULE_empty = 3, RULE_initializer = 4, 
    RULE_afterthought = 5, RULE_declaration = 6, RULE_decltype = 7, RULE_declvar = 8, 
    RULE_expression = 9, RULE_extstart = 10, RULE_extprec = 11, RULE_extcast = 12, 
    RULE_extbrace = 13, RULE_extdot = 14, RULE_exttype = 15, RULE_extcall = 16, 
    RULE_extmember = 17, RULE_arguments = 18, RULE_increment = 19;
  public static final String[] ruleNames = {
    "source", "statement", "block", "empty", "initializer", "afterthought", 
    "declaration", "decltype", "declvar", "expression", "extstart", "extprec", 
    "extcast", "extbrace", "extdot", "exttype", "extcall", "extmember", "arguments", 
    "increment"
  };

  private static final String[] _LITERAL_NAMES = {
    null, null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'.'", "','", "';'", 
    "'if'", "'else'", "'while'", "'do'", "'for'", "'continue'", "'break'", 
    "'return'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", "'<<'", "'>>'", 
    "'>>>'", "'..'", "'<'", "'<='", "'>'", "'>='", "'=='", "'==='", "'!='", 
    "'!=='", "'&'", "'^'", "'|'", "'&&'", "'||'", "'?'", "':'", "'++'", "'--'", 
    "'='", "'+='", "'-='", "'*='", "'/='", "'%='", "'&='", "'^='", "'|='", 
    "'<<='", "'>>='", "'>>>='", "'..='", null, null, null, null, null, null, 
    "'true'", "'false'", "'null'", "'void'"
  };
  private static final String[] _SYMBOLIC_NAMES = {
    null, "WS", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", "DOT", 
    "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE", 
    "BREAK", "RETURN", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", "SUB", 
    "LSH", "RSH", "USH", "CAT", "LT", "LTE", "GT", "GTE", "EQ", "EQR", "NE", 
    "NER", "BWAND", "BWXOR", "BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", 
    "INCR", "DECR", "ASSIGN", "AADD", "ASUB", "AMUL", "ADIV", "AREM", "AAND", 
    "AXOR", "AOR", "ALSH", "ARSH", "AUSH", "ACAT", "OCTAL", "HEX", "INTEGER", 
    "DECIMAL", "STRING", "CHAR", "TRUE", "FALSE", "NULL", "VOID", "ID"
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
  public String getGrammarFileName() { return "PlanA.g4"; }

  @Override
  public String[] getRuleNames() { return ruleNames; }

  @Override
  public String getSerializedATN() { return _serializedATN; }

  @Override
  public ATN getATN() { return _ATN; }


      private Set<String> definition = null;

      void setTypes(Set<String> definition) {
          this.definition = definition;
      }

      boolean isType() {
          if (definition == null) {
              throw new IllegalStateException();
          }

          return definition.contains(getCurrentToken().getText());
      }

  public PlanAParser(TokenStream input) {
    super(input);
    _interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
  }
  public static class SourceContext extends ParserRuleContext {
    public TerminalNode EOF() { return getToken(PlanAParser.EOF, 0); }
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
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitSource(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SourceContext source() throws RecognitionException {
    SourceContext _localctx = new SourceContext(_ctx, getState());
    enterRule(_localctx, 0, RULE_source);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(41); 
      _errHandler.sync(this);
      _alt = 1;
      do {
        switch (_alt) {
        case 1:
          {
          {
          setState(40);
          statement();
          }
          }
          break;
        default:
          throw new NoViableAltException(this);
        }
        setState(43); 
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,0,_ctx);
      } while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
      setState(45);
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
    public TerminalNode SEMICOLON() { return getToken(PlanAParser.SEMICOLON, 0); }
    public DeclContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitDecl(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class BreakContext extends StatementContext {
    public TerminalNode BREAK() { return getToken(PlanAParser.BREAK, 0); }
    public TerminalNode SEMICOLON() { return getToken(PlanAParser.SEMICOLON, 0); }
    public BreakContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitBreak(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ContinueContext extends StatementContext {
    public TerminalNode CONTINUE() { return getToken(PlanAParser.CONTINUE, 0); }
    public TerminalNode SEMICOLON() { return getToken(PlanAParser.SEMICOLON, 0); }
    public ContinueContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitContinue(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ForContext extends StatementContext {
    public TerminalNode FOR() { return getToken(PlanAParser.FOR, 0); }
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public List<TerminalNode> SEMICOLON() { return getTokens(PlanAParser.SEMICOLON); }
    public TerminalNode SEMICOLON(int i) {
      return getToken(PlanAParser.SEMICOLON, i);
    }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public EmptyContext empty() {
      return getRuleContext(EmptyContext.class,0);
    }
    public InitializerContext initializer() {
      return getRuleContext(InitializerContext.class,0);
    }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public AfterthoughtContext afterthought() {
      return getRuleContext(AfterthoughtContext.class,0);
    }
    public ForContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitFor(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ExprContext extends StatementContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode SEMICOLON() { return getToken(PlanAParser.SEMICOLON, 0); }
    public ExprContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExpr(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class DoContext extends StatementContext {
    public TerminalNode DO() { return getToken(PlanAParser.DO, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public TerminalNode WHILE() { return getToken(PlanAParser.WHILE, 0); }
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
    public TerminalNode SEMICOLON() { return getToken(PlanAParser.SEMICOLON, 0); }
    public DoContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitDo(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class WhileContext extends StatementContext {
    public TerminalNode WHILE() { return getToken(PlanAParser.WHILE, 0); }
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
    public BlockContext block() {
      return getRuleContext(BlockContext.class,0);
    }
    public EmptyContext empty() {
      return getRuleContext(EmptyContext.class,0);
    }
    public WhileContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitWhile(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class IfContext extends StatementContext {
    public TerminalNode IF() { return getToken(PlanAParser.IF, 0); }
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
    public List<BlockContext> block() {
      return getRuleContexts(BlockContext.class);
    }
    public BlockContext block(int i) {
      return getRuleContext(BlockContext.class,i);
    }
    public TerminalNode ELSE() { return getToken(PlanAParser.ELSE, 0); }
    public IfContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitIf(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ReturnContext extends StatementContext {
    public TerminalNode RETURN() { return getToken(PlanAParser.RETURN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode SEMICOLON() { return getToken(PlanAParser.SEMICOLON, 0); }
    public ReturnContext(StatementContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitReturn(this);
      else return visitor.visitChildren(this);
    }
  }

  public final StatementContext statement() throws RecognitionException {
    StatementContext _localctx = new StatementContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_statement);
    try {
      setState(112);
      switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
      case 1:
        _localctx = new IfContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(47);
        match(IF);
        setState(48);
        match(LP);
        setState(49);
        expression(0);
        setState(50);
        match(RP);
        setState(51);
        block();
        setState(54);
        switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
        case 1:
          {
          setState(52);
          match(ELSE);
          setState(53);
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
        setState(56);
        match(WHILE);
        setState(57);
        match(LP);
        setState(58);
        expression(0);
        setState(59);
        match(RP);
        setState(62);
        switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
        case 1:
          {
          setState(60);
          block();
          }
          break;
        case 2:
          {
          setState(61);
          empty();
          }
          break;
        }
        }
        break;
      case 3:
        _localctx = new DoContext(_localctx);
        enterOuterAlt(_localctx, 3);
        {
        setState(64);
        match(DO);
        setState(65);
        block();
        setState(66);
        match(WHILE);
        setState(67);
        match(LP);
        setState(68);
        expression(0);
        setState(69);
        match(RP);
        setState(71);
        switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
        case 1:
          {
          setState(70);
          match(SEMICOLON);
          }
          break;
        }
        }
        break;
      case 4:
        _localctx = new ForContext(_localctx);
        enterOuterAlt(_localctx, 4);
        {
        setState(73);
        match(FOR);
        setState(74);
        match(LP);
        setState(76);
        switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
        case 1:
          {
          setState(75);
          initializer();
          }
          break;
        }
        setState(78);
        match(SEMICOLON);
        setState(80);
        switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
        case 1:
          {
          setState(79);
          expression(0);
          }
          break;
        }
        setState(82);
        match(SEMICOLON);
        setState(84);
        switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
        case 1:
          {
          setState(83);
          afterthought();
          }
          break;
        }
        setState(86);
        match(RP);
        setState(89);
        switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
        case 1:
          {
          setState(87);
          block();
          }
          break;
        case 2:
          {
          setState(88);
          empty();
          }
          break;
        }
        }
        break;
      case 5:
        _localctx = new DeclContext(_localctx);
        enterOuterAlt(_localctx, 5);
        {
        setState(91);
        declaration();
        setState(93);
        switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
        case 1:
          {
          setState(92);
          match(SEMICOLON);
          }
          break;
        }
        }
        break;
      case 6:
        _localctx = new ContinueContext(_localctx);
        enterOuterAlt(_localctx, 6);
        {
        setState(95);
        match(CONTINUE);
        setState(97);
        switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
        case 1:
          {
          setState(96);
          match(SEMICOLON);
          }
          break;
        }
        }
        break;
      case 7:
        _localctx = new BreakContext(_localctx);
        enterOuterAlt(_localctx, 7);
        {
        setState(99);
        match(BREAK);
        setState(101);
        switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
        case 1:
          {
          setState(100);
          match(SEMICOLON);
          }
          break;
        }
        }
        break;
      case 8:
        _localctx = new ReturnContext(_localctx);
        enterOuterAlt(_localctx, 8);
        {
        setState(103);
        match(RETURN);
        setState(104);
        expression(0);
        setState(106);
        switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
        case 1:
          {
          setState(105);
          match(SEMICOLON);
          }
          break;
        }
        }
        break;
      case 9:
        _localctx = new ExprContext(_localctx);
        enterOuterAlt(_localctx, 9);
        {
        setState(108);
        expression(0);
        setState(110);
        switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
        case 1:
          {
          setState(109);
          match(SEMICOLON);
          }
          break;
        }
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
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitSingle(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class MultipleContext extends BlockContext {
    public TerminalNode LBRACK() { return getToken(PlanAParser.LBRACK, 0); }
    public TerminalNode RBRACK() { return getToken(PlanAParser.RBRACK, 0); }
    public List<StatementContext> statement() {
      return getRuleContexts(StatementContext.class);
    }
    public StatementContext statement(int i) {
      return getRuleContext(StatementContext.class,i);
    }
    public MultipleContext(BlockContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitMultiple(this);
      else return visitor.visitChildren(this);
    }
  }

  public final BlockContext block() throws RecognitionException {
    BlockContext _localctx = new BlockContext(_ctx, getState());
    enterRule(_localctx, 4, RULE_block);
    try {
      int _alt;
      setState(123);
      switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
      case 1:
        _localctx = new MultipleContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(114);
        match(LBRACK);
        setState(118);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,14,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(115);
            statement();
            }
            } 
          }
          setState(120);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,14,_ctx);
        }
        setState(121);
        match(RBRACK);
        }
        break;
      case 2:
        _localctx = new SingleContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(122);
        statement();
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

  public static class EmptyContext extends ParserRuleContext {
    public TerminalNode SEMICOLON() { return getToken(PlanAParser.SEMICOLON, 0); }
    public EmptyContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_empty; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitEmpty(this);
      else return visitor.visitChildren(this);
    }
  }

  public final EmptyContext empty() throws RecognitionException {
    EmptyContext _localctx = new EmptyContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_empty);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(125);
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

  public static class InitializerContext extends ParserRuleContext {
    public DeclarationContext declaration() {
      return getRuleContext(DeclarationContext.class,0);
    }
    public ExtstartContext extstart() {
      return getRuleContext(ExtstartContext.class,0);
    }
    public TerminalNode ASSIGN() { return getToken(PlanAParser.ASSIGN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public InitializerContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_initializer; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitInitializer(this);
      else return visitor.visitChildren(this);
    }
  }

  public final InitializerContext initializer() throws RecognitionException {
    InitializerContext _localctx = new InitializerContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_initializer);
    try {
      setState(132);
      switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(127);
        declaration();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(128);
        extstart();
        setState(129);
        match(ASSIGN);
        setState(130);
        expression(0);
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

  public static class AfterthoughtContext extends ParserRuleContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public AfterthoughtContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_afterthought; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitAfterthought(this);
      else return visitor.visitChildren(this);
    }
  }

  public final AfterthoughtContext afterthought() throws RecognitionException {
    AfterthoughtContext _localctx = new AfterthoughtContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_afterthought);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(134);
      expression(0);
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
    public List<DeclvarContext> declvar() {
      return getRuleContexts(DeclvarContext.class);
    }
    public DeclvarContext declvar(int i) {
      return getRuleContext(DeclvarContext.class,i);
    }
    public List<TerminalNode> COMMA() { return getTokens(PlanAParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PlanAParser.COMMA, i);
    }
    public DeclarationContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declaration; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitDeclaration(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclarationContext declaration() throws RecognitionException {
    DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_declaration);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(136);
      decltype();
      setState(137);
      declvar();
      setState(142);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,17,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(138);
          match(COMMA);
          setState(139);
          declvar();
          }
          } 
        }
        setState(144);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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
    public TerminalNode ID() { return getToken(PlanAParser.ID, 0); }
    public List<TerminalNode> LBRACE() { return getTokens(PlanAParser.LBRACE); }
    public TerminalNode LBRACE(int i) {
      return getToken(PlanAParser.LBRACE, i);
    }
    public List<TerminalNode> RBRACE() { return getTokens(PlanAParser.RBRACE); }
    public TerminalNode RBRACE(int i) {
      return getToken(PlanAParser.RBRACE, i);
    }
    public DecltypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_decltype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitDecltype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DecltypeContext decltype() throws RecognitionException {
    DecltypeContext _localctx = new DecltypeContext(_ctx, getState());
    enterRule(_localctx, 14, RULE_decltype);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(145);
      if (!(isType())) throw new FailedPredicateException(this, "isType()");
      setState(146);
      match(ID);
      setState(151);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,18,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(147);
          match(LBRACE);
          setState(148);
          match(RBRACE);
          }
          } 
        }
        setState(153);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,18,_ctx);
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

  public static class DeclvarContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(PlanAParser.ID, 0); }
    public TerminalNode ASSIGN() { return getToken(PlanAParser.ASSIGN, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public DeclvarContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_declvar; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitDeclvar(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclvarContext declvar() throws RecognitionException {
    DeclvarContext _localctx = new DeclvarContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_declvar);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(154);
      if (!(!isType())) throw new FailedPredicateException(this, "!isType()");
      setState(155);
      match(ID);
      setState(158);
      switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
      case 1:
        {
        setState(156);
        match(ASSIGN);
        setState(157);
        expression(0);
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
  public static class CompContext extends ExpressionContext {
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public TerminalNode LT() { return getToken(PlanAParser.LT, 0); }
    public TerminalNode LTE() { return getToken(PlanAParser.LTE, 0); }
    public TerminalNode GT() { return getToken(PlanAParser.GT, 0); }
    public TerminalNode GTE() { return getToken(PlanAParser.GTE, 0); }
    public TerminalNode EQ() { return getToken(PlanAParser.EQ, 0); }
    public TerminalNode EQR() { return getToken(PlanAParser.EQR, 0); }
    public TerminalNode NE() { return getToken(PlanAParser.NE, 0); }
    public TerminalNode NER() { return getToken(PlanAParser.NER, 0); }
    public CompContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitComp(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class StringContext extends ExpressionContext {
    public TerminalNode STRING() { return getToken(PlanAParser.STRING, 0); }
    public StringContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitString(this);
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
    public TerminalNode BOOLAND() { return getToken(PlanAParser.BOOLAND, 0); }
    public TerminalNode BOOLOR() { return getToken(PlanAParser.BOOLOR, 0); }
    public BoolContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitBool(this);
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
    public TerminalNode COND() { return getToken(PlanAParser.COND, 0); }
    public TerminalNode COLON() { return getToken(PlanAParser.COLON, 0); }
    public ConditionalContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitConditional(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class AssignmentContext extends ExpressionContext {
    public ExtstartContext extstart() {
      return getRuleContext(ExtstartContext.class,0);
    }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode ASSIGN() { return getToken(PlanAParser.ASSIGN, 0); }
    public TerminalNode AADD() { return getToken(PlanAParser.AADD, 0); }
    public TerminalNode ASUB() { return getToken(PlanAParser.ASUB, 0); }
    public TerminalNode AMUL() { return getToken(PlanAParser.AMUL, 0); }
    public TerminalNode ADIV() { return getToken(PlanAParser.ADIV, 0); }
    public TerminalNode AREM() { return getToken(PlanAParser.AREM, 0); }
    public TerminalNode AAND() { return getToken(PlanAParser.AAND, 0); }
    public TerminalNode AXOR() { return getToken(PlanAParser.AXOR, 0); }
    public TerminalNode AOR() { return getToken(PlanAParser.AOR, 0); }
    public TerminalNode ALSH() { return getToken(PlanAParser.ALSH, 0); }
    public TerminalNode ARSH() { return getToken(PlanAParser.ARSH, 0); }
    public TerminalNode AUSH() { return getToken(PlanAParser.AUSH, 0); }
    public TerminalNode ACAT() { return getToken(PlanAParser.ACAT, 0); }
    public AssignmentContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitAssignment(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class FalseContext extends ExpressionContext {
    public TerminalNode FALSE() { return getToken(PlanAParser.FALSE, 0); }
    public FalseContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitFalse(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NumericContext extends ExpressionContext {
    public TerminalNode OCTAL() { return getToken(PlanAParser.OCTAL, 0); }
    public TerminalNode HEX() { return getToken(PlanAParser.HEX, 0); }
    public TerminalNode INTEGER() { return getToken(PlanAParser.INTEGER, 0); }
    public TerminalNode DECIMAL() { return getToken(PlanAParser.DECIMAL, 0); }
    public NumericContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitNumeric(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class UnaryContext extends ExpressionContext {
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode BOOLNOT() { return getToken(PlanAParser.BOOLNOT, 0); }
    public TerminalNode BWNOT() { return getToken(PlanAParser.BWNOT, 0); }
    public TerminalNode ADD() { return getToken(PlanAParser.ADD, 0); }
    public TerminalNode SUB() { return getToken(PlanAParser.SUB, 0); }
    public UnaryContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitUnary(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class PrecedenceContext extends ExpressionContext {
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
    public PrecedenceContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitPrecedence(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class PreincContext extends ExpressionContext {
    public IncrementContext increment() {
      return getRuleContext(IncrementContext.class,0);
    }
    public ExtstartContext extstart() {
      return getRuleContext(ExtstartContext.class,0);
    }
    public PreincContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitPreinc(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class PostincContext extends ExpressionContext {
    public ExtstartContext extstart() {
      return getRuleContext(ExtstartContext.class,0);
    }
    public IncrementContext increment() {
      return getRuleContext(IncrementContext.class,0);
    }
    public PostincContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitPostinc(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class CastContext extends ExpressionContext {
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public CastContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitCast(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class ExternalContext extends ExpressionContext {
    public ExtstartContext extstart() {
      return getRuleContext(ExtstartContext.class,0);
    }
    public ExternalContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExternal(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class NullContext extends ExpressionContext {
    public TerminalNode NULL() { return getToken(PlanAParser.NULL, 0); }
    public NullContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitNull(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class CatContext extends ExpressionContext {
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public TerminalNode CAT() { return getToken(PlanAParser.CAT, 0); }
    public CatContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitCat(this);
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
    public TerminalNode MUL() { return getToken(PlanAParser.MUL, 0); }
    public TerminalNode DIV() { return getToken(PlanAParser.DIV, 0); }
    public TerminalNode REM() { return getToken(PlanAParser.REM, 0); }
    public TerminalNode ADD() { return getToken(PlanAParser.ADD, 0); }
    public TerminalNode SUB() { return getToken(PlanAParser.SUB, 0); }
    public TerminalNode LSH() { return getToken(PlanAParser.LSH, 0); }
    public TerminalNode RSH() { return getToken(PlanAParser.RSH, 0); }
    public TerminalNode USH() { return getToken(PlanAParser.USH, 0); }
    public TerminalNode BWAND() { return getToken(PlanAParser.BWAND, 0); }
    public TerminalNode BWXOR() { return getToken(PlanAParser.BWXOR, 0); }
    public TerminalNode BWOR() { return getToken(PlanAParser.BWOR, 0); }
    public BinaryContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitBinary(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class CharContext extends ExpressionContext {
    public TerminalNode CHAR() { return getToken(PlanAParser.CHAR, 0); }
    public CharContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitChar(this);
      else return visitor.visitChildren(this);
    }
  }
  public static class TrueContext extends ExpressionContext {
    public TerminalNode TRUE() { return getToken(PlanAParser.TRUE, 0); }
    public TrueContext(ExpressionContext ctx) { copyFrom(ctx); }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitTrue(this);
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
    int _startState = 18;
    enterRecursionRule(_localctx, 18, RULE_expression, _p);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(189);
      switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
      case 1:
        {
        _localctx = new UnaryContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;

        setState(161);
        _la = _input.LA(1);
        if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB))) != 0)) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(162);
        expression(15);
        }
        break;
      case 2:
        {
        _localctx = new CastContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(163);
        match(LP);
        setState(164);
        decltype();
        setState(165);
        match(RP);
        setState(166);
        expression(14);
        }
        break;
      case 3:
        {
        _localctx = new AssignmentContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(168);
        extstart();
        setState(169);
        _la = _input.LA(1);
        if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << AADD) | (1L << ASUB) | (1L << AMUL) | (1L << ADIV) | (1L << AREM) | (1L << AAND) | (1L << AXOR) | (1L << AOR) | (1L << ALSH) | (1L << ARSH) | (1L << AUSH) | (1L << ACAT))) != 0)) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(170);
        expression(1);
        }
        break;
      case 4:
        {
        _localctx = new PrecedenceContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(172);
        match(LP);
        setState(173);
        expression(0);
        setState(174);
        match(RP);
        }
        break;
      case 5:
        {
        _localctx = new NumericContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(176);
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
        setState(177);
        match(STRING);
        }
        break;
      case 7:
        {
        _localctx = new CharContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(178);
        match(CHAR);
        }
        break;
      case 8:
        {
        _localctx = new TrueContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(179);
        match(TRUE);
        }
        break;
      case 9:
        {
        _localctx = new FalseContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(180);
        match(FALSE);
        }
        break;
      case 10:
        {
        _localctx = new NullContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(181);
        match(NULL);
        }
        break;
      case 11:
        {
        _localctx = new ExternalContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(182);
        extstart();
        }
        break;
      case 12:
        {
        _localctx = new PostincContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(183);
        extstart();
        setState(184);
        increment();
        }
        break;
      case 13:
        {
        _localctx = new PreincContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(186);
        increment();
        setState(187);
        extstart();
        }
        break;
      }
      _ctx.stop = _input.LT(-1);
      setState(232);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,22,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          setState(230);
          switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
          case 1:
            {
            _localctx = new CatContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(191);
            if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
            setState(192);
            match(CAT);
            setState(193);
            expression(14);
            }
            break;
          case 2:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(194);
            if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
            setState(195);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << REM))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(196);
            expression(13);
            }
            break;
          case 3:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(197);
            if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
            setState(198);
            _la = _input.LA(1);
            if ( !(_la==ADD || _la==SUB) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(199);
            expression(12);
            }
            break;
          case 4:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(200);
            if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
            setState(201);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSH) | (1L << RSH) | (1L << USH))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(202);
            expression(11);
            }
            break;
          case 5:
            {
            _localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(203);
            if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
            setState(204);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LTE) | (1L << GT) | (1L << GTE))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(205);
            expression(10);
            }
            break;
          case 6:
            {
            _localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(206);
            if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
            setState(207);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << EQR) | (1L << NE) | (1L << NER))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(208);
            expression(9);
            }
            break;
          case 7:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(209);
            if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
            setState(210);
            match(BWAND);
            setState(211);
            expression(8);
            }
            break;
          case 8:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(212);
            if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
            setState(213);
            match(BWXOR);
            setState(214);
            expression(7);
            }
            break;
          case 9:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(215);
            if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
            setState(216);
            match(BWOR);
            setState(217);
            expression(6);
            }
            break;
          case 10:
            {
            _localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(218);
            if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
            setState(219);
            match(BOOLAND);
            setState(220);
            expression(5);
            }
            break;
          case 11:
            {
            _localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(221);
            if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
            setState(222);
            match(BOOLOR);
            setState(223);
            expression(4);
            }
            break;
          case 12:
            {
            _localctx = new ConditionalContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(224);
            if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
            setState(225);
            match(COND);
            setState(226);
            expression(0);
            setState(227);
            match(COLON);
            setState(228);
            expression(2);
            }
            break;
          }
          } 
        }
        setState(234);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,22,_ctx);
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
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExtstart(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExtstartContext extstart() throws RecognitionException {
    ExtstartContext _localctx = new ExtstartContext(_ctx, getState());
    enterRule(_localctx, 20, RULE_extstart);
    try {
      setState(239);
      switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(235);
        extprec();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(236);
        extcast();
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(237);
        exttype();
        }
        break;
      case 4:
        enterOuterAlt(_localctx, 4);
        {
        setState(238);
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
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
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
    public ExtbraceContext extbrace() {
      return getRuleContext(ExtbraceContext.class,0);
    }
    public ExtprecContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_extprec; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExtprec(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExtprecContext extprec() throws RecognitionException {
    ExtprecContext _localctx = new ExtprecContext(_ctx, getState());
    enterRule(_localctx, 22, RULE_extprec);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(241);
      match(LP);
      setState(246);
      switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
      case 1:
        {
        setState(242);
        extprec();
        }
        break;
      case 2:
        {
        setState(243);
        extcast();
        }
        break;
      case 3:
        {
        setState(244);
        exttype();
        }
        break;
      case 4:
        {
        setState(245);
        extmember();
        }
        break;
      }
      setState(248);
      match(RP);
      setState(251);
      switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
      case 1:
        {
        setState(249);
        extdot();
        }
        break;
      case 2:
        {
        setState(250);
        extbrace();
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
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public DecltypeContext decltype() {
      return getRuleContext(DecltypeContext.class,0);
    }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
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
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExtcast(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExtcastContext extcast() throws RecognitionException {
    ExtcastContext _localctx = new ExtcastContext(_ctx, getState());
    enterRule(_localctx, 24, RULE_extcast);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(253);
      match(LP);
      setState(254);
      decltype();
      setState(255);
      match(RP);
      setState(260);
      switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
      case 1:
        {
        setState(256);
        extprec();
        }
        break;
      case 2:
        {
        setState(257);
        extcast();
        }
        break;
      case 3:
        {
        setState(258);
        exttype();
        }
        break;
      case 4:
        {
        setState(259);
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

  public static class ExtbraceContext extends ParserRuleContext {
    public TerminalNode LBRACE() { return getToken(PlanAParser.LBRACE, 0); }
    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class,0);
    }
    public TerminalNode RBRACE() { return getToken(PlanAParser.RBRACE, 0); }
    public ExtdotContext extdot() {
      return getRuleContext(ExtdotContext.class,0);
    }
    public ExtbraceContext extbrace() {
      return getRuleContext(ExtbraceContext.class,0);
    }
    public ExtbraceContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_extbrace; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExtbrace(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExtbraceContext extbrace() throws RecognitionException {
    ExtbraceContext _localctx = new ExtbraceContext(_ctx, getState());
    enterRule(_localctx, 26, RULE_extbrace);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(262);
      match(LBRACE);
      setState(263);
      expression(0);
      setState(264);
      match(RBRACE);
      setState(267);
      switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
      case 1:
        {
        setState(265);
        extdot();
        }
        break;
      case 2:
        {
        setState(266);
        extbrace();
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
    public TerminalNode DOT() { return getToken(PlanAParser.DOT, 0); }
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
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExtdot(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExtdotContext extdot() throws RecognitionException {
    ExtdotContext _localctx = new ExtdotContext(_ctx, getState());
    enterRule(_localctx, 28, RULE_extdot);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(269);
      match(DOT);
      setState(272);
      switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
      case 1:
        {
        setState(270);
        extcall();
        }
        break;
      case 2:
        {
        setState(271);
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
    public TerminalNode ID() { return getToken(PlanAParser.ID, 0); }
    public ExtdotContext extdot() {
      return getRuleContext(ExtdotContext.class,0);
    }
    public ExttypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_exttype; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExttype(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExttypeContext exttype() throws RecognitionException {
    ExttypeContext _localctx = new ExttypeContext(_ctx, getState());
    enterRule(_localctx, 30, RULE_exttype);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(274);
      if (!(isType())) throw new FailedPredicateException(this, "isType()");
      setState(275);
      match(ID);
      setState(276);
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
    public TerminalNode ID() { return getToken(PlanAParser.ID, 0); }
    public ArgumentsContext arguments() {
      return getRuleContext(ArgumentsContext.class,0);
    }
    public ExtdotContext extdot() {
      return getRuleContext(ExtdotContext.class,0);
    }
    public ExtbraceContext extbrace() {
      return getRuleContext(ExtbraceContext.class,0);
    }
    public ExtcallContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_extcall; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExtcall(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExtcallContext extcall() throws RecognitionException {
    ExtcallContext _localctx = new ExtcallContext(_ctx, getState());
    enterRule(_localctx, 32, RULE_extcall);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(278);
      match(ID);
      setState(279);
      arguments();
      setState(282);
      switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
      case 1:
        {
        setState(280);
        extdot();
        }
        break;
      case 2:
        {
        setState(281);
        extbrace();
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
    public TerminalNode ID() { return getToken(PlanAParser.ID, 0); }
    public ExtdotContext extdot() {
      return getRuleContext(ExtdotContext.class,0);
    }
    public ExtbraceContext extbrace() {
      return getRuleContext(ExtbraceContext.class,0);
    }
    public ExtmemberContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_extmember; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExtmember(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExtmemberContext extmember() throws RecognitionException {
    ExtmemberContext _localctx = new ExtmemberContext(_ctx, getState());
    enterRule(_localctx, 34, RULE_extmember);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(284);
      if (!(!isType())) throw new FailedPredicateException(this, "!isType()");
      setState(285);
      match(ID);
      setState(288);
      switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
      case 1:
        {
        setState(286);
        extdot();
        }
        break;
      case 2:
        {
        setState(287);
        extbrace();
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
    public TerminalNode LP() { return getToken(PlanAParser.LP, 0); }
    public TerminalNode RP() { return getToken(PlanAParser.RP, 0); }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public List<TerminalNode> COMMA() { return getTokens(PlanAParser.COMMA); }
    public TerminalNode COMMA(int i) {
      return getToken(PlanAParser.COMMA, i);
    }
    public ArgumentsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_arguments; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitArguments(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ArgumentsContext arguments() throws RecognitionException {
    ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
    enterRule(_localctx, 36, RULE_arguments);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(290);
      match(LP);
      setState(299);
      switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
      case 1:
        {
        setState(291);
        expression(0);
        setState(296);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(292);
          match(COMMA);
          setState(293);
          expression(0);
          }
          }
          setState(298);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
        break;
      }
      setState(301);
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

  public static class IncrementContext extends ParserRuleContext {
    public TerminalNode INCR() { return getToken(PlanAParser.INCR, 0); }
    public TerminalNode DECR() { return getToken(PlanAParser.DECR, 0); }
    public IncrementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_increment; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitIncrement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final IncrementContext increment() throws RecognitionException {
    IncrementContext _localctx = new IncrementContext(_ctx, getState());
    enterRule(_localctx, 38, RULE_increment);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(303);
      _la = _input.LA(1);
      if ( !(_la==INCR || _la==DECR) ) {
      _errHandler.recoverInline(this);
      } else {
        consume();
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
    case 7:
      return decltype_sempred((DecltypeContext)_localctx, predIndex);
    case 8:
      return declvar_sempred((DeclvarContext)_localctx, predIndex);
    case 9:
      return expression_sempred((ExpressionContext)_localctx, predIndex);
    case 15:
      return exttype_sempred((ExttypeContext)_localctx, predIndex);
    case 17:
      return extmember_sempred((ExtmemberContext)_localctx, predIndex);
    }
    return true;
  }
  private boolean decltype_sempred(DecltypeContext _localctx, int predIndex) {
    switch (predIndex) {
    case 0:
      return isType();
    }
    return true;
  }
  private boolean declvar_sempred(DeclvarContext _localctx, int predIndex) {
    switch (predIndex) {
    case 1:
      return !isType();
    }
    return true;
  }
  private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
    switch (predIndex) {
    case 2:
      return precpred(_ctx, 13);
    case 3:
      return precpred(_ctx, 12);
    case 4:
      return precpred(_ctx, 11);
    case 5:
      return precpred(_ctx, 10);
    case 6:
      return precpred(_ctx, 9);
    case 7:
      return precpred(_ctx, 8);
    case 8:
      return precpred(_ctx, 7);
    case 9:
      return precpred(_ctx, 6);
    case 10:
      return precpred(_ctx, 5);
    case 11:
      return precpred(_ctx, 4);
    case 12:
      return precpred(_ctx, 3);
    case 13:
      return precpred(_ctx, 2);
    }
    return true;
  }
  private boolean exttype_sempred(ExttypeContext _localctx, int predIndex) {
    switch (predIndex) {
    case 14:
      return isType();
    }
    return true;
  }
  private boolean extmember_sempred(ExtmemberContext _localctx, int predIndex) {
    switch (predIndex) {
    case 15:
      return !isType();
    }
    return true;
  }

  public static final String _serializedATN =
    "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3H\u0134\4\2\t\2\4"+
    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
    "\4\23\t\23\4\24\t\24\4\25\t\25\3\2\6\2,\n\2\r\2\16\2-\3\2\3\2\3\3\3\3"+
    "\3\3\3\3\3\3\3\3\3\3\5\39\n\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3A\n\3\3\3\3\3"+
    "\3\3\3\3\3\3\3\3\3\3\5\3J\n\3\3\3\3\3\3\3\5\3O\n\3\3\3\3\3\5\3S\n\3\3"+
    "\3\3\3\5\3W\n\3\3\3\3\3\3\3\5\3\\\n\3\3\3\3\3\5\3`\n\3\3\3\3\3\5\3d\n"+
    "\3\3\3\3\3\5\3h\n\3\3\3\3\3\3\3\5\3m\n\3\3\3\3\3\5\3q\n\3\5\3s\n\3\3\4"+
    "\3\4\7\4w\n\4\f\4\16\4z\13\4\3\4\3\4\5\4~\n\4\3\5\3\5\3\6\3\6\3\6\3\6"+
    "\3\6\5\6\u0087\n\6\3\7\3\7\3\b\3\b\3\b\3\b\7\b\u008f\n\b\f\b\16\b\u0092"+
    "\13\b\3\t\3\t\3\t\3\t\7\t\u0098\n\t\f\t\16\t\u009b\13\t\3\n\3\n\3\n\3"+
    "\n\5\n\u00a1\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
    "\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
    "\3\13\3\13\3\13\3\13\5\13\u00c0\n\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
    "\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
    "\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
    "\3\13\3\13\3\13\3\13\7\13\u00e9\n\13\f\13\16\13\u00ec\13\13\3\f\3\f\3"+
    "\f\3\f\5\f\u00f2\n\f\3\r\3\r\3\r\3\r\3\r\5\r\u00f9\n\r\3\r\3\r\3\r\5\r"+
    "\u00fe\n\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u0107\n\16\3\17\3\17"+
    "\3\17\3\17\3\17\5\17\u010e\n\17\3\20\3\20\3\20\5\20\u0113\n\20\3\21\3"+
    "\21\3\21\3\21\3\22\3\22\3\22\3\22\5\22\u011d\n\22\3\23\3\23\3\23\3\23"+
    "\5\23\u0123\n\23\3\24\3\24\3\24\3\24\7\24\u0129\n\24\f\24\16\24\u012c"+
    "\13\24\5\24\u012e\n\24\3\24\3\24\3\25\3\25\3\25\2\3\24\26\2\4\6\b\n\f"+
    "\16\20\22\24\26\30\32\34\36 \"$&(\2\13\4\2\25\26\32\33\3\2\61=\3\2>A\3"+
    "\2\27\31\3\2\32\33\3\2\34\36\3\2 #\3\2$\'\3\2/\60\u0166\2+\3\2\2\2\4r"+
    "\3\2\2\2\6}\3\2\2\2\b\177\3\2\2\2\n\u0086\3\2\2\2\f\u0088\3\2\2\2\16\u008a"+
    "\3\2\2\2\20\u0093\3\2\2\2\22\u009c\3\2\2\2\24\u00bf\3\2\2\2\26\u00f1\3"+
    "\2\2\2\30\u00f3\3\2\2\2\32\u00ff\3\2\2\2\34\u0108\3\2\2\2\36\u010f\3\2"+
    "\2\2 \u0114\3\2\2\2\"\u0118\3\2\2\2$\u011e\3\2\2\2&\u0124\3\2\2\2(\u0131"+
    "\3\2\2\2*,\5\4\3\2+*\3\2\2\2,-\3\2\2\2-+\3\2\2\2-.\3\2\2\2./\3\2\2\2/"+
    "\60\7\2\2\3\60\3\3\2\2\2\61\62\7\r\2\2\62\63\7\b\2\2\63\64\5\24\13\2\64"+
    "\65\7\t\2\2\658\5\6\4\2\66\67\7\16\2\2\679\5\6\4\28\66\3\2\2\289\3\2\2"+
    "\29s\3\2\2\2:;\7\17\2\2;<\7\b\2\2<=\5\24\13\2=@\7\t\2\2>A\5\6\4\2?A\5"+
    "\b\5\2@>\3\2\2\2@?\3\2\2\2As\3\2\2\2BC\7\20\2\2CD\5\6\4\2DE\7\17\2\2E"+
    "F\7\b\2\2FG\5\24\13\2GI\7\t\2\2HJ\7\f\2\2IH\3\2\2\2IJ\3\2\2\2Js\3\2\2"+
    "\2KL\7\21\2\2LN\7\b\2\2MO\5\n\6\2NM\3\2\2\2NO\3\2\2\2OP\3\2\2\2PR\7\f"+
    "\2\2QS\5\24\13\2RQ\3\2\2\2RS\3\2\2\2ST\3\2\2\2TV\7\f\2\2UW\5\f\7\2VU\3"+
    "\2\2\2VW\3\2\2\2WX\3\2\2\2X[\7\t\2\2Y\\\5\6\4\2Z\\\5\b\5\2[Y\3\2\2\2["+
    "Z\3\2\2\2\\s\3\2\2\2]_\5\16\b\2^`\7\f\2\2_^\3\2\2\2_`\3\2\2\2`s\3\2\2"+
    "\2ac\7\22\2\2bd\7\f\2\2cb\3\2\2\2cd\3\2\2\2ds\3\2\2\2eg\7\23\2\2fh\7\f"+
    "\2\2gf\3\2\2\2gh\3\2\2\2hs\3\2\2\2ij\7\24\2\2jl\5\24\13\2km\7\f\2\2lk"+
    "\3\2\2\2lm\3\2\2\2ms\3\2\2\2np\5\24\13\2oq\7\f\2\2po\3\2\2\2pq\3\2\2\2"+
    "qs\3\2\2\2r\61\3\2\2\2r:\3\2\2\2rB\3\2\2\2rK\3\2\2\2r]\3\2\2\2ra\3\2\2"+
    "\2re\3\2\2\2ri\3\2\2\2rn\3\2\2\2s\5\3\2\2\2tx\7\4\2\2uw\5\4\3\2vu\3\2"+
    "\2\2wz\3\2\2\2xv\3\2\2\2xy\3\2\2\2y{\3\2\2\2zx\3\2\2\2{~\7\5\2\2|~\5\4"+
    "\3\2}t\3\2\2\2}|\3\2\2\2~\7\3\2\2\2\177\u0080\7\f\2\2\u0080\t\3\2\2\2"+
    "\u0081\u0087\5\16\b\2\u0082\u0083\5\26\f\2\u0083\u0084\7\61\2\2\u0084"+
    "\u0085\5\24\13\2\u0085\u0087\3\2\2\2\u0086\u0081\3\2\2\2\u0086\u0082\3"+
    "\2\2\2\u0087\13\3\2\2\2\u0088\u0089\5\24\13\2\u0089\r\3\2\2\2\u008a\u008b"+
    "\5\20\t\2\u008b\u0090\5\22\n\2\u008c\u008d\7\13\2\2\u008d\u008f\5\22\n"+
    "\2\u008e\u008c\3\2\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091"+
    "\3\2\2\2\u0091\17\3\2\2\2\u0092\u0090\3\2\2\2\u0093\u0094\6\t\2\2\u0094"+
    "\u0099\7H\2\2\u0095\u0096\7\6\2\2\u0096\u0098\7\7\2\2\u0097\u0095\3\2"+
    "\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a"+
    "\21\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009d\6\n\3\2\u009d\u00a0\7H\2\2"+
    "\u009e\u009f\7\61\2\2\u009f\u00a1\5\24\13\2\u00a0\u009e\3\2\2\2\u00a0"+
    "\u00a1\3\2\2\2\u00a1\23\3\2\2\2\u00a2\u00a3\b\13\1\2\u00a3\u00a4\t\2\2"+
    "\2\u00a4\u00c0\5\24\13\21\u00a5\u00a6\7\b\2\2\u00a6\u00a7\5\20\t\2\u00a7"+
    "\u00a8\7\t\2\2\u00a8\u00a9\5\24\13\20\u00a9\u00c0\3\2\2\2\u00aa\u00ab"+
    "\5\26\f\2\u00ab\u00ac\t\3\2\2\u00ac\u00ad\5\24\13\3\u00ad\u00c0\3\2\2"+
    "\2\u00ae\u00af\7\b\2\2\u00af\u00b0\5\24\13\2\u00b0\u00b1\7\t\2\2\u00b1"+
    "\u00c0\3\2\2\2\u00b2\u00c0\t\4\2\2\u00b3\u00c0\7B\2\2\u00b4\u00c0\7C\2"+
    "\2\u00b5\u00c0\7D\2\2\u00b6\u00c0\7E\2\2\u00b7\u00c0\7F\2\2\u00b8\u00c0"+
    "\5\26\f\2\u00b9\u00ba\5\26\f\2\u00ba\u00bb\5(\25\2\u00bb\u00c0\3\2\2\2"+
    "\u00bc\u00bd\5(\25\2\u00bd\u00be\5\26\f\2\u00be\u00c0\3\2\2\2\u00bf\u00a2"+
    "\3\2\2\2\u00bf\u00a5\3\2\2\2\u00bf\u00aa\3\2\2\2\u00bf\u00ae\3\2\2\2\u00bf"+
    "\u00b2\3\2\2\2\u00bf\u00b3\3\2\2\2\u00bf\u00b4\3\2\2\2\u00bf\u00b5\3\2"+
    "\2\2\u00bf\u00b6\3\2\2\2\u00bf\u00b7\3\2\2\2\u00bf\u00b8\3\2\2\2\u00bf"+
    "\u00b9\3\2\2\2\u00bf\u00bc\3\2\2\2\u00c0\u00ea\3\2\2\2\u00c1\u00c2\f\17"+
    "\2\2\u00c2\u00c3\7\37\2\2\u00c3\u00e9\5\24\13\20\u00c4\u00c5\f\16\2\2"+
    "\u00c5\u00c6\t\5\2\2\u00c6\u00e9\5\24\13\17\u00c7\u00c8\f\r\2\2\u00c8"+
    "\u00c9\t\6\2\2\u00c9\u00e9\5\24\13\16\u00ca\u00cb\f\f\2\2\u00cb\u00cc"+
    "\t\7\2\2\u00cc\u00e9\5\24\13\r\u00cd\u00ce\f\13\2\2\u00ce\u00cf\t\b\2"+
    "\2\u00cf\u00e9\5\24\13\f\u00d0\u00d1\f\n\2\2\u00d1\u00d2\t\t\2\2\u00d2"+
    "\u00e9\5\24\13\13\u00d3\u00d4\f\t\2\2\u00d4\u00d5\7(\2\2\u00d5\u00e9\5"+
    "\24\13\n\u00d6\u00d7\f\b\2\2\u00d7\u00d8\7)\2\2\u00d8\u00e9\5\24\13\t"+
    "\u00d9\u00da\f\7\2\2\u00da\u00db\7*\2\2\u00db\u00e9\5\24\13\b\u00dc\u00dd"+
    "\f\6\2\2\u00dd\u00de\7+\2\2\u00de\u00e9\5\24\13\7\u00df\u00e0\f\5\2\2"+
    "\u00e0\u00e1\7,\2\2\u00e1\u00e9\5\24\13\6\u00e2\u00e3\f\4\2\2\u00e3\u00e4"+
    "\7-\2\2\u00e4\u00e5\5\24\13\2\u00e5\u00e6\7.\2\2\u00e6\u00e7\5\24\13\4"+
    "\u00e7\u00e9\3\2\2\2\u00e8\u00c1\3\2\2\2\u00e8\u00c4\3\2\2\2\u00e8\u00c7"+
    "\3\2\2\2\u00e8\u00ca\3\2\2\2\u00e8\u00cd\3\2\2\2\u00e8\u00d0\3\2\2\2\u00e8"+
    "\u00d3\3\2\2\2\u00e8\u00d6\3\2\2\2\u00e8\u00d9\3\2\2\2\u00e8\u00dc\3\2"+
    "\2\2\u00e8\u00df\3\2\2\2\u00e8\u00e2\3\2\2\2\u00e9\u00ec\3\2\2\2\u00ea"+
    "\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\25\3\2\2\2\u00ec\u00ea\3\2\2"+
    "\2\u00ed\u00f2\5\30\r\2\u00ee\u00f2\5\32\16\2\u00ef\u00f2\5 \21\2\u00f0"+
    "\u00f2\5$\23\2\u00f1\u00ed\3\2\2\2\u00f1\u00ee\3\2\2\2\u00f1\u00ef\3\2"+
    "\2\2\u00f1\u00f0\3\2\2\2\u00f2\27\3\2\2\2\u00f3\u00f8\7\b\2\2\u00f4\u00f9"+
    "\5\30\r\2\u00f5\u00f9\5\32\16\2\u00f6\u00f9\5 \21\2\u00f7\u00f9\5$\23"+
    "\2\u00f8\u00f4\3\2\2\2\u00f8\u00f5\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f8\u00f7"+
    "\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u00fd\7\t\2\2\u00fb\u00fe\5\36\20\2"+
    "\u00fc\u00fe\5\34\17\2\u00fd\u00fb\3\2\2\2\u00fd\u00fc\3\2\2\2\u00fd\u00fe"+
    "\3\2\2\2\u00fe\31\3\2\2\2\u00ff\u0100\7\b\2\2\u0100\u0101\5\20\t\2\u0101"+
    "\u0106\7\t\2\2\u0102\u0107\5\30\r\2\u0103\u0107\5\32\16\2\u0104\u0107"+
    "\5 \21\2\u0105\u0107\5$\23\2\u0106\u0102\3\2\2\2\u0106\u0103\3\2\2\2\u0106"+
    "\u0104\3\2\2\2\u0106\u0105\3\2\2\2\u0107\33\3\2\2\2\u0108\u0109\7\6\2"+
    "\2\u0109\u010a\5\24\13\2\u010a\u010d\7\7\2\2\u010b\u010e\5\36\20\2\u010c"+
    "\u010e\5\34\17\2\u010d\u010b\3\2\2\2\u010d\u010c\3\2\2\2\u010d\u010e\3"+
    "\2\2\2\u010e\35\3\2\2\2\u010f\u0112\7\n\2\2\u0110\u0113\5\"\22\2\u0111"+
    "\u0113\5$\23\2\u0112\u0110\3\2\2\2\u0112\u0111\3\2\2\2\u0113\37\3\2\2"+
    "\2\u0114\u0115\6\21\20\2\u0115\u0116\7H\2\2\u0116\u0117\5\36\20\2\u0117"+
    "!\3\2\2\2\u0118\u0119\7H\2\2\u0119\u011c\5&\24\2\u011a\u011d\5\36\20\2"+
    "\u011b\u011d\5\34\17\2\u011c\u011a\3\2\2\2\u011c\u011b\3\2\2\2\u011c\u011d"+
    "\3\2\2\2\u011d#\3\2\2\2\u011e\u011f\6\23\21\2\u011f\u0122\7H\2\2\u0120"+
    "\u0123\5\36\20\2\u0121\u0123\5\34\17\2\u0122\u0120\3\2\2\2\u0122\u0121"+
    "\3\2\2\2\u0122\u0123\3\2\2\2\u0123%\3\2\2\2\u0124\u012d\7\b\2\2\u0125"+
    "\u012a\5\24\13\2\u0126\u0127\7\13\2\2\u0127\u0129\5\24\13\2\u0128\u0126"+
    "\3\2\2\2\u0129\u012c\3\2\2\2\u012a\u0128\3\2\2\2\u012a\u012b\3\2\2\2\u012b"+
    "\u012e\3\2\2\2\u012c\u012a\3\2\2\2\u012d\u0125\3\2\2\2\u012d\u012e\3\2"+
    "\2\2\u012e\u012f\3\2\2\2\u012f\u0130\7\t\2\2\u0130\'\3\2\2\2\u0131\u0132"+
    "\t\n\2\2\u0132)\3\2\2\2#-8@INRV[_cglprx}\u0086\u0090\u0099\u00a0\u00bf"+
    "\u00e8\u00ea\u00f1\u00f8\u00fd\u0106\u010d\u0112\u011c\u0122\u012a\u012d";
  public static final ATN _ATN =
    new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
