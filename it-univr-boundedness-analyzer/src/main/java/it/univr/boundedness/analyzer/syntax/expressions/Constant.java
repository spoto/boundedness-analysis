package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record Constant(int c) implements Expression {

	@Override
	public Stream<Type> sideEffects() {
		return Stream.empty();
	}
}