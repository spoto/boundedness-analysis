package it.univr.smartcontracts.syntax.classes;

import java.util.Optional;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.syntax.Command;

public record Method(Class definingClass, String name, Command body) implements Comparable<Method> {
	
	// custom record constructor
	public Method {
		definingClass.add(this);
	}

	public boolean isConstructor() {
		return name.equals(definingClass.name());
	}

	@Override
	public final boolean equals(Object other) {
		return other instanceof Method method
				&& name.equals(method.name)
				&& definingClass.equals(method.definingClass);
	}

	@Override
	public final int hashCode() {
		return name.hashCode() ^ definingClass.hashCode();
	}

	@Override
	public final String toString() {
		return definingClass.name() + '.' + name + "()";
	}

	public String toCode() {
		return toCode(null);
	}

	public final String toCode(BoundednessAnalysis boundedness) {
		var sb = new StringBuilder();
		toCode(sb, "", Optional.ofNullable(boundedness));
		return sb.toString();
	}

	public void toCode(StringBuilder sb, String nesting, Optional<BoundednessAnalysis> boundedness) {
		sb.append(nesting);
		sb.append(name);
		sb.append("() {\n");
		body.toCode(sb, nesting + Class.NESTING, boundedness);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}

	@Override
	public int compareTo(Method other) {
		int diff = definingClass.compareTo(other.definingClass);
		if (diff != 0)
			return diff;

		return name.compareTo(other.name);
	}
}