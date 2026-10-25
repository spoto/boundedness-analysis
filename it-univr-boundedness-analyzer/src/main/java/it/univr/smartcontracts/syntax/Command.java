package it.univr.smartcontracts.syntax;

import java.util.Optional;

import it.univr.smartcontracts.sideeffects.SideEffectsProducer;

public interface Command extends SideEffectsProducer {
	String toString();
	void toString(StringBuilder sb, String nesting);
	Optional<Command> next();
}