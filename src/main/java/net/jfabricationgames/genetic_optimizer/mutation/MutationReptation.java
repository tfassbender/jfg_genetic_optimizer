package net.jfabricationgames.genetic_optimizer.mutation;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Mutation by Reptation:
 * Take some code sequences from the start of the DNA-Code and put them at the end
 */
public class MutationReptation implements Mutation {
	
	private double mutationRate;
	private double reptationLengthMin;
	private double reptationLengthMax;
	private double frontToBackProbability;
	
	public MutationReptation(double mutationRate, double reptationLengthMin, double reptationLengthMax, double frontToBackProbability) {
		this.mutationRate = mutationRate;
		this.reptationLengthMin = reptationLengthMin;
		this.reptationLengthMax = reptationLengthMax;
		this.frontToBackProbability = frontToBackProbability;
	}
	
	@Override
	public void mutate(DNA dna) {
		if (Math.random() < mutationRate) {
			int n = dna.getLength();
			int k = (int) (Math.random() * (reptationLengthMax - reptationLengthMin) + reptationLengthMin);
			
			double[] dnaCode = dna.getDNACode();
			double[] tmp = new double[k];
			if (Math.random() < frontToBackProbability) {
				//move k DNA-Parts from the front of the DNA to the back
				System.arraycopy(dnaCode, 0, tmp, 0, k);
				System.arraycopy(dnaCode, k+1, dnaCode, 0, n-k);
				System.arraycopy(tmp, 0, dnaCode, n-k, k);
			}
			else {
				//move k DNA-Parts from the back of the DNA to the front
				System.arraycopy(dnaCode, n-k, tmp, 0, k);
				System.arraycopy(dnaCode, 0, dnaCode, k+1, n-k);
				System.arraycopy(tmp, 0, dnaCode, 0, k);
			}
		}
	}
}
