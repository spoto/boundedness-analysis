package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

// elementsType is the type of the elements of exp1
public record Push(Expression exp1, Type elementsType, Expression exp2) implements Command {	

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(Stream.of(elementsType),
			Stream.concat(exp1.sideEffects(), exp2.sideEffects()));
	}
}