package net.jfabricationgames.genetic_optimizer.mutation;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public interface Mutation {
	
	public void mutate(DNA dna);
}