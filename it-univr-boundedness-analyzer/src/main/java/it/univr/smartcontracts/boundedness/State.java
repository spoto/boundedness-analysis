package it.univr.smartcontracts.boundedness;

import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.univr.smartcontracts.syntax.COP;
import it.univr.smartcontracts.syntax.Expression;
import it.univr.smartcontracts.syntax.LValue;
import it.univr.smartcontracts.syntax.Path;
import it.univr.smartcontracts.syntax.Type;
import it.univr.smartcontracts.syntax.classes.Class;
import it.univr.smartcontracts.syntax.commands.Assignment;
import it.univr.smartcontracts.syntax.expressions.Constant;
import it.univr.smartcontracts.syntax.expressions.ConstructorCall;
import it.univr.smartcontracts.syntax.expressions.Size;
import it.univr.smartcontracts.syntax.expressions.Variable;

/**
 * A boundedness state is a set of constraints path.size() <= n.
 * States are kept normalized: a state never contains two constraints
 * path.size() <= n and path.size() <= m with n != m.
 * 
 * Boundedness states are immutable.
 */
public class State {
	private final Set<Constraint> constraints;

	/**
	 * The constraints parameter is assumed to be normalized already.
	 */
	private State(Set<Constraint> constraints) {
		this.constraints = constraints;
	}

	/**
	 * Creates a clone of original, with the same constraints.
	 * The clone can later be modified independently from original.
	 */
	private State(State original) {
		this.constraints = new HashSet<>(original.constraints);
	}

	/**
	 * Yields the least upper bound of this and other.
	 */
	public State lub(State other) {
		var constraints = new HashSet<Constraint>();

		for (Constraint constraint1: this.constraints) {
			var path1 = constraint1.path();
			var n1 = constraint1.n();

			for (Constraint constraint2: other.constraints)
				if (path1.equals(constraint2.path()))
					constraints.add(new Constraint(path1, Math.max(n1, constraint2.n())));
		}

		// by construction, there is only one constraint in constraints for each given path,
		// hence constraints is normalized
		return new State(constraints);
	}

	/**
	 * Yields the widening of this and other.
	 */
	public State widen(State other) {
		var constraints = new HashSet<>(this.constraints);
		constraints.retainAll(other.constraints);
	
		return new State(constraints);
	}

	public State survive(Stream<Type> updated) {
		var constraints = new HashSet<Constraint>();
		var updatedAsSet = updated.collect(Collectors.toSet());

		for (var constraint: this.constraints)
			if (constraint.path().survives(updatedAsSet.stream()))
				constraints.add(constraint);

		// normalized since it can only be smaller than this
		return new State(constraints);
	}

	public State push(Type updated) {
		var constraints = new HashSet<Constraint>();

		for (var constraint: this.constraints)
			if (constraint.path().survives(Stream.of(updated)))
				constraints.add(constraint);
			else
				constraints.add(new Constraint(constraint.path(), constraint.n() + 1));

		// normalized since it has constraints for exactly the same paths as this
		return new State(constraints);
	}

	public State invalidate(Path path) {
		return invalidate(path, false);
	}

	public State improve(Expression exp1, COP cop, Expression exp2) {
		Optional<Path> pathUnderSize1 = pathUnderSize(exp1);
		Optional<Path> pathUnderSize2 = pathUnderSize(exp2);
		OptionalInt n1 = exp1 instanceof Constant con ? OptionalInt.of(con.c()) : OptionalInt.empty();
		OptionalInt n2 = exp2 instanceof Constant con ? OptionalInt.of(con.c()) : OptionalInt.empty();

		if (pathUnderSize1.isPresent() && n2.isPresent()) {
			switch (cop) {
			case LE, EQ:
				return new State(this).add(new Constraint(pathUnderSize1.get(), n2.getAsInt()));
			case LT:
				return new State(this).add(new Constraint(pathUnderSize1.get(), n2.getAsInt() - 1));
			default:
				return this;
			}
		}
		else if (n1.isPresent() && pathUnderSize2.isPresent()) {
			switch (cop) {
			case GE, EQ:
				return new State(this).add(new Constraint(pathUnderSize2.get(), n1.getAsInt()));
			case GT:
				return new State(this).add(new Constraint(pathUnderSize2.get(), n1.getAsInt() - 1));
			default:
				return this;
			}
		}
		else
			return this;
	}

	public State assign(Assignment assignment) {
		Optional<Path> lvalueAsPath = assignment.lvalue().asPath();
		if (lvalueAsPath.isPresent()) {
			var rvalue = assignment.exp();

			if (rvalue instanceof ConstructorCall cc && cc.clazz().equals(Class.LIST))
				// we need to clone before add
				return invalidate(lvalueAsPath.get(), true).add(new Constraint(lvalueAsPath.get(), 0));
			else {
				Optional<Path> rvalueAsPath = rvalue instanceof LValue lvalue ? lvalue.asPath() : Optional.empty();
				if (rvalueAsPath.isPresent()) {
					var path = rvalueAsPath.get();

					Constraint toAdd = null;
					for (Constraint constraint: constraints)
						if (path.equals(constraint.path())) {
							toAdd = new Constraint(lvalueAsPath.get(), constraint.n());
							break;
						}

					if (toAdd == null)
						return this;
					else
						// we need to clone before add
						return invalidate(lvalueAsPath.get(), true).add(toAdd);
				}
				else
					return invalidate(lvalueAsPath.get());
			}
		}
		else
			return this;
	}

	@Override
	public String toString() {
		return constraints.toString();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof State state && constraints.equals(state.constraints);
	}

	@Override
	public int hashCode() {
		return constraints.hashCode();
	}

	private State invalidate(Path path, boolean clone) {
		if (path instanceof Variable v) {
			var constraints = new HashSet<Constraint>();
	
			for (Constraint constraint: this.constraints)
				if (!constraint.path().root().equals(v))
					constraints.add(constraint);
	
			// constraints is a subset of this.constraints, hence it is normalized
			return new State(constraints);
		}
		else
			return clone ? new State(this) : this;
	}

	/**
	 * Yields the path if exp has the form path.size().
	 */
	private Optional<Path> pathUnderSize(Expression exp) {
		if (exp instanceof Size size && size.exp() instanceof LValue lv)
			return lv.asPath();
		else
			return Optional.empty();
	}

	/**
	 * Modifies this state by adding the given constraint, keeping normalization.
	 * 
	 * @param constraint the constraint to add
	 * @return this same state, after being modified
	 */
	private State add(Constraint constraint) {
		// we check if there is a constraint for the same path already and, in that case,
		// we keep to most precise of the two
		var path = constraint.path();

		Constraint toReplace = null;
		for (var oldConstraint: constraints)
			if (oldConstraint.path().equals(path)) {
				if (oldConstraint.n() > constraint.n()) // it was less precise: we replace it
					// we replace it outside the loop, so that we do not need
					// to clone the set of all the constraints
					toReplace = oldConstraint;

				// in any case, we stop since there is at most one constraint for path
				break;
			}

		if (toReplace != null) {
			constraints.remove(toReplace);
			constraints.add(constraint);
		}

		return this;
	}
}