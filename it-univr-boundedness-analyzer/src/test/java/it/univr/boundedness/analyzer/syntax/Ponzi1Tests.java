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

public class Ponzi1Tests {

	private static Class ponzi1;

	@BeforeAll
	public static void mkProgram() {
		ponzi1 = new Class("Ponzi1", Class.CONTRACT);
		var listOfContract = new List(Class.CONTRACT);
		var investors = new Field(ponzi1, "investors", listOfContract, true);
		new Method(ponzi1, ponzi1.name(),
				new Sequence(
						new FieldWrite(Variable.THIS, investors, new ConstructorCall(Class.LIST)),
						new Push(new FieldRead(Variable.THIS, investors), Class.CONTRACT, Variable.CALLER)
						)
				);

		var eachInvestorGets = new Variable("eachInvestorGets");
		var investor = new Variable("investor");
		var thisInvestors = new FieldRead(Variable.THIS, investors);

		new Method(ponzi1, "invest",
				new Sequence(new Sequence(new Sequence(
						new Require(Variable.AMOUNT, COP.GE, new Constant(1000)),

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
    @DisplayName("Checks correctness of the side-effects of Ponzi1")
	public void sideEffectsOfPoinzi1AreCorrect() {
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(Stream.of(ponzi1));
		sideEffectsAnalysis.forEach((method, types) -> System.out.println(method + ": " + types.collect(Collectors.toList())));
	}
}
