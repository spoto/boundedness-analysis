package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;
import it.univr.smartcontracts.syntax.expressions.Variable;

public class For extends AbstractCommand {	
	private final Variable v;
	private final Expression exp;
	private final Type elementsType;
	private final AbstractCommand com;

	public For(Variable v, Expression exp, Type elementsType, AbstractCommand com) {
		this.v = v;
		this.exp = exp;
		this.elementsType = elementsType;
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
		sb.append("for ");
		sb.append(v.name());
		sb.append(" in ");
		exp.toCode(sb);
		sb.append(" {\n");
		com.toCode(sb, nesting + Class.NESTING, boundedness);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.of(exp, com).flatMap(x -> x.sideEffects(interpretation));
	}

	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		return before;
	}

	public State boundednessInner(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		// the other rule for inner is implied by this one that yields less precise results, in general
		return exp.boundedness(before, sideEffectsAnalysis).invalidate(v);
	}

	@Override
	public Stream<Command> subcommands() {
		return Stream.concat(Stream.of(this), com.subcommands());
	}

	public boolean hasWarning(BoundednessAnalysis boundednessAnalysis) {
		return !boundednessAnalysis.allListsAreBounded(elementsType);
	}
}