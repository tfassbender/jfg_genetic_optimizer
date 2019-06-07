package net.jfabricationgames.genetic_optimizer.optimizer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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

import net.jfabricationgames.genetic_optimizer.abort_condition.TimedAbortCondition;
import net.jfabricationgames.genetic_optimizer.heredity.Heredity;
import net.jfabricationgames.genetic_optimizer.mutation.Mutation;
import net.jfabricationgames.genetic_optimizer.selection.FitnessProportionalSelectionPressure;
import net.jfabricationgames.genetic_optimizer.selection.FitnessProportionalSelector;

class GeneticOptimizerTest {
	
	@Test
	public void testOptimizeWithFitnessAsSumOfGenomes_shouldMinimizeTheBestDNAToFitnessOfZero() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(10);
		//the other chromosomes are generated randomly (by DNA.generateRandomDNA(int, int))
		DNA optimalPart1 = new DNA(problem.getLength());
		DNA optimalPart2 = new DNA(problem.getLength());
		for (int i = 5; i < 10; i++) {
			optimalPart1.getDNACode()[i] = 1;//only the first half is optimal
		}
		for (int i = 0; i < 5; i++) {
			optimalPart2.getDNACode()[i] = 1;//only the second half is optimal
		}
		InitialDNAGenerator generator = generateDnaGeneratorThatReturnsSpecificDnaAt(new int[] {3, 4}, new DNA[] {optimalPart1, optimalPart2},
				problem.getLength(), 100);
		
		Heredity heredity = generateHeredityThatReturnsRandomCombination(0.5);
		List<Mutation> mutations = new ArrayList<Mutation>(1);
		Mutation mutation = (dna) -> {
			for (int i = 0; i < dna.getLength(); i++) {
				if (Math.random() < 0.01) {//1% chance of lowering the current genome to 0
					dna.getDNACode()[i] = 0;
				}
			}
		};
		mutations.add(mutation);
		int time = 50;//50 ms
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, 5, heredity, mutations, time);
		optimizer.setDnaGenerator(generator);
		
		//ACT
		optimizer.optimize();
		
		//ASSERT
		assertEquals(0, optimizer.getBestDNA().getFitness(), 1e-8,
				"The optimal fitness of 0 should be found as combination of optimalPart1 and optimalPart2");
	}
	
	@Test
	public void testOptimizeUsingMaximizationProblem_shouldFindTheOptimalFitnessOfZero() {
		//ARRANGE
		GeneticOptimizerProblem maximizationProblem = new GeneticOptimizerProblem() {
			
			private GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
			
			@Override
			public int getLength() {
				return 10;
			}
			@Override
			public double calculateFitness(DNA dna) {
				return -problem.calculateFitness(dna);
			}
		};
		//the other chromosomes are generated randomly (by DNA.generateRandomDNA(int, int))
		DNA optimalPart1 = new DNA(maximizationProblem.getLength());
		DNA optimalPart2 = new DNA(maximizationProblem.getLength());
		for (int i = 5; i < 10; i++) {
			optimalPart1.getDNACode()[i] = 1;//only the first half is optimal
		}
		for (int i = 0; i < 5; i++) {
			optimalPart2.getDNACode()[i] = 1;//only the second half is optimal
		}
		InitialDNAGenerator generator = generateDnaGeneratorThatReturnsSpecificDnaAt(new int[] {3, 4}, new DNA[] {optimalPart1, optimalPart2},
				maximizationProblem.getLength(), 100);
		
		Heredity heredity = generateHeredityThatReturnsRandomCombination(0.5);
		List<Mutation> mutations = new ArrayList<Mutation>(1);
		Mutation mutation = (dna) -> {
			for (int i = 0; i < dna.getLength(); i++) {
				if (Math.random() < 0.01) {//1% chance of lowering the current genome to 0
					dna.getDNACode()[i] = 0;
				}
			}
		};
		mutations.add(mutation);
		int time = 50;//50 ms
		
		GeneticOptimizer optimizer = new GeneticOptimizer(maximizationProblem, 5, heredity, mutations, time);
		optimizer.setDnaGenerator(generator);
		optimizer.setMinimize(false);
		optimizer.setElites(2);
		
		//ACT
		optimizer.optimize();
		
		//ASSERT
		assertEquals(0, optimizer.getBestDNA().getFitness(), 1e-8,
				"The optimal fitness of 0 should be found as combination of optimalPart1 and optimalPart2");
	}
	
	@Test
	public void testOptimizeUsingOnlyMutation_shouldFindAnOptimalSolution() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		InitialDNAGenerator generator = (size) -> {
			DNA dna = new DNA(size);
			for (int i = 0; i < size; i++) {
				dna.getDNACode()[i] = Math.random() + 1;
			}
			return dna;
		};
		
		Heredity heredity = generateHeredityThatReturnsFatherCromosome();
		List<Mutation> mutations = new ArrayList<Mutation>(1);
		Mutation mutation = (dna) -> {
			for (int i = 0; i < dna.getLength(); i++) {
				if (Math.random() < 0.05) {//5% chance of lowering the current genome to 0
					dna.getDNACode()[i] = 0;
				}
			}
		};
		mutations.add(mutation);
		int time = 50;//50 ms
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, 5, heredity, mutations, time);
		optimizer.setDnaGenerator(generator);
		
		//ACT
		optimizer.optimize();
		
		//ASSERT
		assertEquals(0, optimizer.getBestDNA().getFitness(), 1e-8, "The optimal fitness of 0 should be found by the mutations");
	}
	
	@Test
	public void testCreateInitialPopulationFromRootPopulation_shouldCreateAnInitialPopulationFromTheRootPopulation() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFixedFitness(42d);
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
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
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
	public void testCreateInitialPopulationWithMaximizationProblem_shouldCreateAnInitialPopulationFromTheRootPopulation() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
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
		assertEquals(42d, population[0].getDNACode()[0], 1e-8);
		assertEquals(43d, population[1].getDNACode()[0], 1e-8);
		assertEquals(44d, population[2].getDNACode()[0], 1e-8);
		assertEquals(45d, population[3].getDNACode()[0], 1e-8);
		assertEquals(46d, population[4].getDNACode()[0], 1e-8);
	}
	
	@Test
	public void testCreateInitialPopulationWithGenerator() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
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
		assertEquals(42d, population[0].getDNACode()[0], 1e-8);
		assertEquals(43d, population[1].getDNACode()[0], 1e-8);
		assertEquals(3d, population[2].getDNACode()[0], 1e-8);
		assertEquals(3d, population[3].getDNACode()[0], 1e-8);
		assertEquals(3d, population[4].getDNACode()[0], 1e-8);
	}
	
	@Test
	public void testCreateInitialPopulationWithDefaultGenerator_shouldGenerateRandomValuesForEveryGenomeInASpecifiedRange() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = new ArrayList<DNA>(0);//no initial population is given
		
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
	public void testGenerateNextPopulation_verifiesTheCorrectMethodCalls() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
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
		DNA[] childs = new DNA[5];
		
		//ACT
		optimizer.generateNextPopulation(new int[10], population, childs);
		
		//ASSERT
		//mixDNA is called for every chromosome in the childs array
		verify(heredity, times(childs.length)).mixDNA(any(DNA.class), any(DNA.class));
		for (Mutation mutation : mutations) {
			//every mutation has to be called once for every child
			verify(mutation, times(childs.length)).mutate(any(DNA.class));
		}
	}
	
	@Test
	public void testGenerateNextPopulationUsesRightFatherChromosome() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
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
		DNA[] childs = new DNA[5];
		
		//ACT
		optimizer.generateNextPopulation(new int[10], population, childs);
		
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
	public void testGenerateChilds_noHeredityOfSameChromosomes() {
		//ARRANGE
		GeneticOptimizerProblem problem = generateProblemWithFitnessAsSumOfGenomes();
		when(problem.getLength()).thenReturn(5);
		List<DNA> initialPopulation = generateInitialPopulation(2, 5);//small population for testing
		//use a Heredity that throws an exception when the father and mother chromosomes are the same (using ==, not equals)
		Heredity heredity = generateHeredityThatNoticesSameChromosomes();
		List<Mutation> mutations = Collections.emptyList();
		int time = 1000;//1 second
		
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, initialPopulation, heredity, mutations, time);
		optimizer.setBestDNA(new DNA(5));//set any DNA to prevent NullPointerException
		DNA[] population = new DNA[2];
		optimizer.createInitialPopulation(population);
		DNA[] childs = new DNA[2];
		
		//for testing the heredity
		DNA dna = new DNA(5);
		
		//ACT, ASSERT
		try {
			//the test will fail with an IllegalStateException when the same chromosomes are used for father and mother
			optimizer.generateNextPopulation(new int[] {0, 1, 0, 1}, population, childs);
		}
		catch (IllegalStateException ise) {
			fail("The generateChilds method should not have thrown an IllegalStateException.");
		}
		assertThrows(IllegalStateException.class, () -> heredity.mixDNA(dna, dna));
	}
	
	@Test
	public void testGenerateNextPopulation() {
		int populationSize = 5;
		int dnaLength = 5;
		
		GeneticOptimizerBuilder builder = generateDefaultBuilder();
		builder.setHeredity(generateHeredityThatReturnsFatherCromosome()).setMutations(new ArrayList<Mutation>(0)).setUseLocalElitism(false)
				.setElites(0).setPopulationSize(populationSize);
		GeneticOptimizer optimizer = builder.build();
		
		DNA[] population = new DNA[populationSize];
		DNA[] nextPopulation = new DNA[populationSize];
		
		for (int i = 0; i < population.length; i++) {
			double[] dnaCode = new double[dnaLength];
			for (int j = 0; j < dnaCode.length; j++) {
				dnaCode[j] = Math.random();
			}
			population[i] = new DNA(dnaCode);
		}
		
		int[] selectedReproductionIndividuals = new int[] {0, 1, 0, 2, 0, 0, 1, 2, 4, 3};
		DNA[] expectedNextPopulation = new DNA[] {population[0], population[0], population[0], population[1], population[4]};
		
		optimizer.generateNextPopulation(selectedReproductionIndividuals, population, nextPopulation);
		
		assertArrayEquals(expectedNextPopulation, nextPopulation);
	}
	
	@Test
	public void testAddIndividual() {
		GeneticOptimizerBuilder builder = generateDefaultBuilder();
		GeneticOptimizer optimizer = builder.build();
		GeneticOptimizer localElitismOptimizer = builder.setUseLocalElitism(true).build();
		
		DNA[] population = new DNA[4];
		
		DNA father = new DNA(0);
		DNA mother = new DNA(0);
		DNA child = new DNA(0);
		father.setFitness(10);
		mother.setFitness(42);
		child.setFitness(5);
		
		optimizer.addIndividual(father, mother, child, population, 0);
		localElitismOptimizer.addIndividual(father, mother, child, population, 1);
		father.setFitness(43);
		localElitismOptimizer.addIndividual(father, mother, child, population, 2);
		child.setFitness(44);
		localElitismOptimizer.addIndividual(father, mother, child, population, 3);
		
		assertArrayEquals(new DNA[] {child, mother, father, child}, population);
	}
	
	@Test
	public void testAddElites() {
		int populationSize = 5;
		int dnaLength = 5;
		int elites = 1;
		
		GeneticOptimizerBuilder builder = generateDefaultBuilder();
		builder.setHeredity(generateHeredityThatReturnsFatherCromosome()).setMutations(new ArrayList<Mutation>(0)).setUseLocalElitism(false)
				.setElites(elites).setPopulationSize(populationSize).setMinimize(true);
		GeneticOptimizer optimizer = builder.build();
		
		DNA[] population = new DNA[populationSize];
		DNA[] nextPopulation = new DNA[populationSize];
		
		for (int i = 0; i < population.length - 1; i++) {
			double[] dnaCode = new double[dnaLength];
			for (int j = 0; j < dnaCode.length; j++) {
				dnaCode[j] = Math.random();
			}
			population[i] = new DNA(dnaCode);
		}
		population[populationSize - 1] = new DNA(new double[] {0, 0, 0, 0, 0});//optimal solution
		
		//calculate the fitness of the population
		for (int i = 0; i < population.length; i++) {
			population[i].setFitness(optimizer.getProblem().calculateFitness(population[i]));
		}
		
		//only the elites are added
		DNA[] expectedNextPopulation = new DNA[] {null, null, null, null, population[4]};
		
		optimizer.addElites(population, nextPopulation);
		
		assertArrayEquals(expectedNextPopulation, nextPopulation);
	}
	
	@Test
	public void testInterruptThread() {
		GeneticOptimizerBuilder builder = generateDefaultBuilder();
		builder.setAbortCondition(new TimedAbortCondition(1000));//run 1 second
		builder.setPopulationSize(50).setProblem(generateProblemWithFitnessAsSumOfGenomes(5));
		
		GeneticOptimizer optimizer = builder.build();
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				optimizer.optimize();
			}
		});
		thread.start();
		long startingTime = System.currentTimeMillis();
		
		//interrupting the thread should lead to the interrupting of the calculation 
		thread.interrupt();
		try {
			//wait for the calculation thread to join
			thread.join();
		}
		catch (InterruptedException ie) {
			fail("Interrupted while waiting for joining thread: " + ie.getMessage());
		}
		
		long timeUsed = System.currentTimeMillis() - startingTime;
		
		//assert that the calculation thread did not take the whole second of calculation time because it got interrupted
		assertTrue(timeUsed < 50);
	}
	
	private GeneticOptimizerBuilder generateDefaultBuilder() {
		GeneticOptimizerBuilder builder = new GeneticOptimizerBuilder();
		builder.setProblem(generateProblemWithFitnessAsSumOfGenomes()).setHeredity(generateHeredityThatReturnsFatherCromosome())
				.setMutations(new ArrayList<Mutation>(0)).setAbortCondition(new TimedAbortCondition(100))
				.setSelectionPressure(new FitnessProportionalSelectionPressure()).setSelector(new FitnessProportionalSelector())
				.setRootPopulation(generateInitialPopulation(5, 5));
		
		return builder;
	}
	
	private List<DNA> generateInitialPopulation(int populationSize, int dnaSize) {
		List<DNA> initialPopulation = new ArrayList<DNA>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			initialPopulation.add(new DNA(dnaSize));
		}
		return initialPopulation;
	}
	
	private GeneticOptimizerProblem generateProblemWithFixedFitness(double fitness) {
		GeneticOptimizerProblem problem = mock(GeneticOptimizerProblem.class);
		when(problem.calculateFitness(any())).thenReturn(fitness);
		return problem;
	}
	
	private GeneticOptimizerProblem generateProblemWithFitnessAsSumOfGenomes() {
		return generateProblemWithFitnessAsSumOfGenomes(0);
	}
	private GeneticOptimizerProblem generateProblemWithFitnessAsSumOfGenomes(int dnaSize) {
		GeneticOptimizerProblem problem = mock(GeneticOptimizerProblem.class);
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
		when(problem.getLength()).thenReturn(dnaSize);
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
	
	private Heredity generateHeredityThatNoticesSameChromosomes() {
		Heredity heredity = mock(Heredity.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenAnswer(new Answer<DNA>() {
			
			@Override
			public DNA answer(InvocationOnMock invocation) throws Throwable {
				DNA fatherDna = invocation.getArgument(0);
				DNA motherDna = invocation.getArgument(1);
				if (fatherDna == motherDna) {
					throw new IllegalStateException("Father and Mother chromosomes mussn't be the same instance.");
				}
				return fatherDna;
			}
		});
		return heredity;
	}
	
	private Heredity generateHeredityThatReturnsRandomCombination(double probabilityFather) {
		Heredity heredity = mock(Heredity.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenAnswer(new Answer<DNA>() {
			
			@Override
			public DNA answer(InvocationOnMock invocation) throws Throwable {
				DNA father = invocation.getArgument(0);
				DNA mother = invocation.getArgument(1);
				DNA dna = new DNA(father.getLength());
				double[][] chromosomes = new double[][] {father.getDNACode(), mother.getDNACode()};
				double[] dnaCode = dna.getDNACode();
				for (int i = 0; i < father.getLength(); i++) {
					int selection;
					if (Math.random() < probabilityFather) {
						selection = 0;
					}
					else {
						selection = 1;
					}
					dnaCode[i] = chromosomes[selection][i];
				}
				return dna;
			}
		});
		return heredity;
	}
	
	/**
	 * A generator that returns DNA.generateRandomDNA(dnaLen, randomDnaRange) except for specified positions where a specified DNA is returned.
	 */
	private InitialDNAGenerator generateDnaGeneratorThatReturnsSpecificDnaAt(final int[] pos, final DNA[] dna, final int dnaLen,
			final int randomDnaRange) {
		InitialDNAGenerator generator = mock(InitialDNAGenerator.class);
		when(generator.generateRandomDNA(any(Integer.class))).thenAnswer(new Answer<DNA>() {
			
			private int callNum = -1;//to start with index 0
			
			@Override
			public DNA answer(InvocationOnMock invocation) throws Throwable {
				callNum++;
				for (int i = 0; i < pos.length; i++) {
					if (callNum == pos[i]) {
						return dna[i];
					}
				}
				return DNA.generateRandomDNA(dnaLen, randomDnaRange);
			}
		});
		return generator;
	}
}