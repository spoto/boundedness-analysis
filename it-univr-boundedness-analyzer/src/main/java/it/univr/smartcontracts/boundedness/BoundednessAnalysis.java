package it.univr.smartcontracts.boundedness;

import java.util.function.BiConsumer;

import it.univr.smartcontracts.syntax.Command;

public interface BoundednessAnalysis {
	State of(Command command);
	void forEach(BiConsumer<Command, State> action);
}
