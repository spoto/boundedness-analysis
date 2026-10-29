package it.univr.smartcontracts.boundedness;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.Program;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;
import it.univr.smartcontracts.syntax.classes.Method;
import it.univr.smartcontracts.syntax.commands.Assignment;
import it.univr.smartcontracts.syntax.commands.For;
import it.univr.smartcontracts.syntax.commands.If;
import it.univr.smartcontracts.syntax.commands.MethodCall;
import it.univr.smartcontracts.syntax.commands.Nop;
import it.univr.smartcontracts.syntax.commands.Push;
import it.univr.smartcontracts.syntax.commands.Receive;
import it.univr.smartcontracts.syntax.commands.RemoveFirst;
import it.univr.smartcontracts.syntax.commands.Require;
import it.univr.smartcontracts.syntax.commands.Return;
import it.univr.smartcontracts.syntax.commands.Sequence;
import it.univr.smartcontracts.syntax.commands.While;

public class BoundednessAnalyzer {

	public BoundednessAnalysis analyze(Program program, SideEffectsAnalysis sideEffectsAnalysis, int thresholdForWidening) {
		BoundednessApploximation approximation = mkInitialApproximation(program);

		// we initialize the working set of commands with the bodies of the methods of the classes of the program
		// the simple constructors are put at the end, so that they are analyzed before anything else

		var comparator = new Comparator<Method>() {

			@Override
			public int compare(Method method1, Method method2) {
				boolean method1IsSimple = method1.isSimpleConstructor();
				boolean method2IsSimple = method2.isSimpleConstructor();
				if (method1IsSimple && !method2IsSimple)
					return 1;
				else if (!method1IsSimple && method2IsSimple)
					return -1;
				else
					return method1.toString().compareTo(method2.toString());
			}
		};

		var ws = program.classes()
				.flatMap(Class::methods)
				.sorted(comparator)
				.map(Method::body)
				.collect(Collectors.toCollection(ArrayList::new));

		// for each command, there is a corresponding approximation that needs to be propagated
		var wsApprox = ws.stream().map(_ -> State.TOP).collect(Collectors.toCollection(ArrayList::new));

		// a map stating how many times we analyzed each given command
		var counters = new HashMap<Command, Integer>();

		while (!ws.isEmpty()) {
			var command = ws.removeLast();
			var approx = wsApprox.removeLast();
			int howManyTimesAlreadyAnalyzed = Optional.ofNullable(counters.get(command)).orElse(0);
			approx = approximation.merge(command, approx, howManyTimesAlreadyAnalyzed, thresholdForWidening);
			counters.put(command, howManyTimesAlreadyAnalyzed + 1);

			switch (command) {
			case Nop nop:
				approximation.propagate(nop.next(), nop.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case Assignment assignment:
				approximation.propagate(assignment.next(), assignment.boundedness(approx, sideEffectsAnalysis, approximation), ws, wsApprox, counters, thresholdForWidening);
				break;
			case Push push:
				approximation.propagate(push.next(), push.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case RemoveFirst removeFirst:
				approximation.propagate(removeFirst.next(), removeFirst.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case Receive receive:
				approximation.propagate(receive.next(), receive.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case Require require:
				approximation.propagate(require.next(), require.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case MethodCall methodCall:
				approximation.propagate(methodCall.next(), methodCall.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case If _if:
				approximation.propagate(_if.then(), _if.boundednessThen(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				approximation.propagate(_if._else(), _if.boundednessElse(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case For _for:
				approximation.propagate(_for.inner(), _for.boundednessInner(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				approximation.propagate(_for.next(), _for.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case While _while:
				approximation.propagate(_while.inner(), _while.boundednessInner(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				approximation.propagate(_while.next(), _while.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case Sequence sequence:
				approximation.propagate(sequence.first(), sequence.boundednessFirst(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case Return _:
				break;
			default:
				throw new IllegalStateException("Unexpected command of class " + command.getClass().getName());
			}
		}

		return approximation;
	}

	private BoundednessApploximation mkInitialApproximation(Program program) {
		return new BoundednessApploximation(program, new HashMap<Command, State>());
	}

	private static class BoundednessApploximation implements BoundednessAnalysis {
		private final Program program;
		private final Map<Command, State> approximations;

		private BoundednessApploximation(Program program, Map<Command, State> approximation) {
			this.program = program;
			this.approximations = approximation;
		}

		@Override
		public Program program() {
			return program;
		}

		@Override
		public State at(Command command) {
			var result = approximations.get(command);
			if (result == null)
				// if there is no information, the command is deemed unreachable
				// and the best approximation is the most precise one
				return State.BOTTOM;
			else
				return result;
		}

		@Override
		public void forEach(BiConsumer<Command, State> action) {
			approximations.entrySet().forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
		}

		@Override
		public boolean allListsAreBounded(Type elementsType) {
			return program.classes()
					.flatMap(Class::methods)
					.map(Method::body)
					.flatMap(Command::subcommands)
					.filter(command -> command instanceof Push)
					.map(command -> (Push) command)
					.filter(push -> push.canAffect(elementsType))
					.allMatch(push -> push.isBounded(this));
		}

		public State merge(Command command, State approx, int howManyTimesAlreadyAnalyzed, int thresholdForWidening) {
			var oldApprox = at(command);

			if (howManyTimesAlreadyAnalyzed <= thresholdForWidening)
				return oldApprox.lub(approx);
			else
				return oldApprox.lub(approx).widen(oldApprox);
		}

		@Override
		public int hashCode() {
			return approximations.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof BoundednessApploximation se && approximations.equals(se.approximations);
		}

		private void propagate(Command where, State approx, List<Command> ws, List<State> wsApprox, Map<Command, Integer> counters, int thresholdForWidening) {
			int howManyTimesAlreadyAnalyzed = Optional.ofNullable(counters.get(where)).orElse(0);
			approx = merge(where, approx, howManyTimesAlreadyAnalyzed, thresholdForWidening);
			counters.put(where, howManyTimesAlreadyAnalyzed + 1);

			if (!approx.equals(at(where))) {
				approximations.put(where, approx);
				ws.addLast(where);
				wsApprox.addLast(approx);
			}
		}
	}
}