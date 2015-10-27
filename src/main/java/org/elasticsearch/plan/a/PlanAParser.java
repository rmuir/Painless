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
    WS=1, COMMENT=2, LBRACK=3, RBRACK=4, LBRACE=5, RBRACE=6, LP=7, RP=8, DOT=9, 
    COMMA=10, SEMICOLON=11, IF=12, ELSE=13, WHILE=14, DO=15, FOR=16, CONTINUE=17, 
    BREAK=18, RETURN=19, NEW=20, BOOLNOT=21, BWNOT=22, MUL=23, DIV=24, REM=25, 
    ADD=26, SUB=27, LSH=28, RSH=29, USH=30, CAT=31, LT=32, LTE=33, GT=34, 
    GTE=35, EQ=36, EQR=37, NE=38, NER=39, BWAND=40, BWXOR=41, BWOR=42, BOOLAND=43, 
    BOOLOR=44, COND=45, COLON=46, INCR=47, DECR=48, ASSIGN=49, AADD=50, ASUB=51, 
    AMUL=52, ADIV=53, AREM=54, AAND=55, AXOR=56, AOR=57, ALSH=58, ARSH=59, 
    AUSH=60, ACAT=61, OCTAL=62, HEX=63, INTEGER=64, DECIMAL=65, STRING=66, 
    CHAR=67, TRUE=68, FALSE=69, NULL=70, ID=71, GENERIC=72;
  public static final int
    RULE_source = 0, RULE_statement = 1, RULE_block = 2, RULE_empty = 3, RULE_initializer = 4, 
    RULE_afterthought = 5, RULE_declaration = 6, RULE_decltype = 7, RULE_declvar = 8, 
    RULE_type = 9, RULE_id = 10, RULE_expression = 11, RULE_extstart = 12, 
    RULE_extprec = 13, RULE_extcast = 14, RULE_extbrace = 15, RULE_extdot = 16, 
    RULE_exttype = 17, RULE_extcall = 18, RULE_extmember = 19, RULE_extnew = 20, 
    RULE_arguments = 21, RULE_increment = 22;
  public static final String[] ruleNames = {
    "source", "statement", "block", "empty", "initializer", "afterthought", 
    "declaration", "decltype", "declvar", "type", "id", "expression", "extstart", 
    "extprec", "extcast", "extbrace", "extdot", "exttype", "extcall", "extmember", 
    "extnew", "arguments", "increment"
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
      setState(47); 
      _errHandler.sync(this);
      _alt = 1;
      do {
        switch (_alt) {
        case 1:
          {
          {
          setState(46);
          statement();
          }
          }
          break;
        default:
          throw new NoViableAltException(this);
        }
        setState(49); 
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,0,_ctx);
      } while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
      setState(51);
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
      setState(118);
      switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
      case 1:
        _localctx = new IfContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(53);
        match(IF);
        setState(54);
        match(LP);
        setState(55);
        expression(0);
        setState(56);
        match(RP);
        setState(57);
        block();
        setState(60);
        switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
        case 1:
          {
          setState(58);
          match(ELSE);
          setState(59);
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
        setState(62);
        match(WHILE);
        setState(63);
        match(LP);
        setState(64);
        expression(0);
        setState(65);
        match(RP);
        setState(68);
        switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
        case 1:
          {
          setState(66);
          block();
          }
          break;
        case 2:
          {
          setState(67);
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
        setState(70);
        match(DO);
        setState(71);
        block();
        setState(72);
        match(WHILE);
        setState(73);
        match(LP);
        setState(74);
        expression(0);
        setState(75);
        match(RP);
        setState(77);
        switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
        case 1:
          {
          setState(76);
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
        setState(79);
        match(FOR);
        setState(80);
        match(LP);
        setState(82);
        switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
        case 1:
          {
          setState(81);
          initializer();
          }
          break;
        }
        setState(84);
        match(SEMICOLON);
        setState(86);
        switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
        case 1:
          {
          setState(85);
          expression(0);
          }
          break;
        }
        setState(88);
        match(SEMICOLON);
        setState(90);
        switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
        case 1:
          {
          setState(89);
          afterthought();
          }
          break;
        }
        setState(92);
        match(RP);
        setState(95);
        switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
        case 1:
          {
          setState(93);
          block();
          }
          break;
        case 2:
          {
          setState(94);
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
        setState(97);
        declaration();
        setState(99);
        switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
        case 1:
          {
          setState(98);
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
        setState(101);
        match(CONTINUE);
        setState(103);
        switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
        case 1:
          {
          setState(102);
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
        setState(105);
        match(BREAK);
        setState(107);
        switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
        case 1:
          {
          setState(106);
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
        setState(109);
        match(RETURN);
        setState(110);
        expression(0);
        setState(112);
        switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
        case 1:
          {
          setState(111);
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
        setState(114);
        expression(0);
        setState(116);
        switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
        case 1:
          {
          setState(115);
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
      setState(129);
      switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
      case 1:
        _localctx = new MultipleContext(_localctx);
        enterOuterAlt(_localctx, 1);
        {
        setState(120);
        match(LBRACK);
        setState(124);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,14,_ctx);
        while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
          if ( _alt==1 ) {
            {
            {
            setState(121);
            statement();
            }
            } 
          }
          setState(126);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,14,_ctx);
        }
        setState(127);
        match(RBRACK);
        }
        break;
      case 2:
        _localctx = new SingleContext(_localctx);
        enterOuterAlt(_localctx, 2);
        {
        setState(128);
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
      setState(131);
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
      setState(135);
      switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(133);
        declaration();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(134);
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
      setState(137);
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
      setState(139);
      decltype();
      setState(140);
      declvar();
      setState(145);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,17,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(141);
          match(COMMA);
          setState(142);
          declvar();
          }
          } 
        }
        setState(147);
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
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
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
      setState(148);
      type();
      setState(153);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,18,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          {
          {
          setState(149);
          match(LBRACE);
          setState(150);
          match(RBRACE);
          }
          } 
        }
        setState(155);
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
    public IdContext id() {
      return getRuleContext(IdContext.class,0);
    }
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
      setState(156);
      id();
      setState(159);
      switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
      case 1:
        {
        setState(157);
        match(ASSIGN);
        setState(158);
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

  public static class TypeContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(PlanAParser.ID, 0); }
    public TerminalNode GENERIC() { return getToken(PlanAParser.GENERIC, 0); }
    public TypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_type; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitType(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TypeContext type() throws RecognitionException {
    TypeContext _localctx = new TypeContext(_ctx, getState());
    enterRule(_localctx, 18, RULE_type);
    try {
      setState(165);
      switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(161);
        if (!(isType())) throw new FailedPredicateException(this, "isType()");
        setState(162);
        match(ID);
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(163);
        if (!(isType())) throw new FailedPredicateException(this, "isType()");
        setState(164);
        match(GENERIC);
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

  public static class IdContext extends ParserRuleContext {
    public TerminalNode ID() { return getToken(PlanAParser.ID, 0); }
    public IdContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_id; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitId(this);
      else return visitor.visitChildren(this);
    }
  }

  public final IdContext id() throws RecognitionException {
    IdContext _localctx = new IdContext(_ctx, getState());
    enterRule(_localctx, 20, RULE_id);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(167);
      if (!(!isType())) throw new FailedPredicateException(this, "!isType()");
      setState(168);
      match(ID);
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
    int _startState = 22;
    enterRecursionRule(_localctx, 22, RULE_expression, _p);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(199);
      switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
      case 1:
        {
        _localctx = new UnaryContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;

        setState(171);
        _la = _input.LA(1);
        if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB))) != 0)) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(172);
        expression(15);
        }
        break;
      case 2:
        {
        _localctx = new CastContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(173);
        match(LP);
        setState(174);
        decltype();
        setState(175);
        match(RP);
        setState(176);
        expression(14);
        }
        break;
      case 3:
        {
        _localctx = new AssignmentContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(178);
        extstart();
        setState(179);
        _la = _input.LA(1);
        if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << AADD) | (1L << ASUB) | (1L << AMUL) | (1L << ADIV) | (1L << AREM) | (1L << AAND) | (1L << AXOR) | (1L << AOR) | (1L << ALSH) | (1L << ARSH) | (1L << AUSH) | (1L << ACAT))) != 0)) ) {
        _errHandler.recoverInline(this);
        } else {
          consume();
        }
        setState(180);
        expression(1);
        }
        break;
      case 4:
        {
        _localctx = new PrecedenceContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(182);
        match(LP);
        setState(183);
        expression(0);
        setState(184);
        match(RP);
        }
        break;
      case 5:
        {
        _localctx = new NumericContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(186);
        _la = _input.LA(1);
        if ( !(((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & ((1L << (OCTAL - 62)) | (1L << (HEX - 62)) | (1L << (INTEGER - 62)) | (1L << (DECIMAL - 62)))) != 0)) ) {
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
        setState(187);
        match(STRING);
        }
        break;
      case 7:
        {
        _localctx = new CharContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(188);
        match(CHAR);
        }
        break;
      case 8:
        {
        _localctx = new TrueContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(189);
        match(TRUE);
        }
        break;
      case 9:
        {
        _localctx = new FalseContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(190);
        match(FALSE);
        }
        break;
      case 10:
        {
        _localctx = new NullContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(191);
        match(NULL);
        }
        break;
      case 11:
        {
        _localctx = new ExternalContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(192);
        extstart();
        }
        break;
      case 12:
        {
        _localctx = new PostincContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(193);
        extstart();
        setState(194);
        increment();
        }
        break;
      case 13:
        {
        _localctx = new PreincContext(_localctx);
        _ctx = _localctx;
        _prevctx = _localctx;
        setState(196);
        increment();
        setState(197);
        extstart();
        }
        break;
      }
      _ctx.stop = _input.LT(-1);
      setState(242);
      _errHandler.sync(this);
      _alt = getInterpreter().adaptivePredict(_input,23,_ctx);
      while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
        if ( _alt==1 ) {
          if ( _parseListeners!=null ) triggerExitRuleEvent();
          _prevctx = _localctx;
          {
          setState(240);
          switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
          case 1:
            {
            _localctx = new CatContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(201);
            if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
            setState(202);
            match(CAT);
            setState(203);
            expression(14);
            }
            break;
          case 2:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(204);
            if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
            setState(205);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << REM))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(206);
            expression(13);
            }
            break;
          case 3:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(207);
            if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
            setState(208);
            _la = _input.LA(1);
            if ( !(_la==ADD || _la==SUB) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(209);
            expression(12);
            }
            break;
          case 4:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(210);
            if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
            setState(211);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSH) | (1L << RSH) | (1L << USH))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(212);
            expression(11);
            }
            break;
          case 5:
            {
            _localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(213);
            if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
            setState(214);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LTE) | (1L << GT) | (1L << GTE))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(215);
            expression(10);
            }
            break;
          case 6:
            {
            _localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(216);
            if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
            setState(217);
            _la = _input.LA(1);
            if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << EQR) | (1L << NE) | (1L << NER))) != 0)) ) {
            _errHandler.recoverInline(this);
            } else {
              consume();
            }
            setState(218);
            expression(9);
            }
            break;
          case 7:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(219);
            if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
            setState(220);
            match(BWAND);
            setState(221);
            expression(8);
            }
            break;
          case 8:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(222);
            if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
            setState(223);
            match(BWXOR);
            setState(224);
            expression(7);
            }
            break;
          case 9:
            {
            _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(225);
            if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
            setState(226);
            match(BWOR);
            setState(227);
            expression(6);
            }
            break;
          case 10:
            {
            _localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(228);
            if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
            setState(229);
            match(BOOLAND);
            setState(230);
            expression(5);
            }
            break;
          case 11:
            {
            _localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(231);
            if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
            setState(232);
            match(BOOLOR);
            setState(233);
            expression(4);
            }
            break;
          case 12:
            {
            _localctx = new ConditionalContext(new ExpressionContext(_parentctx, _parentState));
            pushNewRecursionContext(_localctx, _startState, RULE_expression);
            setState(234);
            if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
            setState(235);
            match(COND);
            setState(236);
            expression(0);
            setState(237);
            match(COLON);
            setState(238);
            expression(2);
            }
            break;
          }
          } 
        }
        setState(244);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input,23,_ctx);
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
    public ExtnewContext extnew() {
      return getRuleContext(ExtnewContext.class,0);
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
    enterRule(_localctx, 24, RULE_extstart);
    try {
      setState(250);
      switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
      case 1:
        enterOuterAlt(_localctx, 1);
        {
        setState(245);
        extprec();
        }
        break;
      case 2:
        enterOuterAlt(_localctx, 2);
        {
        setState(246);
        extcast();
        }
        break;
      case 3:
        enterOuterAlt(_localctx, 3);
        {
        setState(247);
        exttype();
        }
        break;
      case 4:
        enterOuterAlt(_localctx, 4);
        {
        setState(248);
        extmember();
        }
        break;
      case 5:
        enterOuterAlt(_localctx, 5);
        {
        setState(249);
        extnew();
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
    public ExtnewContext extnew() {
      return getRuleContext(ExtnewContext.class,0);
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
    enterRule(_localctx, 26, RULE_extprec);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(252);
      match(LP);
      setState(258);
      switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
      case 1:
        {
        setState(253);
        extprec();
        }
        break;
      case 2:
        {
        setState(254);
        extcast();
        }
        break;
      case 3:
        {
        setState(255);
        exttype();
        }
        break;
      case 4:
        {
        setState(256);
        extmember();
        }
        break;
      case 5:
        {
        setState(257);
        extnew();
        }
        break;
      }
      setState(260);
      match(RP);
      setState(263);
      switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
      case 1:
        {
        setState(261);
        extdot();
        }
        break;
      case 2:
        {
        setState(262);
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
    public ExtnewContext extnew() {
      return getRuleContext(ExtnewContext.class,0);
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
    enterRule(_localctx, 28, RULE_extcast);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(265);
      match(LP);
      setState(266);
      decltype();
      setState(267);
      match(RP);
      setState(273);
      switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
      case 1:
        {
        setState(268);
        extprec();
        }
        break;
      case 2:
        {
        setState(269);
        extcast();
        }
        break;
      case 3:
        {
        setState(270);
        exttype();
        }
        break;
      case 4:
        {
        setState(271);
        extmember();
        }
        break;
      case 5:
        {
        setState(272);
        extnew();
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
    enterRule(_localctx, 30, RULE_extbrace);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(275);
      match(LBRACE);
      setState(276);
      expression(0);
      setState(277);
      match(RBRACE);
      setState(280);
      switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
      case 1:
        {
        setState(278);
        extdot();
        }
        break;
      case 2:
        {
        setState(279);
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
    enterRule(_localctx, 32, RULE_extdot);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(282);
      match(DOT);
      setState(285);
      switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
      case 1:
        {
        setState(283);
        extcall();
        }
        break;
      case 2:
        {
        setState(284);
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
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
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
    enterRule(_localctx, 34, RULE_exttype);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(287);
      type();
      setState(288);
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
    public IdContext id() {
      return getRuleContext(IdContext.class,0);
    }
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
    enterRule(_localctx, 36, RULE_extcall);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(290);
      id();
      setState(291);
      arguments();
      setState(294);
      switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
      case 1:
        {
        setState(292);
        extdot();
        }
        break;
      case 2:
        {
        setState(293);
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
    public IdContext id() {
      return getRuleContext(IdContext.class,0);
    }
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
    enterRule(_localctx, 38, RULE_extmember);
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(296);
      id();
      setState(299);
      switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
      case 1:
        {
        setState(297);
        extdot();
        }
        break;
      case 2:
        {
        setState(298);
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

  public static class ExtnewContext extends ParserRuleContext {
    public TerminalNode NEW() { return getToken(PlanAParser.NEW, 0); }
    public TypeContext type() {
      return getRuleContext(TypeContext.class,0);
    }
    public ArgumentsContext arguments() {
      return getRuleContext(ArgumentsContext.class,0);
    }
    public ExtdotContext extdot() {
      return getRuleContext(ExtdotContext.class,0);
    }
    public ExtbraceContext extbrace() {
      return getRuleContext(ExtbraceContext.class,0);
    }
    public List<TerminalNode> LBRACE() { return getTokens(PlanAParser.LBRACE); }
    public TerminalNode LBRACE(int i) {
      return getToken(PlanAParser.LBRACE, i);
    }
    public List<ExpressionContext> expression() {
      return getRuleContexts(ExpressionContext.class);
    }
    public ExpressionContext expression(int i) {
      return getRuleContext(ExpressionContext.class,i);
    }
    public List<TerminalNode> RBRACE() { return getTokens(PlanAParser.RBRACE); }
    public TerminalNode RBRACE(int i) {
      return getToken(PlanAParser.RBRACE, i);
    }
    public ExtnewContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }
    @Override public int getRuleIndex() { return RULE_extnew; }
    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if ( visitor instanceof PlanAVisitor ) return ((PlanAVisitor<? extends T>)visitor).visitExtnew(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExtnewContext extnew() throws RecognitionException {
    ExtnewContext _localctx = new ExtnewContext(_ctx, getState());
    enterRule(_localctx, 40, RULE_extnew);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
      setState(301);
      match(NEW);
      setState(302);
      type();
      setState(319);
      switch (_input.LA(1)) {
      case LP:
        {
        {
        setState(303);
        arguments();
        setState(306);
        switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
        case 1:
          {
          setState(304);
          extdot();
          }
          break;
        case 2:
          {
          setState(305);
          extbrace();
          }
          break;
        }
        }
        }
        break;
      case LBRACE:
        {
        {
        setState(312); 
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
          case 1:
            {
            {
            setState(308);
            match(LBRACE);
            setState(309);
            expression(0);
            setState(310);
            match(RBRACE);
            }
            }
            break;
          default:
            throw new NoViableAltException(this);
          }
          setState(314); 
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input,33,_ctx);
        } while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
        setState(317);
        switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
        case 1:
          {
          setState(316);
          extdot();
          }
          break;
        }
        }
        }
        break;
      default:
        throw new NoViableAltException(this);
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
    enterRule(_localctx, 42, RULE_arguments);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      {
      setState(321);
      match(LP);
      setState(330);
      switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
      case 1:
        {
        setState(322);
        expression(0);
        setState(327);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la==COMMA) {
          {
          {
          setState(323);
          match(COMMA);
          setState(324);
          expression(0);
          }
          }
          setState(329);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        }
        break;
      }
      setState(332);
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
    enterRule(_localctx, 44, RULE_increment);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
      setState(334);
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
    case 9:
      return type_sempred((TypeContext)_localctx, predIndex);
    case 10:
      return id_sempred((IdContext)_localctx, predIndex);
    case 11:
      return expression_sempred((ExpressionContext)_localctx, predIndex);
    }
    return true;
  }
  private boolean type_sempred(TypeContext _localctx, int predIndex) {
    switch (predIndex) {
    case 0:
      return isType();
    case 1:
      return isType();
    }
    return true;
  }
  private boolean id_sempred(IdContext _localctx, int predIndex) {
    switch (predIndex) {
    case 2:
      return !isType();
    }
    return true;
  }
  private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
    switch (predIndex) {
    case 3:
      return precpred(_ctx, 13);
    case 4:
      return precpred(_ctx, 12);
    case 5:
      return precpred(_ctx, 11);
    case 6:
      return precpred(_ctx, 10);
    case 7:
      return precpred(_ctx, 9);
    case 8:
      return precpred(_ctx, 8);
    case 9:
      return precpred(_ctx, 7);
    case 10:
      return precpred(_ctx, 6);
    case 11:
      return precpred(_ctx, 5);
    case 12:
      return precpred(_ctx, 4);
    case 13:
      return precpred(_ctx, 3);
    case 14:
      return precpred(_ctx, 2);
    }
    return true;
  }

  public static final String _serializedATN =
    "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3J\u0153\4\2\t\2\4"+
    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\6\2\62"+
    "\n\2\r\2\16\2\63\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3?\n\3\3\3\3\3"+
    "\3\3\3\3\3\3\3\3\5\3G\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3P\n\3\3\3\3\3"+
    "\3\3\5\3U\n\3\3\3\3\3\5\3Y\n\3\3\3\3\3\5\3]\n\3\3\3\3\3\3\3\5\3b\n\3\3"+
    "\3\3\3\5\3f\n\3\3\3\3\3\5\3j\n\3\3\3\3\3\5\3n\n\3\3\3\3\3\3\3\5\3s\n\3"+
    "\3\3\3\3\5\3w\n\3\5\3y\n\3\3\4\3\4\7\4}\n\4\f\4\16\4\u0080\13\4\3\4\3"+
    "\4\5\4\u0084\n\4\3\5\3\5\3\6\3\6\5\6\u008a\n\6\3\7\3\7\3\b\3\b\3\b\3\b"+
    "\7\b\u0092\n\b\f\b\16\b\u0095\13\b\3\t\3\t\3\t\7\t\u009a\n\t\f\t\16\t"+
    "\u009d\13\t\3\n\3\n\3\n\5\n\u00a2\n\n\3\13\3\13\3\13\3\13\5\13\u00a8\n"+
    "\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
    "\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00ca"+
    "\n\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
    "\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
    "\3\r\3\r\3\r\3\r\3\r\7\r\u00f3\n\r\f\r\16\r\u00f6\13\r\3\16\3\16\3\16"+
    "\3\16\3\16\5\16\u00fd\n\16\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u0105\n"+
    "\17\3\17\3\17\3\17\5\17\u010a\n\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20"+
    "\3\20\5\20\u0114\n\20\3\21\3\21\3\21\3\21\3\21\5\21\u011b\n\21\3\22\3"+
    "\22\3\22\5\22\u0120\n\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\5\24\u0129"+
    "\n\24\3\25\3\25\3\25\5\25\u012e\n\25\3\26\3\26\3\26\3\26\3\26\5\26\u0135"+
    "\n\26\3\26\3\26\3\26\3\26\6\26\u013b\n\26\r\26\16\26\u013c\3\26\5\26\u0140"+
    "\n\26\5\26\u0142\n\26\3\27\3\27\3\27\3\27\7\27\u0148\n\27\f\27\16\27\u014b"+
    "\13\27\5\27\u014d\n\27\3\27\3\27\3\30\3\30\3\30\2\3\30\31\2\4\6\b\n\f"+
    "\16\20\22\24\26\30\32\34\36 \"$&(*,.\2\13\4\2\27\30\34\35\3\2\63?\3\2"+
    "@C\3\2\31\33\3\2\34\35\3\2\36 \3\2\"%\3\2&)\3\2\61\62\u018b\2\61\3\2\2"+
    "\2\4x\3\2\2\2\6\u0083\3\2\2\2\b\u0085\3\2\2\2\n\u0089\3\2\2\2\f\u008b"+
    "\3\2\2\2\16\u008d\3\2\2\2\20\u0096\3\2\2\2\22\u009e\3\2\2\2\24\u00a7\3"+
    "\2\2\2\26\u00a9\3\2\2\2\30\u00c9\3\2\2\2\32\u00fc\3\2\2\2\34\u00fe\3\2"+
    "\2\2\36\u010b\3\2\2\2 \u0115\3\2\2\2\"\u011c\3\2\2\2$\u0121\3\2\2\2&\u0124"+
    "\3\2\2\2(\u012a\3\2\2\2*\u012f\3\2\2\2,\u0143\3\2\2\2.\u0150\3\2\2\2\60"+
    "\62\5\4\3\2\61\60\3\2\2\2\62\63\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64"+
    "\65\3\2\2\2\65\66\7\2\2\3\66\3\3\2\2\2\678\7\16\2\289\7\t\2\29:\5\30\r"+
    "\2:;\7\n\2\2;>\5\6\4\2<=\7\17\2\2=?\5\6\4\2><\3\2\2\2>?\3\2\2\2?y\3\2"+
    "\2\2@A\7\20\2\2AB\7\t\2\2BC\5\30\r\2CF\7\n\2\2DG\5\6\4\2EG\5\b\5\2FD\3"+
    "\2\2\2FE\3\2\2\2Gy\3\2\2\2HI\7\21\2\2IJ\5\6\4\2JK\7\20\2\2KL\7\t\2\2L"+
    "M\5\30\r\2MO\7\n\2\2NP\7\r\2\2ON\3\2\2\2OP\3\2\2\2Py\3\2\2\2QR\7\22\2"+
    "\2RT\7\t\2\2SU\5\n\6\2TS\3\2\2\2TU\3\2\2\2UV\3\2\2\2VX\7\r\2\2WY\5\30"+
    "\r\2XW\3\2\2\2XY\3\2\2\2YZ\3\2\2\2Z\\\7\r\2\2[]\5\f\7\2\\[\3\2\2\2\\]"+
    "\3\2\2\2]^\3\2\2\2^a\7\n\2\2_b\5\6\4\2`b\5\b\5\2a_\3\2\2\2a`\3\2\2\2b"+
    "y\3\2\2\2ce\5\16\b\2df\7\r\2\2ed\3\2\2\2ef\3\2\2\2fy\3\2\2\2gi\7\23\2"+
    "\2hj\7\r\2\2ih\3\2\2\2ij\3\2\2\2jy\3\2\2\2km\7\24\2\2ln\7\r\2\2ml\3\2"+
    "\2\2mn\3\2\2\2ny\3\2\2\2op\7\25\2\2pr\5\30\r\2qs\7\r\2\2rq\3\2\2\2rs\3"+
    "\2\2\2sy\3\2\2\2tv\5\30\r\2uw\7\r\2\2vu\3\2\2\2vw\3\2\2\2wy\3\2\2\2x\67"+
    "\3\2\2\2x@\3\2\2\2xH\3\2\2\2xQ\3\2\2\2xc\3\2\2\2xg\3\2\2\2xk\3\2\2\2x"+
    "o\3\2\2\2xt\3\2\2\2y\5\3\2\2\2z~\7\5\2\2{}\5\4\3\2|{\3\2\2\2}\u0080\3"+
    "\2\2\2~|\3\2\2\2~\177\3\2\2\2\177\u0081\3\2\2\2\u0080~\3\2\2\2\u0081\u0084"+
    "\7\6\2\2\u0082\u0084\5\4\3\2\u0083z\3\2\2\2\u0083\u0082\3\2\2\2\u0084"+
    "\7\3\2\2\2\u0085\u0086\7\r\2\2\u0086\t\3\2\2\2\u0087\u008a\5\16\b\2\u0088"+
    "\u008a\5\30\r\2\u0089\u0087\3\2\2\2\u0089\u0088\3\2\2\2\u008a\13\3\2\2"+
    "\2\u008b\u008c\5\30\r\2\u008c\r\3\2\2\2\u008d\u008e\5\20\t\2\u008e\u0093"+
    "\5\22\n\2\u008f\u0090\7\f\2\2\u0090\u0092\5\22\n\2\u0091\u008f\3\2\2\2"+
    "\u0092\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\17"+
    "\3\2\2\2\u0095\u0093\3\2\2\2\u0096\u009b\5\24\13\2\u0097\u0098\7\7\2\2"+
    "\u0098\u009a\7\b\2\2\u0099\u0097\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099"+
    "\3\2\2\2\u009b\u009c\3\2\2\2\u009c\21\3\2\2\2\u009d\u009b\3\2\2\2\u009e"+
    "\u00a1\5\26\f\2\u009f\u00a0\7\63\2\2\u00a0\u00a2\5\30\r\2\u00a1\u009f"+
    "\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\23\3\2\2\2\u00a3\u00a4\6\13\2\2\u00a4"+
    "\u00a8\7I\2\2\u00a5\u00a6\6\13\3\2\u00a6\u00a8\7J\2\2\u00a7\u00a3\3\2"+
    "\2\2\u00a7\u00a5\3\2\2\2\u00a8\25\3\2\2\2\u00a9\u00aa\6\f\4\2\u00aa\u00ab"+
    "\7I\2\2\u00ab\27\3\2\2\2\u00ac\u00ad\b\r\1\2\u00ad\u00ae\t\2\2\2\u00ae"+
    "\u00ca\5\30\r\21\u00af\u00b0\7\t\2\2\u00b0\u00b1\5\20\t\2\u00b1\u00b2"+
    "\7\n\2\2\u00b2\u00b3\5\30\r\20\u00b3\u00ca\3\2\2\2\u00b4\u00b5\5\32\16"+
    "\2\u00b5\u00b6\t\3\2\2\u00b6\u00b7\5\30\r\3\u00b7\u00ca\3\2\2\2\u00b8"+
    "\u00b9\7\t\2\2\u00b9\u00ba\5\30\r\2\u00ba\u00bb\7\n\2\2\u00bb\u00ca\3"+
    "\2\2\2\u00bc\u00ca\t\4\2\2\u00bd\u00ca\7D\2\2\u00be\u00ca\7E\2\2\u00bf"+
    "\u00ca\7F\2\2\u00c0\u00ca\7G\2\2\u00c1\u00ca\7H\2\2\u00c2\u00ca\5\32\16"+
    "\2\u00c3\u00c4\5\32\16\2\u00c4\u00c5\5.\30\2\u00c5\u00ca\3\2\2\2\u00c6"+
    "\u00c7\5.\30\2\u00c7\u00c8\5\32\16\2\u00c8\u00ca\3\2\2\2\u00c9\u00ac\3"+
    "\2\2\2\u00c9\u00af\3\2\2\2\u00c9\u00b4\3\2\2\2\u00c9\u00b8\3\2\2\2\u00c9"+
    "\u00bc\3\2\2\2\u00c9\u00bd\3\2\2\2\u00c9\u00be\3\2\2\2\u00c9\u00bf\3\2"+
    "\2\2\u00c9\u00c0\3\2\2\2\u00c9\u00c1\3\2\2\2\u00c9\u00c2\3\2\2\2\u00c9"+
    "\u00c3\3\2\2\2\u00c9\u00c6\3\2\2\2\u00ca\u00f4\3\2\2\2\u00cb\u00cc\f\17"+
    "\2\2\u00cc\u00cd\7!\2\2\u00cd\u00f3\5\30\r\20\u00ce\u00cf\f\16\2\2\u00cf"+
    "\u00d0\t\5\2\2\u00d0\u00f3\5\30\r\17\u00d1\u00d2\f\r\2\2\u00d2\u00d3\t"+
    "\6\2\2\u00d3\u00f3\5\30\r\16\u00d4\u00d5\f\f\2\2\u00d5\u00d6\t\7\2\2\u00d6"+
    "\u00f3\5\30\r\r\u00d7\u00d8\f\13\2\2\u00d8\u00d9\t\b\2\2\u00d9\u00f3\5"+
    "\30\r\f\u00da\u00db\f\n\2\2\u00db\u00dc\t\t\2\2\u00dc\u00f3\5\30\r\13"+
    "\u00dd\u00de\f\t\2\2\u00de\u00df\7*\2\2\u00df\u00f3\5\30\r\n\u00e0\u00e1"+
    "\f\b\2\2\u00e1\u00e2\7+\2\2\u00e2\u00f3\5\30\r\t\u00e3\u00e4\f\7\2\2\u00e4"+
    "\u00e5\7,\2\2\u00e5\u00f3\5\30\r\b\u00e6\u00e7\f\6\2\2\u00e7\u00e8\7-"+
    "\2\2\u00e8\u00f3\5\30\r\7\u00e9\u00ea\f\5\2\2\u00ea\u00eb\7.\2\2\u00eb"+
    "\u00f3\5\30\r\6\u00ec\u00ed\f\4\2\2\u00ed\u00ee\7/\2\2\u00ee\u00ef\5\30"+
    "\r\2\u00ef\u00f0\7\60\2\2\u00f0\u00f1\5\30\r\4\u00f1\u00f3\3\2\2\2\u00f2"+
    "\u00cb\3\2\2\2\u00f2\u00ce\3\2\2\2\u00f2\u00d1\3\2\2\2\u00f2\u00d4\3\2"+
    "\2\2\u00f2\u00d7\3\2\2\2\u00f2\u00da\3\2\2\2\u00f2\u00dd\3\2\2\2\u00f2"+
    "\u00e0\3\2\2\2\u00f2\u00e3\3\2\2\2\u00f2\u00e6\3\2\2\2\u00f2\u00e9\3\2"+
    "\2\2\u00f2\u00ec\3\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4"+
    "\u00f5\3\2\2\2\u00f5\31\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f7\u00fd\5\34\17"+
    "\2\u00f8\u00fd\5\36\20\2\u00f9\u00fd\5$\23\2\u00fa\u00fd\5(\25\2\u00fb"+
    "\u00fd\5*\26\2\u00fc\u00f7\3\2\2\2\u00fc\u00f8\3\2\2\2\u00fc\u00f9\3\2"+
    "\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fb\3\2\2\2\u00fd\33\3\2\2\2\u00fe\u0104"+
    "\7\t\2\2\u00ff\u0105\5\34\17\2\u0100\u0105\5\36\20\2\u0101\u0105\5$\23"+
    "\2\u0102\u0105\5(\25\2\u0103\u0105\5*\26\2\u0104\u00ff\3\2\2\2\u0104\u0100"+
    "\3\2\2\2\u0104\u0101\3\2\2\2\u0104\u0102\3\2\2\2\u0104\u0103\3\2\2\2\u0105"+
    "\u0106\3\2\2\2\u0106\u0109\7\n\2\2\u0107\u010a\5\"\22\2\u0108\u010a\5"+
    " \21\2\u0109\u0107\3\2\2\2\u0109\u0108\3\2\2\2\u0109\u010a\3\2\2\2\u010a"+
    "\35\3\2\2\2\u010b\u010c\7\t\2\2\u010c\u010d\5\20\t\2\u010d\u0113\7\n\2"+
    "\2\u010e\u0114\5\34\17\2\u010f\u0114\5\36\20\2\u0110\u0114\5$\23\2\u0111"+
    "\u0114\5(\25\2\u0112\u0114\5*\26\2\u0113\u010e\3\2\2\2\u0113\u010f\3\2"+
    "\2\2\u0113\u0110\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0112\3\2\2\2\u0114"+
    "\37\3\2\2\2\u0115\u0116\7\7\2\2\u0116\u0117\5\30\r\2\u0117\u011a\7\b\2"+
    "\2\u0118\u011b\5\"\22\2\u0119\u011b\5 \21\2\u011a\u0118\3\2\2\2\u011a"+
    "\u0119\3\2\2\2\u011a\u011b\3\2\2\2\u011b!\3\2\2\2\u011c\u011f\7\13\2\2"+
    "\u011d\u0120\5&\24\2\u011e\u0120\5(\25\2\u011f\u011d\3\2\2\2\u011f\u011e"+
    "\3\2\2\2\u0120#\3\2\2\2\u0121\u0122\5\24\13\2\u0122\u0123\5\"\22\2\u0123"+
    "%\3\2\2\2\u0124\u0125\5\26\f\2\u0125\u0128\5,\27\2\u0126\u0129\5\"\22"+
    "\2\u0127\u0129\5 \21\2\u0128\u0126\3\2\2\2\u0128\u0127\3\2\2\2\u0128\u0129"+
    "\3\2\2\2\u0129\'\3\2\2\2\u012a\u012d\5\26\f\2\u012b\u012e\5\"\22\2\u012c"+
    "\u012e\5 \21\2\u012d\u012b\3\2\2\2\u012d\u012c\3\2\2\2\u012d\u012e\3\2"+
    "\2\2\u012e)\3\2\2\2\u012f\u0130\7\26\2\2\u0130\u0141\5\24\13\2\u0131\u0134"+
    "\5,\27\2\u0132\u0135\5\"\22\2\u0133\u0135\5 \21\2\u0134\u0132\3\2\2\2"+
    "\u0134\u0133\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0142\3\2\2\2\u0136\u0137"+
    "\7\7\2\2\u0137\u0138\5\30\r\2\u0138\u0139\7\b\2\2\u0139\u013b\3\2\2\2"+
    "\u013a\u0136\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013a\3\2\2\2\u013c\u013d"+
    "\3\2\2\2\u013d\u013f\3\2\2\2\u013e\u0140\5\"\22\2\u013f\u013e\3\2\2\2"+
    "\u013f\u0140\3\2\2\2\u0140\u0142\3\2\2\2\u0141\u0131\3\2\2\2\u0141\u013a"+
    "\3\2\2\2\u0142+\3\2\2\2\u0143\u014c\7\t\2\2\u0144\u0149\5\30\r\2\u0145"+
    "\u0146\7\f\2\2\u0146\u0148\5\30\r\2\u0147\u0145\3\2\2\2\u0148\u014b\3"+
    "\2\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014d\3\2\2\2\u014b"+
    "\u0149\3\2\2\2\u014c\u0144\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014e\3\2"+
    "\2\2\u014e\u014f\7\n\2\2\u014f-\3\2\2\2\u0150\u0151\t\n\2\2\u0151/\3\2"+
    "\2\2(\63>FOTX\\aeimrvx~\u0083\u0089\u0093\u009b\u00a1\u00a7\u00c9\u00f2"+
    "\u00f4\u00fc\u0104\u0109\u0113\u011a\u011f\u0128\u012d\u0134\u013c\u013f"+
    "\u0141\u0149\u014c";
  public static final ATN _ATN =
    new ATNDeserializer().deserialize(_serializedATN.toCharArray());
  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
