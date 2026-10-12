package it.univr.boundedness.analyzer.syntax.classes;

import it.univr.boundedness.analyzer.syntax.Type;

public record Field(Class definingClass, String name, Type type, boolean isFinal) implements Comparable<Field> {

	// custom record constructor
	public Field {
		definingClass.add(this);
	}

	@Override
	public final boolean equals(Object other) {
		return other instanceof Field field
				&& name.equals(field.name)
				&& definingClass.equals(field.definingClass);
	}

	@Override
	public final int hashCode() {
		return name.hashCode() ^ definingClass.hashCode();
	}

	@Override
	public final String toString() {
		return definingClass.name() + '.' + name + ":" + type;
	}

	public final String toCode() {
		var sb = new StringBuilder();
		toCode(sb, "");
		return sb.toString();
	}

	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append(type);
		sb.append(' ');
		sb.append(name);
		sb.append(';');
	}

	@Override
	public int compareTo(Field other) {
		int diff = definingClass.compareTo(other.definingClass);
		if (diff != 0)
			return diff;

		return name.compareTo(other.name);
	}
}