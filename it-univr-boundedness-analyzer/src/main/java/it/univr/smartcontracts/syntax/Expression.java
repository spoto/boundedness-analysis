package it.univr.smartcontracts.syntax;

import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalyzable;

public interface Expression extends SideEffectsAnalyzable {
	State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis);
	Stream<Expression> subexpressions();
	void toCode(StringBuilder sb);

	default String toCode() {
		var sb = new StringBuilder();
		toCode(sb);
		return sb.toString();
	}
}