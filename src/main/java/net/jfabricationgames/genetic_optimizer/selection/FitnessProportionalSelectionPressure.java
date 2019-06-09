package net.jfabricationgames.genetic_optimizer.selection;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Calculate the reproduction probability proportional to the fitness.
 * 
 * A default value for the fitness can be chosen to rescale this point to zero.
 * 
 * WARNING: The selection pressure won't work if there are fitness values included that are below the defaultFitness (or above when minimizing),
 * because that will lead to negative probabilities.
 */
public class FitnessProportionalSelectionPressure implements SelectionPressure {
	
	private double defaultFitness;
	
	public FitnessProportionalSelectionPressure() {
		this(0d);
	}
	public FitnessProportionalSelectionPressure(double defaultFitness) {
		this.defaultFitness = defaultFitness;
	}
	
	@Override
	public double[] calculateSelectionProbability(DNA[] population, int generation, boolean minimize, long timeUsed) {
		double[] probabilities = new double[population.length];
		
		//add the absolute fitness values of each individual to the probabilities array
		if (minimize) {
			for (int i = 0; i < population.length; i++) {
				probabilities[i] = defaultFitness - population[i].getFitness();
			}
		}
		else {
			for (int i = 0; i < population.length; i++) {
				probabilities[i] = population[i].getFitness() - defaultFitness;
			}
		}
		
		//sum up the total fitness
		double totalFitness = 0;
		for (int i = 0; i < probabilities.length; i++) {
			totalFitness += probabilities[i];
		}
		
		//divide all fitness values by the total fitness to get the probabilities
		for (int i = 0; i < probabilities.length; i++) {
			probabilities[i] /= totalFitness;
		}
		
		return probabilities;
	}
	
	@Override
	public String toString() {
		return "FitnessProportionalSelectionPressure [defaultFitness=" + defaultFitness + "]";
	}
	
	public double getDefaultFitness() {
		return defaultFitness;
	}
	public void setDefaultFitness(double defaultFitness) {
		this.defaultFitness = defaultFitness;
	}
}