package it.univr.smartcontracts.boundedness;

import it.univr.smartcontracts.syntax.Path;

/**
 * A boundedness constraint of the form path.size() <= n.
 */
public record Constraint(Path path, int n) {
	
	@Override
	public final String toString() {
		return path + ".size() <= " + n;
	}
}