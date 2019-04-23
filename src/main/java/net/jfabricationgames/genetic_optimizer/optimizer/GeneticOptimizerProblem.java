package net.jfabricationgames.genetic_optimizer.optimizer;


public interface GeneticOptimizerProblem {
	
	/**
	 * The length of this problem to use as DNA length.
	 */
	public int getLength();
	
	/**
	 * Calculate the fitness of a given DNA.
	 */
	public double calculateFitness(DNA dna);
}