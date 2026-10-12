package it.univr.boundedness.analyzer.syntax.classes;

import it.univr.boundedness.analyzer.syntax.Type;

public record Int() implements Type {

	@Override
	public final String toString() {
		return "int";
	}
}