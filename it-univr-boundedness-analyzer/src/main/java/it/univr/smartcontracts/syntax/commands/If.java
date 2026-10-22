package it.univr.smartcontracts.syntax.commands;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.COP;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;

public class If extends AbstractCommand {	
	private final Expression exp1;
	private final COP cop;
	private final Expression exp2;
	private final AbstractCommand com1;
	private final AbstractCommand com2;

	public If(Expression exp1, COP cop, Expression exp2, AbstractCommand com1, AbstractCommand com2) {
		this.exp1 = exp1;
		this.cop = cop;
		this.exp2 = exp2;
		this.com1 = com1;
		this.com2 = com2;
	}

	public Command getThen() {
		return com1;
	}

	public Command getElse() {
		return com2;
	}

	@Override
	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append("if (");
		exp1.toCode(sb);
		sb.append(' ');
		cop.toCode(sb);
		sb.append(' ');
		exp2.toCode(sb);
		sb.append(") {\n");
		com1.toCode(sb, nesting + Class.NESTING);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}\n");
		sb.append(nesting);
		sb.append("else {\n");
		com2.toCode(sb, nesting + Class.NESTING);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2, com1, com2).flatMap(x -> x.sideEffects(interpretation));
	}
}