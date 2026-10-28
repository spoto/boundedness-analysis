package it.univr.smartcontracts.boundedness;

import java.util.function.BiConsumer;

import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Program;
import it.univr.smartcontracts.syntax.Type;

public interface BoundednessAnalysis {
	Program program();

	/**
	 * Determines if all lists with the given elements type are bounded in this program.
	 */
	boolean allListsAreBounded(Type elementsType);

	State at(Command command);
	void forEach(BiConsumer<Command, State> action);
}
