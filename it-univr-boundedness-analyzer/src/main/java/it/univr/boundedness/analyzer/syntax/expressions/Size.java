package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record Size(Expression exp) implements Expression {

	@Override
	public void toCode(StringBuilder sb) {
		exp.toCode(sb);
		sb.append(".size()");
	}

	@Override
	public Stream<Type> sideEffects() {
		return exp.sideEffects();
	}
}