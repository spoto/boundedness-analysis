package it.univr.boundedness.analyzer.syntax.classes;

import it.univr.boundedness.analyzer.syntax.Type;

public record List(Type inner) implements Type {

	@Override
	public final String toString() {
		return "List<" + inner + ">";
	}

	@Override
	public int compareTo(Type other) {
		if (other instanceof List list)
			return inner.compareTo(list.inner);
		else
			return getClass().getName().compareTo(other.getClass().getName());
	}

	@Override
	public boolean isSubtypeOf(Type other) {
		return (other instanceof List list && inner.equals(list.inner))
				|| other.equals(Class.OBJECT);
	}
}