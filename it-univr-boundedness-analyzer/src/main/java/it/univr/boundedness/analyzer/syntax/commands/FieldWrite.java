package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;
import it.univr.boundedness.analyzer.syntax.classes.Field;

public record FieldWrite(Expression exp1, Field f, Expression exp2) implements Command {	

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		exp1.toCode(sb);
		sb.append('.');
		sb.append(f.name());
		sb.append(" = ");
		exp2.toCode(sb);
		sb.append(';');
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2).flatMap(exp -> exp.sideEffects(interpretation));
	}
}