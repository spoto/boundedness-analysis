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

public class If extends AbstractCommand {	
	private final Expression exp1;
	private final COP cop;
	private final Expression exp2;
	private final AbstractCommand then;
	private final AbstractCommand _else;

	public If(Expression exp1, COP cop, Expression exp2, AbstractCommand then, AbstractCommand _else) {
		this.exp1 = exp1;
		this.cop = cop;
		this.exp2 = exp2;
		this.then = then;
		this._else = _else;
	}

	public Command then() {
		return then;
	}

	public Command _else() {
		return _else;
	}

	@Override
	public Command next() {
		return super.next();
	}

	@Override
	protected void setNext(AbstractCommand next) {
		super.setNext(next);
		then.setNext(next);
		_else.setNext(next);
	}

	@Override
	public void toCode(StringBuilder sb, String nesting, Optional<BoundednessAnalysis> boundedness) {
		boundedness.ifPresent(b -> {
			sb.append(nesting);
			sb.append(b.at(this));
			sb.append("\n");
		});
		sb.append(nesting);
		sb.append("if (");
		exp1.toCode(sb);
		sb.append(' ');
		cop.toCode(sb);
		sb.append(' ');
		exp2.toCode(sb);
		sb.append(") {\n");
		then.toCode(sb, nesting + Class.NESTING, boundedness);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}\n");
		sb.append(nesting);
		sb.append("else {\n");
		_else.toCode(sb, nesting + Class.NESTING, boundedness);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp1, exp2, then, _else).flatMap(x -> x.sideEffects(interpretation));
	}

	public State boundednessThen(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		State approx = exp1.boundedness(before, sideEffectsAnalysis);
		approx = exp2.boundedness(approx, sideEffectsAnalysis);
		return approx.improve(exp1, cop, exp2);
	}

	public State boundednessElse(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		State approx = exp1.boundedness(before, sideEffectsAnalysis);
		approx = exp2.boundedness(approx, sideEffectsAnalysis);
		return approx.improve(exp1, cop.neg(), exp2);
	}

	@Override
	public Stream<Command> subcommands() {
		return Stream.concat(Stream.of(this), Stream.of(then, _else).flatMap(Command::subcommands));
	}

	@Override
	public Stream<Expression> subexpressions() {
		return Stream.concat(Stream.of(exp1, exp2).flatMap(Expression::subexpressions),
				Stream.of(then, _else).flatMap(Command::subexpressions));
	}
}