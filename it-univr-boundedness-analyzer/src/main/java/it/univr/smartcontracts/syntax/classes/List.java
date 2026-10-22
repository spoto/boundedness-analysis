package it.univr.smartcontracts.syntax.classes;

import it.univr.smartcontracts.syntax.Type;

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

	@Override
	public boolean hasSharedSubtypeWith(Type other) {
		// this is OK since our minimal language has no interfaces nor multiple class inheritance
		return isSubtypeOf(other) || other.isSubtypeOf(this);
	}
}