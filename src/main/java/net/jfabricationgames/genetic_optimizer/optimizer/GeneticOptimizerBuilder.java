package net.jfabricationgames.genetic_optimizer.optimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jfabricationgames.genetic_optimizer.abort_condition.AbortCondition;
import net.jfabricationgames.genetic_optimizer.heredity.Heredity;
import net.jfabricationgames.genetic_optimizer.mutation.Mutation;

public class GeneticOptimizerBuilder {
	
	private Problem problem;
	private Heredity heredity;
	private List<Mutation> mutations;
	private List<DNA> rootPopulation;
	private int populationSize;
	private boolean minimize;
	private double fathersFraction;
	private InitialDNAGenerator dnaGenerator;
	private AbortCondition abortCondition;
	
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
				fathersFraction, minimize);
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
	}
	
	public Problem getProblem() {
		return problem;
	}
	public GeneticOptimizerBuilder setProblem(Problem problem) {
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
	
	public List<DNA> getRootPopulation() {
		return rootPopulation;
	}
	public GeneticOptimizerBuilder setRootPopulation(List<DNA> rootPopulation) {
		this.rootPopulation = rootPopulation;
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
	
	public double getFathersFraction() {
		return fathersFraction;
	}
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
}