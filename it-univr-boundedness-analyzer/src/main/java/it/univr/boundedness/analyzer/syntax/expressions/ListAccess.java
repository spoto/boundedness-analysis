package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.LValue;
import it.univr.boundedness.analyzer.syntax.Type;

public record ListAccess(Expression exp1, Expression exp2) implements LValue {

	@Override
	public void toCode(StringBuilder sb) {
		exp1.toCode(sb);
		sb.append('[');
		exp2.toCode(sb);
		sb.append(']');
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2).flatMap(exp -> exp.sideEffects(interpretation));
	}
}