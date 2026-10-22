package it.univr.smartcontracts.syntax.expressions;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.LValue;
import it.univr.smartcontracts.syntax.Type;

public record Variable(String name) implements LValue {
	public final static Variable THIS = new Variable("this");
	public final static Variable CALLER = new Variable("caller");
	public final static Variable AMOUNT = new Variable("amount");
	public final static Variable NOW = new Variable("now");

	@Override
	public void toCode(StringBuilder sb) {
		sb.append(name);
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.empty();
	}
}