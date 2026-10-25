package it.univr.smartcontracts.boundedness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;
import it.univr.smartcontracts.syntax.Command;
import it.univr.smartcontracts.syntax.classes.Class;
import it.univr.smartcontracts.syntax.classes.Method;
import it.univr.smartcontracts.syntax.commands.Assignment;
import it.univr.smartcontracts.syntax.commands.For;
import it.univr.smartcontracts.syntax.commands.If;
import it.univr.smartcontracts.syntax.commands.MethodCall;
import it.univr.smartcontracts.syntax.commands.Nop;
import it.univr.smartcontracts.syntax.commands.Push;
import it.univr.smartcontracts.syntax.commands.Receive;
import it.univr.smartcontracts.syntax.commands.Require;
import it.univr.smartcontracts.syntax.commands.Return;
import it.univr.smartcontracts.syntax.commands.Sequence;

public class BoundednessAnalyzer {

	public BoundednessAnalysis analyze(Stream<Class> program, SideEffectsAnalysis sideEffectsAnalysis, int thresholdForWidening) {
		Set<Class> classes = program.collect(Collectors.toSet());
		BoundednessApploximation approximation = mkInitialApproximation(classes);

		// we initialize the working set of commands with the bodies of the methods of the classes of the program
		var ws = classes.stream().flatMap(Class::methods).map(Method::body).collect(Collectors.toCollection(ArrayList::new));

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
				approximation.propagate(assignment.next(), assignment.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
				break;
			case Push push:
				approximation.propagate(push.next(), push.boundedness(approx, sideEffectsAnalysis), ws, wsApprox, counters, thresholdForWidening);
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

	private BoundednessApploximation mkInitialApproximation(Set<Class> classes) {
		return new BoundednessApploximation(new HashMap<Command, State>());
	}

	private static class BoundednessApploximation implements BoundednessAnalysis {
		private final Map<Command, State> approximations;

		private BoundednessApploximation(Map<Command, State> approximation) {
			this.approximations = approximation;
		}

		@Override
		public State of(Command command) {
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

		public State merge(Command command, State approx, int howManyTimesAlreadyAnalyzed, int thresholdForWidening) {
			var oldApprox = of(command);

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

			if (!approx.equals(of(where))) {
				approximations.put(where, approx);
				ws.addLast(where);
				wsApprox.addLast(approx);
			}
		}
	}
}