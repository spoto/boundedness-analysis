package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Type;

public record Sequence(Command com1, Command com2) implements Command {	

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		com1.toCode(sb, nesting);
		sb.append("\n");
		com2.toCode(sb, nesting);
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.concat(com1.sideEffects(interpretation), com2.sideEffects(interpretation));
	}
}