package net.jfabricationgames.genetic_optimizer.optimizer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.abort_condition.AbortCondition;
import net.jfabricationgames.genetic_optimizer.heredity.Heredity;
import net.jfabricationgames.genetic_optimizer.heredity.HeredityRandom;
import net.jfabricationgames.genetic_optimizer.mutation.Mutation;
import net.jfabricationgames.genetic_optimizer.mutation.MutationScramble;

class GeneticOptimizerBuilderTest {
	
	private GeneticOptimizerProblem getDefaultProblem() {
		GeneticOptimizerProblem problem = new GeneticOptimizerProblem() {
			
			@Override
			public int getLength() {
				return 5;
			}
			
			@Override
			public double calculateFitness(DNA dna) {
				return 42;
			}
		};
		return problem;
	}
	
	@Test
	public void testBuilder() {
		//fitness is always 42
		GeneticOptimizerProblem problem = getDefaultProblem();
		Heredity heredity = new HeredityRandom(0.5);
		Mutation mutation = new MutationScramble(0.5, 2);
		int populationSize = 3;
		boolean minimize = true;
		
		//create an empty DNA as initialization
		InitialDNAGenerator dnaGenerator = lenth -> new DNA(lenth);
		
		//every DNA is good enough (because the fitness is always 42)
		AbortCondition abortCondition = (bestDNA, timeUsed) -> bestDNA.getFitness() > 40 || timeUsed > 50;
		
		GeneticOptimizerBuilder builder = new GeneticOptimizerBuilder();
		builder.setProblem(problem).setHeredity(heredity).addMutation(mutation).setPopulationSize(populationSize).setMinimize(minimize)
				.setDnaGenerator(dnaGenerator).setAbortCondition(abortCondition);
		
		//just test that the creation succeeds without exceptions and the optimizer works
		GeneticOptimizer optimizer = builder.build();
		optimizer.optimize();
		
		assertArrayEquals(new double[5], optimizer.getBestDNA().getDNACode(), 1e-5);
	}
}