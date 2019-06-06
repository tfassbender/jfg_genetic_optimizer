package net.jfabricationgames.genetic_optimizer.heredity;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Create a cross over heredity by shuffling the parent chromosomes, using a single cross heredity and un-shuffle the chromosomes.
 * 
 * The number of changed chromosomes is equally distributed.
 */
public class HeredityShuffleCross implements Heredity {
	
	@Override
	public DNA mixDNA(DNA father, DNA mother) {
		int n = father.getLength();
		
		double[] fatherChromosomes;
		double[] motherChromosomes;
		double[] childChromosomes = new double[n];
		
		//copy the arrays because it's faster than in place changes
		fatherChromosomes = new double[n];
		motherChromosomes = new double[n];
		System.arraycopy(father.getDNACode(), 0, fatherChromosomes, 0, n);
		System.arraycopy(mother.getDNACode(), 0, motherChromosomes, 0, n);
		
		//generate the indices to shuffle the parent arrays
		int[] shuffleIndices = generateShuffleIndices(n);
		
		shuffleArray(fatherChromosomes, shuffleIndices);
		shuffleArray(motherChromosomes, shuffleIndices);
		
		//mix the DNA codes
		int splitAt = (int) (getRandomNumber() * n);
		for (int i = 0; i < splitAt; i++) {
			childChromosomes[i] = fatherChromosomes[i];
		}
		for (int i = splitAt; i < n; i++) {
			childChromosomes[i] = motherChromosomes[i];
		}
		
		unshuffleArray(childChromosomes, shuffleIndices);
		//parent codes are copied and don't need to be changed back
		
		return new DNA(childChromosomes);
	}
	
	@Override
	public String toString() {
		return "HeredityShuffleCross []";
	}
	
	@VisibleForTesting
	/*private*/ int[] generateShuffleIndices(int arrayLength) {
		//shuffle algorithm by fisher-yates (creates only the indices but the algorithm is similar)
		//https://www.geeksforgeeks.org/shuffle-a-given-array-using-fisher-yates-shuffle-algorithm/
		
		int[] shuffleIndices = new int[arrayLength - 1];
		int j = 0;
		for (int i = arrayLength - 1; i > 0; i--, j++) {//i > 0 because the first element is not swapped (not needed)
			shuffleIndices[j] = (int) (getRandomNumber() * (i + 1));
		}
		
		return shuffleIndices;
	}
	
	/**
	 * Shuffle the array (in place) using the indices created by the generateShuffleIndices(int) method.
	 */
	@VisibleForTesting
	/*private*/ void shuffleArray(double[] shuffled, int[] indices) {
		double tmp;
		int j = 0;
		for (int i = shuffled.length - 1; i > 0; i--, j++) {//i > 0 because the first element is not swapped (not needed)
			tmp = shuffled[i];
			shuffled[i] = shuffled[indices[j]];
			shuffled[indices[j]] = tmp;
		}
	}
	
	/**
	 * Un-shuffle the array (in place) using the same indices that were used to shuffle it (created by the generateShuffleIndices(int) method).<br>
	 * Afterwards the array is the same as before shuffling.
	 */
	@VisibleForTesting
	/*private*/ void unshuffleArray(double[] shuffled, int[] indices) {
		double tmp;
		int numIndices = indices.length;
		for (int i = 1; i < shuffled.length; i++) {
			tmp = shuffled[i];
			shuffled[i] = shuffled[indices[numIndices - i]];
			shuffled[indices[numIndices - i]] = tmp;
		}
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
}