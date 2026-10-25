package it.univr.smartcontracts.syntax.commands;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.LValue;
import it.univr.smartcontracts.syntax.Type;

public class Assignment extends AbstractCommand {
	private final LValue lvalue;
	private final Expression exp;

	public Assignment(LValue lvalue, Expression exp) {
		this.lvalue = lvalue;
		this.exp = exp;
	}

	public LValue lvalue() {
		return lvalue;
	}

	public Expression exp() {
		return exp;
	}

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