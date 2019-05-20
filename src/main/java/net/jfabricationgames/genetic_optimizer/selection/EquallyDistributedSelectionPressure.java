package net.jfabricationgames.genetic_optimizer.selection;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * A selection pressure implementation that gives the same probability of reproduction to every fitness.
 */
public class EquallyDistributedSelectionPressure implements SelectionPressure {
	
	@Override
	public double[] calculateSelectionProbability(DNA[] population, int generation, boolean minimize, long timeUsed) {
		double[] probabilities = new double[population.length];
		double equalProbability = 1d / probabilities.length;
		for (int i = 0; i < population.length; i++) {
			probabilities[i] = equalProbability;
		}
		return probabilities;
	}
}