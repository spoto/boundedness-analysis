package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record FieldRead(Expression exp, String f) implements Expression {
	
	@Override
	public Stream<Type> sideEffects() {
		return exp.sideEffects();
	}
}