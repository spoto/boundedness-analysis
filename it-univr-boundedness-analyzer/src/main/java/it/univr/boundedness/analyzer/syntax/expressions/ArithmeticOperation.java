package it.univr.boundedness.analyzer.syntax.expressions;

import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.types.Type;

public record ArithmeticOperation(Expression exp1, AOP aop, Expression exp2) implements Expression {

	public static enum AOP {
		ADD, SUB, MUL, DIV
	}

	@Override
	public Stream<Type> sideEffects() {
		return Stream.concat(exp1.sideEffects(), exp2.sideEffects());
	}
}