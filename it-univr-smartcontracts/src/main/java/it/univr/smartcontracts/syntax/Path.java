package it.univr.smartcontracts.syntax;

import java.util.stream.Stream;

import it.univr.smartcontracts.syntax.expressions.Variable;

public interface Path extends LValue {
	Variable root();
	Type type();
	String toString();
	boolean equals(Object other);
	int hashCode();

	/**
	 * Determines if this path survives all updates to a list whose element type is in the given
	 * stream of types.
	 */
	boolean survives(Stream<Type> updated);

	Path replaceRootWith(Path path);
}