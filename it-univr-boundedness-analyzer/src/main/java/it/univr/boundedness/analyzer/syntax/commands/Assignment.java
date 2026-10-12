package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;
import it.univr.boundedness.analyzer.syntax.expressions.Variable;

public record Assignment(Variable v, Expression exp) implements Command {

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append(v.name());
		sb.append(" = ");
		exp.toCode(sb);
		sb.append(';');
	}

	@Override
	public Stream<Type> sideEffects() {
		return exp.sideEffects();
	}	
}