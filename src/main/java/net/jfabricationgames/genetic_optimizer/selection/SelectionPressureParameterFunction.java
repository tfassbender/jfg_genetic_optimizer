package net.jfabricationgames.genetic_optimizer.selection;

/**
 * A wrapper for a function that is used for a parameter in the SelectionPressure implementations.
 */
@FunctionalInterface
public interface SelectionPressureParameterFunction {
	
	public double getParameterValue(int generation, long timeUsed);
}