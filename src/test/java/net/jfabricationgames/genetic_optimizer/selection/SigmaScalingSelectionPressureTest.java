package net.jfabricationgames.genetic_optimizer.selection;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class SigmaScalingSelectionPressureTest {
	
	@Test
	public void testCalculateSelectionProbability_defaultSettings_maximize() {
		SelectionPressure selectionPressure = new SigmaScalingSelectionPressure();
		
		double[] fitness = new double[] {0, 1, 2, 3, 4};//mu = 2, sigma = sqrt(10)
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double epsilon = 1e-5;
		double div = 5 * Math.sqrt(10);
		assertArrayEquals(new double[] {(Math.sqrt(10) - 2) / div, (Math.sqrt(10) - 1) / div, Math.sqrt(10) / div, (Math.sqrt(10) + 1) / div,
				(Math.sqrt(10) + 2) / div}, selectionPressure.calculateSelectionProbability(population, 0, false, 0), epsilon);
	}
	
	@Test
	public void testCalculateSelectionProbability_defaultSettings_minimize() {
		SelectionPressure selectionPressure = new SigmaScalingSelectionPressure();
		
		double[] fitness = new double[] {0, 1, 2, 3, 4};//mu = 2, sigma = sqrt(10)
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double epsilon = 1e-5;
		double div = 5 * Math.sqrt(10);
		assertArrayEquals(new double[] {(Math.sqrt(10) + 2) / div, (Math.sqrt(10) + 1) / div, Math.sqrt(10) / div, (Math.sqrt(10) - 1) / div,
				(Math.sqrt(10) - 2) / div}, selectionPressure.calculateSelectionProbability(population, 0, true, 0), epsilon);
	}
	
	@Test
	public void testCalculateSelectionProbability_beta2() {
		SelectionPressure selectionPressure = new SigmaScalingSelectionPressure(2d);
		
		double[] fitness = new double[] {0, 1, 2, 3, 4};//mu = 2, sigma = sqrt(10)
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double epsilon = 1e-5;
		double div = 10 * Math.sqrt(10);
		assertArrayEquals(new double[] {(2 * Math.sqrt(10) - 2) / div, (2 * Math.sqrt(10) - 1) / div, 2 * Math.sqrt(10) / div,
				(2 * Math.sqrt(10) + 1) / div, (2 * Math.sqrt(10) + 2) / div},
				selectionPressure.calculateSelectionProbability(population, 0, false, 0), epsilon);
	}
	
	@Test
	public void testCalculateSelectionProbability_beta0() {
		SelectionPressure selectionPressure = new SigmaScalingSelectionPressure((gen, tUsed) -> 0d);
		
		double[] fitness = new double[] {0, 1, 2, 3, 4};//mu = 2, sigma = sqrt(10)
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double epsilon = 1e-5;
		double div = 3d;
		assertArrayEquals(new double[] {0, 0, 0, 1d / div, 2d / div}, selectionPressure.calculateSelectionProbability(population, 0, false, 0),
				epsilon);
	}
	
	@Test
	public void testCalculateSelectionProbability_betaFunction() {
		SelectionPressure selectionPressure = new SigmaScalingSelectionPressure((gen, tUsed) -> gen);
		
		double[] fitness = new double[] {0, 1, 2, 3, 4};//mu = 2, sigma = sqrt(10)
		
		DNA[] population = new DNA[fitness.length];
		for (int i = 0; i < population.length; i++) {
			DNA dna = new DNA(0);
			dna.setFitness(fitness[i]);
			population[i] = dna;
		}
		
		double epsilon = 1e-5;
		double div = 3d;
		assertArrayEquals(new double[] {0, 0, 0, 1d / div, 2d / div}, selectionPressure.calculateSelectionProbability(population, 0, false, 0),
				epsilon);
		
		div = 5 * Math.sqrt(10);
		assertArrayEquals(new double[] {(Math.sqrt(10) - 2) / div, (Math.sqrt(10) - 1) / div, Math.sqrt(10) / div, (Math.sqrt(10) + 1) / div,
				(Math.sqrt(10) + 2) / div}, selectionPressure.calculateSelectionProbability(population, 1, false, 0), epsilon);
	}
	
	@Test
	public void testCalculateSelectionProbability_randomFitness() {
		SelectionPressure selectionPressureDefault = new SigmaScalingSelectionPressure();
		SelectionPressure selectionPressureBeta42 = new SigmaScalingSelectionPressure(42);
		SelectionPressure selectionPressureFunction = new SigmaScalingSelectionPressure((gen, tUsed) -> 2 * gen + 3);
		
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
		
		double[] probabilityDefault = selectionPressureDefault.calculateSelectionProbability(population, 0, false, 0);
		double[] probabilityBeta42 = selectionPressureBeta42.calculateSelectionProbability(population, 0, true, 0);
		double[] probabilityFunction = selectionPressureFunction.calculateSelectionProbability(population, 5, false, 0);
		
		double sumDefault = 0;
		double sumBeta42 = 0;
		double sumFunction = 0;
		
		for (int i = 0; i < probabilityBeta42.length; i++) {
			sumDefault += probabilityDefault[i];
			sumBeta42 += probabilityBeta42[i];
			sumFunction += probabilityFunction[i];
		}
		
		//the summed probability has to be 1
		double epsilon = 1e-5;
		assertEquals(1d, sumDefault, epsilon);
		assertEquals(1d, sumBeta42, epsilon);
		assertEquals(1d, sumFunction, epsilon);
	}
}