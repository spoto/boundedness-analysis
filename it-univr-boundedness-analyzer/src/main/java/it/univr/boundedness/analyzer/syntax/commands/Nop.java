package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Type;

public record Nop() implements Command {

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("nop");
	}

	@Override
	public Stream<Type> sideEffects() {
		return Stream.empty();
	}	
}