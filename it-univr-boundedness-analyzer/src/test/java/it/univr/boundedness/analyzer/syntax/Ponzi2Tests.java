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

public class Ponzi2Tests {

	public Class mkPonzi2() {
		// we start with the representation of the library classes
		var object = new Class("Object");
		var contract = new Class("Contract", object);
		var list = new Class("List", object);

		// then we represent class Ponzi2
		var ponzi2 = new Class("Ponzi2", contract);
		var listOfContract = new List(contract);
		var investors = new Field(ponzi2, "investors", listOfContract, true);
		new Method(ponzi2, ponzi2.name(),
				new Sequence(
						new FieldWrite(Variable.THIS, investors, new ConstructorCall(list)),
						new Push(new FieldRead(Variable.THIS, investors), contract, Variable.CALLER)
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

						new Push(thisInvestors, contract, Variable.CALLER)
						)
				);

		return ponzi2;
	}

	@Test
    @DisplayName("Checks correctness of the side-effects of Ponzi2")
	public void sideEffectsOfPoinzi2AreCorrect() {
		System.out.println(mkPonzi2().toCode());
	}
}
