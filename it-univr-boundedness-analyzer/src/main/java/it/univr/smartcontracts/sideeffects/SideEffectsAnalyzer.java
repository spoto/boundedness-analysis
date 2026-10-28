package it.univr.smartcontracts.sideeffects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.univr.smartcontracts.syntax.Program;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;
import it.univr.smartcontracts.syntax.classes.Method;

public class SideEffectsAnalyzer {

	public SideEffectsAnalysis analyze(Program program) {
		Set<Class> classes = program.classes().collect(Collectors.toSet());
		SideEffects interpretation = mkInitialInterpretation(classes);
		SideEffects oldInterpretation;

		do {
			oldInterpretation = interpretation;
			interpretation = mkNextInterpretation(classes, oldInterpretation);
		}
		while (!oldInterpretation.equals(interpretation));

		return interpretation;
	}

	private SideEffects mkInitialInterpretation(Set<Class> classes) {
		var noSideEffects = new HashMap<Method, Set<Type>>();
		classes.stream().flatMap(Class::methods).forEach(method -> noSideEffects.put(method, Collections.emptySet()));

		return new SideEffects(noSideEffects);
	}

	private SideEffects mkNextInterpretation(Set<Class> classes, SideEffects interpretation) {
		var sideEffects = new HashMap<Method, Set<Type>>();
		classes.stream().flatMap(Class::methods).forEach
			(method -> sideEffects.put(method, method.body().sideEffects(interpretation).collect(Collectors.toSet())));

		return new SideEffects(sideEffects);
	}

	private static class SideEffects implements SideEffectsAnalysis {
		private final Map<Method, Set<Type>> sideEffects;

		private SideEffects(Map<Method, Set<Type>> sideEffects) {
			this.sideEffects = sideEffects;
		}

		@Override
		public Stream<Type> of(Method method) {
			return sideEffects.get(method).stream();
		}

		@Override
		public void forEach(BiConsumer<Method, Stream<Type>> action) {
			sideEffects.entrySet().forEach(entry -> action.accept(entry.getKey(), entry.getValue().stream()));
		}

		@Override
		public int hashCode() {
			return sideEffects.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof SideEffects se && sideEffects.equals(se.sideEffects);
		}
	}
}