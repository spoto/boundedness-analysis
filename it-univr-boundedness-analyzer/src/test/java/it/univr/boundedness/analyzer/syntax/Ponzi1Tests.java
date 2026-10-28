package it.univr.boundedness.analyzer.syntax;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.univr.smartcontracts.boundedness.BoundednessAnalyzer;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalyzer;
import it.univr.smartcontracts.syntax.AOP;
import it.univr.smartcontracts.syntax.COP;
import it.univr.smartcontracts.syntax.Program;
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

public class Ponzi1Tests extends AbstractTest {
	private static Program program;
	private static Class ponzi1;
	private static Method ponzi1Constructor;
	private static Method ponzi1Invest;
	private static For _for;

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
						_for = new For(investor, thisInvestors, Class.CONTRACT, new Receive(investor, eachInvestorGets))),
						new Push(thisInvestors, Class.CONTRACT, Variable.CALLER)),
						new Return()
		));

		program = new Program(ponzi1);
	}

	@Test
    @DisplayName("Checks correctness of the side-effects analysis of Ponzi1")
	public void sideEffectsAnalysisOfPoinzi1AreCorrect() {
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(program);
		var onlyContract = new HashSet<Type>();
		onlyContract.add(Class.CONTRACT);
		assertTrue(sideEffectsAnalysis.of(ponzi1Constructor).collect(Collectors.toSet()).equals(onlyContract));
		assertTrue(sideEffectsAnalysis.of(ponzi1Invest).collect(Collectors.toSet()).equals(onlyContract));
		//sideEffectsAnalysis.forEach((method, types) -> System.out.println(method + ": " + types.collect(Collectors.toList())));
	}

	@Test
    @DisplayName("Checks correctness of the boundedness analysis of Ponzi1")
	public void boundednessAnalysisOfPonzi1IsCorrect() {
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(program);
		var boundednessAnalysis = new BoundednessAnalyzer().analyze(program, sideEffectsAnalysis, 4);
		assertFalse(boundednessAnalysis.allListsAreBounded(Class.CONTRACT));
		assertTrue(_for.hasWarning(boundednessAnalysis));

		if (isPrintRequested())
			System.out.println(program.toCode(boundednessAnalysis));
	}
}