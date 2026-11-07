package it.univr.smartcontracts.syntax;

public interface Type extends Comparable<Type> {
	boolean isSubtypeOf(Type other);
	boolean hasSharedSubtypeWith(Type other);
}