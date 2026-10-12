package it.univr.boundedness.analyzer.syntax.classes;

import it.univr.boundedness.analyzer.syntax.Command;

public record Method(Class definingClass, String name, Command body) {
	
	// custom record constructor
	public Method {
		definingClass.add(this);
	}

	public boolean isConstructor() {
		return name.equals(definingClass.name());
	}

	@Override
	public final String toString() {
		return definingClass.name() + '.' + name + "()";
	}

	public String toCode() {
		var sb = new StringBuilder();
		toCode(sb, "");
		return sb.toString();
	}

	public void toCode(StringBuilder sb, String nesting) {
		sb.append(nesting);
		sb.append(name);
		sb.append("() {\n");
		body.toCode(sb, nesting + Class.NESTING);
		sb.append("\n");
		sb.append(nesting);
		sb.append("}");
	}
}