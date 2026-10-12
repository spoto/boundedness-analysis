package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

// elementsType is the type of the elements of exp1
public record Push(Expression exp1, Type elementsType, Expression exp2) implements Command {	

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		exp1.toCode(sb);
		sb.append(".push(");
		exp2.toCode(sb);
		sb.append(");");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.concat(Stream.of(elementsType),
			Stream.of(exp1, exp2).flatMap(exp -> exp.sideEffects(interpretation)));
	}
}