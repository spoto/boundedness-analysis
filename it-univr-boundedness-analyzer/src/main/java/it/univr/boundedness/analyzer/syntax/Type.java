package it.univr.boundedness.analyzer.syntax;

public interface Type extends Comparable<Type> {
	boolean isSubtypeOf(Type other);
}