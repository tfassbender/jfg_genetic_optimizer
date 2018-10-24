package net.jfabricationgames.genetic_optimizer.optimizer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
		//ARRANGE
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
		
		//ACT
		optimizer.createInitialPopulation(population);
		
		//ASSERT
		//the population array is filled with the initial populations (except that the fitness is added)
		assertEquals(42d, population[0].getDNACode()[0], 1e-8);
		assertEquals(42d, population[1].getDNACode()[0], 1e-8);
		assertEquals(42d, population[2].getDNACode()[0], 1e-8);
		assertEquals(42d, population[3].getDNACode()[0], 1e-8);
		assertEquals(42d, population[4].getDNACode()[0], 1e-8);
	}
	
	@Test
	public void testCreateSortedInitialPopulation_shouldCreateAnInitialPopulationFromTheRootPopulationAndSortItByItsFitness() {
		//ARRANGE
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
		
		//ACT
		optimizer.createInitialPopulation(population);
		
		//ASSERT
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
		//ARRANGE
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
		
		//ACT
		optimizer.createInitialPopulation(population);
		
		//ASSERT
		//the population array is filled with the initial populations (except that the fitness is added)
		//the DNA with the lowest fitness is the first in the population because the optimizer minimizes by default
		assertEquals(46d, population[0].getDNACode()[0], 1e-8);
		assertEquals(45d, population[1].getDNACode()[0], 1e-8);
		assertEquals(44d, population[2].getDNACode()[0], 1e-8);
		assertEquals(43d, population[3].getDNACode()[0], 1e-8);
		assertEquals(42d, population[4].getDNACode()[0], 1e-8);
	}
	
	@Test
	public void testCreateInitialPopulationWithGenerator() {
		//ARRANGE
		Problem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = generateInitialPopulation(2, 5);//only two initial population chromosomes
		
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
		//a population of 5 is requested but only to are given
		DNA[] population = new DNA[5];
		//the other 3 chromosomes are created using the generator
		InitialDNAGenerator generator = mock(InitialDNAGenerator.class);
		when(generator.generateRandomDNA(5)).thenAnswer(new Answer<DNA>() {
			
			@Override
			public DNA answer(InvocationOnMock invocation) throws Throwable {
				DNA dna = new DNA(5);
				dna.getDNACode()[0] = 3;//return a chromosome with a specific first genome to identify it
				return dna;
			}
		});
		optimizer.setDnaGenerator(generator);
		
		//ACT
		optimizer.createInitialPopulation(population);
		
		//ASSERT
		//the population array is filled with the two initial populations and three generated chromsomes
		//the DNA with the lowest fitness is the first in the population because the optimizer minimizes by default
		assertEquals(3d, population[0].getDNACode()[0], 1e-8);
		assertEquals(3d, population[1].getDNACode()[0], 1e-8);
		assertEquals(3d, population[2].getDNACode()[0], 1e-8);
		assertEquals(42d, population[3].getDNACode()[0], 1e-8);
		assertEquals(43d, population[4].getDNACode()[0], 1e-8);
	}
	
	@Test
	public void testCreateInitialPopulationWithDefaultGenerator_shouldGenerateRandomValuesForEveryGenomeInASpecifiedRange() {
		//ARRANGE
		Problem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = new ArrayList<DNA>(0);//no initial population is given
		
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
		//a population of 50 is requested; all 50 chromosomes are created using the default generator of the DNA class
		DNA[] population = new DNA[50];
		//the range of the random values is set to 0.1
		optimizer.setRandomDNARange(0.1);
		
		//ACT
		//the population array is filled with the generated chromsomes
		optimizer.createInitialPopulation(population);
		
		//ASSERT
		//every DNA fitness has to be between 0 (inclusive) and 0.5 (exclusive) because the fitness is the sum of the genomes
		for (int i = 0; i < population.length; i++) {
			assertTrue(population[i].getFitness() >= 0);
			assertTrue(population[i].getFitness() < 0.5, "The population fitness should be lower than 0.5 but was " + population[i].getFitness());
		}
	}
	
	@Test
	public void testGenerateChilds_verifiesTheCorrectMethodCalls() {
		//ARRANGE
		Problem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = generateInitialPopulation(5, 5);
		//use a Heredity that just return the father chromosome (because something needs to be returned)
		Heredity heredity = generateHeredityThatReturnsFatherCromosome();
		List<Mutation> mutations = new ArrayList<Mutation>(3);
		for (int i = 0; i < 3; i++) {
			mutations.add(mock(Mutation.class));
		}
		int time = 1000;//1 second
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, initialPopulation, heredity, mutations, time);
		optimizer.setBestDNA(new DNA(5));//set any DNA to prevent NullPointerException
		DNA[] population = new DNA[5];
		//generate an initial population (that is the initial population that was given as parameter)
		optimizer.createInitialPopulation(population);
		
		//the space for the generated childs (bigger than the population to test the correct number of generated childs, and method calls)
		DNA[] childs = new DNA[7];
		
		//ACT
		optimizer.generateChilds(population, childs);
		
		//ASSERT
		//mixDNA is called for every chromosome in the childs array
		verify(heredity, times(childs.length)).mixDNA(any(DNA.class), any(DNA.class));
		for (Mutation mutation : mutations) {
			//every mutation has to be called once for every child
			verify(mutation, times(childs.length)).mutate(any(DNA.class));
		}
	}
	
	@Test
	public void testGenerateChildsUsesRightFatherChromosome() {
		//ARRANGE
		Problem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = generateInitialPopulation(5, 5);
		//set some values to the genomes of the initial population to make them sortable (using the sum of genomes as fitness)
		for (int i = 0; i < initialPopulation.size(); i++) {
			initialPopulation.get(i).getDNACode()[0] = 42 + i;
		}
		//use a Heredity that just return the father chromosome (because something needs to be returned)
		Heredity heredity = generateHeredityThatReturnsFatherCromosome();
		List<Mutation> mutations = new ArrayList<Mutation>(3);
		for (int i = 0; i < 3; i++) {
			mutations.add(mock(Mutation.class));
		}
		int time = 1000;//1 second
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, initialPopulation, heredity, mutations, time);
		optimizer.setBestDNA(new DNA(5));//set any DNA to prevent NullPointerException
		DNA[] population = new DNA[5];
		//generate an initial population (that is the initial population that was given as parameter)
		optimizer.createInitialPopulation(population);
		
		//the space for the generated childs (bigger than the population to test the correct number of generated childs, and method calls)
		DNA[] childs = new DNA[7];
		
		//ACT
		optimizer.generateChilds(population, childs);
		
		//ASSERT
		//the heredity always returns the father and the mutation does nothing -> the father chromosome is always the same and the childs are all the father chromosome
		ArgumentCaptor<DNA> argumentCaptorFatherChromosome = ArgumentCaptor.forClass(DNA.class);
		verify(heredity, times(childs.length)).mixDNA(argumentCaptorFatherChromosome.capture(), any(DNA.class));
		
		List<DNA> captured = argumentCaptorFatherChromosome.getAllValues();
		//the expected value is always the chromosome "population[0]"
		for (DNA dna : captured) {
			assertEquals(population[0], dna);
		}
		
		//all childs should be the father chromosome (because that's the one that is returned and there is no mutation)
		for (DNA child : childs) {
			assertEquals(population[0], child);
		}
	}
	
	@Test
	public void testChooseNextPopulation() {
		//ARRANGE
		GeneticOptimizer optimizer = generateDefaultGeneticOptimizer();
		
		DNA[] population = new DNA[] {new DNA(5), new DNA(5), new DNA(5)};
		DNA[] childs = new DNA[] {new DNA(5), new DNA(5), new DNA(5)};
		DNA[] nextPopulation = new DNA[3];
		
		population[0].setFitness(42);
		population[1].setFitness(45);
		population[2].setFitness(46);
		
		childs[0].setFitness(41);
		childs[1].setFitness(43);
		childs[2].setFitness(50);
		
		optimizer.chooseNextPopulation(population, childs, nextPopulation);
		
		DNA[] expectedNextPopulation = new DNA[] {childs[0], population[0], childs[1]};
		assertArrayEquals(expectedNextPopulation, nextPopulation);
	}
	
	@Test
	public void testChooseNextPopulationWithToBigArrayForNextPopulation_shouldThrowAnIndexOutOfBoundsException() {
		//ARRANGE
		GeneticOptimizer optimizer = generateDefaultGeneticOptimizer();
		
		DNA[] population = new DNA[] {new DNA(5), new DNA(5), new DNA(5)};
		DNA[] childs = new DNA[] {new DNA(5), new DNA(5), new DNA(5)};
		DNA[] nextPopulation = new DNA[10];//the next population has more elements than population and childs combined
		
		//ACT, ASSERT
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> optimizer.chooseNextPopulation(population, childs, nextPopulation));
	}
	
	private GeneticOptimizer generateDefaultGeneticOptimizer() {
		Problem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = Collections.emptyList();
		Heredity heredity = mock(Heredity.class);
		List<Mutation> mutations = Collections.emptyList();
		int time = 1000;
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, initialPopulation, heredity, mutations, time);
		
		return optimizer;
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