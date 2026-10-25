package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Type;

public class Sequence extends AbstractCommand {	
	private final AbstractCommand com1;
	private final AbstractCommand com2;

	public Sequence(AbstractCommand com1, AbstractCommand com2) {
		this.com1 = com1;
		this.com2 = com2;
		com1.setNext(com2);
	}

	public Command first() {
		return com1;
	}

	@Override
	public Command next() {
		return super.next();
	}

	@Override
	protected void setNext(AbstractCommand next) {
		super.setNext(next);
		com2.setNext(next);
	}

	@Override
	public void toCode(StringBuilder sb, String nesting, Optional<BoundednessAnalysis> boundedness) {
		com1.toCode(sb, nesting, boundedness);
		sb.append("\n");
		com2.toCode(sb, nesting, boundedness);
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.concat(com1.sideEffects(interpretation), com2.sideEffects(interpretation));
	}

	public State boundednessFirst(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		return before;
	}
}