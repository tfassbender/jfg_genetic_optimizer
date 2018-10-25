package net.jfabricationgames.genetic_optimizer.optimizer;

@FunctionalInterface
public interface InitialDNAGenerator {
	
	public DNA generateRandomDNA(int length);
}