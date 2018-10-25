package net.jfabricationgames.genetic_optimizer.mutation;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

@FunctionalInterface
public interface Mutation {
	
	public void mutate(DNA dna);
}