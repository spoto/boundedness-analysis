package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record FieldWrite(Expression exp1, String f, Expression exp2) implements Command {	

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(exp1.sideEffects(), exp2.sideEffects());
	}
}