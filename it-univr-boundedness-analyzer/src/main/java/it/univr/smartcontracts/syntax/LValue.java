package it.univr.smartcontracts.syntax;

import java.util.Optional;

public interface LValue extends Expression {

	/**
	 * Yields this leftvalue as a path, if it is a path.
	 * 
	 * @return this leftvalue seen as a path, if possible.
	 */
	Optional<Path> asPath();
}