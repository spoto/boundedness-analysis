package it.univr.smartcontracts.syntax;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalyzable;

public interface Command extends SideEffectsAnalyzable {
	String toString();
	void toString(StringBuilder sb, String nesting);
}