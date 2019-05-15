package net.jfabricationgames.genetic_optimizer.selection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class EquallyDistributedSelectionPressureTest {
	
	@Test
	public void testEqualDistribution() {
		SelectionPressure selectionPressure = new EquallyDistributedSelectionPressure();
		
		DNA[] population = new DNA[5];
		DNA[] population2 = new DNA[10];
		for (int i = 0; i < population2.length; i++) {
			population2[i] = new DNA(0);
			population2[i].setFitness(Math.random() * 42);
		}
		
		double[] probabilities = selectionPressure.calculateSelectionProbability(population, 0, false, 0);
		double[] probabilities2 = selectionPressure.calculateSelectionProbability(population2, 0, true, 0);
		
		double epsilon = 1e-5;
		assertArrayEquals(new double[] {0.2, 0.2, 0.2, 0.2, 0.2}, probabilities, epsilon);
		assertArrayEquals(new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1}, probabilities2, epsilon);
	}
}