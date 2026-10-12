package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.COP;
import it.univr.boundedness.analyzer.syntax.Command;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

public record If(Expression exp1, COP cop, Expression exp2, Command com1, Command com2) implements Command {	

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2, com1, com2).flatMap(x -> x.sideEffects(interpretation));
	}
}