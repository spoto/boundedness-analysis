package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record ConstructorCall(String C, Expression... exps) implements Expression {

	@Override
	public Stream<Type> sideEffects() {
		return Stream.of(exps).flatMap(Expression::sideEffects);
	}
}