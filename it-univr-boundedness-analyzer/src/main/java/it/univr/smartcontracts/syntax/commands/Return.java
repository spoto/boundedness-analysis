package it.univr.smartcontracts.syntax.commands;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Type;

public class Return extends AbstractCommand {

	@Override
	public void toString(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("return");
	}

	@Override
	protected void setNext(AbstractCommand next) {
		throw new IllegalStateException("Misplaced return, followed by deadcode");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.empty();
	}	
}