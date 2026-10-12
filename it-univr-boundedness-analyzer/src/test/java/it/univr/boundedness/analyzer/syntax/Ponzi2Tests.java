package it.univr.boundedness.analyzer.syntax;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalyzer;
import it.univr.boundedness.analyzer.syntax.classes.Class;
import it.univr.boundedness.analyzer.syntax.classes.Field;
import it.univr.boundedness.analyzer.syntax.classes.List;
import it.univr.boundedness.analyzer.syntax.classes.Method;
import it.univr.boundedness.analyzer.syntax.commands.Assignment;
import it.univr.boundedness.analyzer.syntax.commands.FieldWrite;
import it.univr.boundedness.analyzer.syntax.commands.For;
import it.univr.boundedness.analyzer.syntax.commands.Push;
import it.univr.boundedness.analyzer.syntax.commands.Receive;
import it.univr.boundedness.analyzer.syntax.commands.Require;
import it.univr.boundedness.analyzer.syntax.commands.Sequence;
import it.univr.boundedness.analyzer.syntax.expressions.ArithmeticOperation;
import it.univr.boundedness.analyzer.syntax.expressions.Constant;
import it.univr.boundedness.analyzer.syntax.expressions.ConstructorCall;
import it.univr.boundedness.analyzer.syntax.expressions.FieldRead;
import it.univr.boundedness.analyzer.syntax.expressions.Size;
import it.univr.boundedness.analyzer.syntax.expressions.Variable;

public class Ponzi2Tests {

	private static Class ponzi2;

	@BeforeAll
	public static void mkProgram() {
		ponzi2 = new Class("Ponzi2", Class.CONTRACT);
		var listOfContract = new List(Class.CONTRACT);
		var investors = new Field(ponzi2, "investors", listOfContract, true);
		new Method(ponzi2, ponzi2.name(),
				new Sequence(
						new FieldWrite(Variable.THIS, investors, new ConstructorCall(Class.LIST)),
						new Push(new FieldRead(Variable.THIS, investors), Class.CONTRACT, Variable.CALLER)
						)
				);

		var eachInvestorGets = new Variable("eachInvestorGets");
		var investor = new Variable("investor");
		var thisInvestors = new FieldRead(Variable.THIS, investors);

		new Method(ponzi2, "invest",
				new Sequence(new Sequence(new Sequence(new Sequence(
						new Require(Variable.AMOUNT, COP.GE, new Constant(1000)),
						new Require(new Size(thisInvestors), COP.LT, new Constant(100))),

						new Assignment(eachInvestorGets, new ArithmeticOperation(
								Variable.AMOUNT,
								AOP.DIV,
								new Size(thisInvestors)
								))),

						new For(investor, thisInvestors, new Receive(investor, eachInvestorGets))),

						new Push(thisInvestors, Class.CONTRACT, Variable.CALLER)
		));
	}

	@Test
    @DisplayName("Checks correctness of the side-effects of Ponzi2")
	public void sideEffectsOfPoinzi1AreCorrect() {
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(Stream.of(ponzi2));
		sideEffectsAnalysis.forEach((method, types) -> System.out.println(method + ": " + types.collect(Collectors.toList())));
	}
}
