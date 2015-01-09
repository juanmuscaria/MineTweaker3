/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zencode.parser.expression;

import org.openzen.zencode.IZenCompileEnvironment;
import org.openzen.zencode.symbolic.scope.IMethodScope;
import org.openzen.zencode.symbolic.expression.IPartialExpression;
import org.openzen.zencode.parser.expression.ParsedCallArguments.MatchedArguments;
import org.openzen.zencode.parser.type.IParsedType;
import org.openzen.zencode.runtime.IAny;
import org.openzen.zencode.symbolic.type.ITypeInstance;
import org.openzen.zencode.util.CodePosition;

/**
 * TODO: handle anonymous class construction
 *
 * @author Stan
 */
public class ParsedExpressionNew extends ParsedExpression
{
	private final IParsedType type;
	private final ParsedCallArguments callArguments;

	public ParsedExpressionNew(CodePosition position, IParsedType type, ParsedCallArguments callArguments)
	{
		super(position);

		this.type = type;
		this.callArguments = callArguments;
	}

	@Override
	public <E extends IPartialExpression<E, T>, T extends ITypeInstance<E, T>>
		 IPartialExpression<E, T> compilePartial(IMethodScope<E, T> scope, T predictedType)
	{
		T cType = type.compile(scope);
		MatchedArguments<E, T> compiledArguments = callArguments.compile(cType.getConstructors(), scope);
		return scope.getExpressionCompiler().constructNew(getPosition(), scope, cType, compiledArguments.method, compiledArguments.arguments);
	}

	@Override
	public IAny eval(IZenCompileEnvironment<?, ?> environment)
	{
		return null;
	}
}