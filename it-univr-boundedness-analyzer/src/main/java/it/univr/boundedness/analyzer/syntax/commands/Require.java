package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.COP;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record Require(Expression exp1, COP cop, Expression exp2) implements Command {	

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("require(");
		exp1.toCode(sb);
		sb.append(' ');
		cop.toCode(sb);
		sb.append(' ');
		exp2.toCode(sb);
		sb.append(");");
	}

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(exp1.sideEffects(), exp2.sideEffects());
	}
}