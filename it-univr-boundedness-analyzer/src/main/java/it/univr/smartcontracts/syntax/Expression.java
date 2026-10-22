package it.univr.smartcontracts.syntax;

import it.univr.smartcontracts.sideeffects.SideEffectsProducer;

public interface Expression extends SideEffectsProducer {

	default String toCode() {
		var sb = new StringBuilder();
		toCode(sb);
		return sb.toString();
	}

	default void toCode(StringBuilder sb) {
		sb.append("exp");
	}
}