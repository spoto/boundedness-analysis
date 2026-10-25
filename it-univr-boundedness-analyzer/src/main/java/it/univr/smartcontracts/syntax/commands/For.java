package it.univr.smartcontracts.syntax.commands;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;
import it.univr.smartcontracts.syntax.expressions.Variable;

public class For extends AbstractCommand {	
	private final Variable v;
	private final Expression exp;
	private final AbstractCommand com;

	public For(Variable v, Expression exp, AbstractCommand com) {
		this.v = v;
		this.exp = exp;
		this.com = com;
		com.setNext(this);
	}

	public Command inner() {
		return com;
	}

	@Override
	public Command next() {
		return super.next();
	}

	@Override
	public void toString(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("for ");
		sb.append(v.name());
		sb.append(" in ");
		exp.toCode(sb);
		sb.append(" {\n");
		com.toString(sb, nesting + Class.NESTING);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp, com).flatMap(x -> x.sideEffects(interpretation));
	}
}