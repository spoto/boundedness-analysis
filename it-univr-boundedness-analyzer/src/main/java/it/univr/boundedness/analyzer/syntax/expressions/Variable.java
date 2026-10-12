package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record Variable(String name) implements Expression {
	public final static Variable THIS = new Variable("this");
	public final static Variable CALLER = new Variable("caller");
	public final static Variable AMOUNT = new Variable("amount");
	public final static Variable NOW = new Variable("now");

	@Override
	public void toCode(StringBuilder sb) {
		sb.append(name);
	}

	@Override
	public Stream<Type> sideEffects() {
		return Stream.empty();
	}
}