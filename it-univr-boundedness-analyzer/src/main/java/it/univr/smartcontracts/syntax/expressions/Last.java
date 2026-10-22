package it.univr.smartcontracts.syntax.expressions;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;

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