package it.univr.smartcontracts.syntax.expressions;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.LValue;
import it.univr.smartcontracts.syntax.Path;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Field;
import it.univr.smartcontracts.syntax.classes.List;

public record FieldAccess(Expression exp, Field f) implements LValue {

	@Override
	public void toCode(StringBuilder sb) {
		exp.toCode(sb);
		sb.append('.');
		sb.append(f.name());
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return exp.sideEffects(interpretation);
	}

	@Override
	public Optional<Path> asPath() {
		Optional<Path> expAsPath;

		if (exp instanceof LValue lv && f.isFinal() && (expAsPath = lv.asPath()).isPresent())
			return Optional.of(new FieldAccessPath(expAsPath.get()));
		else
			return Optional.empty();
	}

	private class FieldAccessPath implements Path {
		private final Path inner;

		private FieldAccessPath(Path inner) {
			this.inner = inner;
		}

		@Override
		public Type getType() {
			return f.type();
		}

		@Override
		public String toString() {
			return inner + "." + f.name();
		}

		@Override
		public boolean survives(Stream<Type> updated) {
			return !(getType() instanceof List list) || updated.noneMatch(u -> u.hasSharedSubtypeWith(list.inner()));
		}
	}
}