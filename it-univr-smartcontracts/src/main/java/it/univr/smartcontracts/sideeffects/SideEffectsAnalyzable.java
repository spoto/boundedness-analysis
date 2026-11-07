package it.univr.smartcontracts.sideeffects;

import java.util.stream.Stream;

import it.univr.smartcontracts.syntax.Type;

public interface SideEffectsAnalyzable {
	Stream<Type> sideEffects(SideEffectsAnalysis intepretation);
}