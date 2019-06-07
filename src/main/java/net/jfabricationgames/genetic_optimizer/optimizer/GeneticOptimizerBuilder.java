package net.jfabricationgames.genetic_optimizer.optimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jfabricationgames.genetic_optimizer.abort_condition.AbortCondition;
import net.jfabricationgames.genetic_optimizer.heredity.Heredity;
import net.jfabricationgames.genetic_optimizer.mutation.Mutation;
import net.jfabricationgames.genetic_optimizer.selection.SelectionPressure;
import net.jfabricationgames.genetic_optimizer.selection.Selector;

public class GeneticOptimizerBuilder {
	
	private GeneticOptimizerProblem problem;
	private Heredity heredity;
	private List<Mutation> mutations;
	private SelectionPressure selectionPressure;
	private Selector selector;
	private List<DNA> rootPopulation;
	private int populationSize;
	private boolean minimize;
	@Deprecated
	private double fathersFraction;
	private InitialDNAGenerator dnaGenerator;
	private AbortCondition abortCondition;
	private boolean useLocalElitism;
	private int elites;
	private int usedThreads;
	
	public GeneticOptimizerBuilder() {
		reset();
	}
	
	/**
	 * Create a GeneticOptimizer that uses the given parameters.
	 *
	 * If the creation fails the constructors exceptions are thrown.
	 */
	public GeneticOptimizer build() throws IllegalArgumentException, NullPointerException {
		GeneticOptimizer optimizer = new GeneticOptimizer(problem, populationSize, dnaGenerator, rootPopulation, heredity, mutations, abortCondition,
				selectionPressure, selector, fathersFraction, minimize, useLocalElitism, elites, usedThreads);
		return optimizer;
	}
	
	public void reset() {
		problem = null;
		heredity = null;
		mutations = null;
		rootPopulation = Collections.emptyList();
		populationSize = 0;
		minimize = true;
		fathersFraction = 0.15;
		dnaGenerator = null;
		abortCondition = null;
		useLocalElitism = false;
		elites = 0;
		usedThreads = 1;
	}
	
	public GeneticOptimizerProblem getProblem() {
		return problem;
	}
	public GeneticOptimizerBuilder setProblem(GeneticOptimizerProblem problem) {
		this.problem = problem;
		return this;
	}
	
	public Heredity getHeredity() {
		return heredity;
	}
	public GeneticOptimizerBuilder setHeredity(Heredity heredity) {
		this.heredity = heredity;
		return this;
	}
	
	public List<Mutation> getMutations() {
		return mutations;
	}
	public GeneticOptimizerBuilder setMutations(List<Mutation> mutations) {
		this.mutations = mutations;
		return this;
	}
	public GeneticOptimizerBuilder addMutation(Mutation mutation) {
		if (this.mutations == null) {
			this.mutations = new ArrayList<Mutation>();
		}
		mutations.add(mutation);
		return this;
	}
	
	public SelectionPressure getSelectionPressure() {
		return selectionPressure;
	}
	public GeneticOptimizerBuilder setSelectionPressure(SelectionPressure selectionPressure) {
		this.selectionPressure = selectionPressure;
		return this;
	}
	
	public Selector getSelector() {
		return selector;
	}
	public GeneticOptimizerBuilder setSelector(Selector selector) {
		this.selector = selector;
		return this;
	}
	
	public List<DNA> getRootPopulation() {
		return rootPopulation;
	}
	public GeneticOptimizerBuilder setRootPopulation(List<DNA> rootPopulation) {
		if (rootPopulation == null) {
			this.rootPopulation = Collections.emptyList();
		}
		else {
			this.rootPopulation = rootPopulation;			
		}
		return this;
	}
	
	public int getPopulationSize() {
		return populationSize;
	}
	public GeneticOptimizerBuilder setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
		return this;
	}
	
	public boolean isMinimize() {
		return minimize;
	}
	public GeneticOptimizerBuilder setMinimize(boolean minimize) {
		this.minimize = minimize;
		return this;
	}
	
	@Deprecated
	public double getFathersFraction() {
		return fathersFraction;
	}
	@Deprecated
	public GeneticOptimizerBuilder setFathersFraction(double fathersFraction) {
		this.fathersFraction = fathersFraction;
		return this;
	}
	
	public InitialDNAGenerator getDnaGenerator() {
		return dnaGenerator;
	}
	public GeneticOptimizerBuilder setDnaGenerator(InitialDNAGenerator dnaGenerator) {
		this.dnaGenerator = dnaGenerator;
		return this;
	}
	
	public AbortCondition getAbortCondition() {
		return abortCondition;
	}
	public GeneticOptimizerBuilder setAbortCondition(AbortCondition abortCondition) {
		this.abortCondition = abortCondition;
		return this;
	}
	
	public boolean isUseLocalElitism() {
		return useLocalElitism;
	}
	public GeneticOptimizerBuilder setUseLocalElitism(boolean useLocalElitism) {
		this.useLocalElitism = useLocalElitism;
		return this;
	}
	
	public int getElites() {
		return elites;
	}
	public GeneticOptimizerBuilder setElites(int elites) {
		this.elites = elites;
		return this;
	}
	
	public int getUsedThreads() {
		return usedThreads;
	}
	public GeneticOptimizerBuilder setUsedThreads(int usedThreads) {
		this.usedThreads = usedThreads;
		return this;
	}
	public GeneticOptimizerBuilder setUsedThreadsToNumCores() {
		int poolThreads = Runtime.getRuntime().availableProcessors();
		poolThreads = Math.max(1, poolThreads);
		return setUsedThreads(poolThreads);
	}
	public GeneticOptimizerBuilder setUsedThreadsCpuUsage(double aimedCpuUsageInPercent) {
		double Ncpu = Runtime.getRuntime().availableProcessors();
		double Ucpu = aimedCpuUsageInPercent;
		double W_C = 0.25;//waiting time / calculation time (assumed)
		int poolThreads = (int) Math.round((Ncpu * Ucpu * (1d + W_C)));
		poolThreads = Math.max(1, poolThreads);
		return setUsedThreads(poolThreads);
	}
}