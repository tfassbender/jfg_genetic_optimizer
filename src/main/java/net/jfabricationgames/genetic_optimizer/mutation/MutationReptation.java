package net.jfabricationgames.genetic_optimizer.mutation;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Mutation by Reptation: Take some code sequences from the start of the DNA-Code and put them at the end
 */
public class MutationReptation implements Mutation {
	
	private double mutationRate;
	private int reptationLengthMin;
	private int reptationLengthMax;
	private double frontToBackProbability;
	
	public MutationReptation(double mutationRate, int reptationLengthMin, int reptationLengthMax, double frontToBackProbability) {
		this.mutationRate = mutationRate;
		this.reptationLengthMin = reptationLengthMin;
		this.reptationLengthMax = reptationLengthMax;
		this.frontToBackProbability = frontToBackProbability;
	}
	
	@Override
	public void mutate(DNA dna) {
		if (getRandomNumber() < mutationRate) {
			int n = dna.getLength();
			int k = (int) (getRandomNumber() * (getReptationLengthMax() - getReptationLengthMin()) + getReptationLengthMin());
			
			double[] dnaCode = dna.getDNACode();
			double[] tmp = new double[k];
			if (getRandomNumber() < frontToBackProbability) {
				//move k DNA-Parts from the front of the DNA to the back
				System.arraycopy(dnaCode, 0, tmp, 0, k);
				System.arraycopy(dnaCode, k, dnaCode, 0, n - k);
				System.arraycopy(tmp, 0, dnaCode, n - k, k);
			}
			else {
				//move k DNA-Parts from the back of the DNA to the front
				System.arraycopy(dnaCode, n - k, tmp, 0, k);
				System.arraycopy(dnaCode, 0, dnaCode, k, n - k);
				System.arraycopy(tmp, 0, dnaCode, 0, k);
			}
		}
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	@VisibleForTesting
	/*private*/ int getReptationLengthMin() {
		return reptationLengthMin;
	}
	
	@VisibleForTesting
	/*private*/ int getReptationLengthMax() {
		return reptationLengthMax;
	}
}