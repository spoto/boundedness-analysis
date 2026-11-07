package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;

public class Receive extends AbstractCommand {	
	private final Expression exp1;
	private final Expression exp2;

	public Receive(Expression exp1, Expression exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
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
		exp1.toCode(sb);
		sb.append(".receive(");
		exp2.toCode(sb);
		sb.append(");");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2).flatMap(exp -> exp.sideEffects(interpretation));
	}

	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		return exp2.boundedness(exp1.boundedness(before, sideEffectsAnalysis), sideEffectsAnalysis);
	}

	@Override
	public Stream<Command> subcommands() {
		return Stream.of(this);
	}

	@Override
	public Stream<Expression> subexpressions() {
		return Stream.of(exp1, exp2).flatMap(Expression::subexpressions);
	}
}