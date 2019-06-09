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
	
	/**
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public MutationInversion() {
		
	}
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
			
			double[] dnaCode = dna.getDnaCode();
			for (int i = k1; i <= k2; i++) {
				dnaCode[i] = getMaxValue() - dnaCode[i];
			}
		}
	}
	
	@Override
	public String toString() {
		return "MutationInversion [mutationRate=" + mutationRate + ", rangeSize=" + rangeSize + ", maxValue=" + maxValue + "]";
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
	
	public int getRangeSize() {
		return rangeSize;
	}
	public void setRangeSize(int rangeSize) {
		this.rangeSize = rangeSize;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
}