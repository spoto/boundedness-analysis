package it.univr.smartcontracts.syntax.expressions;

import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;

public record Random(Expression exp) implements Expression {

	@Override
	public void toCode(StringBuilder sb) {
		sb.append("random(");
		exp.toCode(sb);
		sb.append(')');
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return exp.sideEffects(interpretation);
	}

	@Override
	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		return exp.boundedness(before, sideEffectsAnalysis);
	}
}