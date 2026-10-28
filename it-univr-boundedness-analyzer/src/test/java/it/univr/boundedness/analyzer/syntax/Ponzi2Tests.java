package it.univr.boundedness.analyzer.syntax;

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

public class Ponzi2Tests {
	private static Program program;
	private static Class ponzi2;
	private static Method ponzi2Constructor;
	private static Method ponzi2Invest;

	@BeforeAll
	public static void mkProgram() {
		ponzi2 = new Class("Ponzi2", Class.CONTRACT);
		var listOfContract = new List(Class.CONTRACT);
		var investors = new Field(ponzi2, "investors", listOfContract, true);
		var THIS = new Variable("this", ponzi2);

		ponzi2Constructor = new Method(ponzi2, ponzi2.name(),
				new Sequence(new Sequence(
						new Assignment(new FieldAccess(THIS, investors), new ConstructorCall(Class.LIST)),
						new Push(new FieldAccess(THIS, investors), Class.CONTRACT, Variable.CALLER)),
						new Return()
				));

		var eachInvestorGets = new Variable("eachInvestorGets", Int.INSTANCE);
		var investor = new Variable("investor", Class.CONTRACT);
		var thisInvestors = new FieldAccess(THIS, investors);

		ponzi2Invest = new Method(ponzi2, "invest",
				new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(
						new Require(Variable.AMOUNT, COP.GE, new Constant(1000)),
						new Require(new Size(thisInvestors), COP.LT, new Constant(100))),
						new Assignment(eachInvestorGets, new ArithmeticOperation(
								Variable.AMOUNT,
								AOP.DIV,
								new Size(thisInvestors)
								))),
						new For(investor, thisInvestors, new Receive(investor, eachInvestorGets))),
						new Push(thisInvestors, Class.CONTRACT, Variable.CALLER)),
						new Return()
		));

		program = new Program(ponzi2);
	}

	@Test
    @DisplayName("Checks correctness of the side-effects of Ponzi2")
	public void sideEffectsOfPoinzi1AreCorrect() {
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(program);
		var onlyContract = new HashSet<Type>();
		onlyContract.add(Class.CONTRACT);
		assertTrue(sideEffectsAnalysis.of(ponzi2Constructor).collect(Collectors.toSet()).equals(onlyContract));
		assertTrue(sideEffectsAnalysis.of(ponzi2Invest).collect(Collectors.toSet()).equals(onlyContract));
		//sideEffectsAnalysis.forEach((method, types) -> System.out.println(method + ": " + types.collect(Collectors.toList())));
	}

	@Test
    @DisplayName("Checks correctness of the boundedness analysis of Ponzi2")
	public void boundednessAnalysisOfPonzi2IsCorrect() {
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(program);
		var boundednessAnalysis = new BoundednessAnalyzer().analyze(program, sideEffectsAnalysis, 4);
		//boundednessAnalysis.forEach((command, state) -> System.out.println(command + ": " + state));
		System.out.println(program.toCode(boundednessAnalysis));
	}
}
