package net.jfabricationgames.genetic_optimizer.selection;

import java.util.Objects;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Scale the selection probability of the fitness to:<br>
 * f'(s) = (alpha * f(s)) - min(f(s') | s' in population) + beta
 * 
 * Where alpha and beta can be constant values or functions based on the generation or the time used (and time left).
 */
public class LinearDynamicScalingSelectionPressure implements SelectionPressure {
	
	private double alpha;
	private double beta;
	
	private SelectionPressureParameterFunction alphaFunction;
	private SelectionPressureParameterFunction betaFunction;
	
	/**
	 * Create a new {@link LinearDynamicScalingSelectionPressure} with fixed parameters alpha and beta (where alpha = 1 and beta = 0).
	 */
	public LinearDynamicScalingSelectionPressure() {
		this(1, 0);
	}
	/**
	 * Create a new {@link LinearDynamicScalingSelectionPressure} with fixed parameters alpha and beta.
	 * 
	 * @param alpha
	 *        The scaling parameter.
	 * 
	 * @param beta
	 *        The offset parameter.
	 */
	public LinearDynamicScalingSelectionPressure(double alpha, double beta) {
		if (alpha <= 0) {
			throw new IllegalArgumentException("The parameter alpha must be greater than 0.");
		}
		this.alpha = alpha;
		this.beta = beta;
	}
	/**
	 * Create a new {@link LinearDynamicScalingSelectionPressure} with variable parameters alpha and beta using the parameter functions.
	 * 
	 * @param alphaFunction
	 *        The function for the scaling parameter alpha.
	 * 
	 * @param betaFunction
	 *        The function for the offset parameter beta.
	 */
	public LinearDynamicScalingSelectionPressure(SelectionPressureParameterFunction alphaFunction, SelectionPressureParameterFunction betaFunction) {
		Objects.requireNonNull(alphaFunction,
				"A parameter function (alphaFunction) mussn't be null. Use a different constructor if you want to use fixed values.");
		Objects.requireNonNull(betaFunction,
				"A parameter function (betaFunction) mussn't be null. Use a different constructor if you want to use fixed values.");
		this.alphaFunction = alphaFunction;
		this.betaFunction = betaFunction;
	}
	
	@Override
	public double[] calculateSelectionProbability(DNA[] population, int generation, boolean minimize, long timeUsed) {
		double[] probabilities = new double[population.length];
		
		if (alphaFunction != null) {
			alpha = alphaFunction.getParameterValue(generation, timeUsed);
			beta = betaFunction.getParameterValue(generation, timeUsed);
		}
		
		//copy the fitness values to the probabilities array and find the minimum
		double minFitness = Double.MAX_VALUE;
		if (minimize) {
			for (int i = 0; i < population.length; i++) {
				probabilities[i] = -population[i].getFitness();
				minFitness = Math.min(minFitness, probabilities[i]);
			}
		}
		else {
			for (int i = 0; i < population.length; i++) {
				probabilities[i] = population[i].getFitness();
				minFitness = Math.min(minFitness, probabilities[i]);
			}
		}
		
		//apply the linear dynamic scaling function
		for (int i = 0; i < probabilities.length; i++) {
			probabilities[i] = (alpha * probabilities[i]) - minFitness + beta;
		}
		
		//sum up the probabilities
		double totalProbability = 0;
		for (int i = 0; i < probabilities.length; i++) {
			totalProbability += probabilities[i];
		}
		
		//divide all probability values by the total probability to get the correct probability values
		for (int i = 0; i < probabilities.length; i++) {
			probabilities[i] /= totalProbability;
		}
		
		return probabilities;
	}
	
	@Override
	public String toString() {
		return "LinearDynamicScalingSelectionPressure [alpha=" + alpha + ", beta=" + beta + ", alphaFunction=" + alphaFunction + ", betaFunction="
				+ betaFunction + "]";
	}
}