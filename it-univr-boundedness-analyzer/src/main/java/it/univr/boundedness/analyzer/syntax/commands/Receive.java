package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record Receive(Expression exp1, Expression exp2) implements Command {	

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		exp1.toCode(sb);
		sb.append(".receive(");
		exp2.toCode(sb);
		sb.append(");");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2).flatMap(exp -> exp.sideEffects(interpretation));
	}
}