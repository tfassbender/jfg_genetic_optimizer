package net.jfabricationgames.genetic_optimizer.selection;

/**
 * A selector has to select a given number of pairs to be combined for the next generation.
 */
public interface Selector {
	
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
	public int[] select(double[] selectionProbability, int numPairs);
	
	/**
	 * Build the summed up probabilities from a probability array:<br>
	 * E.g. an input of: [0.1, 0.2, 0.4, 0.3] will lead to an output of [0.1, 0.3, 0.7, 1]
	 * 
	 * @param selectionProbability
	 *        The selection probability of every index.
	 * 
	 * @return The summed probability array.
	 */
	public static double[] toSummedProbabilities(double[] selectionProbability) {
		double[] summedProbabilities = new double[selectionProbability.length];
		
		summedProbabilities[0] = selectionProbability[0];
		for (int i = 1; i < selectionProbability.length; i++) {
			summedProbabilities[i] = summedProbabilities[i - 1] + selectionProbability[i];
		}
		
		return summedProbabilities;
	}
	
	/**
	 * Find the index that was selected by the chosen probability.<br>
	 * The index is found using bisection search (in O(n) = ln(n)).
	 * 
	 * Bisection search should usually be faster (especially for big population sizes). <br>
	 * For very small population sizes (< 20) linear search can be faster.
	 * 
	 * @param summedProbabilities
	 *        The summed probabilities of the indices.
	 * 
	 * @param chosenProbability
	 *        The probability that was (randomly) chosen.
	 * 
	 * @return The index that was selected.
	 */
	public static int getSelectedIndexByBisectionSearch(double[] summedProbabilities, double chosenProbability) {
		int searchIndex = summedProbabilities.length / 2;
		int min = 0;
		int max = summedProbabilities.length - 1;
		
		if (chosenProbability < summedProbabilities[0]) {
			return 0;
		}
		
		while (max - min > 1) {
			if (summedProbabilities[searchIndex] >= chosenProbability) {
				max = searchIndex;
			}
			else {
				min = searchIndex;
			}
			searchIndex = (max - min) / 2 + min;
		}
		
		return max;
	}
	
	/**
	 * Find the index that was selected by the chosen probability.<br>
	 * The index is found using linear search (in O(n) = n).
	 * 
	 * Bisection search should usually be faster (especially for big population sizes). <br>
	 * For very small population sizes (< 20) linear search can be faster.
	 * 
	 * @param summedProbabilities
	 *        The summed probabilities of the indices.
	 * 
	 * @param chosenProbability
	 *        The probability that was (randomly) chosen.
	 * 
	 * @return The index that was selected.
	 */
	public static int getSelectedIndexByLinearSearch(double[] summedProbabilities, double chosenProbability) {
		int selectedIndex = 0;
		
		if (chosenProbability < summedProbabilities[0]) {
			return 0;
		}
		
		for (int i = 0; i < summedProbabilities.length - 1; i++) {
			if (chosenProbability > summedProbabilities[i]) {
				selectedIndex = i;
			}
		}
		
		return selectedIndex + 1;
	}
}