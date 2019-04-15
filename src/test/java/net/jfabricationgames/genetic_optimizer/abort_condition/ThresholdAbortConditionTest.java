package net.jfabricationgames.genetic_optimizer.abort_condition;

import static org.junit.jupiter.api.Assertions.*;

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
		
		assertFalse(upperLimitThreshold.abort(dnaFitness10, -1));
		assertFalse(upperLimitThreshold.abort(dnaFitness42, -1));
		assertTrue(upperLimitThreshold.abort(dnaFitness90, -1));
		assertTrue(upperLimitThreshold.abort(dnaFitness100, -1));
		assertTrue(upperLimitThreshold.abort(dnaFitness200, -1));
		
		assertTrue(lowerLimitThreshold.abort(dnaFitness10, -1));
		assertTrue(lowerLimitThreshold.abort(dnaFitness42, -1));
		assertTrue(lowerLimitThreshold.abort(dnaFitness90, -1));
		assertFalse(lowerLimitThreshold.abort(dnaFitness100, -1));
		assertFalse(lowerLimitThreshold.abort(dnaFitness200, -1));
	}
}