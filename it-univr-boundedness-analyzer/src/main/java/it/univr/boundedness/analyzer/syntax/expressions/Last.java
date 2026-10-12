package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record Last(Expression exp) implements Expression {

	@Override
	public void toCode(StringBuilder sb) {
		exp.toCode(sb);
		sb.append(".last()");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return exp.sideEffects(interpretation);
	}
}