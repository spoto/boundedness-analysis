package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.syntax.Command;

abstract class AbstractCommand implements Command {
	private AbstractCommand next;

	protected void setNext(AbstractCommand next) {
		this.next = next;
	}

	protected Command next() {
		return next;
	}

	@Override
	public final String toCode() {
		return toCode(null);
	}

	public final String toCode(BoundednessAnalysis boundedness) {
		var sb = new StringBuilder();
		toCode(sb, "", Optional.ofNullable(boundedness));
		return sb.toString();
	}

	/**
	 * Commands are uniquely identified by object identity, so that they stand
	 * for their same program point.
	 */
	@Override
	public final boolean equals(Object other) {
		return this == other;
	}

	public final int hashCode() {
		return super.hashCode();
	}
}