package it.univr.smartcontracts.syntax.expressions;

import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.boundedness.State;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.LValue;
import it.univr.smartcontracts.syntax.Path;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Field;
import it.univr.smartcontracts.syntax.classes.List;

public class FieldAccess implements LValue {
	private final Expression exp;
	private final Field field;

	public FieldAccess(Expression exp, Field field) {
		this.exp = exp;
		this.field = field;
	}

	@Override
	public void toCode(StringBuilder sb) {
		exp.toCode(sb);
		sb.append('.');
		sb.append(field.name());
	}

	public Expression exp() {
		return exp;
	}

	public Field field() {
		return field;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof FieldAccess fa) {
			System.out.println(fa.exp + " vs " + exp);
			System.out.println("1: " + (fa.exp.equals(exp)));
			System.out.println("1: " + (fa.field.equals(field)));
		}
		return other instanceof FieldAccess fa && fa.exp.equals(exp) && fa.field.equals(field);
	}

	@Override
	public int hashCode() {
		return exp.hashCode() ^ field.hashCode();
	}

	@Override
	public Stream<Type> sideEffects(SideEffectsAnalysis interpretation) {
		return exp.sideEffects(interpretation);
	}

	@Override
	public State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis) {
		return exp.boundedness(before, sideEffectsAnalysis);
	}

	@Override
	public Optional<Path> asPath() {
		Optional<Path> expAsPath;

		if (exp instanceof LValue lv && field.isFinal() && (expAsPath = lv.asPath()).isPresent())
			return Optional.of(new FieldAccessPath(expAsPath.get()));
		else
			return Optional.empty();
	}

	@Override
	public Stream<Expression> subexpressions() {
		return Stream.concat(Stream.of(this), exp.subexpressions());
	}

	private class FieldAccessPath extends FieldAccess implements Path {
		private final Path inner;

		private FieldAccessPath(Path inner) {
			super(exp, field);

			this.inner = inner;
		}

		@Override
		public Variable root() {
			return inner.root();
		}

		@Override
		public Type type() {
			return field.type();
		}

		@Override
		public String toString() {
			return inner + "." + field.name();
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof FieldAccessPath && super.equals(other);
		}

		@Override
		public boolean survives(Stream<Type> updated) {
			return !(type() instanceof List list) || updated.noneMatch(u -> u.hasSharedSubtypeWith(list.inner()));
		}

		@Override
		public Path replaceRootWith(Path path) {
			return new FieldAccess(inner.replaceRootWith(path), field).asPath().get();
		}
	}
}