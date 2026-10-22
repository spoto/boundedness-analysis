package it.univr.boundedness.analyzer.syntax;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalyzer;
import it.univr.smartcontracts.syntax.AOP;
import it.univr.smartcontracts.syntax.COP;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;
import it.univr.smartcontracts.syntax.classes.Field;
import it.univr.smartcontracts.syntax.classes.Int;
import it.univr.smartcontracts.syntax.classes.List;
import it.univr.smartcontracts.syntax.classes.Method;
import it.univr.smartcontracts.syntax.commands.Assignment;
import it.univr.smartcontracts.syntax.commands.For;
import it.univr.smartcontracts.syntax.commands.Push;
import it.univr.smartcontracts.syntax.commands.Receive;
import it.univr.smartcontracts.syntax.commands.Require;
import it.univr.smartcontracts.syntax.commands.Return;
import it.univr.smartcontracts.syntax.commands.Sequence;
import it.univr.smartcontracts.syntax.expressions.ArithmeticOperation;
import it.univr.smartcontracts.syntax.expressions.Constant;
import it.univr.smartcontracts.syntax.expressions.ConstructorCall;
import it.univr.smartcontracts.syntax.expressions.FieldAccess;
import it.univr.smartcontracts.syntax.expressions.Size;
import it.univr.smartcontracts.syntax.expressions.Variable;

public class Ponzi1Tests {

	private static Class ponzi1;
	private static Method ponzi1Constructor;
	private static Method ponzi1Invest;

	@BeforeAll
	public static void mkProgram() {
		ponzi1 = new Class("Ponzi1", Class.CONTRACT);
		var listOfContract = new List(Class.CONTRACT);
		var investors = new Field(ponzi1, "investors", listOfContract, true);
		var THIS = new Variable("this", ponzi1);

		ponzi1Constructor = new Method(ponzi1, ponzi1.name(),
				new Sequence(new Sequence(
						new Assignment(new FieldAccess(THIS, investors), new ConstructorCall(Class.LIST)),
						new Push(new FieldAccess(THIS, investors), Class.CONTRACT, Variable.CALLER)),
						new Return()
				));

		var eachInvestorGets = new Variable("eachInvestorGets", Int.INSTANCE);
		var investor = new Variable("investor", Class.CONTRACT);
		var thisInvestors = new FieldAccess(THIS, investors);

		ponzi1Invest = new Method(ponzi1, "invest",
				new Sequence(new Sequence(new Sequence(new Sequence(
						new Require(Variable.AMOUNT, COP.GE, new Constant(1000)),
						new Assignment(eachInvestorGets, new ArithmeticOperation(
								Variable.AMOUNT,
								AOP.DIV,
								new Size(thisInvestors)))),
						new For(investor, thisInvestors, new Receive(investor, eachInvestorGets))),
						new Push(thisInvestors, Class.CONTRACT, Variable.CALLER)),
						new Return()
		));
	}

	@Test
    @DisplayName("Checks correctness of the side-effects of Ponzi1")
	public void sideEffectsOfPoinzi1AreCorrect() {
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(Stream.of(ponzi1));
		var onlyContract = new HashSet<Type>();
		onlyContract.add(Class.CONTRACT);
		assertTrue(sideEffectsAnalysis.of(ponzi1Constructor).collect(Collectors.toSet()).equals(onlyContract));
		assertTrue(sideEffectsAnalysis.of(ponzi1Invest).collect(Collectors.toSet()).equals(onlyContract));
		//sideEffectsAnalysis.forEach((method, types) -> System.out.println(method + ": " + types.collect(Collectors.toList())));
	}
}