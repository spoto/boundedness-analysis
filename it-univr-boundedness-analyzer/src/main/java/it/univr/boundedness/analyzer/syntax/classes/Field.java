package it.univr.boundedness.analyzer.syntax.classes;

import it.univr.boundedness.analyzer.syntax.Type;

public record Field(Class definingClass, String name, Type type, boolean isFinal) {

	// custom record constructor
	public Field {
		definingClass.add(this);
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
}