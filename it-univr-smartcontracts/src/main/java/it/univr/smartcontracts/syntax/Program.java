package it.univr.smartcontracts.syntax;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.syntax.classes.Class;

public class Program implements Iterable<Class> {
	private final Set<Class> classes = new HashSet<>();

	public Program(Class... classes) {
		for (var clazz: classes)
			this.classes.add(clazz);
	}

	public final String toCode() {
		return toCode(Optional.empty());
	}

	public final String toCode(BoundednessAnalysis boundednessAnalysis) {
		return toCode(Optional.ofNullable(boundednessAnalysis));
	}

	public String toCode(Optional<BoundednessAnalysis> boundednessAnalysis) {
		var sb = new StringBuilder();
		for (var clazz: classes) {
			if (!sb.isEmpty())
				sb.append("\n\n");
			clazz.toCode(sb, boundednessAnalysis);
		}

		return sb.toString();
	}

	@Override
	public Iterator<Class> iterator() {
		return classes.iterator();
	}

	public Stream<Class> classes() {
		return classes.stream();
	}
}