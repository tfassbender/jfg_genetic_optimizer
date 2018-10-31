package net.jfabricationgames.genetic_optimizer.heredity;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

@FunctionalInterface
public interface Heredity {
	
	public DNA mixDNA(DNA father, DNA mother);
}