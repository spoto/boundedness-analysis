package it.univr.smartcontracts.syntax;

import java.util.stream.Stream;

public interface Path {
	Type getType();
	String toString();

	/**
	 * Determines if this path survives all updates to a list whose element type is in the given
	 * stream of types.
	 */
	boolean survives(Stream<Type> updated);
}