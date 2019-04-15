package net.jfabricationgames.genetic_optimizer.abort_condition;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

@FunctionalInterface
public interface AbortCondition {
	
	/**
	 * The condition to abort the optimization.
	 * 
	 * @param bestDna
	 *        The current best DNA that includes the genetic code and it's fitness.
	 * 
	 * @param timeUsed
	 *        The time used for the optimization till now (in milliseconds).
	 * 
	 * @return Returns true if the optimization can be aborted. False otherwise.
	 */
	public boolean abort(DNA bestDna, long timeUsed);
}