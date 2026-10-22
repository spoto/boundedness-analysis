package it.univr.boundedness.analyzer.syntax.commands;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalysis;
import it.univr.boundedness.analyzer.syntax.Expression;
import it.univr.boundedness.analyzer.syntax.Type;

// elementsType is the type of the elements of exp1
public class Push extends AbstractCommand {	
	private final Expression exp1;
	private final Type elementsType;
	private final Expression exp2;

	public Push(Expression exp1, Type elementsType, Expression exp2) {
		this.exp1 = exp1;
		this.elementsType = elementsType;
		this.exp2 = exp2;
	}

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