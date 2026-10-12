package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;
import it.univr.boundedness.analyzer.syntax.classes.Class;
import it.univr.boundedness.analyzer.syntax.expressions.Variable;

public record For(Variable v, Expression exp, Command com) implements Command {	

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("for ");
		sb.append(v.name());
		sb.append(" in ");
		exp.toCode(sb);
		sb.append(" {\n");
		com.toCode(sb, nesting + Class.NESTING);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(exp.sideEffects(), com.sideEffects());
	}
}