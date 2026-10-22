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
import it.univr.smartcontracts.syntax.classes.Long;
import it.univr.smartcontracts.syntax.classes.Method;
import it.univr.smartcontracts.syntax.commands.Assignment;
import it.univr.smartcontracts.syntax.commands.For;
import it.univr.smartcontracts.syntax.commands.If;
import it.univr.smartcontracts.syntax.commands.Nop;
import it.univr.smartcontracts.syntax.commands.Push;
import it.univr.smartcontracts.syntax.commands.Receive;
import it.univr.smartcontracts.syntax.commands.Require;
import it.univr.smartcontracts.syntax.commands.Return;
import it.univr.smartcontracts.syntax.commands.Sequence;
import it.univr.smartcontracts.syntax.expressions.ArithmeticOperation;
import it.univr.smartcontracts.syntax.expressions.Constant;
import it.univr.smartcontracts.syntax.expressions.ConstructorCall;
import it.univr.smartcontracts.syntax.expressions.FieldAccess;
import it.univr.smartcontracts.syntax.expressions.Last;
import it.univr.smartcontracts.syntax.expressions.ListAccess;
import it.univr.smartcontracts.syntax.expressions.Null;
import it.univr.smartcontracts.syntax.expressions.Random;
import it.univr.smartcontracts.syntax.expressions.Size;
import it.univr.smartcontracts.syntax.expressions.Variable;

public class Lottery2Tests {
	private static Class ticketBatchSale;
	private static Class round;
	private static Class lottery2;
	private static Method ticketBatchSaleConstructor;
	private static Method roundConstructor;
	private static Method lottery2Constructor;
	private static Method lottery2Buy;
	private static Method lottery2DrawWinner;

	@BeforeAll
	public static void mkProgram() {
		ticketBatchSale = new Class("TicketBatchSale", Class.OBJECT);
		var buyer = new Field(ticketBatchSale, "buyer", Class.CONTRACT, true);
		var tickets = new Field(ticketBatchSale, "tickets", Int.INSTANCE, true);

		ticketBatchSaleConstructor = new Method(ticketBatchSale, ticketBatchSale.name(), new Sequence(new Sequence(
			new Assignment(new FieldAccess(Variable.THIS, buyer), new Variable("buyer")),
			new Assignment(new FieldAccess(Variable.THIS, tickets), new Variable("tickets"))),
			new Return()
		));

		round = new Class("Round", Class.OBJECT);
		var sales = new Field(round, "sales", new List(ticketBatchSale), true);
		var endTime = new Field(round, "endTime", Long.INSTANCE, true);
		var totalTickets = new Field(round, "totalTickets", Int.INSTANCE, false);
		var winner = new Field(round, "winner", Class.CONTRACT, false);

		roundConstructor = new Method(round, round.name(), new Sequence(new Sequence(
			new Assignment(new FieldAccess(Variable.THIS, endTime), new Variable("endTime")),
			new Assignment(new FieldAccess(Variable.THIS, sales), new ConstructorCall(Class.LIST))),
			new Return()
		));

		lottery2 = new Class("Lottery2", Class.CONTRACT);
		var rounds = new Field(lottery2, "rounds", new List(round), true);
		var duration = new Field(lottery2, "duration", Long.INSTANCE, true);

		lottery2Constructor = new Method(lottery2, lottery2.name(), new Sequence(new Sequence(new Sequence(
			new Assignment(new FieldAccess(Variable.THIS, duration), new Variable("duration")),
			new Assignment(new FieldAccess(Variable.THIS, rounds), new ConstructorCall(Class.LIST))),
			new Push(new FieldAccess(Variable.THIS, rounds), round,
				new ConstructorCall(round, new ArithmeticOperation(Variable.NOW, AOP.ADD, new Variable("duration"))))),
			new Return()
		));

		var last = new Variable("last");
		var quantity = new Variable("quantity");

		lottery2Buy = new Method(lottery2, "buy", new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(
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
			new If(new Size(new FieldAccess(last, sales)), COP.LT, new Constant(5000),
				new Sequence(new Sequence(
					new Assignment(quantity, new ArithmeticOperation(Variable.AMOUNT, AOP.DIV, new Constant(1000))),
					new Push(new FieldAccess(last, sales), ticketBatchSale, new ConstructorCall(ticketBatchSale, Variable.CALLER, quantity))),
					new Assignment(new FieldAccess(last, totalTickets), new ArithmeticOperation(new FieldAccess(last, totalTickets), AOP.ADD, quantity))),
				new Nop())),
			new Return()
		));

		var round = new Variable("round");
		var sale = new Variable("sale");
		var roundNumber = new Variable("roundNumber");
		var numWinningTicket = new Variable("numWinningTicket");

		lottery2DrawWinner = new Method(lottery2, "drawWinner", new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(
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
						new Return()
					),
					new Assignment(numWinningTicket, new ArithmeticOperation(numWinningTicket, AOP.SUB, new FieldAccess(sale, tickets)))
				)
			)),
			new Return())
		);
	}

	@Test
    @DisplayName("Checks correctness of the side-effects of Lottery2")
	public void sideEffectsOfPoinzi1AreCorrect() {
		System.out.println(lottery2.toCode());
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(Stream.of(ticketBatchSale, round, lottery2));
		assertTrue(sideEffectsAnalysis.of(ticketBatchSaleConstructor).count() == 0L);
		assertTrue(sideEffectsAnalysis.of(roundConstructor).count() == 0L);
		var onlyRound = new HashSet<Type>();
		onlyRound.add(round);
		assertTrue(sideEffectsAnalysis.of(lottery2Constructor).collect(Collectors.toSet()).equals(onlyRound));
		assertTrue(sideEffectsAnalysis.of(lottery2DrawWinner).count() == 0L);
		var roundAndTicketBatchSale = new HashSet<Type>();
		roundAndTicketBatchSale.add(round);
		roundAndTicketBatchSale.add(ticketBatchSale);
		assertTrue(sideEffectsAnalysis.of(lottery2Buy).collect(Collectors.toSet()).equals(roundAndTicketBatchSale));
		sideEffectsAnalysis.forEach((method, types) -> System.out.println(method + ": " + types.collect(Collectors.toList())));
	}
}
