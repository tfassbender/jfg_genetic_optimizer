package net.jfabricationgames.genetic_optimizer.selection;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

/**
 * A selector that chooses the pairs to be reproduced by a stochastically distributed selection method.
 * 
 * The selection probability is proportional to the given probability, but it's ensured, that individuals with a probability above average are chosen
 * at least once.
 */
public class StochasticallyDistributedSelector implements Selector {
	
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
		double[] summedProbability = Selector.toSummedProbabilities(selectionProbability);
		int[] selectedPairs = new int[2 * numPairs];
		double startPoint = getRandomNumber();
		double addedAverage = 1d / (2d * numPairs);
		double stochasticallySelectedProbability;
		
		for (int i = 0; i < numPairs * 2; i++) {
			//select the pairs stochastically
			stochasticallySelectedProbability = startPoint + i * addedAverage;
			stochasticallySelectedProbability %= 1;
			selectedPairs[i] = Selector.getSelectedIndexByBisectionSearch(summedProbability, stochasticallySelectedProbability);
		}
		
		//shuffle the pairs to distribute them stochastically
		shuffle(selectedPairs);
		
		return selectedPairs;
	}
	
	@VisibleForTesting
	/*private*/ void shuffle(int[] selectedPairs) {
		//shuffle the selected pairs in place
		int swapIndex;
		int tmp;
		for (int i = selectedPairs.length - 1; i > 0; i--) {
			swapIndex = (int) (getRandomNumber() * (i + 1));
			
			tmp = selectedPairs[i];
			selectedPairs[i] = selectedPairs[swapIndex];
			selectedPairs[swapIndex] = tmp;
		}
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
}