package it.univr.boundedness.analyzer.syntax;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsProducer;

public interface Command extends SideEffectsProducer {

	default String toCode() {
		var sb = new StringBuilder();
		toCode(sb, "");
		return sb.toString();
	}

	default void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting + "com;");
	}
}