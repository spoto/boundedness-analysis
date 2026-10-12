package it.univr.boundedness.analyzer.syntax;

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
import it.univr.boundedness.analyzer.syntax.commands.FieldWrite;
import it.univr.boundedness.analyzer.syntax.commands.If;
import it.univr.boundedness.analyzer.syntax.commands.Nop;
import it.univr.boundedness.analyzer.syntax.commands.Push;
import it.univr.boundedness.analyzer.syntax.commands.Require;
import it.univr.boundedness.analyzer.syntax.commands.Sequence;
import it.univr.boundedness.analyzer.syntax.expressions.ArithmeticOperation;
import it.univr.boundedness.analyzer.syntax.expressions.Constant;
import it.univr.boundedness.analyzer.syntax.expressions.ConstructorCall;
import it.univr.boundedness.analyzer.syntax.expressions.FieldRead;
import it.univr.boundedness.analyzer.syntax.expressions.Last;
import it.univr.boundedness.analyzer.syntax.expressions.Variable;

public class Lottery1Tests {

	private static Class ticketBatchSale;
	private static Field buyer;
	private static Field tickets;
	private static Class round;
	private static Class lottery1;
	private static Field endTime;
	private static Field sales;
	private static Field totalTickets;
	private static Field winner;

	@BeforeAll
	public static void mkProgram() {
		ticketBatchSale = new Class("TicketBatchSale", Class.OBJECT);
		buyer = new Field(ticketBatchSale, "buyer", Class.CONTRACT, true);
		tickets = new Field(ticketBatchSale, "tickets", Int.INSTANCE, true);

		new Method(ticketBatchSale, ticketBatchSale.name(), new Sequence(
			new FieldWrite(Variable.THIS, buyer, new Variable("buyer")),
			new FieldWrite(Variable.THIS, tickets, new Variable("tickets"))
		));

		round = new Class("Round", Class.OBJECT);
		sales = new Field(round, "sales", new List(ticketBatchSale), true);
		endTime = new Field(round, "endTime", Long.INSTANCE, true);
		totalTickets = new Field(round, "totalTickets", Int.INSTANCE, false);
		winner = new Field(round, "winner", Class.CONTRACT, false);

		new Method(round, round.name(), new Sequence(
			new FieldWrite(Variable.THIS, endTime, new Variable("endTime")),
			new FieldWrite(Variable.THIS, sales, new ConstructorCall(Class.LIST))
		));

		lottery1 = new Class("Lottery1", Class.CONTRACT);
		var rounds = new Field(lottery1, "rounds", new List(round), true);
		var duration = new Field(lottery1, "duration", Long.INSTANCE, true);

		new Method(lottery1, lottery1.name(), new Sequence(new Sequence(
			new FieldWrite(Variable.THIS, duration, new Variable("duration")),
			new FieldWrite(Variable.THIS, rounds, new ConstructorCall(Class.LIST))),
			new Push(new FieldRead(Variable.THIS, rounds), round,
				new ConstructorCall(round, new ArithmeticOperation(Variable.NOW, AOP.ADD, new Variable("duration"))))
		));

		var last = new Variable("last");
		var quantity = new Variable("quantity");

		new Method(lottery1, "buy", new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(new Sequence(
			new Require(Variable.AMOUNT, COP.GT, new Constant(0)),
			new Require(new ArithmeticOperation(Variable.AMOUNT, AOP.REM, new Constant(1000)), COP.EQ, new Constant(0))),
			new Assignment(last, new Last(new FieldRead(Variable.THIS, rounds)))),
			new If(Variable.NOW, COP.GT, new FieldRead(last, endTime),
				new Sequence(
					new Assignment(last, new ConstructorCall(round, new ArithmeticOperation(Variable.NOW, AOP.ADD, new Variable("duration")))),
					new Push(new FieldRead(Variable.THIS, rounds), round, last)
				),
				new Nop()
			)),
			new Assignment(quantity, new ArithmeticOperation(Variable.AMOUNT, AOP.DIV, new Constant(1000)))),
			new Push(new FieldRead(last, sales), ticketBatchSale, new ConstructorCall(ticketBatchSale, Variable.CALLER, quantity))),
			new FieldWrite(last, totalTickets, new ArithmeticOperation(new FieldRead(last, totalTickets), AOP.ADD, quantity))
		));
		
		new Method(lottery1, "drawWinner", new Nop());
	}

	@Test
    @DisplayName("Checks correctness of the side-effects of Lottery1")
	public void sideEffectsOfPoinzi1AreCorrect() {
		System.out.println(lottery1.toCode());
		var sideEffectsAnalysis = new SideEffectsAnalyzer().analyze(Stream.of(ticketBatchSale, round, lottery1));
		sideEffectsAnalysis.forEach((method, types) -> System.out.println(method + ": " + types.collect(Collectors.toList())));
	}
}
