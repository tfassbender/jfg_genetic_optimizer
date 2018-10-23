package net.jfabricationgames.genetic_optimizer.optimizer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import net.jfabricationgames.genetic_optimizer.heredity.Heredity;
import net.jfabricationgames.genetic_optimizer.mutation.Mutation;

class GeneticOptimizerTest {
	
	@Test
	public void testReverseDNA_shouldRevertTheArrayContent() {
		DNA dna1 = new DNA(5);
		DNA dna2 = new DNA(5);
		DNA dna3 = new DNA(5);
		DNA dna4 = new DNA(5);
		DNA dna5 = new DNA(5);
		
		DNA[] populationEven = new DNA[] {dna1, dna2, dna3, dna4};
		DNA[] populationOdd = new DNA[] {dna1, dna2, dna3, dna4, dna5};
		
		DNA[] populationEvenReversed = new DNA[] {dna4, dna3, dna2, dna1};
		DNA[] populationOddReversed = new DNA[] {dna5, dna4, dna3, dna2, dna1};
		
		GeneticOptimizer.reverse(populationEven);
		GeneticOptimizer.reverse(populationOdd);
		
		assertArrayEquals(populationEvenReversed, populationEven);
		assertArrayEquals(populationOddReversed, populationOdd);
	}
	
	@Test
	public void testCreateInitialPopulationFromRootPopulation_shouldCreateAnInitialPopulationFromTheRootPopulation() {
		Problem problem = generateProblemWithFixedFitness(42d);
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = generateInitialPopulation(5, 5);
		
		//mark each initial population with a specific genome
		for (int i = 0; i < initialPopulation.size(); i++) {
			initialPopulation.get(i).getDNACode()[0] = 42d;
		}
		
		Heredity heredity = mock(Heredity.class);
		List<Mutation> mutations = new ArrayList<Mutation>(1);
		mutations.add(mock(Mutation.class));
		int time = 1000;//1 second
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, initialPopulation, heredity, mutations, time);
		optimizer.setBestDNA(new DNA(5));
		DNA[] population = new DNA[5];
		
		optimizer.createInitialPopulation(population);
		
		//the population array is filled with the initial populations (except that the fitness is added)
		assertEquals(42d, population[0].getDNACode()[0], 1e-8);
		assertEquals(42d, population[1].getDNACode()[0], 1e-8);
		assertEquals(42d, population[2].getDNACode()[0], 1e-8);
		assertEquals(42d, population[3].getDNACode()[0], 1e-8);
		assertEquals(42d, population[4].getDNACode()[0], 1e-8);
	}
	
	@Test
	public void testCreateSortedInitialPopulation_shouldCreateAnInitialPopulationFromTheRootPopulationAndSortItByItsFitness() {
		Problem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = generateInitialPopulation(5, 5);
		
		//mark each initial population with a specific genome
		for (int i = 0; i < initialPopulation.size(); i++) {
			initialPopulation.get(i).getDNACode()[0] = 42d + i;
		}
		
		Heredity heredity = mock(Heredity.class);
		List<Mutation> mutations = new ArrayList<Mutation>(1);
		mutations.add(mock(Mutation.class));
		int time = 1000;//1 second
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, initialPopulation, heredity, mutations, time);
		optimizer.setBestDNA(new DNA(5));
		DNA[] population = new DNA[5];
		
		optimizer.createInitialPopulation(population);
		
		//the population array is filled with the initial populations (except that the fitness is added)
		//the DNA with the lowest fitness is the first in the population because the optimizer minimizes by default
		assertEquals(42d, population[0].getDNACode()[0], 1e-8);
		assertEquals(43d, population[1].getDNACode()[0], 1e-8);
		assertEquals(44d, population[2].getDNACode()[0], 1e-8);
		assertEquals(45d, population[3].getDNACode()[0], 1e-8);
		assertEquals(46d, population[4].getDNACode()[0], 1e-8);
	}
	
	@Test
	public void testCreateSortedInitialPopulationWithMaximizationProblem_shouldCreateAnInitialPopulationFromTheRootPopulationAndSortItByItsFitness_highesFitnessFirst() {
		Problem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = generateInitialPopulation(5, 5);
		
		//mark each initial population with a specific genome
		for (int i = 0; i < initialPopulation.size(); i++) {
			initialPopulation.get(i).getDNACode()[0] = 42d + i;
		}
		
		Heredity heredity = mock(Heredity.class);
		List<Mutation> mutations = new ArrayList<Mutation>(1);
		mutations.add(mock(Mutation.class));
		int time = 1000;//1 second
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, initialPopulation, heredity, mutations, time);
		optimizer.setBestDNA(new DNA(5));
		optimizer.setMinimize(false);
		DNA[] population = new DNA[5];
		
		optimizer.createInitialPopulation(population);
		
		//the population array is filled with the initial populations (except that the fitness is added)
		//the DNA with the lowest fitness is the first in the population because the optimizer minimizes by default
		assertEquals(46d, population[0].getDNACode()[0], 1e-8);
		assertEquals(45d, population[1].getDNACode()[0], 1e-8);
		assertEquals(44d, population[2].getDNACode()[0], 1e-8);
		assertEquals(43d, population[3].getDNACode()[0], 1e-8);
		assertEquals(42d, population[4].getDNACode()[0], 1e-8);
	}
	
	private List<DNA> generateInitialPopulation(int populationSize, int dnaSize) {
		List<DNA> initialPopulation = new ArrayList<DNA>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			initialPopulation.add(new DNA(dnaSize));
		}
		return initialPopulation;
	}
	
	private Problem generateProblemWithFixedFitness(double fitness) {
		Problem problem = mock(Problem.class);
		when(problem.calculateFitness(any())).thenReturn(fitness);
		return problem;
	}
	
	private Problem generateProblemWithFitnessAsSumOfGenomes() {
		Problem problem = mock(Problem.class);
		when(problem.calculateFitness(any(DNA.class))).thenAnswer(new Answer<Double>() {
			
			@Override
			public Double answer(InvocationOnMock invocation) throws Throwable {
				DNA dna = invocation.getArgument(0);
				double[] chromosome = dna.getDNACode();
				double sum = 0;
				for (double d : chromosome) {
					sum += d;
				}
				return sum;
			}
		});
		return problem;
	}
	
	private Heredity generateHeredityThatReturnsFatherCromosome() {
		Heredity heredity = mock(Heredity.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenAnswer(new Answer<DNA>() {
			
			@Override
			public DNA answer(InvocationOnMock invocation) throws Throwable {
				DNA fatherDna = invocation.getArgument(0);
				return fatherDna;
			}
		});
		return heredity;
	}
}