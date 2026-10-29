package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.Type;

// elementsType is the type of the elements of exp
public class RemoveFirst extends AbstractCommand {	
	private final Expression exp;
	private final Type elementsType;

	public RemoveFirst(Expression exp, Type elementsType) {
		this.exp = exp;
		this.elementsType = elementsType;
	}

	@Override
	public Command next() {
		return super.next();
	}

	public boolean canAffect(Type elementsType) {
		return elementsType.hasSharedSubtypeWith(this.elementsType);
	}

	@Override
	public void toCode(StringBuilder sb, String nesting, Optional<BoundednessAnalysis> boundedness) {
		boundedness.ifPresent(b -> {
			sb.append(nesting);
			sb.append(b.at(this));
			sb.append("\n");
		});

		sb.append(nesting);
		exp.toCode(sb);
		sb.append(".removeFirst();");
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return exp.sideEffects(interpretation);
	}

	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		return exp.boundedness(before, sideEffectsAnalysis); // TODO: improve?
	}

	@Override
	public Stream<Command> subcommands() {
		return Stream.of(this);
	}
}