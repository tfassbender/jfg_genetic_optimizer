package net.jfabricationgames.genetic_optimizer.abort_condition;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class TimedAbortCondition implements AbortCondition {
	
	private long optimizationTime;
	
	/**
	 * A timed condition that aborts when the optimization time is up.
	 * 
	 * @param optimizationTime
	 *        The time to optimize in milliseconds.
	 */
	public TimedAbortCondition(long optimizationTime) {
		this.optimizationTime = optimizationTime;
	}
	
	@Override
	public boolean abort(DNA bestDna, long timeUsed) {
		return timeUsed > optimizationTime;
	}
	
	public long getOptimizationTime() {
		return optimizationTime;
	}
}