package it.univr.smartcontracts.syntax.commands;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;

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

	public Expression exp1() {
		return exp1;
	}

	public Expression exp2() {
		return exp2;
	}

	@Override
	public Command next() {
		return super.next();
	}

	public Type elementsType() {
		return elementsType;
	}

	@Override
	public void toString(StringBuilder sb, String nesting) {
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