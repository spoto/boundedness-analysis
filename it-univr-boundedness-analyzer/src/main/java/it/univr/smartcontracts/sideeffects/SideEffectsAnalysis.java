package it.univr.smartcontracts.sideeffects;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Method;

public interface SideEffectsAnalysis {
	Stream<Type> of(Method method);
	void forEach(BiConsumer<Method, Stream<Type>> action);
}
