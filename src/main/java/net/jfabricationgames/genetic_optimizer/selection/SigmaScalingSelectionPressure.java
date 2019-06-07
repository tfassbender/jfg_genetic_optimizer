package net.jfabricationgames.genetic_optimizer.selection;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

/**
 * Scale the selection probability of the fitness to:<br>
 * f'(s) = f(s) - (mu(t) - beta * sigma(t))
 * 
 * Where mu(t) is the expected value of the populations fitness, sigma(t) is the standard deviation of the populations fitness and beta is a parameter
 * the user can choose (also beta can be a function of generation, used time and total time).
 */
public class SigmaScalingSelectionPressure implements SelectionPressure {
	
	private double beta;
	
	private SelectionPressureParameterFunction betaFunction;
	
	/**
	 * Create a new {@link SigmaScalingSelectionPressure} with the default value of beta = 1.
	 */
	public SigmaScalingSelectionPressure() {
		this(1);
	}
	/**
	 * Create a new {@link SigmaScalingSelectionPressure} with a fixed value for the parameter beta.
	 * 
	 * @param beta
	 *        A scaling parameter.
	 */
	public SigmaScalingSelectionPressure(double beta) {
		if (beta <= 0) {
			throw new IllegalArgumentException("Beta must be greater than 0.");
		}
		this.beta = beta;
	}
	/**
	 * Create a new {@link SigmaScalingSelectionPressure} with a variable value for the parameter beta (given as a function).
	 * 
	 * @param betaFunction
	 *        The function for the scaling parameter.
	 */
	public SigmaScalingSelectionPressure(SelectionPressureParameterFunction betaFunction) {
		this.betaFunction = betaFunction;
	}
	
	@Override
	public double[] calculateSelectionProbability(DNA[] population, int generation, boolean minimize, long timeUsed) {
		double[] probabilities = new double[population.length];
		
		if (betaFunction != null) {
			beta = betaFunction.getParameterValue(generation, timeUsed);
		}
		
		//copy the fitness values to the probabilities array and find the expected value mu
		double mu = 0;
		if (minimize) {
			double maxValue = -Double.MAX_VALUE;
			for (int i = 0; i < population.length; i++) {
				maxValue = Math.max(maxValue, population[i].getFitness());
			}
			for (int i = 0; i < population.length; i++) {
				probabilities[i] = maxValue - population[i].getFitness();
				mu += probabilities[i];
			}
		}
		else {
			for (int i = 0; i < population.length; i++) {
				probabilities[i] = population[i].getFitness();
				mu += probabilities[i];
			}
		}
		mu /= probabilities.length;
		
		//calculate the standard deviation sigma
		double sigma = 0;
		for (int i = 0; i < population.length; i++) {
			sigma += (probabilities[i] - mu) * (probabilities[i] - mu);
		}
		sigma = Math.sqrt((1d / (mu - 1)) * sigma);
		
		//apply the sigma scaling function
		for (int i = 0; i < probabilities.length; i++) {
			probabilities[i] = probabilities[i] - (mu - beta * sigma);
			//probabilities lower than 0 are set to 0
			probabilities[i] = Math.max(probabilities[i], 0);
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
		return "SigmaScalingSelectionPressure [beta=" + beta + ", betaFunction=" + betaFunction + "]";
	}
}