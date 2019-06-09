package net.jfabricationgames.genetic_optimizer.abort_condition;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class ThresholdAbortCondition implements AbortCondition {
	
	private double threshold;
	private boolean upperLimit;
	
	private double initialFitness;
	private boolean initialFitnessSet;
	
	/**
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public ThresholdAbortCondition() {
		
	}
	
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
		
		initialFitness = 0;
		initialFitnessSet = false;
	}
	
	@Override
	public boolean abort(DNA bestDna, long timeUsed, int generation) {
		double bestFitness = bestDna.getFitness();
		//best fitness above threshold (if upperLimit == true) or below threshold (if upperLimit == false)
		return (upperLimit && (bestFitness > threshold)) || (!upperLimit && (bestFitness < threshold));
	}
	
	@Override
	public double getProgress(DNA bestDNA, long timeUsed, int generation) {
		if (!initialFitnessSet) {
			initialFitnessSet = true;
			initialFitness = bestDNA.getFitness();
			return 0d;
		}
		else {
			double initDiff;
			double currDiff;
			if (upperLimit) {
				initDiff = threshold - initialFitness;
				currDiff = threshold - bestDNA.getFitness();
			}
			else {
				initDiff = initialFitness - threshold;
				currDiff = bestDNA.getFitness() - threshold;
			}
			double progress = 1d - (currDiff / initDiff);
			return progress;
		}
	}
	
	@Override
	public String toString() {
		return "ThresholdAbortCondition [threshold=" + threshold + ", upperLimit=" + upperLimit + "]";
	}
	
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public boolean isUpperLimit() {
		return upperLimit;
	}
	public void setUpperLimit(boolean upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	public double getInitialFitness() {
		return initialFitness;
	}
	public void setInitialFitness(double initialFitness) {
		this.initialFitness = initialFitness;
	}
	
	public boolean isInitialFitnessSet() {
		return initialFitnessSet;
	}
	public void setInitialFitnessSet(boolean initialFitnessSet) {
		this.initialFitnessSet = initialFitnessSet;
	}
}