package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record Nop() implements Command {

	@Override
	public Stream<Type> sideEffects() {
		return Stream.empty();
	}	
}