package net.jfabricationgames.genetic_optimizer.abort_condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class ThresholdAbortConditionTest {
	
	@Test
	public void testThresholdAbortCondition() {
		ThresholdAbortCondition upperLimitThreshold = new ThresholdAbortCondition(42d, true);
		ThresholdAbortCondition lowerLimitThreshold = new ThresholdAbortCondition(100d, false);
		
		DNA dnaFitness10 = new DNA(5);
		dnaFitness10.setFitness(10d);
		
		DNA dnaFitness42 = new DNA(5);
		dnaFitness42.setFitness(42d);
		
		DNA dnaFitness90 = new DNA(5);
		dnaFitness90.setFitness(90d);
		
		DNA dnaFitness100 = new DNA(5);
		dnaFitness100.setFitness(100d);
		
		DNA dnaFitness200 = new DNA(5);
		dnaFitness200.setFitness(200d);
		
		assertFalse(upperLimitThreshold.abort(dnaFitness10, -1, 0));
		assertFalse(upperLimitThreshold.abort(dnaFitness42, -1, 0));
		assertTrue(upperLimitThreshold.abort(dnaFitness90, -1, 0));
		assertTrue(upperLimitThreshold.abort(dnaFitness100, -1, 0));
		assertTrue(upperLimitThreshold.abort(dnaFitness200, -1, 0));
		
		assertTrue(lowerLimitThreshold.abort(dnaFitness10, -1, 0));
		assertTrue(lowerLimitThreshold.abort(dnaFitness42, -1, 0));
		assertTrue(lowerLimitThreshold.abort(dnaFitness90, -1, 0));
		assertFalse(lowerLimitThreshold.abort(dnaFitness100, -1, 0));
		assertFalse(lowerLimitThreshold.abort(dnaFitness200, -1, 0));
	}
	
	@Test
	public void testProgress() {
		ThresholdAbortCondition upperLimit = new ThresholdAbortCondition(100, true);
		ThresholdAbortCondition lowerLimit = new ThresholdAbortCondition(0, false);
		
		DNA dna0 = new DNA(0);
		dna0.setFitness(0);
		DNA dna10 = new DNA(0);
		dna10.setFitness(10);
		DNA dna50 = new DNA(0);
		dna50.setFitness(50);
		DNA dna100 = new DNA(0);
		dna100.setFitness(100);
		
		//set the initial fitness
		upperLimit.getProgress(dna0, 0, 0);
		lowerLimit.getProgress(dna100, 0, 0);
		
		double epsilon = 1e-5;
		assertEquals(0, upperLimit.getProgress(dna0, 0, 0), epsilon);
		assertEquals(0.1, upperLimit.getProgress(dna10, 0, 0), epsilon);
		assertEquals(0.5, upperLimit.getProgress(dna50, 0, 0), epsilon);
		assertEquals(1, upperLimit.getProgress(dna100, 0, 0), epsilon);
		assertEquals(0, lowerLimit.getProgress(dna100, 0, 0), epsilon);
		assertEquals(0.5, lowerLimit.getProgress(dna50, 0, 0), epsilon);
		assertEquals(0.9, lowerLimit.getProgress(dna10, 0, 0), epsilon);
		assertEquals(1, lowerLimit.getProgress(dna0, 0, 0), epsilon);
	}
}