package it.univr.smartcontracts;

public abstract class AbstractTest {
	
	protected boolean isPrintRequested() {
		return "true".equals(System.getProperty("print"));
	}
}