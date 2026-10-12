package it.univr.boundedness.analyzer.sideeffects;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Type;
import it.univr.boundedness.analyzer.syntax.classes.Method;

public interface SideEffectsAnalysis {
	Stream<Type> of(Method method);
	void forEach(BiConsumer<Method, Stream<Type>> action);
}
