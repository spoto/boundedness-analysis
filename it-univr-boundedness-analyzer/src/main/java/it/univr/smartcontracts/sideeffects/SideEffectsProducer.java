package it.univr.smartcontracts.sideeffects;

import java.util.stream.Stream;

import it.univr.smartcontracts.syntax.Type;

public interface SideEffectsProducer {
	Stream<Type> sideEffects(SideEffectsAnalysis intepretation);
}