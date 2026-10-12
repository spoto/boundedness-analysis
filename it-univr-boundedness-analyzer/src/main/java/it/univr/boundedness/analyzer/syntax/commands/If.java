package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.COP;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;
import it.univr.boundedness.analyzer.syntax.classes.Class;

public record If(Expression exp1, COP cop, Expression exp2, Command com1, Command com2) implements Command {	

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("if (");
		exp1.toCode(sb);
		sb.append(' ');
		cop.toCode(sb);
		sb.append(' ');
		exp2.toCode(sb);
		sb.append(") {\n");
		com1.toCode(sb, nesting + Class.NESTING);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}\n");
		sb.append(nesting);
		sb.append("else {\n");
		com2.toCode(sb, nesting + Class.NESTING);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2, com1, com2).flatMap(x -> x.sideEffects(interpretation));
	}
}