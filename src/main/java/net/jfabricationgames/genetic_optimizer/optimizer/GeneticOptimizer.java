package net.jfabricationgames.genetic_optimizer.optimizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.abort_condition.AbortCondition;
import net.jfabricationgames.genetic_optimizer.abort_condition.TimedAbortCondition;
import net.jfabricationgames.genetic_optimizer.heredity.Heredity;
import net.jfabricationgames.genetic_optimizer.mutation.Mutation;
import net.jfabricationgames.genetic_optimizer.selection.EquallyDistributedSelectionPressure;
import net.jfabricationgames.genetic_optimizer.selection.FitnessProportionalSelectionPressure;
import net.jfabricationgames.genetic_optimizer.selection.SelectionPressure;
import net.jfabricationgames.genetic_optimizer.selection.Selector;
import net.jfabricationgames.genetic_optimizer.selection.StochasticallyDistributedSelector;

/**
 * Solve an optimization problem using a genetic search algorithm.
 */
public class GeneticOptimizer {
	
	private GeneticOptimizerProblem problem;
	private Heredity heredity;
	private Selector selector;
	private SelectionPressure selectionPressure;
	private List<Mutation> mutations;
	private List<DNA> rootPopulation;
	//private int optimizationTime; //replaced with abortCondition
	private int populationSize;
	private boolean useLocalElitism;//local elitism to choose the best individual when reproducing
	private int elites;//global used elites to not loose the best individuals (regardless of generation)
	
	//private int simulations;
	private int generation;
	
	/**
	 * Indicates whether the problem is a minimization (default) or maximization problem
	 */
	private boolean minimize = true;
	/**
	 * A coefficient that tells which part of the population can be chosen as father (e.g. 0.1 would be the best 10% of the population)
	 */
	@Deprecated
	private double fathersFraction = 0.15;
	/**
	 * The range a DNA has when randomly initialized {@see DNA.generateRandomDNA(int lenght, double range)}.
	 */
	private double randomDNARange = 1;
	/**
	 * A random generator to generate better random DNAs that the default implementation.
	 */
	private InitialDNAGenerator dnaGenerator;
	/**
	 * The condition that has to be reached to stop the optimization.
	 */
	private AbortCondition abortCondition;
	
	private DNA bestDNA;
	
	/**
	 * @param problem
	 *        A wrapper implementation for the problem that calculates the fitness of a DNA.
	 * 
	 * @param rootPopulation
	 *        Some root DNAs. If none are given a random DNA is used.
	 * 
	 * @param heredity
	 *        The heredity for this optimization.
	 * 
	 * @param mutations
	 *        A list of mutations for the optimization.
	 * 
	 * @param optimizationTime
	 *        The optimization time in milliseconds
	 */
	public GeneticOptimizer(GeneticOptimizerProblem problem, List<DNA> rootPopulation, Heredity heredity, List<Mutation> mutations,
			int optimizationTime) {
		this.problem = problem;
		this.rootPopulation = rootPopulation;
		this.populationSize = rootPopulation.size();
		this.heredity = heredity;
		this.mutations = mutations;
		this.abortCondition = new TimedAbortCondition(optimizationTime);
		
		selectionPressure = new EquallyDistributedSelectionPressure();
		selector = new StochasticallyDistributedSelector();
	}
	/**
	 * @param problem
	 *        A wrapper implementation for the problem that calculates the fitness of a DNA.
	 * 
	 * @param populationSize
	 *        The size of the population that should be used.
	 * 
	 * @param heredity
	 *        The heredity for this optimization.
	 * 
	 * @param mutations
	 *        A list of mutations for the optimization.
	 * 
	 * @param optimizationTime
	 *        The optimization time in milliseconds
	 */
	public GeneticOptimizer(GeneticOptimizerProblem problem, int populationSize, Heredity heredity, List<Mutation> mutations, int optimizationTime) {
		this.problem = problem;
		this.rootPopulation = Collections.emptyList();
		this.populationSize = populationSize;
		this.heredity = heredity;
		this.mutations = mutations;
		this.abortCondition = new TimedAbortCondition(optimizationTime);
		
		selectionPressure = new EquallyDistributedSelectionPressure();
		selector = new StochasticallyDistributedSelector();
	}
	/**
	 * @param problem
	 *        A wrapper implementation for the problem that calculates the fitness of a DNA.
	 * 
	 * @param populationSize
	 *        The size of the population that should be used.
	 * 
	 * @param heredity
	 *        The heredity for this optimization.
	 * 
	 * @param mutations
	 *        A list of mutations for the optimization.
	 * 
	 * @param abortCondition
	 *        The condition for the optimization to terminate.
	 */
	public GeneticOptimizer(GeneticOptimizerProblem problem, int populationSize, Heredity heredity, List<Mutation> mutations,
			AbortCondition abortCondition) {
		this.problem = problem;
		this.rootPopulation = Collections.emptyList();
		this.populationSize = populationSize;
		this.heredity = heredity;
		this.mutations = mutations;
		this.abortCondition = abortCondition;
	}
	/**
	 * Used only for the builder pattern.
	 */
	protected GeneticOptimizer(GeneticOptimizerProblem problem, int populationSize, InitialDNAGenerator dnaGenerator, List<DNA> rootPopulation,
			Heredity heredity, List<Mutation> mutations, AbortCondition abortCondition, SelectionPressure selectionPressure, Selector selector,
			double fathersFraction, boolean minimize, boolean useLocalElitism, int elites) throws IllegalArgumentException, NullPointerException {
		Objects.requireNonNull(problem, "The problem mussn't be null.");
		Objects.requireNonNull(heredity, "Heredity mussn't be null.");
		Objects.requireNonNull(mutations, "Mutations mussn't be null. Use an empty list if you don't want any mutations.");
		Objects.requireNonNull(abortCondition, "The abort condition mussn't be null.");
		if (populationSize <= 0 && (rootPopulation == null || rootPopulation.isEmpty())) {
			throw new IllegalArgumentException("Either a rootPopulation or a populationSize greater than 0 must be specified.");
		}
		
		this.problem = problem;
		this.rootPopulation = rootPopulation;
		this.populationSize = populationSize;
		this.dnaGenerator = dnaGenerator;
		this.heredity = heredity;
		this.mutations = mutations;
		this.abortCondition = abortCondition;
		this.selectionPressure = selectionPressure;
		this.selector = selector;
		//this.fathersFraction = fathersFraction;
		this.minimize = minimize;
		this.useLocalElitism = useLocalElitism;
		this.elites = elites;
		
		if (rootPopulation == null) {
			rootPopulation = Collections.emptyList();
		}
		else if (populationSize <= 0) {
			populationSize = rootPopulation.size();
		}
	}
	
	public void optimize() {
		long start = System.nanoTime();
		long timeUsed = 0;
		
		DNA[] population = new DNA[populationSize];
		//DNA[] childs = new DNA[populationSize];
		DNA[] nextPopulation = new DNA[populationSize];
		
		//create an empty best DNA
		bestDNA = new DNA(problem.getLength());
		if (minimize) {
			bestDNA.setFitness(Double.POSITIVE_INFINITY);
		}
		else {
			bestDNA.setFitness(Double.NEGATIVE_INFINITY);
		}
		
		createInitialPopulation(population);
		
		generation = 0;
		while (!abortCondition.abort(bestDNA, timeUsed)) {
			//generateChilds(population, childs);
			//chooseNextPopulation(population, childs, nextPopulation);
			
			//calculate the chance of each individual to be selected for reproduction
			double[] reproductionProbabilities = selectionPressure.calculateSelectionProbability(population, generation, minimize, timeUsed);
			//choose the individuals that are selected for reproduction
			int[] selectedReproductionIndividuals = selector.select(reproductionProbabilities, populationSize - elites);
			//create the next generation of individuals
			generateNextPopulation(selectedReproductionIndividuals, population, nextPopulation);
			
			//swap the arrays to reuse the allocated space
			DNA[] tmp = population;
			population = nextPopulation;
			nextPopulation = tmp;
			
			//check whether there is a new optimal DNA
			for (int i = 0; i < populationSize; i++) {
				if (isBestDNA(population[i])) {
					population[i].copyTo(bestDNA);
				}
			}
			
			generation++;
			timeUsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		}
	}
	
	private boolean isBestDNA(DNA dna) {
		if (minimize) {
			return dna.getFitness() < bestDNA.getFitness();
		}
		else {
			return dna.getFitness() > bestDNA.getFitness();
		}
	}
	
	@VisibleForTesting
	/*private*/ static void reverse(DNA[] dnas) {
		int len = dnas.length;
		for (int i = 0; i < len / 2; i++) {
			DNA temp = dnas[i];
			dnas[i] = dnas[len - i - 1];
			dnas[len - i - 1] = temp;
		}
	}
	
	@VisibleForTesting
	/*private*/ void createInitialPopulation(DNA[] population) {
		//create the initial population by the rootPopulation or generate a random population
		for (int i = 0; i < population.length; i++) {
			DNA dna;
			if (i < rootPopulation.size()) {
				dna = new DNA(problem.getLength());
				rootPopulation.get(i).copyTo(dna);
			}
			else if (dnaGenerator != null) {
				dna = dnaGenerator.generateRandomDNA(problem.getLength());
			}
			else {
				dna = DNA.generateRandomDNA(problem.getLength(), randomDNARange);
			}
			
			dna.setFitness(problem.calculateFitness(dna));
			population[i] = dna;
			
			if (isBestDNA(dna)) {
				dna.copyTo(bestDNA);
			}
		}
		//sort the initial population
		Arrays.sort(population);
		if (!minimize) {
			reverse(population);
		}
	}
	
	@VisibleForTesting
	/*private*/ void generateNextPopulation(int[] selectedReproductionIndividuals, DNA[] population, DNA[] nextPopulation) {
		//leave some spaces for the elites from the last population
		for (int i = 0; i < populationSize - elites; i++) {
			int fatherIndex = selectedReproductionIndividuals[2 * i];
			int motherIndex = selectedReproductionIndividuals[2 * i + 1];
			
			DNA father = population[fatherIndex];
			DNA mother = population[motherIndex];
			
			//create a child by mixing the DNA
			DNA child = heredity.mixDNA(father, mother);
			
			for (Mutation mutation : mutations) {
				//mutate the child to build new solutions
				mutation.mutate(child);
			}
			
			//calculate and set the fitness of the child
			child.setFitness(problem.calculateFitness(child));
			
			//add the individual to the next population using the chosen settings
			addIndividual(father, mother, child, nextPopulation, i);
		}
		
		//add the elites from the last population to the next population
		addElites(population, nextPopulation);
	}
	
	@VisibleForTesting
	/*private*/ void addIndividual(DNA father, DNA mother, DNA child, DNA[] nextPopulation, int i) {
		if (useLocalElitism) {
			//when local elitism is used only the best DNA (of [father, mother, child]) is added in the next population
			if (child.getFitness() > father.getFitness()) {
				if (child.getFitness() > mother.getFitness()) {
					//the child has the best fitness
					nextPopulation[i] = child;
				}
				else {
					//the mother has the best fitness
					nextPopulation[i] = mother;
				}
			}
			else {
				if (father.getFitness() > mother.getFitness()) {
					//the father has the best fitness
					nextPopulation[i] = father;
				}
				else {
					//the mother has the best fitness
					nextPopulation[i] = mother;
				}
			}
		}
		else {
			//no local elitism -> just add the child
			nextPopulation[i] = child;
		}
	}
	
	@VisibleForTesting
	/*private*/ void addElites(DNA[] population, DNA[] nextPopulation) {
		if (elites > 0) {
			DNA[] eliteIndividuals = new DNA[elites];
			if (minimize) {
				//initialize with maximum
				for (int i = 0; i < elites; i++) {
					eliteIndividuals[i] = new DNA(0);
					eliteIndividuals[i].setFitness(Double.POSITIVE_INFINITY);
				}
				
				//find the best individuals from the last generation
				for (int i = 0; i < population.length; i++) {
					for (int j = 0; j < elites; j++) {
						if (population[i].getFitness() < eliteIndividuals[j].getFitness()) {
							for (int k = eliteIndividuals.length - 2; k >= 0; k--) {
								eliteIndividuals[k + 1] = eliteIndividuals[k];
							}
							eliteIndividuals[j] = population[i];
							break;
						}
					}
				}
			}
			else {
				//initialize with minimum
				for (int i = 0; i < elites; i++) {
					eliteIndividuals[i] = new DNA(0);
					eliteIndividuals[i].setFitness(Double.NEGATIVE_INFINITY);
				}
				
				//find the best individuals from the last generation
				for (int i = 0; i < population.length; i++) {
					for (int j = 0; j < elites; j++) {
						if (population[i].getFitness() > eliteIndividuals[j].getFitness()) {
							for (int k = eliteIndividuals.length - 2; k >= 0; k--) {
								eliteIndividuals[k + 1] = eliteIndividuals[k];
							}
							eliteIndividuals[j] = population[i];
							break;
						}
					}
				}
			}
			
			//add the elites to the next population (append on the end)
			for (int i = 0; i < elites; i++) {
				nextPopulation[i + populationSize - elites] = eliteIndividuals[i];
			}
		}
	}
	
	public DNA getBestDNA() {
		return bestDNA;
	}
	@VisibleForTesting
	void setBestDNA(DNA dna) {
		bestDNA = dna;
	}
	
	public boolean isMinimize() {
		return minimize;
	}
	public void setMinimize(boolean minimize) {
		this.minimize = minimize;
	}
	
	@Deprecated
	public double getFathersFraction() {
		return fathersFraction;
	}
	@Deprecated
	public void setFathersFraction(double fathersFraction) {
		this.fathersFraction = fathersFraction;
	}
	
	public double getRandomDNARange() {
		return randomDNARange;
	}
	public void setRandomDNARange(double randomDNARange) {
		this.randomDNARange = randomDNARange;
	}
	
	public InitialDNAGenerator getDnaGenerator() {
		return dnaGenerator;
	}
	public void setDnaGenerator(InitialDNAGenerator dnaGenerator) {
		this.dnaGenerator = dnaGenerator;
	}
	
	@VisibleForTesting
	/*public*/ void setElites(int elites) {
		this.elites = elites;
	}
	public GeneticOptimizerProblem getProblem() {
		return problem;
	}
	
	public Heredity getHeredity() {
		return heredity;
	}
	
	public List<Mutation> getMutations() {
		return mutations;
	}
	
	public int getGeneration() {
		return generation;
	}
}