package it.univr.boundedness.analyzer.syntax.classes;

import it.univr.boundedness.analyzer.syntax.Type;

public record List(Type inner) implements Type {

	@Override
	public final String toString() {
		return "List<" + inner + ">";
	}
}