package net.jfabricationgames.genetic_optimizer.abort_condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GenerationCountAbortConditionTest {
	
	@Test
	public void testAbortCondition() {
		GenerationCountAbortCondition condition = new GenerationCountAbortCondition(5);
		
		assertFalse(condition.abort(null, 1000, 0));
		assertFalse(condition.abort(null, -1, 4));
		assertTrue(condition.abort(null, 0, 5));
		assertTrue(condition.abort(null, 0, 10));
	}

	@Test
	public void testInitialization() {
		new GenerationCountAbortCondition(1);//positive value -> no exception
		assertThrows(IllegalArgumentException.class, () -> new GenerationCountAbortCondition(-1));
		assertThrows(IllegalArgumentException.class, () -> new GenerationCountAbortCondition(0));
	}
	
	@Test
	public void testProgress() {
		GenerationCountAbortCondition generation51 = new GenerationCountAbortCondition(51);
		GenerationCountAbortCondition generation101 = new GenerationCountAbortCondition(101);
		
		double epsilon = 1e-5;
		assertEquals(0, generation51.getProgress(null, 0, 0), epsilon);
		assertEquals(0.5, generation51.getProgress(null, 0, 25), epsilon);
		assertEquals(1, generation51.getProgress(null, 0, 50), epsilon);
		
		assertEquals(0, generation101.getProgress(null, 0, 0), epsilon);
		assertEquals(0.25, generation101.getProgress(null, 0, 25), epsilon);
		assertEquals(0.5, generation101.getProgress(null, 0, 50), epsilon);
		assertEquals(0.7, generation101.getProgress(null, 0, 70), epsilon);
		assertEquals(0.98, generation101.getProgress(null, 0, 98), epsilon);
		assertEquals(1, generation101.getProgress(null, 0, 100), epsilon);
	}
}