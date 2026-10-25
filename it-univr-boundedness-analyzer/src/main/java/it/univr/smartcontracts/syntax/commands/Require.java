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

public class Require extends AbstractCommand {	
	private final Expression exp1;
	private final COP cop;
	private final Expression exp2;

	public Require(Expression exp1, COP cop, Expression exp2) {
		this.exp1 = exp1;
		this.cop = cop;
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
			sb.append(b.of(this));
			sb.append("\n");
		});

		sb.append(nesting);
		sb.append("require(");
		exp1.toCode(sb);
		sb.append(' ');
		cop.toCode(sb);
		sb.append(' ');
		exp2.toCode(sb);
		sb.append(");");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2).flatMap(exp -> exp.sideEffects(interpretation));
	}

	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		State approx = exp1.boundedness(before, sideEffectsAnalysis);
		approx = exp2.boundedness(approx, sideEffectsAnalysis);
		return approx.improve(exp1, cop, exp2);
	}
}