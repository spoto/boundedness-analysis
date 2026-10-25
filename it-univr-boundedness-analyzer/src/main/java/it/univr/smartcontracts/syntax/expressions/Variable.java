package it.univr.smartcontracts.syntax.expressions;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Path;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;
import it.univr.smartcontracts.syntax.classes.Int;
import it.univr.smartcontracts.syntax.classes.List;
import it.univr.smartcontracts.syntax.classes.Long;

public record Variable(String name, Type type) implements Path {
	public final static Variable CALLER = new Variable("caller", Class.CONTRACT);
	public final static Variable AMOUNT = new Variable("amount", Int.INSTANCE);
	public final static Variable NOW = new Variable("now", Long.INSTANCE);

	@Override
	public void toCode(StringBuilder sb) {
		sb.append(name);
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return Stream.empty();
	}

	@Override
	public Optional<Path> asPath() {
		return Optional.of(this);
	}

	@Override
	public Variable root() {
		return this;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean survives(Stream<Type> updated) {
		return !(type instanceof List list) || updated.noneMatch(u -> u.hasSharedSubtypeWith(list.inner()));
	}
}