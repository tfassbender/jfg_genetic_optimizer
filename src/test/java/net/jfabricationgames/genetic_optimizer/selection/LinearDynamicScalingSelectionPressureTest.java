package net.jfabricationgames.genetic_optimizer.selection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class LinearDynamicScalingSelectionPressureTest {
	
	@Test
	public void testCalculateSelectionProbability_defaultSettings_maximize() {
		SelectionPressure selectionPressure = new LinearDynamicScalingSelectionPressure();
		
		double[][] fitness = new double[][] {{0, 0, 1, 1, 0}, {0, 1, 2, 3, 2, 2}, {1, 2, 1, 1}};
		double[][] probabilities = new double[fitness.length][];
		double[][] expectedProbabilities = new double[][] {{0, 0, 0.5, 0.5, 0}, {0d, 0.1, 0.2, 0.3, 0.2, 0.2}, {0d, 1d, 0d, 0d}};
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
	public void testCalculateSelectionProbability_defaultSettings_minimize() {
		SelectionPressure selectionPressure = new LinearDynamicScalingSelectionPressure();
		
		double[][] fitness = new double[][] {{2, 2, 1, 1, 2}, {5, 4, 3, 2, 3, 3}, {2, 1, 2, 2}};
		double[][] probabilities = new double[fitness.length][];
		double[][] expectedProbabilities = new double[][] {{0, 0, 0.5, 0.5, 0}, {0d, 0.1, 0.2, 0.3, 0.2, 0.2}, {0d, 1d, 0d, 0d}};
		//test all fitness arrays
		for (int i = 0; i < fitness.length; i++) {
			DNA[] population = new DNA[fitness[i].length];
			for (int j = 0; j < population.length; j++) {
				DNA dna = new DNA(0);
				dna.setFitness(fitness[i][j]);
				population[j] = dna;
			}
			
			probabilities[i] = selectionPressure.calculateSelectionProbability(population, 0, true, 0, -1);
		}
		
		double epsilon = 1e-5;
		for (int i = 0; i < probabilities.length; i++) {
			assertArrayEquals(expectedProbabilities[i], probabilities[i], epsilon);
		}
	}
	
	@Test
	public void testCalculateSelectionProbability_alpha2_beta1() {
		SelectionPressure selectionPressure = new LinearDynamicScalingSelectionPressure(2d, 1d);
		
		double[] fitness = new double[] {1, 2, 3, 5};
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double epsilon = 1e-5;
		assertArrayEquals(new double[] {2d / 22d, 4d / 22d, 6d / 22d, 10d / 22d},
				selectionPressure.calculateSelectionProbability(population, 0, false, 0, -1), epsilon);
	}
	
	@Test
	public void testCalculateSelectionProbability_alphaFunction2_betaFunction1() {
		SelectionPressure selectionPressure = new LinearDynamicScalingSelectionPressure((gen, tUsed, tTotal) -> 2d, (gen, tUsed, tTotal) -> 1d);
		
		double[] fitness = new double[] {1, 2, 3, 5};
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double epsilon = 1e-5;
		assertArrayEquals(new double[] {2d / 22d, 4d / 22d, 6d / 22d, 10d / 22d},
				selectionPressure.calculateSelectionProbability(population, 0, false, 0, -1), epsilon);
	}
	
	@Test
	public void testCalculateSelectionProbability_alpha_beta_genreation() {
		SelectionPressure selectionPressure = new LinearDynamicScalingSelectionPressure((gen, tUsed, tTotal) -> 2 * gen, (gen, tUsed, tTotal) -> gen);
		
		double[] fitness = new double[] {1, 2, 3, 5};
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double epsilon = 1e-5;
		assertArrayEquals(new double[] {2d / 22d, 4d / 22d, 6d / 22d, 10d / 22d},
				selectionPressure.calculateSelectionProbability(population, 1, false, 0, -1), epsilon);
		assertArrayEquals(new double[] {5d / 48d, 9d / 48d, 13d / 48d, 21d / 48d},
				selectionPressure.calculateSelectionProbability(population, 2, false, 0, -1), epsilon);
	}
	
	@Test
	public void testCalculateSelectionProbability_randomFitness() {
		SelectionPressure selectionPressureDefault = new LinearDynamicScalingSelectionPressure();
		SelectionPressure selectionPressureAlpha42Beta10 = new LinearDynamicScalingSelectionPressure(42, 10);
		SelectionPressure selectionPressureFunctions = new LinearDynamicScalingSelectionPressure((gen, tUsed, tTotal) -> 2 * gen,
				(gen, tUsed, tTotal) -> 3 * gen + 2);
		
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
		
		double[] probabilityDefault = selectionPressureDefault.calculateSelectionProbability(population, 0, false, 0, -1);
		double[] probabilityAlpha42Beta10 = selectionPressureAlpha42Beta10.calculateSelectionProbability(population, 0, true, 0, -1);
		double[] probabilityFunctions = selectionPressureFunctions.calculateSelectionProbability(population, 5, false, 0, -1);
		
		double sumDefault = 0;
		double sumAlpha42Beta10 = 0;
		double sumFunctions = 0;
		
		for (int i = 0; i < probabilityAlpha42Beta10.length; i++) {
			sumDefault += probabilityDefault[i];
			sumAlpha42Beta10 += probabilityAlpha42Beta10[i];
			sumFunctions += probabilityFunctions[i];
		}
		
		//the summed probability has to be 1
		double epsilon = 1e-5;
		assertEquals(1d, sumDefault, epsilon);
		assertEquals(1d, sumAlpha42Beta10, epsilon);
		assertEquals(1d, sumFunctions, epsilon);
	}
}