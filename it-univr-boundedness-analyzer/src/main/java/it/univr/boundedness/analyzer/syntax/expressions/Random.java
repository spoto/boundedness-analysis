package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record Random(Expression exp) implements Expression {

	@Override
	public Stream<Type> sideEffects() {
		return exp.sideEffects();
	}
}