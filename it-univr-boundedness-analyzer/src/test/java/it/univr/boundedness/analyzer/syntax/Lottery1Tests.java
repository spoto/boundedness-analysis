package it.univr.boundedness.analyzer.syntax;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.univr.boundedness.analyzer.sideeffects.SideEffectsAnalyzer;
import it.univr.boundedness.analyzer.syntax.classes.Class;
import it.univr.boundedness.analyzer.syntax.classes.Field;
import it.univr.boundedness.analyzer.syntax.classes.Int;
import it.univr.boundedness.analyzer.syntax.classes.List;
import it.univr.boundedness.analyzer.syntax.classes.Long;
import it.univr.boundedness.analyzer.syntax.classes.Method;
import it.univr.boundedness.analyzer.syntax.commands.Assignment;
import it.univr.boundedness.analyzer.syntax.commands.For;
import it.univr.boundedness.analyzer.syntax.commands.If;
import it.univr.boundedness.analyzer.syntax.commands.Nop;
import it.univr.boundedness.analyzer.syntax.commands.Push;
import it.univr.boundedness.analyzer.syntax.commands.Receive;
import it.univr.boundedness.analyzer.syntax.commands.Require;
import it.univr.boundedness.analyzer.syntax.commands.Sequence;
import it.univr.boundedness.analyzer.syntax.commands.Stop;
import it.univr.boundedness.analyzer.syntax.expressions.ArithmeticOperation;
import it.univr.boundedness.analyzer.syntax.expressions.Constant;
import it.univr.boundedness.analyzer.syntax.expressions.ConstructorCall;
import it.univr.boundedness.analyzer.syntax.expressions.FieldAccess;
import it.univr.boundedness.analyzer.syntax.expressions.Last;
import it.univr.boundedness.analyzer.syntax.expressions.ListAccess;
import it.univr.boundedness.analyzer.syntax.expressions.Null;
import it.univr.boundedness.analyzer.syntax.expressions.Random;
import it.univr.boundedness.analyzer.syntax.expressions.Variable;

public class Lottery1Tests {
	private static Class ticketBatchSale;
	private static Class round;
	private static Class lottery1;
	private static Method ticketBatchSaleConstructor;
	private static Method roundConstructor;
	private static Method lottery1Constructor;
	private static Method lottery1Buy;
	private static Method lottery1DrawWinner;

	@BeforeAll
	public static void mkProgram() {
		ticketBatchSale = new Class("TicketBatchSale", Class.OBJECT);
		var buyer = new Field(ticketBatchSale, "buyer", Class.CONTRACT, true);
		var tickets = new Field(ticketBatchSale, "tickets", Int.INSTANCE, true);

		ticketBatchSaleConstructor = new Method(ticketBatchSale, ticketBatchSale.name(), new Sequence(new Sequence(
			new Assignment(new FieldAccess(Variable.THIS, buyer), new Variable("buyer")),
			new Assignment(new FieldAccess(Variable.THIS, tickets), new Variable("tickets"))),
			new Stop()
		));

		round = new Class("Round", Class.OBJECT);
		var sales = new Field(round, "sales", new List(ticketBatchSale), true);
		var endTime = new Field(round, "endTime", Long.INSTANCE, true);
		var totalTickets = new Field(round, "totalTickets", Int.INSTANCE, false);
		var winner = new Field(round, "winner", Class.CONTRACT, false);

		roundConstructor = new Method(round, round.name(), new Sequence(new Sequence(
			new Assignment(new FieldAccess(Variable.THIS, endTime), new Variable("endTime")),
			new Assignment(new FieldAccess(Variable.THIS, sales), new ConstructorCall(Class.LIST))),
			new Stop()
		));

		lottery1 = new Class("Lottery1", Class.CONTRACT);
		var rounds = new Field(lottery1, "rounds", new List(round), true);
		var duration = new Field(lottery1, "duration", Long.INSTANCE, true);

		lottery1Constructor = new Method(lottery1, lottery1.name(), new Sequence(new Sequence(new Sequence(
			new Assignment(new FieldAccess(Variable.THIS, duration), new Variable("duration")),
			new Assignment(new FieldAccess(Variable.THIS, rounds), new ConstructorCall(Class.LIST))),
			new Push(new FieldAccess(Variable.THIS, rounds), round,
				new ConstructorCall(round, new ArithmeticOperation(Variable.NOW, AOP.ADD, new Variable("duration"))))),
			new Stop()
		));

		var last = new Variable("last");
		var quantity = new Variable("quantity");

		lottery1Buy = new Method(lottery1, "buy", new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(
			new Require(Variable.AMOUNT, COP.GT, new Constant(0)),
			new Require(new ArithmeticOperation(Variable.AMOUNT, AOP.REM, new Constant(1000)), COP.EQ, new Constant(0))),
			new Assignment(last, new Last(new FieldAccess(Variable.THIS, rounds)))),
			new If(Variable.NOW, COP.GT, new FieldAccess(last, endTime),
				new Sequence(
					new Assignment(last, new ConstructorCall(round, new ArithmeticOperation(Variable.NOW, AOP.ADD, new Variable("duration")))),
					new Push(new FieldAccess(Variable.THIS, rounds), round, last)
				),
				new Nop()
			)),
			new Assignment(quantity, new ArithmeticOperation(Variable.AMOUNT, AOP.DIV, new Constant(1000)))),
			new Push(new FieldAccess(last, sales), ticketBatchSale, new ConstructorCall(ticketBatchSale, Variable.CALLER, quantity))),
			new Assignment(new FieldAccess(last, totalTickets), new ArithmeticOperation(new FieldAccess(last, totalTickets), AOP.ADD, quantity))),
			new Stop()
		));

		var round = new Variable("round");
		var sale = new Variable("sale");
		var roundNumber = new Variable("roundNumber");
		var numWinningTicket = new Variable("numWinningTicket");

		lottery1DrawWinner = new Method(lottery1, "drawWinner", new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(
			new Assignment(round, new ListAccess(new FieldAccess(Variable.THIS, rounds), roundNumber)),
			new Require(new FieldAccess(round, winner), COP.EQ, Null.INSTANCE)),
			new Require(Variable.NOW, COP.GT, new FieldAccess(round, endTime))),
			new Require(new FieldAccess(round, totalTickets), COP.GT, new Constant(0))),
			new Assignment(numWinningTicket, new Random(new FieldAccess(round, totalTickets)))),
			new For(sale, new FieldAccess(round, sales),
				new If(new FieldAccess(sale, tickets), COP.GT, numWinningTicket,
					new Sequence(new Sequence(
						new Assignment(new FieldAccess(round, winner), new FieldAccess(sale, buyer)),
						new Receive(new FieldAccess(round, winner), new ArithmeticOperation(new Constant(100), AOP.MUL, new FieldAccess(round, totalTickets)))),
						new Stop()
					),
					new Assignment(numWinningTicket, new ArithmeticOperation(numWinningTicket, AOP.SUB, new FieldAccess(sale, tickets)))
				)
			))
		);
	}

	@Test
    @DisplayName("Checks correctness of the side-effects of Lottery1")
	public void sideEffectsOfPoinzi1AreCorrect() {
		System.out.println(lottery1.toCode());
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(Stream.of(ticketBatchSale, round, lottery1));
		assertTrue(sideEffectsAnalysis.of(ticketBatchSaleConstructor).count() == 0L);
		assertTrue(sideEffectsAnalysis.of(roundConstructor).count() == 0L);
		var onlyRound = new HashSet<Type>();
		onlyRound.add(round);
		assertTrue(sideEffectsAnalysis.of(lottery1Constructor).collect(Collectors.toSet()).equals(onlyRound));
		assertTrue(sideEffectsAnalysis.of(lottery1DrawWinner).count() == 0L);
		var roundAndTicketBatchSale = new HashSet<Type>();
		roundAndTicketBatchSale.add(round);
		roundAndTicketBatchSale.add(ticketBatchSale);
		assertTrue(sideEffectsAnalysis.of(lottery1Buy).collect(Collectors.toSet()).equals(roundAndTicketBatchSale));
		sideEffectsAnalysis.forEach((method, types) -> System.out.println(method + ": " + types.collect(Collectors.toList())));
	}
}
