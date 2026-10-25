package it.univr.smartcontracts.syntax.expressions;

import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;

public record Constant(int c) implements Expression {

	@Override
	public void toCode(StringBuilder sb) {
		sb.append(c);
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.empty();
	}

	@Override
	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		return before;
	}
}