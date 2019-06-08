package net.jfabricationgames.genetic_optimizer.mutation;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class MutationNormalDistributed implements Mutation {
	
	private double mutationRate;
	private double maxMutatedFieldsPropotion;
	
	private double maxValue;
	private double minValue;
	
	private double expectedValue;
	private double standardDeviation;
	
	/**
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public MutationNormalDistributed() {
		
	}
	
	public MutationNormalDistributed(double mutationRate, double maxMutatedFieldsPropotion, double maxValue, double minValue, double expectedValue,
			double standardDeviation) {
		this.mutationRate = mutationRate;
		this.maxMutatedFieldsPropotion = maxMutatedFieldsPropotion;
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.expectedValue = expectedValue;
		this.standardDeviation = standardDeviation;
	}
	public MutationNormalDistributed(double mutationRate, double maxMutatedFields, double expectedValue, double standardDeviation) {
		this(mutationRate, maxMutatedFields, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, expectedValue, standardDeviation);
	}
	
	@Override
	public void mutate(DNA dna) {
		if (getRandomNumber() < mutationRate) {
			int mutatedFields = (int) (getRandomNumber() * getMaxMutatedFieldsPropotion() * dna.getLength());
			
			//mutate a randomly chosen number of fields using a normal-distribution
			for (int i = 0; i < mutatedFields; i++) {
				//the index of the changed field
				int fieldIndex = (int) (getRandomNumber() * dna.getLength());
				//the change that is added onto the field
				double change = getExpectedValue() + getGaussianRandomNumber() * getStandardDeviation();
				
				//add the change to the dna
				double[] dnaCode = dna.getDNACode();
				dnaCode[fieldIndex] += change;
				
				//check whether the new value is within the allowed range
				dnaCode[fieldIndex] = Math.max(getMinValue(), Math.min(getMaxValue(), dnaCode[fieldIndex]));
			}
		}
	}
	
	@Override
	public String toString() {
		return "MutationNormalDistributed [mutationRate=" + mutationRate + ", maxMutatedFieldsPropotion=" + maxMutatedFieldsPropotion + ", maxValue="
				+ maxValue + ", minValue=" + minValue + ", expectedValue=" + expectedValue + ", standardDeviation=" + standardDeviation + "]";
	}
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	@VisibleForTesting
	/*private*/ double getGaussianRandomNumber() {
		return ThreadLocalRandom.current().nextGaussian();
	}
	
	@VisibleForTesting
	/*private*/ double getMaxMutatedFieldsPropotion() {
		return maxMutatedFieldsPropotion;
	}
	
	@VisibleForTesting
	/*private*/ double getMaxValue() {
		return maxValue;
	}
	
	@VisibleForTesting
	/*private*/ double getMinValue() {
		return minValue;
	}
	
	@VisibleForTesting
	/*private*/ double getExpectedValue() {
		return expectedValue;
	}
	
	@VisibleForTesting
	/*private*/ double getStandardDeviation() {
		return standardDeviation;
	}
}