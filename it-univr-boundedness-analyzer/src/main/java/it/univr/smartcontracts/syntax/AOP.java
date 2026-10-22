package it.univr.smartcontracts.syntax;

public enum AOP {
	ADD, SUB, MUL, DIV, REM;

	public void toCode(StringBuilder sb) {
		switch (this) {
		case ADD: sb.append('+'); break;
		case SUB: sb.append('-'); break;
		case MUL: sb.append('*'); break;
		case DIV: sb.append('/'); break;
		case REM: sb.append('%'); break;
		default:
			throw new IllegalStateException();
		}
	}
}