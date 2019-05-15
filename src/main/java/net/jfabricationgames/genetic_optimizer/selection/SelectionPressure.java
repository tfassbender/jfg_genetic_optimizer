package net.jfabricationgames.genetic_optimizer.selection;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * The selection pressure determines the probability of every individual to be selected for reproduction.
 * 
 * In the beginning the selection pressure should be not very high (good probability for individuals with lower fitness to be selected) so the
 * search-area can be widely searched and the algorithm won't converge in local a minimum.<br>
 * After some generations (or some time) the pressure should be higher to make the algorithm converge.
 */
public interface SelectionPressure {
	
	/**
	 * Calculate the probability to be selected for every individual based on their fitness, the generation or the time.
	 * 
	 * @param population
	 *        The current population of the genetic optimizer.
	 * 
	 * @param generation
	 *        The current generation number.
	 * 
	 * @param minimize
	 *        Indicates whether the optimum that is searched is a minimum (true) or a maximum (false).
	 * 
	 * @param timeUsed
	 *        The time that was used since the optimization was started.
	 * 
	 * @return A double-array (of size [population.length]) including the probabilities of every individual to be selected (that sums up to 1).
	 */
	public double[] calculateSelectionProbability(DNA[] population, int generation, boolean minimize, long timeUsed);
}