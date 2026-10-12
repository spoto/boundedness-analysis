package it.univr.boundedness.analyzer.syntax;

import java.util.stream.Stream;

public interface Command {
	Stream<Type> sideEffects();

	default String toCode() {
		var sb = new StringBuilder();
		toCode(sb, "");
		return sb.toString();
	}

	default void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting + "com;");
	}
}