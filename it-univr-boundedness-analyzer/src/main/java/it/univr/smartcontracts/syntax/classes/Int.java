package it.univr.smartcontracts.syntax.classes;

import it.univr.smartcontracts.syntax.Type;

public record Int() implements Type {

	public final static Int INSTANCE = new Int();

	@Override
	public final String toString() {
		return "int";
	}

	@Override
	public int compareTo(Type other) {
		return getClass().getName().compareTo(other.getClass().getName());
	}

	@Override
	public boolean isSubtypeOf(Type other) {
		return other instanceof Int;
	}
}