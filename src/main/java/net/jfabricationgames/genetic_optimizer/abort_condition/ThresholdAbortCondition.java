package net.jfabricationgames.genetic_optimizer.abort_condition;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class ThresholdAbortCondition implements AbortCondition {
	
	private double threshold;
	private boolean upperLimit;
	
	/**
	 * An abort condition that aborts the optimization as soon as the best fitness reaches a threshold.
	 * 
	 * @param threshold
	 *        The fitness threshold that is to be reached to abort the optimization.
	 * 
	 * @param upperLimit
	 *        A boolean to determine whether the best fitness has to be higher (true) or lower than the threshold (false) to abort the optimization.
	 */
	public ThresholdAbortCondition(double threshold, boolean upperLimit) {
		this.threshold = threshold;
		this.upperLimit = upperLimit;
	}
	
	@Override
	public boolean abort(DNA bestDna, long timeUsed) {
		double bestFitness = bestDna.getFitness();
		//best fitness above threshold (if upperLimit == true) or below threshold (if upperLimit == false)
		return (upperLimit && (bestFitness > threshold)) || (!upperLimit && (bestFitness < threshold));
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	public boolean isUpperLimit() {
		return upperLimit;
	}
}