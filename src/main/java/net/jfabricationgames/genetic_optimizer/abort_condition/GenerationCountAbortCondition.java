package net.jfabricationgames.genetic_optimizer.abort_condition;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class GenerationCountAbortCondition implements AbortCondition {
	
	private int maxGeneration;
	
	/**
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public GenerationCountAbortCondition() {
		
	}
	/**
	 * An abort condition that aborts when the current generation reaches a user-chosen count.
	 * 
	 * @param maxGeneration
	 *        The maximum number of generations that are created.
	 */
	public GenerationCountAbortCondition(int maxGeneration) {
		if (maxGeneration <= 0) {
			throw new IllegalArgumentException("The maximum generation has to be a positive value. Input was: " + maxGeneration);
		}
		this.maxGeneration = maxGeneration;
	}
	
	@Override
	public boolean abort(DNA bestDna, long timeUsed, int generation) {
		return generation >= maxGeneration;
	}
	
	@Override
	public double getProgress(DNA bestDNA, long timeUsed, int generation) {
		double progress = generation;
		progress /= maxGeneration - 1;
		return progress;
	}
	
	@Override
	public String toString() {
		return "GenerationCountAbortCondition [maxGeneration=" + maxGeneration + "]";
	}
}