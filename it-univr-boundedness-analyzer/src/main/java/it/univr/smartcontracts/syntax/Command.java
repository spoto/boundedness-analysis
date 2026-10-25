package it.univr.smartcontracts.syntax;

import java.util.Optional;

import it.univr.smartcontracts.boundedness.BoundednessAnalysis;
import it.univr.smartcontracts.sideeffects.SideEffectsAnalyzable;

public interface Command extends SideEffectsAnalyzable {
	String toCode();
	String toCode(BoundednessAnalysis boundedness);
	void toCode(StringBuilder sb, String nesting, Optional<BoundednessAnalysis> boundedness);
}