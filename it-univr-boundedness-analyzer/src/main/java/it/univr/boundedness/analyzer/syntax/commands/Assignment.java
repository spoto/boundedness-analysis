package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.LValue;
import it.univr.boundedness.analyzer.syntax.Type;

public record Assignment(LValue lvalue, Expression exp) implements Command {

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		lvalue.toCode(sb);
		sb.append(" = ");
		exp.toCode(sb);
		sb.append(';');
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(lvalue, exp).flatMap(exp -> exp.sideEffects(interpretation));
	}	
}