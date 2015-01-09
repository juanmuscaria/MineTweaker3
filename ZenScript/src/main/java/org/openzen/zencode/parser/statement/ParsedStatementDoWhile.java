/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zencode.parser.statement;

import org.openzen.zencode.symbolic.scope.StatementBlockScope;
import org.openzen.zencode.symbolic.scope.IMethodScope;
import org.openzen.zencode.symbolic.statement.Statement;
import org.openzen.zencode.symbolic.statement.StatementDoWhile;
import org.openzen.zencode.symbolic.statement.StatementSwitch;
import org.openzen.zencode.lexer.ZenLexer;
import static org.openzen.zencode.lexer.ZenLexer.*;
import org.openzen.zencode.parser.expression.ParsedExpression;
import org.openzen.zencode.symbolic.expression.IPartialExpression;
import org.openzen.zencode.symbolic.type.ITypeInstance;
import org.openzen.zencode.util.CodePosition;

/**
 *
 * @author Stan
 */
public class ParsedStatementDoWhile extends ParsedStatement
{
	public static ParsedStatementDoWhile parse(ZenLexer lexer)
	{
		CodePosition position = lexer.required(T_DO, "do expected").getPosition();

		String label = null;
		if (lexer.optional(T_COLON) != null)
			label = lexer.required(TOKEN_ID, "identifier expected").getValue();

		ParsedStatement contents = ParsedStatement.parse(lexer);

		lexer.required(T_WHILE, "while expected");
		ParsedExpression condition = ParsedExpression.parse(lexer);
		lexer.required(T_SEMICOLON, "; expected");

		return new ParsedStatementDoWhile(position, label, contents, condition);
	}

	private final String label;
	private final ParsedStatement contents;
	private final ParsedExpression condition;

	public ParsedStatementDoWhile(CodePosition position, String label, ParsedStatement contents, ParsedExpression condition)
	{
		super(position);

		this.label = label;
		this.contents = contents;
		this.condition = condition;
	}

	@Override
	public <E extends IPartialExpression<E, T>, T extends ITypeInstance<E, T>>
		 Statement<E, T> compile(IMethodScope<E, T> scope)
	{
		E compiledCondition = condition.compile(scope, scope.getTypeCompiler().getBool(scope));
		StatementDoWhile<E, T> compiled = new StatementDoWhile<E, T>(getPosition(), scope, compiledCondition);

		StatementBlockScope<E, T> statementScope = new StatementBlockScope<E, T>(scope, compiled, label);
		compiled.setContents(contents.compile(statementScope));
		return compiled;
	}

	@Override
	public <E extends IPartialExpression<E, T>, T extends ITypeInstance<E, T>>
		 void compileSwitch(IMethodScope<E, T> scope, StatementSwitch<E, T> forSwitch)
	{
		forSwitch.onStatement(compile(scope));
	}
}