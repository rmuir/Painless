// Generated from src/java/painless/Painless.g4 by ANTLR 4.5
package painless;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PainlessParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PainlessVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PainlessParser#source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSource(PainlessParser.SourceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code if}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(PainlessParser.IfContext ctx);
	/**
	 * Visit a parse tree produced by the {@code while}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(PainlessParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code do}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDo(PainlessParser.DoContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor(PainlessParser.ForContext ctx);
	/**
	 * Visit a parse tree produced by the {@code decl}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(PainlessParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continue}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinue(PainlessParser.ContinueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code break}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreak(PainlessParser.BreakContext ctx);
	/**
	 * Visit a parse tree produced by the {@code return}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn(PainlessParser.ReturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expr}
	 * labeled alternative in {@link PainlessParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(PainlessParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multiple}
	 * labeled alternative in {@link PainlessParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiple(PainlessParser.MultipleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code single}
	 * labeled alternative in {@link PainlessParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingle(PainlessParser.SingleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code empty}
	 * labeled alternative in {@link PainlessParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmpty(PainlessParser.EmptyContext ctx);
	/**
	 * Visit a parse tree produced by {@link PainlessParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(PainlessParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PainlessParser#decltype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecltype(PainlessParser.DecltypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ext}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExt(PainlessParser.ExtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comp}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp(PainlessParser.CompContext ctx);
	/**
	 * Visit a parse tree produced by the {@code string}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(PainlessParser.StringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bool}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(PainlessParser.BoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditional}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional(PainlessParser.ConditionalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(PainlessParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code false}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalse(PainlessParser.FalseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numeric}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumeric(PainlessParser.NumericContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unary}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary(PainlessParser.UnaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code precedence}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrecedence(PainlessParser.PrecedenceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code cast}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCast(PainlessParser.CastContext ctx);
	/**
	 * Visit a parse tree produced by the {@code null}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNull(PainlessParser.NullContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binary}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary(PainlessParser.BinaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code char}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChar(PainlessParser.CharContext ctx);
	/**
	 * Visit a parse tree produced by the {@code true}
	 * labeled alternative in {@link PainlessParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrue(PainlessParser.TrueContext ctx);
	/**
	 * Visit a parse tree produced by {@link PainlessParser#external}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExternal(PainlessParser.ExternalContext ctx);
	/**
	 * Visit a parse tree produced by {@link PainlessParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(PainlessParser.ArgumentsContext ctx);
}