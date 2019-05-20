package net.jfabricationgames.genetic_optimizer.selection;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

/**
 * A selector that randomly chooses pairs to be selected for reproduction based on their probability to be selected.
 */
public class FitnessProportionalSelector implements Selector {
	
	/**
	 * Select pairs of parents (by index) that are combined to build the next generation.
	 * 
	 * @param selectionProbability
	 *        The probability to be selected for every DNA in the current population (sums up to 1).
	 * 
	 * @param numPairs
	 *        The number of pairs needed (or the number of individuals needed in the next generation).
	 * 
	 * @return Returns an int-array of size [numPairs * 2] including the pairs that are to be combined to create the next population (a pair is on
	 *         position [i, i+1] for i % 2 = 0).
	 */
	@Override
	public int[] select(double[] selectionProbability, int numPairs) {
		double[] summedProbabilities = Selector.toSummedProbabilities(selectionProbability);
		int[] selectionPairs = new int[numPairs * 2];
		double chosenProbability;
		
		for (int i = 0; i < numPairs * 2; i++) {
			chosenProbability = getRandomNumber();
			selectionPairs[i] = Selector.getSelectedIndexByBisectionSearch(summedProbabilities, chosenProbability);
		}
		
		return selectionPairs;
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
}