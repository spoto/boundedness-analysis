package it.univr.smartcontracts.syntax.classes;

import it.univr.smartcontracts.syntax.Type;

public record Long() implements Type {

	public final static Long INSTANCE = new Long();

	@Override
	public final String toString() {
		return "long";
	}

	@Override
	public int compareTo(Type other) {
		return getClass().getName().compareTo(other.getClass().getName());
	}

	@Override
	public boolean isSubtypeOf(Type other) {
		return other instanceof Long;
	}
}