package it.univr.boundedness.analyzer.syntax;

public abstract class AbstractTest {
	
	protected boolean isPrintRequested() {
		return "true".equals(System.getProperty("print"));
	}
}