package it.univr.smartcontracts.syntax.commands;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Type;

public class Nop extends AbstractCommand {

	@Override
	public void toString(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("nop");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.empty();
	}	
}