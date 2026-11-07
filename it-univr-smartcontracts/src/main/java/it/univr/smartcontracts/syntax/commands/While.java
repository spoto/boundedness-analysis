package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.COP;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;

public class While extends AbstractCommand {
	private final Expression exp1;
	private final COP cop;
	private final Expression exp2;
	private final AbstractCommand com;

	public While(Expression exp1, COP cop, Expression exp2, AbstractCommand com) {
		this.exp1 = exp1;
		this.cop = cop;
		this.exp2 = exp2;
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
	public void toCode(StringBuilder sb, String nesting, Optional<BoundednessAnalysis> boundedness) {
		boundedness.ifPresent(b -> {
			sb.append(nesting);
			sb.append(b.at(this));
			sb.append("\n");
		});
		sb.append(nesting);
		sb.append("while (");
		exp1.toCode(sb);
		sb.append(' ');
		cop.toCode(sb);
		sb.append(' ');
		exp2.toCode(sb);
		sb.append(") {\n");
		com.toCode(sb, nesting + Class.NESTING, boundedness);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2, com).flatMap(x -> x.sideEffects(interpretation));
	}

	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		State approx = exp1.boundedness(before, sideEffectsAnalysis);
		approx = exp2.boundedness(approx, sideEffectsAnalysis);
		return approx.improve(exp1, cop.neg(), exp2);
	}

	public State boundednessInner(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		State approx = exp1.boundedness(before, sideEffectsAnalysis);
		approx = exp2.boundedness(approx, sideEffectsAnalysis);
		return approx.improve(exp1, cop, exp2);
	}

	@Override
	public Stream<Command> subcommands() {
		return Stream.concat(Stream.of(this), com.subcommands());
	}

	@Override
	public Stream<Expression> subexpressions() {
		return Stream.concat(com.subexpressions(), Stream.of(exp1, exp2).flatMap(Expression::subexpressions));
	}
}