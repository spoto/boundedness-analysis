package it.univr.smartcontracts.syntax.expressions;

import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.LValue;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Field;

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
}