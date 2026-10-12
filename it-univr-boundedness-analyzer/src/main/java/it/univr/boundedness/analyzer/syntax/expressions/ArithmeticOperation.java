package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.AOP;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record ArithmeticOperation(Expression exp1, AOP aop, Expression exp2) implements Expression {

	@Override
	public void toCode(StringBuilder sb) {
		exp1.toCode(sb);
		sb.append(' ');
		aop.toCode(sb);
		sb.append(' ');
		exp2.toCode(sb);
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2).flatMap(exp -> exp.sideEffects(interpretation));
	}
}