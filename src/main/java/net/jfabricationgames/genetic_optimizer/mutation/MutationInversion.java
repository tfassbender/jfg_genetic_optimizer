package net.jfabricationgames.genetic_optimizer.mutation;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Mutation by inversion: Choose a range within the sequence and invert it
 */
public class MutationInversion implements Mutation {
	
	private double mutationRate;
	private int rangeSize;
	private double maxValue;
	
	public MutationInversion(double mutationRate, int rangeSize, double maxValue) {
		this.mutationRate = mutationRate;
		this.rangeSize = rangeSize;
		this.maxValue = maxValue;
	}
	
	@Override
	public void mutate(DNA dna) {
		if (getRandomNumber() < mutationRate) {
			int n = dna.getLength();
			int k1 = (int) (getRandomNumber() * n);
			int k2 = k1 + getRangeSize() - 1;
			if (k2 >= dna.getLength()) {
				k2 = dna.getLength() - 1;
			}
			
			double[] dnaCode = dna.getDNACode();
			for (int i = k1; i <= k2; i++) {
				dnaCode[i] = getMaxValue() - dnaCode[i];
			}
		}
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	@VisibleForTesting
	/*private*/ int getRangeSize() {
		return rangeSize;
	}
	
	@VisibleForTesting
	/*private*/ double getMaxValue() {
		return maxValue;
	}
}