package it.univr.smartcontracts.syntax.expressions;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;

public record Null() implements Expression {

	public final static Null INSTANCE = new Null();

	@Override
	public void toCode(StringBuilder sb) {
		sb.append("NULL");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.empty();
	}
}