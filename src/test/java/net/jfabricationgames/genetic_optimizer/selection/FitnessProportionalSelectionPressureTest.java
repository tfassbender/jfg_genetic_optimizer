package net.jfabricationgames.genetic_optimizer.selection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class FitnessProportionalSelectionPressureTest {
	
	@Test
	public void testCalculateSelectionProbability_maximizeWithoutDefault() {
		SelectionPressure selectionPressure = new FitnessProportionalSelectionPressure();
		
		double[][] fitness = new double[][] {{0, 0, 1, 1, 0}, {1, 2, 3, 2, 2}, {1, 1, 1, 1}};
		double[][] probabilities = new double[fitness.length][];
		double[][] expectedProbabilities = new double[][] {{0, 0, 0.5, 0.5, 0}, {0.1, 0.2, 0.3, 0.2, 0.2}, {0.25, 0.25, 0.25, 0.25}};
		//test all fitness arrays
		for (int i = 0; i < fitness.length; i++) {
			DNA[] population = new DNA[fitness[i].length];
			for (int j = 0; j < population.length; j++) {
				DNA dna = new DNA(0);
				dna.setFitness(fitness[i][j]);
				population[j] = dna;
			}
			
			probabilities[i] = selectionPressure.calculateSelectionProbability(population, 0, false, 0, -1);
		}
		
		double epsilon = 1e-5;
		for (int i = 0; i < probabilities.length; i++) {
			assertArrayEquals(expectedProbabilities[i], probabilities[i], epsilon);
		}
	}
	
	@Test
	public void testCalculateSelectionProbability_maximizeWithDefault() {
		SelectionPressure selectionPressure = new FitnessProportionalSelectionPressure(3);
		
		double[][] fitness = new double[][] {{3, 3, 4, 4, 3}, {4, 5, 6, 5, 5}, {4, 4, 4, 4}};
		double[][] probabilities = new double[fitness.length][];
		double[][] expectedProbabilities = new double[][] {{0, 0, 0.5, 0.5, 0}, {0.1, 0.2, 0.3, 0.2, 0.2}, {0.25, 0.25, 0.25, 0.25}};
		//test all fitness arrays
		for (int i = 0; i < fitness.length; i++) {
			DNA[] population = new DNA[fitness[i].length];
			for (int j = 0; j < population.length; j++) {
				DNA dna = new DNA(0);
				dna.setFitness(fitness[i][j]);
				population[j] = dna;
			}
			
			probabilities[i] = selectionPressure.calculateSelectionProbability(population, 0, false, 0, -1);
		}
		
		double epsilon = 1e-5;
		for (int i = 0; i < probabilities.length; i++) {
			assertArrayEquals(expectedProbabilities[i], probabilities[i], epsilon);
		}
	}
	
	@Test
	public void testCalculateSelectionProbability_minimizeWithDefault() {
		SelectionPressure selectionPressure = new FitnessProportionalSelectionPressure(3);
		
		double[][] fitness = new double[][] {{3, 3, 2, 2, 3}, {2, 1, 0, 1, 1}, {2, 2, 2, 2}};
		double[][] probabilities = new double[fitness.length][];
		double[][] expectedProbabilities = new double[][] {{0, 0, 0.5, 0.5, 0}, {0.1, 0.2, 0.3, 0.2, 0.2}, {0.25, 0.25, 0.25, 0.25}};
		//test all fitness arrays
		for (int i = 0; i < fitness.length; i++) {
			DNA[] population = new DNA[fitness[i].length];
			for (int j = 0; j < population.length; j++) {
				DNA dna = new DNA(0);
				dna.setFitness(fitness[i][j]);
				population[j] = dna;
			}
			
			probabilities[i] = selectionPressure.calculateSelectionProbability(population, 0, false, 0, -1);
		}
		
		double epsilon = 1e-5;
		for (int i = 0; i < probabilities.length; i++) {
			assertArrayEquals(expectedProbabilities[i], probabilities[i], epsilon);
		}
	}
	
	@Test
	public void testCalculateSelectionProbability_minimizeWithoutDefault() {
		SelectionPressure selectionPressure = new FitnessProportionalSelectionPressure();
		
		double[][] fitness = new double[][] {{0, 0, -1, -1, 0}, {-1, -2, -3, -2, -2}, {-1, -1, -1, -1}};
		double[][] probabilities = new double[fitness.length][];
		double[][] expectedProbabilities = new double[][] {{0, 0, 0.5, 0.5, 0}, {0.1, 0.2, 0.3, 0.2, 0.2}, {0.25, 0.25, 0.25, 0.25}};
		//test all fitness arrays
		for (int i = 0; i < fitness.length; i++) {
			DNA[] population = new DNA[fitness[i].length];
			for (int j = 0; j < population.length; j++) {
				DNA dna = new DNA(0);
				dna.setFitness(fitness[i][j]);
				population[j] = dna;
			}
			
			probabilities[i] = selectionPressure.calculateSelectionProbability(population, 0, false, 0, -1);
		}
		
		double epsilon = 1e-5;
		for (int i = 0; i < probabilities.length; i++) {
			assertArrayEquals(expectedProbabilities[i], probabilities[i], epsilon);
		}
	}
	
	@Test
	public void testCalculateSelectionProbability_randomFitness() {
		SelectionPressure selectionPressure = new FitnessProportionalSelectionPressure();
		SelectionPressure selectionPressureOffset = new FitnessProportionalSelectionPressure(42);
		
		double[] fitness = new double[50];
		for (int i = 0; i < fitness.length; i++) {
			fitness[i] = Math.random() * 100;
		}
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double[] probabilityNoOffset = selectionPressure.calculateSelectionProbability(population, 0, false, 0, -1);
		double[] probabilityOffset = selectionPressureOffset.calculateSelectionProbability(population, 0, true, 0, -1);
		
		double sumNoOffset = 0;
		double sumOffset = 0;
		
		for (int i = 0; i < probabilityOffset.length; i++) {
			sumNoOffset += probabilityNoOffset[i];
			sumOffset += probabilityOffset[i];
		}
		
		//the summed probability has to be 1
		double epsilon = 1e-5;
		assertEquals(1d, sumNoOffset, epsilon);
		assertEquals(1d, sumOffset, epsilon);
	}
}