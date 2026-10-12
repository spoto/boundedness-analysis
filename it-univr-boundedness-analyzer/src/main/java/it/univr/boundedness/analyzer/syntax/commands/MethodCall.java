package it.univr.boundedness.analyzer.syntax.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import it.univr.boundedness.analyzer.syntax.classes.Class;
import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record MethodCall(Expression exp, Class clazz, String name, Expression... exps) implements Command {

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		Set<Type> sideEffectsOfCallees = new HashSet<>();
		// linear search: inefficient but OK for small programs
		interpretation.forEach((method, types) -> {
			if (!method.isConstructor() && method.name().equals(name) &&
					(method.definingClass().isSubtypeOf(clazz) || clazz.isSubtypeOf(method.definingClass())))
				types.forEach(sideEffectsOfCallees::add);
		});

		return Stream.concat(Stream.of(exp), Stream.of(exps))
				.flatMap(exp -> exp.sideEffects(interpretation));
	}
}