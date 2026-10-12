package it.univr.boundedness.analyzer.syntax;

public enum COP {
	LT, LE, GT, GE, EQ, NE;

	public void toCode(StringBuilder sb) {
		switch (this) {
		case LT: sb.append('<'); break;
		case LE: sb.append("<="); break;
		case GT: sb.append('>'); break;
		case GE: sb.append(">="); break;
		case EQ: sb.append("=="); break;
		case NE: sb.append("!="); break;
		default:
			throw new IllegalStateException();
		}
	}
}