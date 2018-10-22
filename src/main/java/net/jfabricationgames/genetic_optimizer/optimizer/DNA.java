package net.jfabricationgames.genetic_optimizer.optimizer;

import java.util.Arrays;

/**
 * A (non-optimal) solution for the given Problem modeled as DNA stream.
 */
public class DNA implements Comparable<DNA> {
	
	private double fitness;
	private double[] dnaCode;
	
	public DNA(int length) {
		this.dnaCode = new double[length];
	}
	
	public static DNA generateRandomDNA(int length, double range) {
		DNA random = new DNA(length);
		for (int i = 0; i < length; i++) {
			random.dnaCode[i] = Math.random() * range;
		}
		return random;
	}
	
	@Override
	public int compareTo(DNA dna) {
		return Double.compare(fitness, dna.fitness);
	}
	
	@Override
	public String toString() {
		return String.format("DNA[fitness: %.3f; " + Arrays.toString(dnaCode) + "]", fitness);
	}
	
	public void copyTo(DNA dna) {
		dna.fitness = fitness;
		for (int i = 0; i < dnaCode.length; i++) {
			dna.dnaCode[i] = dnaCode[i];
		}
	}
	
	public int getLength() {
		return dnaCode.length;
	}
	
	public double[] getDNACode() {
		return dnaCode;
	}
	
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
}