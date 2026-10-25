package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Type;

public class Nop extends AbstractCommand {

	@Override
	public void toCode(StringBuilder sb, String nesting, Optional<BoundednessAnalysis> boundedness) {
		boundedness.ifPresent(b -> {
			sb.append(nesting);
			sb.append(b.of(this));
			sb.append("\n");
		});

		sb.append(nesting);
		sb.append("nop");
	}

	@Override
	public Command next() {
		return super.next();
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.empty();
	}

	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		return before;
	}
}