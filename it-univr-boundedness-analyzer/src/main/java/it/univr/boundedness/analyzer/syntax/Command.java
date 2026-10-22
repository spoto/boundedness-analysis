package it.univr.boundedness.analyzer.syntax;

import java.util.Optional;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsProducer;

public interface Command extends SideEffectsProducer {
	String toCode();
	void toCode(StringBuilder sb, String nesting);
	Optional<Command> next();
}