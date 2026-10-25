package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Type;

public class Return extends AbstractCommand {

	@Override
	public void toCode(StringBuilder sb, String nesting, Optional<BoundednessAnalysis> boundedness) {
		boundedness.ifPresent(b -> {
			sb.append(nesting);
			sb.append(b.of(this));
			sb.append("\n");
		});

		sb.append(nesting);
		sb.append("return");
	}

	@Override
	protected void setNext(AbstractCommand next) {
		// nothing to do, the next of a return is not significant
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.empty();
	}	
}