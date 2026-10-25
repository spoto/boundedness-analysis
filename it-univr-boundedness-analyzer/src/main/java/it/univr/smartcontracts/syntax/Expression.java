package it.univr.smartcontracts.syntax;

import it.univr.smartcontracts.boundedness.BoundednessAnalyzable;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalyzable;

public interface Expression extends SideEffectsAnalyzable, BoundednessAnalyzable {

	default String toCode() {
		var sb = new StringBuilder();
		toCode(sb);
		return sb.toString();
	}

	default void toCode(StringBuilder sb) {
		sb.append("exp");
	}
}