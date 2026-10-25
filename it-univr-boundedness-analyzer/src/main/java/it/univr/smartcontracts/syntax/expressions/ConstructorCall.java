package it.univr.smartcontracts.syntax.expressions;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;

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

	@Override
	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		for (int pos = 0; pos < exps.length; pos++)
			before = exps[pos].boundedness(before, sideEffectsAnalysis);

		// the constructor of List is a sort of built-in without side-effects
		if (clazz.equals(Class.LIST))
			return before;

		// linear search: inefficient but OK for small programs
		var updated = new AtomicReference<Stream<Type>>();
		sideEffectsAnalysis.forEach((method, types) -> {
			if (method.isConstructor() && method.name().equals(clazz.name()) && method.definingClass().equals(clazz))
				updated.set(types);
		});

		if (updated.get() == null)
			throw new IllegalStateException("Unknown constructor target for " + this);

		return before.survive(updated.get());
	}
}