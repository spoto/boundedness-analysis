package it.univr.smartcontracts.syntax.commands;

import java.util.Optional;

import it.univr.smartcontracts.syntax.Command;

abstract class AbstractCommand implements Command {
	private AbstractCommand next;

	protected void setNext(AbstractCommand next) {
		this.next = next;
	}

	@Override
	public final String toString() {
		var sb = new StringBuilder();
		toString(sb, "");
		return sb.toString();
	}

	@Override
	public Optional<Command> next() {
		return Optional.ofNullable(next);
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