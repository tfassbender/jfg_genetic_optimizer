package net.jfabricationgames.genetic_optimizer.mutation;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Mutation by scramble: Take an interval in the DNA-Sequence and mix the entries in this interval stochastically.
 */
public class MutationScramble implements Mutation {
	
	private double mutationRate;
	private int maxSwaps;
	
	/**
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public MutationScramble() {
		
	}
	
	public MutationScramble(double mutationRate, int maxSwaps) {
		this.mutationRate = mutationRate;
		this.maxSwaps = maxSwaps;
	}
	
	@Override
	public void mutate(DNA dna) {
		if (getRandomNumber() < mutationRate) {
			int n = dna.getLength();
			int k1 = (int) (getRandomNumber() * n);
			int k2 = (int) (getRandomNumber() * n);
			int dist = Math.abs(k1 - k2);
			int swaps = (int) (getRandomNumber() * getMaxSwaps());
			
			double[] dnaCode = dna.getDnaCode();
			int minK = Math.min(k1, k2);
			for (int i = 0; i < swaps; i++) {
				int d1 = (int) (getRandomNumber() * dist) + minK;
				int d2 = (int) (getRandomNumber() * dist) + minK;
				
				double tmp = dnaCode[d1];
				dnaCode[d1] = dnaCode[d2];
				dnaCode[d2] = tmp;
			}
		}
	}
	
	@Override
	public String toString() {
		return "MutationScramble [mutationRate=" + mutationRate + ", maxSwaps=" + maxSwaps + "]";
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	public double getMutationRate() {
		return mutationRate;
	}
	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}
	
	public int getMaxSwaps() {
		return maxSwaps;
	}
	public void setMaxSwaps(int maxSwaps) {
		this.maxSwaps = maxSwaps;
	}
}