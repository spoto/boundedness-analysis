package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record Sequence(Command com1, Command com2) implements Command {	

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(com1.sideEffects(), com2.sideEffects());
	}
}