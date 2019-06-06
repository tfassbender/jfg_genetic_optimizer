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
	 * @param generation
	 *        The current generation.
	 * 
	 * @return Returns true if the optimization can be aborted. False otherwise.
	 */
	public boolean abort(DNA bestDna, long timeUsed, int generation);
	
	/**
	 * Indicate the progress on the calculation from 0 (not started) to 1 (done). The default implementation returns -1 to indicate the progress is
	 * unknown.
	 * 
	 * @param bestDna
	 *        The current best DNA that includes the genetic code and it's fitness.
	 * 
	 * @param timeUsed
	 *        The time used for the optimization till now (in milliseconds).
	 * 
	 * @param generation
	 *        The current generation.
	 * 
	 * @return The progress of the calculation from 0 to 1.
	 */
	public default double getProgress(DNA bestDNA, long timeUsed, int generation) {
		return -1d;
	}
}