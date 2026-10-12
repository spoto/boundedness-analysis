package it.univr.boundedness.analyzer.sideeffects;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Type;

public interface SideEffectsProducer {
	Stream<Type> sideEffects(SideEffectsAnalysis intepretation);
}