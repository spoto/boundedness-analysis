package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record MethodCall(Expression exp, String m, Expression... exps) implements Command {

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(Stream.of(exp), Stream.of(exps))
				.flatMap(Expression::sideEffects);
	}
}