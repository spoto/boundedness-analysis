package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record For(String v, Expression exp, Command com) implements Command {	

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(exp.sideEffects(), com.sideEffects());
	}
}