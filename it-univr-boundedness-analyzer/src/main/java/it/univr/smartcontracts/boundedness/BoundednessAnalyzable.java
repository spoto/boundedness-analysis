package it.univr.smartcontracts.boundedness;

import it.univr.smartcontracts.sideeffects.SideEffectsAnalysis;

public interface BoundednessAnalyzable {
	State boundedness(State before, SideEffectsAnalysis sideEffectsAnalysis);
}