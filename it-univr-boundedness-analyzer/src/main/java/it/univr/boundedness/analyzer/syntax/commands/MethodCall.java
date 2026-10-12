package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;
import it.univr.boundedness.analyzer.syntax.classes.Class;

import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record MethodCall(Expression exp, Class clazz, Expression... exps) implements Command {

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(Stream.of(exp), Stream.of(exps))
				.flatMap(Expression::sideEffects);
	}
}