package it.univr.smartcontracts.syntax.expressions;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.AOP;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;

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