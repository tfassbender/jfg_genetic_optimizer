package net.jfabricationgames.genetic_optimizer.optimizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.heredity.Heredity;
import net.jfabricationgames.genetic_optimizer.mutation.Mutation;

/**
 * Solve an optimization problem using a genetic search algorithm.
 */
public class GeneticOptimizer {
	
	private Problem problem;
	private Heredity heredity;
	private List<Mutation> mutations;
	private List<DNA> rootPopulation;
	private int optimizationTime;
	private int populationSize;
	
	private int simulations;
	
	/**
	 * Indicates whether the problem is a minimization (default) or maximization problem
	 */
	private boolean minimize = true;
	/**
	 * A coefficient that tells which part of the population can be chosen as father (e.g. 0.1 would be the best 10% of the population)
	 */
	private double fathersFraction = 0.15;
	/**
	 * The range a DNA has when randomly initialized {@see DNA.generateRandomDNA(int lenght, double range)}.
	 */
	private double randomDNARange = 1;
	/**
	 * A random generator to generate better random DNAs that the default implementation.
	 */
	private InitialDNAGenerator dnaGenerator;
	
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
	public GeneticOptimizer(Problem problem, List<DNA> rootPopulation, Heredity heredity, List<Mutation> mutations, int optimizationTime) {
		this.problem = problem;
		this.rootPopulation = rootPopulation;
		this.populationSize = rootPopulation.size();
		this.heredity = heredity;
		this.mutations = mutations;
		this.optimizationTime = optimizationTime;
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
	public GeneticOptimizer(Problem problem, int populationSize, Heredity heredity, List<Mutation> mutations, int optimizationTime) {
		this.problem = problem;
		this.rootPopulation = Collections.emptyList();
		this.populationSize = populationSize;
		this.heredity = heredity;
		this.mutations = mutations;
		this.optimizationTime = optimizationTime;
	}
	
	public void optimize() {
		long start = System.nanoTime();
		
		DNA[] population = new DNA[populationSize];
		DNA[] childs = new DNA[populationSize];
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
		
		simulations = 0;
		while (TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < optimizationTime) {
			generateChilds(population, childs);
			
			chooseNextPopulation(population, childs, nextPopulation);
			
			//swap the arrays to reuse the allocated space
			DNA[] tmp = population;
			population = nextPopulation;
			nextPopulation = tmp;
			
			//check whether the best DNA in the population is better than the current best DNA
			if (isBestDNA(population[0])) {
				population[0].copyTo(bestDNA);
			}
			
			simulations++;
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
	/*private*/ void generateChilds(DNA[] population, DNA[] childs) {
		//create new childs
		for (int i = 0; i < childs.length; i++) {
			//choose one of the best DNAs as father
			int fatherIndex = (int) (Math.random() * population.length * fathersFraction);
			DNA father = population[fatherIndex];
			//choose a mother DNA randomly
			int motherIndex = (int) (Math.random() * population.length - 1);
			if (motherIndex >= fatherIndex) {
				motherIndex++;
			}
			DNA mother = population[motherIndex];
			
			DNA child = heredity.mixDNA(father, mother);
			
			for (Mutation mutation : mutations) {
				//mutate the child to build new solutions
				mutation.mutate(child);
			}
			
			childs[i] = child;
		}
		
		//calculate the childs' fitness
		for (int i = 0; i < population.length; i++) {
			childs[i].setFitness(problem.calculateFitness(childs[i]));
		}
		//sort the childs by their fitness
		Arrays.sort(childs);
		if (!minimize) {
			reverse(childs);
		}
	}
	
	@VisibleForTesting
	/*private*/ void chooseNextPopulation(DNA[] population, DNA[] childs, DNA[] nextPopulation) {
		//choose the best DNA for the next population
		int i = 0;
		int j = 0;
		if (minimize) {
			for (int d = 0; d < nextPopulation.length; d++) {
				if (population[i].getFitness() < childs[j].getFitness()) {
					nextPopulation[d] = population[i];
					i++;
				}
				else {
					nextPopulation[d] = childs[j];
					j++;
				}
			}
		}
		else {
			for (int d = 0; d < nextPopulation.length; d++) {
				if (population[i].getFitness() > childs[j].getFitness()) {
					nextPopulation[d] = population[i];
					i++;
				}
				else {
					nextPopulation[d] = childs[j];
					j++;
				}
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
	
	public double getFathersFraction() {
		return fathersFraction;
	}
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
	
	public Problem getProblem() {
		return problem;
	}
	
	public Heredity getHeredity() {
		return heredity;
	}
	
	public List<Mutation> getMutations() {
		return mutations;
	}
	
	public int getSimulations() {
		return simulations;
	}
}