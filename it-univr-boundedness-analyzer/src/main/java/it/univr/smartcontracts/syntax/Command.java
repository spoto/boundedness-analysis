package it.univr.smartcontracts.syntax;

import java.util.Optional;

import it.univr.smartcontracts.sideeffects.SideEffectsProducer;

public interface Command extends SideEffectsProducer {
	String toCode();
	void toCode(StringBuilder sb, String nesting);
	Optional<Command> next();
}