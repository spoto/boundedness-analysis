package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

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
	public Stream<Type> sideEffects() {
		return Stream.of(exps).flatMap(Expression::sideEffects);
	}
}