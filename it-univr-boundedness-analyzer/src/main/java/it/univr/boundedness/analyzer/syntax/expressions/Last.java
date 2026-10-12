package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record Last(Expression exp) implements Expression {

	@Override
	public Stream<Type> sideEffects() {
		return exp.sideEffects();
	}
}