package net.jfabricationgames.genetic_optimizer.optimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import net.jfabricationgames.genetic_optimizer.abort_condition.AbortCondition;
import net.jfabricationgames.genetic_optimizer.abort_condition.TimedAbortCondition;
import net.jfabricationgames.genetic_optimizer.heredity.Heredity;
import net.jfabricationgames.genetic_optimizer.mutation.Mutation;
import net.jfabricationgames.genetic_optimizer.selection.EquallyDistributedSelectionPressure;
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
	
	private int usedThreads;//the number of threads used for the calculation
	private ExecutorService executorService;//an executor service for multi-threading
	
	private DoubleProperty progressProperty;//indicates the progress of the calculation (from 0 to 1)
	
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
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public GeneticOptimizer() {
		
	}
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
		progressProperty = new SimpleDoubleProperty(0, "GeneticOptimizerCalculationProgress");
		usedThreads = 1;//no multi-threading by default
		executorService = Executors.newFixedThreadPool(usedThreads);
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
		progressProperty = new SimpleDoubleProperty(0, "GeneticOptimizerCalculationProgress");
		usedThreads = 1;//no multi-threading by default
		executorService = Executors.newFixedThreadPool(usedThreads);
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
		progressProperty = new SimpleDoubleProperty(0, "GeneticOptimizerCalculationProgress");
		usedThreads = 1;//no multi-threading by default
		executorService = Executors.newFixedThreadPool(usedThreads);
	}
	/**
	 * Used only for the builder pattern.
	 * 
	 * @param problem
	 *        A wrapper implementation for the problem that calculates the fitness of a DNA.
	 * 
	 * @param populationSize
	 *        The size of the population that is generated, selected, mutated, ...
	 * 
	 * @param dnaGenerator
	 *        A generator for the initial DNA (if no root population is used).
	 * 
	 * @param rootPopulation
	 *        The root population that is used to start the optimization.
	 * 
	 * @param heredity
	 *        The heredity method that is used to combine two individuals to a new individual.
	 * 
	 * @param mutations
	 *        The mutations that are used to change the new individuals that are created using the heredity (all mutations are applied).
	 * 
	 * @param abortCondition
	 *        The condition to let the algorithm terminate (usually after some time or when a fitness threshold is reached).
	 * 
	 * @param selectionPressure
	 *        A selection pressure to calculate the probability to be selected for reproduction based on the fitness.
	 * 
	 * @param selector
	 *        The selector that selects the individuals for reproduction (usually based on the probabilities that were calculated by the
	 *        selectionPressure).
	 * 
	 * @param fathersFraction
	 *        A deprecated variable that isn't really used. Just for the backwards compatibility.
	 * 
	 * @param minimize
	 *        Determines whether the fitness of the individuals should be minimized (true) or maximized (false).
	 * 
	 * @param useLocalElitism
	 *        Determines whether local elitism should be used.
	 * 
	 * @param elites
	 *        The number of elite individuals that are copied from the last population to the new one to prevent loosing the best individuals.
	 */
	protected GeneticOptimizer(GeneticOptimizerProblem problem, int populationSize, InitialDNAGenerator dnaGenerator, List<DNA> rootPopulation,
			Heredity heredity, List<Mutation> mutations, AbortCondition abortCondition, SelectionPressure selectionPressure, Selector selector,
			double fathersFraction, boolean minimize, boolean useLocalElitism, int elites, int usedThreads)
			throws IllegalArgumentException, NullPointerException {
		Objects.requireNonNull(problem, "The problem mussn't be null.");
		Objects.requireNonNull(heredity, "Heredity mussn't be null.");
		Objects.requireNonNull(mutations, "Mutations mussn't be null. Use an empty list if you don't want any mutations.");
		Objects.requireNonNull(abortCondition, "The abort condition mussn't be null.");
		if (populationSize <= 0 && (rootPopulation == null || rootPopulation.isEmpty())) {
			throw new IllegalArgumentException("Either a rootPopulation or a populationSize greater than 0 must be specified.");
		}
		if (elites >= populationSize && populationSize > 0) {
			throw new IllegalArgumentException("Can't use only elites in a population (or more elites than the population size).");
		}
		if (usedThreads <= 0) {
			throw new IllegalArgumentException("At least 1 thread has to be used to calculate. Input size was: " + usedThreads);
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
		this.usedThreads = usedThreads;
		
		executorService = Executors.newFixedThreadPool(usedThreads);
		progressProperty = new SimpleDoubleProperty(0, "GeneticOptimizerCalculationProgress");
		
		if (rootPopulation == null) {
			rootPopulation = Collections.emptyList();
		}
		else if (populationSize <= 0) {
			this.populationSize = rootPopulation.size();
		}
	}
	
	public void optimize() {
		long start = System.nanoTime();
		long timeUsed = 0;
		
		progressProperty.set(0);
		
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
		
		try {
			createInitialPopulation(population);
		}
		catch (InterruptedException ie) {
			//catch the interrupted exception and set the interrupted state again to terminate directly
			Thread.currentThread().interrupt();
		}
		
		generation = 0;
		while (!abortCondition.abort(bestDNA, timeUsed, generation) && !Thread.currentThread().isInterrupted()) {
			//update the progress property
			progressProperty.set(abortCondition.getProgress(bestDNA, timeUsed, generation));
			
			//calculate the chance of each individual to be selected for reproduction
			double[] reproductionProbabilities = selectionPressure.calculateSelectionProbability(population, generation, minimize, timeUsed);
			//choose the individuals that are selected for reproduction
			int[] selectedReproductionIndividuals = selector.select(reproductionProbabilities, populationSize - elites);
			//create the next generation of individuals
			try {
				generateNextPopulation(selectedReproductionIndividuals, population, nextPopulation);
			}
			catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
			
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
		
		//update the progress
		if (Thread.currentThread().isInterrupted()) {
			//the execution stopped because of an interruption -> calculation is not complete
			progressProperty.set(0d);
		}
		else {
			progressProperty.set(1d);
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
	/*private*/ void createInitialPopulation(DNA[] population) throws InterruptedException {
		//create the initial population by the rootPopulation or generate a random population
		int[] threadTasks = getThreadTasks(population.length, usedThreads);
		Runnable[] runnables = new Runnable[usedThreads];
		
		for (int i = 0; i < usedThreads; i++) {
			final int runnableIndex = i;
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					for (int i = threadTasks[runnableIndex]; i < threadTasks[runnableIndex + 1]; i++) {
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
				}
			};
			runnables[i] = runnable;
		}
		
		submitAndWait(runnables);
	}
	
	@VisibleForTesting
	/*private*/ void generateNextPopulation(int[] selectedReproductionIndividuals, DNA[] population, DNA[] nextPopulation)
			throws InterruptedException {
		//leave some spaces for the elites from the last population
		int[] threadTasks = getThreadTasks(populationSize - elites, usedThreads);
		Runnable[] runnables = new Runnable[usedThreads];
		
		for (int i = 0; i < usedThreads; i++) {
			final int runnableIndex = i;
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					for (int i = threadTasks[runnableIndex]; i < threadTasks[runnableIndex + 1]; i++) {
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
				}
			};
			runnables[i] = runnable;
		}
		
		submitAndWait(runnables);
		
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
	
	/**
	 * Submit the runnables to the executor and wait for all of them to finish.
	 */
	@VisibleForTesting
	/*private*/ void submitAndWait(Runnable[] runnables) throws InterruptedException, IllegalStateException {
		//submit all runnables and save the futures
		List<Future<?>> futures = new ArrayList<Future<?>>(usedThreads);
		for (Runnable runnable : runnables) {
			futures.add(executorService.submit(runnable));
		}
		//wait for all threads to finish
		for (Future<?> future : futures) {
			try {
				//wait for the execution (return value is null and is not used)
				future.get();
			}
			catch (ExecutionException e) {
				throw new IllegalStateException("The execution of a thread failed.", e);
			}
		}
	}
	
	@VisibleForTesting
	/*private*/ static int[] getThreadTasks(int tasks, int threads) {
		int[] splitToThreads = splitToThreads(tasks, threads);
		int[] summed = new int[threads + 1];
		summed[0] = 0;
		for (int i = 0; i < threads; i++) {
			summed[i + 1] = summed[i] + splitToThreads[i];
		}
		return summed;
	}
	@VisibleForTesting
	/*private*/static int[] splitToThreads(int tasks, int threads) {
		int[] tasksPerThread = new int[threads];
		for (int i = 0; i < threads; i++) {
			tasksPerThread[i] = tasks / threads;
		}
		for (int i = 0; i < tasks % threads; i++) {
			tasksPerThread[i]++;
		}
		return tasksPerThread;
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
	void setElites(int elites) {
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
	
	public int getUsedThreads() {
		return usedThreads;
	}
	
	public boolean isMultiThreaded() {
		return usedThreads > 1;
	}
	
	public DoubleProperty getProgressProperty() {
		return progressProperty;
	}
	
	public Selector getSelector() {
		return selector;
	}
	public void setSelector(Selector selector) {
		this.selector = selector;
	}
	
	public SelectionPressure getSelectionPressure() {
		return selectionPressure;
	}
	public void setSelectionPressure(SelectionPressure selectionPressure) {
		this.selectionPressure = selectionPressure;
	}
	
	public List<DNA> getRootPopulation() {
		return rootPopulation;
	}
	public void setRootPopulation(List<DNA> rootPopulation) {
		this.rootPopulation = rootPopulation;
	}
	
	public int getPopulationSize() {
		return populationSize;
	}
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}
	
	public boolean isUseLocalElitism() {
		return useLocalElitism;
	}
	public void setUseLocalElitism(boolean useLocalElitism) {
		this.useLocalElitism = useLocalElitism;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public AbortCondition getAbortCondition() {
		return abortCondition;
	}
	public void setAbortCondition(AbortCondition abortCondition) {
		this.abortCondition = abortCondition;
	}
	
	public int getElites() {
		return elites;
	}
	public void setProblem(GeneticOptimizerProblem problem) {
		this.problem = problem;
	}
	public void setHeredity(Heredity heredity) {
		this.heredity = heredity;
	}
	public void setMutations(List<Mutation> mutations) {
		this.mutations = mutations;
	}
	public void setUsedThreads(int usedThreads) {
		this.usedThreads = usedThreads;
	}
	public void setProgressProperty(DoubleProperty progressProperty) {
		this.progressProperty = progressProperty;
	}
	public void setGeneration(int generation) {
		this.generation = generation;
	}
}