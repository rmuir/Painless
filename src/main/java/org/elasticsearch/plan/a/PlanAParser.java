// Generated from src/main/java/org/elasticsearch/plan/a/PlanA.g4 by ANTLR 4.5
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
public class PlanAParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            WS = 1, LBRACK = 2, RBRACK = 3, LBRACE = 4, RBRACE = 5, LP = 6, RP = 7, DOT = 8, COMMA = 9,
            SEMICOLON = 10, IF = 11, ELSE = 12, WHILE = 13, DO = 14, FOR = 15, CONTINUE = 16, BREAK = 17,
            RETURN = 18, BOOLNOT = 19, BWNOT = 20, MUL = 21, DIV = 22, REM = 23, ADD = 24, SUB = 25,
            LSH = 26, RSH = 27, USH = 28, CAT = 29, LT = 30, LTE = 31, GT = 32, GTE = 33, EQ = 34, NE = 35,
            BWAND = 36, BWXOR = 37, BWOR = 38, BOOLAND = 39, BOOLOR = 40, COND = 41, COLON = 42,
            INCR = 43, DECR = 44, ASSIGN = 45, AADD = 46, ASUB = 47, AMUL = 48, ADIV = 49, AREM = 50,
            AAND = 51, AXOR = 52, AOR = 53, ALSH = 54, ARSH = 55, AUSH = 56, ACAT = 57, OCTAL = 58,
            HEX = 59, INTEGER = 60, DECIMAL = 61, STRING = 62, CHAR = 63, TRUE = 64, FALSE = 65,
            NULL = 66, VOID = 67, ID = 68;
    public static final int
            RULE_source = 0, RULE_statement = 1, RULE_block = 2, RULE_empty = 3, RULE_declaration = 4,
            RULE_decltype = 5, RULE_declvar = 6, RULE_expression = 7, RULE_extstart = 8,
            RULE_extprec = 9, RULE_extcast = 10, RULE_extbrace = 11, RULE_extdot = 12,
            RULE_exttype = 13, RULE_extcall = 14, RULE_extmember = 15, RULE_arguments = 16,
            RULE_increment = 17;
    public static final String[] ruleNames = {
            "source", "statement", "block", "empty", "declaration", "decltype", "declvar",
            "expression", "extstart", "extprec", "extcast", "extbrace", "extdot",
            "exttype", "extcall", "extmember", "arguments", "increment"
    };

    private static final String[] _LITERAL_NAMES = {
            null, null, "'{'", "'}'", "'['", "']'", "'('", "')'", "'.'", "','", "';'",
            "'if'", "'else'", "'while'", "'do'", "'for'", "'continue'", "'break'",
            "'return'", "'!'", "'~'", "'*'", "'/'", "'%'", "'+'", "'-'", "'<<'", "'>>'",
            "'>>>'", "'..'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&'",
            "'^'", "'|'", "'&&'", "'||'", "'?'", "':'", "'++'", "'--'", "'='", "'+='",
            "'-='", "'*='", "'/='", "'%='", "'&='", "'^='", "'|='", "'<<='", "'>>='",
            "'>>>='", "'..='", null, null, null, null, null, null, "'true'", "'false'",
            "'null'", "'void'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, "WS", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "LP", "RP", "DOT",
            "COMMA", "SEMICOLON", "IF", "ELSE", "WHILE", "DO", "FOR", "CONTINUE",
            "BREAK", "RETURN", "BOOLNOT", "BWNOT", "MUL", "DIV", "REM", "ADD", "SUB",
            "LSH", "RSH", "USH", "CAT", "LT", "LTE", "GT", "GTE", "EQ", "NE", "BWAND",
            "BWXOR", "BWOR", "BOOLAND", "BOOLOR", "COND", "COLON", "INCR", "DECR",
            "ASSIGN", "AADD", "ASUB", "AMUL", "ADIV", "AREM", "AAND", "AXOR", "AOR",
            "ALSH", "ARSH", "AUSH", "ACAT", "OCTAL", "HEX", "INTEGER", "DECIMAL",
            "STRING", "CHAR", "TRUE", "FALSE", "NULL", "VOID", "ID"
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
    public String getGrammarFileName() {
        return "PlanA.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }


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
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class SourceContext extends ParserRuleContext {
        public TerminalNode EOF() {
            return getToken(PlanAParser.EOF, 0);
        }

        public List<StatementContext> statement() {
            return getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return getRuleContext(StatementContext.class, i);
        }

        public SourceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_source;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitSource(this);
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
                setState(37);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            {
                                setState(36);
                                statement();
                            }
                        }
                        break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(39);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                setState(41);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class StatementContext extends ParserRuleContext {
        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_statement;
        }

        public StatementContext() {
        }

        public void copyFrom(StatementContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class DeclContext extends StatementContext {
        public DeclarationContext declaration() {
            return getRuleContext(DeclarationContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PlanAParser.SEMICOLON, 0);
        }

        public DeclContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitDecl(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class BreakContext extends StatementContext {
        public TerminalNode BREAK() {
            return getToken(PlanAParser.BREAK, 0);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PlanAParser.SEMICOLON, 0);
        }

        public BreakContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitBreak(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ContinueContext extends StatementContext {
        public TerminalNode CONTINUE() {
            return getToken(PlanAParser.CONTINUE, 0);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PlanAParser.SEMICOLON, 0);
        }

        public ContinueContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitContinue(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ForContext extends StatementContext {
        public TerminalNode FOR() {
            return getToken(PlanAParser.FOR, 0);
        }

        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public List<TerminalNode> SEMICOLON() {
            return getTokens(PlanAParser.SEMICOLON);
        }

        public TerminalNode SEMICOLON(int i) {
            return getToken(PlanAParser.SEMICOLON, i);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public BlockContext block() {
            return getRuleContext(BlockContext.class, 0);
        }

        public EmptyContext empty() {
            return getRuleContext(EmptyContext.class, 0);
        }

        public DeclarationContext declaration() {
            return getRuleContext(DeclarationContext.class, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public ForContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitFor(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ExprContext extends StatementContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PlanAParser.SEMICOLON, 0);
        }

        public ExprContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class DoContext extends StatementContext {
        public TerminalNode DO() {
            return getToken(PlanAParser.DO, 0);
        }

        public BlockContext block() {
            return getRuleContext(BlockContext.class, 0);
        }

        public TerminalNode WHILE() {
            return getToken(PlanAParser.WHILE, 0);
        }

        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public DoContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitDo(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class WhileContext extends StatementContext {
        public TerminalNode WHILE() {
            return getToken(PlanAParser.WHILE, 0);
        }

        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public BlockContext block() {
            return getRuleContext(BlockContext.class, 0);
        }

        public EmptyContext empty() {
            return getRuleContext(EmptyContext.class, 0);
        }

        public WhileContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitWhile(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class IfContext extends StatementContext {
        public TerminalNode IF() {
            return getToken(PlanAParser.IF, 0);
        }

        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public List<BlockContext> block() {
            return getRuleContexts(BlockContext.class);
        }

        public BlockContext block(int i) {
            return getRuleContext(BlockContext.class, i);
        }

        public TerminalNode ELSE() {
            return getToken(PlanAParser.ELSE, 0);
        }

        public IfContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitIf(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ReturnContext extends StatementContext {
        public TerminalNode RETURN() {
            return getToken(PlanAParser.RETURN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode SEMICOLON() {
            return getToken(PlanAParser.SEMICOLON, 0);
        }

        public ReturnContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitReturn(this);
            else return visitor.visitChildren(this);
        }
    }

    public final StatementContext statement() throws RecognitionException {
        StatementContext _localctx = new StatementContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_statement);
        try {
            setState(99);
            switch (getInterpreter().adaptivePredict(_input, 7, _ctx)) {
                case 1:
                    _localctx = new IfContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(43);
                    match(IF);
                    setState(44);
                    match(LP);
                    setState(45);
                    expression(0);
                    setState(46);
                    match(RP);
                    setState(47);
                    block();
                    setState(50);
                    switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                        case 1: {
                            setState(48);
                            match(ELSE);
                            setState(49);
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
                    setState(52);
                    match(WHILE);
                    setState(53);
                    match(LP);
                    setState(54);
                    expression(0);
                    setState(55);
                    match(RP);
                    setState(58);
                    switch (getInterpreter().adaptivePredict(_input, 2, _ctx)) {
                        case 1: {
                            setState(56);
                            block();
                        }
                        break;
                        case 2: {
                            setState(57);
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
                    setState(60);
                    match(DO);
                    setState(61);
                    block();
                    setState(62);
                    match(WHILE);
                    setState(63);
                    match(LP);
                    setState(64);
                    expression(0);
                    setState(65);
                    match(RP);
                }
                break;
                case 4:
                    _localctx = new ForContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(67);
                    match(FOR);
                    setState(68);
                    match(LP);
                    setState(70);
                    switch (getInterpreter().adaptivePredict(_input, 3, _ctx)) {
                        case 1: {
                            setState(69);
                            declaration();
                        }
                        break;
                    }
                    setState(72);
                    match(SEMICOLON);
                    setState(74);
                    switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
                        case 1: {
                            setState(73);
                            expression(0);
                        }
                        break;
                    }
                    setState(76);
                    match(SEMICOLON);
                    setState(78);
                    switch (getInterpreter().adaptivePredict(_input, 5, _ctx)) {
                        case 1: {
                            setState(77);
                            expression(0);
                        }
                        break;
                    }
                    setState(80);
                    match(RP);
                    setState(83);
                    switch (getInterpreter().adaptivePredict(_input, 6, _ctx)) {
                        case 1: {
                            setState(81);
                            block();
                        }
                        break;
                        case 2: {
                            setState(82);
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
                    setState(85);
                    declaration();
                    setState(86);
                    match(SEMICOLON);
                }
                break;
                case 6:
                    _localctx = new ContinueContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                {
                    setState(88);
                    match(CONTINUE);
                    setState(89);
                    match(SEMICOLON);
                }
                break;
                case 7:
                    _localctx = new BreakContext(_localctx);
                    enterOuterAlt(_localctx, 7);
                {
                    setState(90);
                    match(BREAK);
                    setState(91);
                    match(SEMICOLON);
                }
                break;
                case 8:
                    _localctx = new ReturnContext(_localctx);
                    enterOuterAlt(_localctx, 8);
                {
                    setState(92);
                    match(RETURN);
                    setState(93);
                    expression(0);
                    setState(94);
                    match(SEMICOLON);
                }
                break;
                case 9:
                    _localctx = new ExprContext(_localctx);
                    enterOuterAlt(_localctx, 9);
                {
                    setState(96);
                    expression(0);
                    setState(97);
                    match(SEMICOLON);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class BlockContext extends ParserRuleContext {
        public BlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block;
        }

        public BlockContext() {
        }

        public void copyFrom(BlockContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class SingleContext extends BlockContext {
        public StatementContext statement() {
            return getRuleContext(StatementContext.class, 0);
        }

        public SingleContext(BlockContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitSingle(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class MultipleContext extends BlockContext {
        public TerminalNode LBRACK() {
            return getToken(PlanAParser.LBRACK, 0);
        }

        public TerminalNode RBRACK() {
            return getToken(PlanAParser.RBRACK, 0);
        }

        public List<StatementContext> statement() {
            return getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return getRuleContext(StatementContext.class, i);
        }

        public MultipleContext(BlockContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitMultiple(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BlockContext block() throws RecognitionException {
        BlockContext _localctx = new BlockContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_block);
        try {
            int _alt;
            setState(110);
            switch (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
                case 1:
                    _localctx = new MultipleContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(101);
                    match(LBRACK);
                    setState(105);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 8, _ctx);
                    while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                        if (_alt == 1) {
                            {
                                {
                                    setState(102);
                                    statement();
                                }
                            }
                        }
                        setState(107);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 8, _ctx);
                    }
                    setState(108);
                    match(RBRACK);
                }
                break;
                case 2:
                    _localctx = new SingleContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(109);
                    statement();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class EmptyContext extends ParserRuleContext {
        public TerminalNode SEMICOLON() {
            return getToken(PlanAParser.SEMICOLON, 0);
        }

        public EmptyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_empty;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitEmpty(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EmptyContext empty() throws RecognitionException {
        EmptyContext _localctx = new EmptyContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_empty);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(112);
                match(SEMICOLON);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DeclarationContext extends ParserRuleContext {
        public DecltypeContext decltype() {
            return getRuleContext(DecltypeContext.class, 0);
        }

        public List<DeclvarContext> declvar() {
            return getRuleContexts(DeclvarContext.class);
        }

        public DeclvarContext declvar(int i) {
            return getRuleContext(DeclvarContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(PlanAParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(PlanAParser.COMMA, i);
        }

        public DeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_declaration;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitDeclaration(this);
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
                setState(114);
                decltype();
                setState(115);
                declvar();
                setState(120);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(116);
                            match(COMMA);
                            setState(117);
                            declvar();
                        }
                    }
                    setState(122);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DecltypeContext extends ParserRuleContext {
        public TerminalNode ID() {
            return getToken(PlanAParser.ID, 0);
        }

        public List<TerminalNode> LBRACE() {
            return getTokens(PlanAParser.LBRACE);
        }

        public TerminalNode LBRACE(int i) {
            return getToken(PlanAParser.LBRACE, i);
        }

        public List<TerminalNode> RBRACE() {
            return getTokens(PlanAParser.RBRACE);
        }

        public TerminalNode RBRACE(int i) {
            return getToken(PlanAParser.RBRACE, i);
        }

        public DecltypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_decltype;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitDecltype(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DecltypeContext decltype() throws RecognitionException {
        DecltypeContext _localctx = new DecltypeContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_decltype);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(123);
                if (!(isType())) throw new FailedPredicateException(this, "isType()");
                setState(124);
                match(ID);
                setState(129);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 11, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(125);
                                match(LBRACE);
                                setState(126);
                                match(RBRACE);
                            }
                        }
                    }
                    setState(131);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 11, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DeclvarContext extends ParserRuleContext {
        public TerminalNode ID() {
            return getToken(PlanAParser.ID, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(PlanAParser.ASSIGN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public DeclvarContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_declvar;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitDeclvar(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DeclvarContext declvar() throws RecognitionException {
        DeclvarContext _localctx = new DeclvarContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_declvar);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(132);
                if (!(!isType())) throw new FailedPredicateException(this, "!isType()");
                setState(133);
                match(ID);
                setState(136);
                _la = _input.LA(1);
                if (_la == ASSIGN) {
                    {
                        setState(134);
                        match(ASSIGN);
                        setState(135);
                        expression(0);
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExpressionContext extends ParserRuleContext {
        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expression;
        }

        public ExpressionContext() {
        }

        public void copyFrom(ExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ExtContext extends ExpressionContext {
        public ExtstartContext extstart() {
            return getRuleContext(ExtstartContext.class, 0);
        }

        public ExtContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExt(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CompContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode LT() {
            return getToken(PlanAParser.LT, 0);
        }

        public TerminalNode LTE() {
            return getToken(PlanAParser.LTE, 0);
        }

        public TerminalNode GT() {
            return getToken(PlanAParser.GT, 0);
        }

        public TerminalNode GTE() {
            return getToken(PlanAParser.GTE, 0);
        }

        public TerminalNode EQ() {
            return getToken(PlanAParser.EQ, 0);
        }

        public TerminalNode NE() {
            return getToken(PlanAParser.NE, 0);
        }

        public CompContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitComp(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class StringContext extends ExpressionContext {
        public TerminalNode STRING() {
            return getToken(PlanAParser.STRING, 0);
        }

        public StringContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitString(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class BoolContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode BOOLAND() {
            return getToken(PlanAParser.BOOLAND, 0);
        }

        public TerminalNode BOOLOR() {
            return getToken(PlanAParser.BOOLOR, 0);
        }

        public BoolContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitBool(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ConditionalContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode COND() {
            return getToken(PlanAParser.COND, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlanAParser.COLON, 0);
        }

        public ConditionalContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitConditional(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class AssignmentContext extends ExpressionContext {
        public ExtstartContext extstart() {
            return getRuleContext(ExtstartContext.class, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode ASSIGN() {
            return getToken(PlanAParser.ASSIGN, 0);
        }

        public TerminalNode AADD() {
            return getToken(PlanAParser.AADD, 0);
        }

        public TerminalNode ASUB() {
            return getToken(PlanAParser.ASUB, 0);
        }

        public TerminalNode AMUL() {
            return getToken(PlanAParser.AMUL, 0);
        }

        public TerminalNode ADIV() {
            return getToken(PlanAParser.ADIV, 0);
        }

        public TerminalNode AREM() {
            return getToken(PlanAParser.AREM, 0);
        }

        public TerminalNode AAND() {
            return getToken(PlanAParser.AAND, 0);
        }

        public TerminalNode AXOR() {
            return getToken(PlanAParser.AXOR, 0);
        }

        public TerminalNode AOR() {
            return getToken(PlanAParser.AOR, 0);
        }

        public TerminalNode ALSH() {
            return getToken(PlanAParser.ALSH, 0);
        }

        public TerminalNode ARSH() {
            return getToken(PlanAParser.ARSH, 0);
        }

        public TerminalNode AUSH() {
            return getToken(PlanAParser.AUSH, 0);
        }

        public TerminalNode ACAT() {
            return getToken(PlanAParser.ACAT, 0);
        }

        public AssignmentContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitAssignment(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class FalseContext extends ExpressionContext {
        public TerminalNode FALSE() {
            return getToken(PlanAParser.FALSE, 0);
        }

        public FalseContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitFalse(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class NumericContext extends ExpressionContext {
        public TerminalNode OCTAL() {
            return getToken(PlanAParser.OCTAL, 0);
        }

        public TerminalNode HEX() {
            return getToken(PlanAParser.HEX, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlanAParser.INTEGER, 0);
        }

        public TerminalNode DECIMAL() {
            return getToken(PlanAParser.DECIMAL, 0);
        }

        public NumericContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitNumeric(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class UnaryContext extends ExpressionContext {
        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode BOOLNOT() {
            return getToken(PlanAParser.BOOLNOT, 0);
        }

        public TerminalNode BWNOT() {
            return getToken(PlanAParser.BWNOT, 0);
        }

        public TerminalNode ADD() {
            return getToken(PlanAParser.ADD, 0);
        }

        public TerminalNode SUB() {
            return getToken(PlanAParser.SUB, 0);
        }

        public UnaryContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitUnary(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class PrecedenceContext extends ExpressionContext {
        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public PrecedenceContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitPrecedence(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class PreincContext extends ExpressionContext {
        public IncrementContext increment() {
            return getRuleContext(IncrementContext.class, 0);
        }

        public ExtstartContext extstart() {
            return getRuleContext(ExtstartContext.class, 0);
        }

        public PreincContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitPreinc(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class PostincContext extends ExpressionContext {
        public ExtstartContext extstart() {
            return getRuleContext(ExtstartContext.class, 0);
        }

        public IncrementContext increment() {
            return getRuleContext(IncrementContext.class, 0);
        }

        public PostincContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitPostinc(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CastContext extends ExpressionContext {
        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public DecltypeContext decltype() {
            return getRuleContext(DecltypeContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public CastContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitCast(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class NullContext extends ExpressionContext {
        public TerminalNode NULL() {
            return getToken(PlanAParser.NULL, 0);
        }

        public NullContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitNull(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CatContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode CAT() {
            return getToken(PlanAParser.CAT, 0);
        }

        public CatContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitCat(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class BinaryContext extends ExpressionContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode MUL() {
            return getToken(PlanAParser.MUL, 0);
        }

        public TerminalNode DIV() {
            return getToken(PlanAParser.DIV, 0);
        }

        public TerminalNode REM() {
            return getToken(PlanAParser.REM, 0);
        }

        public TerminalNode ADD() {
            return getToken(PlanAParser.ADD, 0);
        }

        public TerminalNode SUB() {
            return getToken(PlanAParser.SUB, 0);
        }

        public TerminalNode LSH() {
            return getToken(PlanAParser.LSH, 0);
        }

        public TerminalNode RSH() {
            return getToken(PlanAParser.RSH, 0);
        }

        public TerminalNode USH() {
            return getToken(PlanAParser.USH, 0);
        }

        public TerminalNode BWAND() {
            return getToken(PlanAParser.BWAND, 0);
        }

        public TerminalNode BWXOR() {
            return getToken(PlanAParser.BWXOR, 0);
        }

        public TerminalNode BWOR() {
            return getToken(PlanAParser.BWOR, 0);
        }

        public BinaryContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitBinary(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CharContext extends ExpressionContext {
        public TerminalNode CHAR() {
            return getToken(PlanAParser.CHAR, 0);
        }

        public CharContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitChar(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class TrueContext extends ExpressionContext {
        public TerminalNode TRUE() {
            return getToken(PlanAParser.TRUE, 0);
        }

        public TrueContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitTrue(this);
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
        int _startState = 14;
        enterRecursionRule(_localctx, 14, RULE_expression, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(167);
                switch (getInterpreter().adaptivePredict(_input, 13, _ctx)) {
                    case 1: {
                        _localctx = new UnaryContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(139);
                        _la = _input.LA(1);
                        if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLNOT) | (1L << BWNOT) | (1L << ADD) | (1L << SUB))) != 0))) {
                            _errHandler.recoverInline(this);
                        } else {
                            consume();
                        }
                        setState(140);
                        expression(15);
                    }
                    break;
                    case 2: {
                        _localctx = new CastContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(141);
                        match(LP);
                        setState(142);
                        decltype();
                        setState(143);
                        match(RP);
                        setState(144);
                        expression(14);
                    }
                    break;
                    case 3: {
                        _localctx = new AssignmentContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(146);
                        extstart();
                        setState(147);
                        _la = _input.LA(1);
                        if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << AADD) | (1L << ASUB) | (1L << AMUL) | (1L << ADIV) | (1L << AREM) | (1L << AAND) | (1L << AXOR) | (1L << AOR) | (1L << ALSH) | (1L << ARSH) | (1L << AUSH) | (1L << ACAT))) != 0))) {
                            _errHandler.recoverInline(this);
                        } else {
                            consume();
                        }
                        setState(148);
                        expression(1);
                    }
                    break;
                    case 4: {
                        _localctx = new PrecedenceContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(150);
                        match(LP);
                        setState(151);
                        expression(0);
                        setState(152);
                        match(RP);
                    }
                    break;
                    case 5: {
                        _localctx = new NumericContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(154);
                        _la = _input.LA(1);
                        if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OCTAL) | (1L << HEX) | (1L << INTEGER) | (1L << DECIMAL))) != 0))) {
                            _errHandler.recoverInline(this);
                        } else {
                            consume();
                        }
                    }
                    break;
                    case 6: {
                        _localctx = new StringContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(155);
                        match(STRING);
                    }
                    break;
                    case 7: {
                        _localctx = new CharContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(156);
                        match(CHAR);
                    }
                    break;
                    case 8: {
                        _localctx = new TrueContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(157);
                        match(TRUE);
                    }
                    break;
                    case 9: {
                        _localctx = new FalseContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(158);
                        match(FALSE);
                    }
                    break;
                    case 10: {
                        _localctx = new NullContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(159);
                        match(NULL);
                    }
                    break;
                    case 11: {
                        _localctx = new ExtContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(160);
                        extstart();
                    }
                    break;
                    case 12: {
                        _localctx = new PostincContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(161);
                        extstart();
                        setState(162);
                        increment();
                    }
                    break;
                    case 13: {
                        _localctx = new PreincContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(164);
                        increment();
                        setState(165);
                        extstart();
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(210);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 15, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(208);
                            switch (getInterpreter().adaptivePredict(_input, 14, _ctx)) {
                                case 1: {
                                    _localctx = new CatContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(169);
                                    if (!(precpred(_ctx, 13)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 13)");
                                    setState(170);
                                    match(CAT);
                                    setState(171);
                                    expression(14);
                                }
                                break;
                                case 2: {
                                    _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(172);
                                    if (!(precpred(_ctx, 12)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 12)");
                                    setState(173);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << REM))) != 0))) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(174);
                                    expression(13);
                                }
                                break;
                                case 3: {
                                    _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(175);
                                    if (!(precpred(_ctx, 11)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 11)");
                                    setState(176);
                                    _la = _input.LA(1);
                                    if (!(_la == ADD || _la == SUB)) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(177);
                                    expression(12);
                                }
                                break;
                                case 4: {
                                    _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(178);
                                    if (!(precpred(_ctx, 10)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 10)");
                                    setState(179);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LSH) | (1L << RSH) | (1L << USH))) != 0))) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(180);
                                    expression(11);
                                }
                                break;
                                case 5: {
                                    _localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(181);
                                    if (!(precpred(_ctx, 9)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 9)");
                                    setState(182);
                                    _la = _input.LA(1);
                                    if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LTE) | (1L << GT) | (1L << GTE))) != 0))) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(183);
                                    expression(10);
                                }
                                break;
                                case 6: {
                                    _localctx = new CompContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(184);
                                    if (!(precpred(_ctx, 8)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 8)");
                                    setState(185);
                                    _la = _input.LA(1);
                                    if (!(_la == EQ || _la == NE)) {
                                        _errHandler.recoverInline(this);
                                    } else {
                                        consume();
                                    }
                                    setState(186);
                                    expression(9);
                                }
                                break;
                                case 7: {
                                    _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(187);
                                    if (!(precpred(_ctx, 7)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 7)");
                                    setState(188);
                                    match(BWAND);
                                    setState(189);
                                    expression(8);
                                }
                                break;
                                case 8: {
                                    _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(190);
                                    if (!(precpred(_ctx, 6)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 6)");
                                    setState(191);
                                    match(BWXOR);
                                    setState(192);
                                    expression(7);
                                }
                                break;
                                case 9: {
                                    _localctx = new BinaryContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(193);
                                    if (!(precpred(_ctx, 5)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 5)");
                                    setState(194);
                                    match(BWOR);
                                    setState(195);
                                    expression(6);
                                }
                                break;
                                case 10: {
                                    _localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(196);
                                    if (!(precpred(_ctx, 4)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                                    setState(197);
                                    match(BOOLAND);
                                    setState(198);
                                    expression(5);
                                }
                                break;
                                case 11: {
                                    _localctx = new BoolContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(199);
                                    if (!(precpred(_ctx, 3)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                    setState(200);
                                    match(BOOLOR);
                                    setState(201);
                                    expression(4);
                                }
                                break;
                                case 12: {
                                    _localctx = new ConditionalContext(new ExpressionContext(_parentctx, _parentState));
                                    pushNewRecursionContext(_localctx, _startState, RULE_expression);
                                    setState(202);
                                    if (!(precpred(_ctx, 2)))
                                        throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                    setState(203);
                                    match(COND);
                                    setState(204);
                                    expression(0);
                                    setState(205);
                                    match(COLON);
                                    setState(206);
                                    expression(2);
                                }
                                break;
                            }
                        }
                    }
                    setState(212);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 15, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public static class ExtstartContext extends ParserRuleContext {
        public ExtprecContext extprec() {
            return getRuleContext(ExtprecContext.class, 0);
        }

        public ExtcastContext extcast() {
            return getRuleContext(ExtcastContext.class, 0);
        }

        public ExttypeContext exttype() {
            return getRuleContext(ExttypeContext.class, 0);
        }

        public ExtmemberContext extmember() {
            return getRuleContext(ExtmemberContext.class, 0);
        }

        public ExtstartContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_extstart;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExtstart(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExtstartContext extstart() throws RecognitionException {
        ExtstartContext _localctx = new ExtstartContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_extstart);
        try {
            setState(217);
            switch (getInterpreter().adaptivePredict(_input, 16, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(213);
                    extprec();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(214);
                    extcast();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(215);
                    exttype();
                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(216);
                    extmember();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExtprecContext extends ParserRuleContext {
        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public ExtprecContext extprec() {
            return getRuleContext(ExtprecContext.class, 0);
        }

        public ExtcastContext extcast() {
            return getRuleContext(ExtcastContext.class, 0);
        }

        public ExttypeContext exttype() {
            return getRuleContext(ExttypeContext.class, 0);
        }

        public ExtmemberContext extmember() {
            return getRuleContext(ExtmemberContext.class, 0);
        }

        public ExtdotContext extdot() {
            return getRuleContext(ExtdotContext.class, 0);
        }

        public ExtbraceContext extbrace() {
            return getRuleContext(ExtbraceContext.class, 0);
        }

        public ExtprecContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_extprec;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExtprec(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExtprecContext extprec() throws RecognitionException {
        ExtprecContext _localctx = new ExtprecContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_extprec);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(219);
                match(LP);
                setState(224);
                switch (getInterpreter().adaptivePredict(_input, 17, _ctx)) {
                    case 1: {
                        setState(220);
                        extprec();
                    }
                    break;
                    case 2: {
                        setState(221);
                        extcast();
                    }
                    break;
                    case 3: {
                        setState(222);
                        exttype();
                    }
                    break;
                    case 4: {
                        setState(223);
                        extmember();
                    }
                    break;
                }
                setState(226);
                match(RP);
                setState(229);
                switch (getInterpreter().adaptivePredict(_input, 18, _ctx)) {
                    case 1: {
                        setState(227);
                        extdot();
                    }
                    break;
                    case 2: {
                        setState(228);
                        extbrace();
                    }
                    break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExtcastContext extends ParserRuleContext {
        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public DecltypeContext decltype() {
            return getRuleContext(DecltypeContext.class, 0);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public ExtprecContext extprec() {
            return getRuleContext(ExtprecContext.class, 0);
        }

        public ExtcastContext extcast() {
            return getRuleContext(ExtcastContext.class, 0);
        }

        public ExttypeContext exttype() {
            return getRuleContext(ExttypeContext.class, 0);
        }

        public ExtmemberContext extmember() {
            return getRuleContext(ExtmemberContext.class, 0);
        }

        public ExtcastContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_extcast;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExtcast(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExtcastContext extcast() throws RecognitionException {
        ExtcastContext _localctx = new ExtcastContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_extcast);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(231);
                match(LP);
                setState(232);
                decltype();
                setState(233);
                match(RP);
                setState(238);
                switch (getInterpreter().adaptivePredict(_input, 19, _ctx)) {
                    case 1: {
                        setState(234);
                        extprec();
                    }
                    break;
                    case 2: {
                        setState(235);
                        extcast();
                    }
                    break;
                    case 3: {
                        setState(236);
                        exttype();
                    }
                    break;
                    case 4: {
                        setState(237);
                        extmember();
                    }
                    break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExtbraceContext extends ParserRuleContext {
        public TerminalNode LBRACE() {
            return getToken(PlanAParser.LBRACE, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RBRACE() {
            return getToken(PlanAParser.RBRACE, 0);
        }

        public ExtdotContext extdot() {
            return getRuleContext(ExtdotContext.class, 0);
        }

        public ExtbraceContext extbrace() {
            return getRuleContext(ExtbraceContext.class, 0);
        }

        public ExtbraceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_extbrace;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExtbrace(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExtbraceContext extbrace() throws RecognitionException {
        ExtbraceContext _localctx = new ExtbraceContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_extbrace);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(240);
                match(LBRACE);
                setState(241);
                expression(0);
                setState(242);
                match(RBRACE);
                setState(245);
                switch (getInterpreter().adaptivePredict(_input, 20, _ctx)) {
                    case 1: {
                        setState(243);
                        extdot();
                    }
                    break;
                    case 2: {
                        setState(244);
                        extbrace();
                    }
                    break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExtdotContext extends ParserRuleContext {
        public TerminalNode DOT() {
            return getToken(PlanAParser.DOT, 0);
        }

        public ExtcallContext extcall() {
            return getRuleContext(ExtcallContext.class, 0);
        }

        public ExtmemberContext extmember() {
            return getRuleContext(ExtmemberContext.class, 0);
        }

        public ExtdotContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_extdot;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExtdot(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExtdotContext extdot() throws RecognitionException {
        ExtdotContext _localctx = new ExtdotContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_extdot);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(247);
                match(DOT);
                setState(250);
                switch (getInterpreter().adaptivePredict(_input, 21, _ctx)) {
                    case 1: {
                        setState(248);
                        extcall();
                    }
                    break;
                    case 2: {
                        setState(249);
                        extmember();
                    }
                    break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExttypeContext extends ParserRuleContext {
        public TerminalNode ID() {
            return getToken(PlanAParser.ID, 0);
        }

        public ExtdotContext extdot() {
            return getRuleContext(ExtdotContext.class, 0);
        }

        public ExttypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_exttype;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExttype(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExttypeContext exttype() throws RecognitionException {
        ExttypeContext _localctx = new ExttypeContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_exttype);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(252);
                if (!(isType())) throw new FailedPredicateException(this, "isType()");
                setState(253);
                match(ID);
                setState(254);
                extdot();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExtcallContext extends ParserRuleContext {
        public TerminalNode ID() {
            return getToken(PlanAParser.ID, 0);
        }

        public ArgumentsContext arguments() {
            return getRuleContext(ArgumentsContext.class, 0);
        }

        public ExtdotContext extdot() {
            return getRuleContext(ExtdotContext.class, 0);
        }

        public ExtbraceContext extbrace() {
            return getRuleContext(ExtbraceContext.class, 0);
        }

        public ExtcallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_extcall;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExtcall(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExtcallContext extcall() throws RecognitionException {
        ExtcallContext _localctx = new ExtcallContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_extcall);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(256);
                match(ID);
                setState(257);
                arguments();
                setState(260);
                switch (getInterpreter().adaptivePredict(_input, 22, _ctx)) {
                    case 1: {
                        setState(258);
                        extdot();
                    }
                    break;
                    case 2: {
                        setState(259);
                        extbrace();
                    }
                    break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExtmemberContext extends ParserRuleContext {
        public TerminalNode ID() {
            return getToken(PlanAParser.ID, 0);
        }

        public ExtdotContext extdot() {
            return getRuleContext(ExtdotContext.class, 0);
        }

        public ExtbraceContext extbrace() {
            return getRuleContext(ExtbraceContext.class, 0);
        }

        public ExtmemberContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_extmember;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitExtmember(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExtmemberContext extmember() throws RecognitionException {
        ExtmemberContext _localctx = new ExtmemberContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_extmember);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(262);
                if (!(!isType())) throw new FailedPredicateException(this, "!isType()");
                setState(263);
                match(ID);
                setState(266);
                switch (getInterpreter().adaptivePredict(_input, 23, _ctx)) {
                    case 1: {
                        setState(264);
                        extdot();
                    }
                    break;
                    case 2: {
                        setState(265);
                        extbrace();
                    }
                    break;
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ArgumentsContext extends ParserRuleContext {
        public TerminalNode LP() {
            return getToken(PlanAParser.LP, 0);
        }

        public TerminalNode RP() {
            return getToken(PlanAParser.RP, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(PlanAParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(PlanAParser.COMMA, i);
        }

        public ArgumentsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_arguments;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitArguments(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ArgumentsContext arguments() throws RecognitionException {
        ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_arguments);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                {
                    setState(268);
                    match(LP);
                    setState(277);
                    switch (getInterpreter().adaptivePredict(_input, 25, _ctx)) {
                        case 1: {
                            setState(269);
                            expression(0);
                            setState(274);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            while (_la == COMMA) {
                                {
                                    {
                                        setState(270);
                                        match(COMMA);
                                        setState(271);
                                        expression(0);
                                    }
                                }
                                setState(276);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            }
                        }
                        break;
                    }
                    setState(279);
                    match(RP);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class IncrementContext extends ParserRuleContext {
        public TerminalNode INCR() {
            return getToken(PlanAParser.INCR, 0);
        }

        public TerminalNode DECR() {
            return getToken(PlanAParser.DECR, 0);
        }

        public IncrementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_increment;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlanAVisitor) return ((PlanAVisitor<? extends T>)visitor).visitIncrement(this);
            else return visitor.visitChildren(this);
        }
    }

    public final IncrementContext increment() throws RecognitionException {
        IncrementContext _localctx = new IncrementContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_increment);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(281);
                _la = _input.LA(1);
                if (!(_la == INCR || _la == DECR)) {
                    _errHandler.recoverInline(this);
                } else {
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 5:
                return decltype_sempred((DecltypeContext)_localctx, predIndex);
            case 6:
                return declvar_sempred((DeclvarContext)_localctx, predIndex);
            case 7:
                return expression_sempred((ExpressionContext)_localctx, predIndex);
            case 13:
                return exttype_sempred((ExttypeContext)_localctx, predIndex);
            case 15:
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
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3F\u011e\4\2\t\2\4" +
                    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t" +
                    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\3\2\6\2(\n\2\r\2\16\2)\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3" +
                    "\5\3\65\n\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3=\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3" +
                    "\3\3\3\3\3\3\3\5\3I\n\3\3\3\3\3\5\3M\n\3\3\3\3\3\5\3Q\n\3\3\3\3\3\3\3" +
                    "\5\3V\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3" +
                    "f\n\3\3\4\3\4\7\4j\n\4\f\4\16\4m\13\4\3\4\3\4\5\4q\n\4\3\5\3\5\3\6\3\6" +
                    "\3\6\3\6\7\6y\n\6\f\6\16\6|\13\6\3\7\3\7\3\7\3\7\7\7\u0082\n\7\f\7\16" +
                    "\7\u0085\13\7\3\b\3\b\3\b\3\b\5\b\u008b\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3" +
                    "\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t" +
                    "\3\t\3\t\3\t\3\t\3\t\5\t\u00aa\n\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t" +
                    "\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3" +
                    "\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\7\t\u00d3\n\t\f\t\16" +
                    "\t\u00d6\13\t\3\n\3\n\3\n\3\n\5\n\u00dc\n\n\3\13\3\13\3\13\3\13\3\13\5" +
                    "\13\u00e3\n\13\3\13\3\13\3\13\5\13\u00e8\n\13\3\f\3\f\3\f\3\f\3\f\3\f" +
                    "\3\f\5\f\u00f1\n\f\3\r\3\r\3\r\3\r\3\r\5\r\u00f8\n\r\3\16\3\16\3\16\5" +
                    "\16\u00fd\n\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\5\20\u0107\n\20" +
                    "\3\21\3\21\3\21\3\21\5\21\u010d\n\21\3\22\3\22\3\22\3\22\7\22\u0113\n" +
                    "\22\f\22\16\22\u0116\13\22\5\22\u0118\n\22\3\22\3\22\3\23\3\23\3\23\2" +
                    "\3\20\24\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$\2\13\4\2\25\26\32" +
                    "\33\3\2/;\3\2<?\3\2\27\31\3\2\32\33\3\2\34\36\3\2 #\3\2$%\3\2-.\u014b" +
                    "\2\'\3\2\2\2\4e\3\2\2\2\6p\3\2\2\2\br\3\2\2\2\nt\3\2\2\2\f}\3\2\2\2\16" +
                    "\u0086\3\2\2\2\20\u00a9\3\2\2\2\22\u00db\3\2\2\2\24\u00dd\3\2\2\2\26\u00e9" +
                    "\3\2\2\2\30\u00f2\3\2\2\2\32\u00f9\3\2\2\2\34\u00fe\3\2\2\2\36\u0102\3" +
                    "\2\2\2 \u0108\3\2\2\2\"\u010e\3\2\2\2$\u011b\3\2\2\2&(\5\4\3\2\'&\3\2" +
                    "\2\2()\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*+\3\2\2\2+,\7\2\2\3,\3\3\2\2\2-.\7" +
                    "\r\2\2./\7\b\2\2/\60\5\20\t\2\60\61\7\t\2\2\61\64\5\6\4\2\62\63\7\16\2" +
                    "\2\63\65\5\6\4\2\64\62\3\2\2\2\64\65\3\2\2\2\65f\3\2\2\2\66\67\7\17\2" +
                    "\2\678\7\b\2\289\5\20\t\29<\7\t\2\2:=\5\6\4\2;=\5\b\5\2<:\3\2\2\2<;\3" +
                    "\2\2\2=f\3\2\2\2>?\7\20\2\2?@\5\6\4\2@A\7\17\2\2AB\7\b\2\2BC\5\20\t\2" +
                    "CD\7\t\2\2Df\3\2\2\2EF\7\21\2\2FH\7\b\2\2GI\5\n\6\2HG\3\2\2\2HI\3\2\2" +
                    "\2IJ\3\2\2\2JL\7\f\2\2KM\5\20\t\2LK\3\2\2\2LM\3\2\2\2MN\3\2\2\2NP\7\f" +
                    "\2\2OQ\5\20\t\2PO\3\2\2\2PQ\3\2\2\2QR\3\2\2\2RU\7\t\2\2SV\5\6\4\2TV\5" +
                    "\b\5\2US\3\2\2\2UT\3\2\2\2Vf\3\2\2\2WX\5\n\6\2XY\7\f\2\2Yf\3\2\2\2Z[\7" +
                    "\22\2\2[f\7\f\2\2\\]\7\23\2\2]f\7\f\2\2^_\7\24\2\2_`\5\20\t\2`a\7\f\2" +
                    "\2af\3\2\2\2bc\5\20\t\2cd\7\f\2\2df\3\2\2\2e-\3\2\2\2e\66\3\2\2\2e>\3" +
                    "\2\2\2eE\3\2\2\2eW\3\2\2\2eZ\3\2\2\2e\\\3\2\2\2e^\3\2\2\2eb\3\2\2\2f\5" +
                    "\3\2\2\2gk\7\4\2\2hj\5\4\3\2ih\3\2\2\2jm\3\2\2\2ki\3\2\2\2kl\3\2\2\2l" +
                    "n\3\2\2\2mk\3\2\2\2nq\7\5\2\2oq\5\4\3\2pg\3\2\2\2po\3\2\2\2q\7\3\2\2\2" +
                    "rs\7\f\2\2s\t\3\2\2\2tu\5\f\7\2uz\5\16\b\2vw\7\13\2\2wy\5\16\b\2xv\3\2" +
                    "\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{\13\3\2\2\2|z\3\2\2\2}~\6\7\2\2~\u0083" +
                    "\7F\2\2\177\u0080\7\6\2\2\u0080\u0082\7\7\2\2\u0081\177\3\2\2\2\u0082" +
                    "\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084\r\3\2\2\2" +
                    "\u0085\u0083\3\2\2\2\u0086\u0087\6\b\3\2\u0087\u008a\7F\2\2\u0088\u0089" +
                    "\7/\2\2\u0089\u008b\5\20\t\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b" +
                    "\17\3\2\2\2\u008c\u008d\b\t\1\2\u008d\u008e\t\2\2\2\u008e\u00aa\5\20\t" +
                    "\21\u008f\u0090\7\b\2\2\u0090\u0091\5\f\7\2\u0091\u0092\7\t\2\2\u0092" +
                    "\u0093\5\20\t\20\u0093\u00aa\3\2\2\2\u0094\u0095\5\22\n\2\u0095\u0096" +
                    "\t\3\2\2\u0096\u0097\5\20\t\3\u0097\u00aa\3\2\2\2\u0098\u0099\7\b\2\2" +
                    "\u0099\u009a\5\20\t\2\u009a\u009b\7\t\2\2\u009b\u00aa\3\2\2\2\u009c\u00aa" +
                    "\t\4\2\2\u009d\u00aa\7@\2\2\u009e\u00aa\7A\2\2\u009f\u00aa\7B\2\2\u00a0" +
                    "\u00aa\7C\2\2\u00a1\u00aa\7D\2\2\u00a2\u00aa\5\22\n\2\u00a3\u00a4\5\22" +
                    "\n\2\u00a4\u00a5\5$\23\2\u00a5\u00aa\3\2\2\2\u00a6\u00a7\5$\23\2\u00a7" +
                    "\u00a8\5\22\n\2\u00a8\u00aa\3\2\2\2\u00a9\u008c\3\2\2\2\u00a9\u008f\3" +
                    "\2\2\2\u00a9\u0094\3\2\2\2\u00a9\u0098\3\2\2\2\u00a9\u009c\3\2\2\2\u00a9" +
                    "\u009d\3\2\2\2\u00a9\u009e\3\2\2\2\u00a9\u009f\3\2\2\2\u00a9\u00a0\3\2" +
                    "\2\2\u00a9\u00a1\3\2\2\2\u00a9\u00a2\3\2\2\2\u00a9\u00a3\3\2\2\2\u00a9" +
                    "\u00a6\3\2\2\2\u00aa\u00d4\3\2\2\2\u00ab\u00ac\f\17\2\2\u00ac\u00ad\7" +
                    "\37\2\2\u00ad\u00d3\5\20\t\20\u00ae\u00af\f\16\2\2\u00af\u00b0\t\5\2\2" +
                    "\u00b0\u00d3\5\20\t\17\u00b1\u00b2\f\r\2\2\u00b2\u00b3\t\6\2\2\u00b3\u00d3" +
                    "\5\20\t\16\u00b4\u00b5\f\f\2\2\u00b5\u00b6\t\7\2\2\u00b6\u00d3\5\20\t" +
                    "\r\u00b7\u00b8\f\13\2\2\u00b8\u00b9\t\b\2\2\u00b9\u00d3\5\20\t\f\u00ba" +
                    "\u00bb\f\n\2\2\u00bb\u00bc\t\t\2\2\u00bc\u00d3\5\20\t\13\u00bd\u00be\f" +
                    "\t\2\2\u00be\u00bf\7&\2\2\u00bf\u00d3\5\20\t\n\u00c0\u00c1\f\b\2\2\u00c1" +
                    "\u00c2\7\'\2\2\u00c2\u00d3\5\20\t\t\u00c3\u00c4\f\7\2\2\u00c4\u00c5\7" +
                    "(\2\2\u00c5\u00d3\5\20\t\b\u00c6\u00c7\f\6\2\2\u00c7\u00c8\7)\2\2\u00c8" +
                    "\u00d3\5\20\t\7\u00c9\u00ca\f\5\2\2\u00ca\u00cb\7*\2\2\u00cb\u00d3\5\20" +
                    "\t\6\u00cc\u00cd\f\4\2\2\u00cd\u00ce\7+\2\2\u00ce\u00cf\5\20\t\2\u00cf" +
                    "\u00d0\7,\2\2\u00d0\u00d1\5\20\t\4\u00d1\u00d3\3\2\2\2\u00d2\u00ab\3\2" +
                    "\2\2\u00d2\u00ae\3\2\2\2\u00d2\u00b1\3\2\2\2\u00d2\u00b4\3\2\2\2\u00d2" +
                    "\u00b7\3\2\2\2\u00d2\u00ba\3\2\2\2\u00d2\u00bd\3\2\2\2\u00d2\u00c0\3\2" +
                    "\2\2\u00d2\u00c3\3\2\2\2\u00d2\u00c6\3\2\2\2\u00d2\u00c9\3\2\2\2\u00d2" +
                    "\u00cc\3\2\2\2\u00d3\u00d6\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5\3\2" +
                    "\2\2\u00d5\21\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d7\u00dc\5\24\13\2\u00d8" +
                    "\u00dc\5\26\f\2\u00d9\u00dc\5\34\17\2\u00da\u00dc\5 \21\2\u00db\u00d7" +
                    "\3\2\2\2\u00db\u00d8\3\2\2\2\u00db\u00d9\3\2\2\2\u00db\u00da\3\2\2\2\u00dc" +
                    "\23\3\2\2\2\u00dd\u00e2\7\b\2\2\u00de\u00e3\5\24\13\2\u00df\u00e3\5\26" +
                    "\f\2\u00e0\u00e3\5\34\17\2\u00e1\u00e3\5 \21\2\u00e2\u00de\3\2\2\2\u00e2" +
                    "\u00df\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e4\3\2" +
                    "\2\2\u00e4\u00e7\7\t\2\2\u00e5\u00e8\5\32\16\2\u00e6\u00e8\5\30\r\2\u00e7" +
                    "\u00e5\3\2\2\2\u00e7\u00e6\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\25\3\2\2" +
                    "\2\u00e9\u00ea\7\b\2\2\u00ea\u00eb\5\f\7\2\u00eb\u00f0\7\t\2\2\u00ec\u00f1" +
                    "\5\24\13\2\u00ed\u00f1\5\26\f\2\u00ee\u00f1\5\34\17\2\u00ef\u00f1\5 \21" +
                    "\2\u00f0\u00ec\3\2\2\2\u00f0\u00ed\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f0\u00ef" +
                    "\3\2\2\2\u00f1\27\3\2\2\2\u00f2\u00f3\7\6\2\2\u00f3\u00f4\5\20\t\2\u00f4" +
                    "\u00f7\7\7\2\2\u00f5\u00f8\5\32\16\2\u00f6\u00f8\5\30\r\2\u00f7\u00f5" +
                    "\3\2\2\2\u00f7\u00f6\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\31\3\2\2\2\u00f9" +
                    "\u00fc\7\n\2\2\u00fa\u00fd\5\36\20\2\u00fb\u00fd\5 \21\2\u00fc\u00fa\3" +
                    "\2\2\2\u00fc\u00fb\3\2\2\2\u00fd\33\3\2\2\2\u00fe\u00ff\6\17\20\2\u00ff" +
                    "\u0100\7F\2\2\u0100\u0101\5\32\16\2\u0101\35\3\2\2\2\u0102\u0103\7F\2" +
                    "\2\u0103\u0106\5\"\22\2\u0104\u0107\5\32\16\2\u0105\u0107\5\30\r\2\u0106" +
                    "\u0104\3\2\2\2\u0106\u0105\3\2\2\2\u0106\u0107\3\2\2\2\u0107\37\3\2\2" +
                    "\2\u0108\u0109\6\21\21\2\u0109\u010c\7F\2\2\u010a\u010d\5\32\16\2\u010b" +
                    "\u010d\5\30\r\2\u010c\u010a\3\2\2\2\u010c\u010b\3\2\2\2\u010c\u010d\3" +
                    "\2\2\2\u010d!\3\2\2\2\u010e\u0117\7\b\2\2\u010f\u0114\5\20\t\2\u0110\u0111" +
                    "\7\13\2\2\u0111\u0113\5\20\t\2\u0112\u0110\3\2\2\2\u0113\u0116\3\2\2\2" +
                    "\u0114\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0118\3\2\2\2\u0116\u0114" +
                    "\3\2\2\2\u0117\u010f\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u0119\3\2\2\2\u0119" +
                    "\u011a\7\t\2\2\u011a#\3\2\2\2\u011b\u011c\t\n\2\2\u011c%\3\2\2\2\34)\64" +
                    "<HLPUekpz\u0083\u008a\u00a9\u00d2\u00d4\u00db\u00e2\u00e7\u00f0\u00f7" +
                    "\u00fc\u0106\u010c\u0114\u0117";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}