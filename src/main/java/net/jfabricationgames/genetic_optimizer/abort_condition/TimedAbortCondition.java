package net.jfabricationgames.genetic_optimizer.abort_condition;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class TimedAbortCondition implements AbortCondition {
	
	private long optimizationTime;
	
	/**
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public TimedAbortCondition() {
		
	}
	
	/**
	 * A timed condition that aborts when the optimization time is up.
	 * 
	 * @param optimizationTime
	 *        The time to optimize in milliseconds.
	 */
	public TimedAbortCondition(long optimizationTime) {
		if (optimizationTime <= 0) {
			throw new IllegalArgumentException("The optimization time has to be a positive value. Input was: " + optimizationTime);
		}
		this.optimizationTime = optimizationTime;
	}
	
	@Override
	public boolean abort(DNA bestDna, long timeUsed, int generation) {
		return timeUsed > optimizationTime;
	}
	
	@Override
	public double getProgress(DNA bestDNA, long timeUsed, int generation) {
		double progress = timeUsed;
		progress /= optimizationTime;
		return progress;
	}
	
	@Override
	public String toString() {
		return "TimedAbortCondition [optimizationTime=" + optimizationTime + " ms]";
	}
	
	public long getOptimizationTime() {
		return optimizationTime;
	}
	public void setOptimizationTime(long optimizationTime) {
		this.optimizationTime = optimizationTime;
	}
}