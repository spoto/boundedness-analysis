package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record Constant(int c) implements Expression {

	@Override
	public void toCode(StringBuilder sb) {
		sb.append(c);
	}

	@Override
	public Stream<Type> sideEffects() {
		return Stream.empty();
	}
}