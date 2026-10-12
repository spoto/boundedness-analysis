package it.univr.boundedness.analyzer.syntax;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

	public Class mkPonzi1() {
		// we start with the representation of the library classes
		var object = new Class("Object");
		var contract = new Class("Contract", object);
		var list = new Class("List", object);

		// then we represent class Ponzi1
		var ponzi1 = new Class("Ponzi1", contract);
		var listOfContract = new List(contract);
		var investors = new Field(ponzi1, "investors", listOfContract, true);
		new Method(ponzi1, ponzi1.name(),
				new Sequence(
						new FieldWrite(Variable.THIS, investors, new ConstructorCall(list)),
						new Push(new FieldRead(Variable.THIS, investors), contract, Variable.CALLER)
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

						new Push(thisInvestors, contract, Variable.CALLER)
						)
				);

		return ponzi1;
	}

	@Test
    @DisplayName("Checks correctness of the side-effects of Ponzi1")
	public void sideEffectsOfPoinzi1AreCorrect() {
		System.out.println(mkPonzi1().toCode());
	}
}
