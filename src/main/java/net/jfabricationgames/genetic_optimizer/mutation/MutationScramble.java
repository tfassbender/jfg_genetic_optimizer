package net.jfabricationgames.genetic_optimizer.mutation;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Mutation by scramble:
 * Take an interval in the DNA-Sequence and mix the entries in this interval stochastically.
 */
public class MutationScramble implements Mutation {
	
	private double mutationRate;
	private int maxSwaps;
	
	public MutationScramble(double mutationRate, int maxSwaps) {
		this.mutationRate = mutationRate;
		this.maxSwaps = maxSwaps;
	}
	
	@Override
	public void mutate(DNA dna) {
		if (Math.random() < mutationRate) {
			int n = dna.getLength();
			int k1 = (int) (Math.random() * n);
			int k2 = (int) (Math.random() * n);
			int dist = Math.abs(k1 - k2);
			int swaps = (int) (Math.random() * maxSwaps);
			
			for (int i = 0; i < swaps; i++) {
				int d1 = (int) (Math.random() * dist);
				int d2 = (int) (Math.random() * dist);
				
				double[] dnaCode = dna.getDNACode();
				double tmp = dnaCode[d1];
				dnaCode[d1] = dnaCode[d2];
				dnaCode[d2] = tmp;
			}
		}
	}
}