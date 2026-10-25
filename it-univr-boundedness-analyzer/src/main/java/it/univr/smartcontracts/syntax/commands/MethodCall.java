package it.univr.smartcontracts.syntax.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;

public class MethodCall extends AbstractCommand {
	private final Expression exp;
	private final Class clazz;
	private final String name;
	private final Expression[] exps;
	
	public MethodCall(Expression exp, Class clazz, String name, Expression... exps) {
		this.exp = exp;
		this.clazz = clazz;
		this.name = name;
		this.exps = exps;
	}

	@Override
	public Command next() {
		return super.next();
	}

	@Override
	public void toString(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append(name);
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