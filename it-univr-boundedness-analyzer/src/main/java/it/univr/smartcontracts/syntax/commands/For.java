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

	Command getInner() {
		return com;
	}

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("for ");
		sb.append(v.name());
		sb.append(" in ");
		exp.toCode(sb);
		sb.append(" {\n");
		com.toCode(sb, nesting + Class.NESTING);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp, com).flatMap(x -> x.sideEffects(interpretation));
	}
}