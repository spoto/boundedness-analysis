package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;
import it.univr.boundedness.analyzer.syntax.classes.Class;

public record ConstructorCall(Class clazz, Expression... exps) implements Expression {

	@Override
	public void toCode(StringBuilder sb) {
		sb.append("new ");
		sb.append(clazz.name());
		sb.append('(');

		for (int pos = 0; pos < exps.length; pos++) {
			exps[pos].toCode(sb);
			if (pos < exps.length - 1)
				sb.append(", ");
		}

		sb.append(')');
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		Set<Type> sideEffectsOfCallee = new HashSet<>();
		// linear search: inefficient but OK for small programs
		interpretation.forEach((method, types) -> {
			if (method.isConstructor() && method.name().equals(clazz.name()) && method.definingClass().equals(clazz))
				types.forEach(sideEffectsOfCallee::add);
		});

		return Stream.concat(sideEffectsOfCallee.stream(), Stream.of(exps).flatMap(exp -> exp.sideEffects(interpretation)));
	}
}