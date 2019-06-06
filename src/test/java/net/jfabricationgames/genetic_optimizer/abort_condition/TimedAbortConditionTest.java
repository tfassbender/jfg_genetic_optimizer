package net.jfabricationgames.genetic_optimizer.abort_condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimedAbortConditionTest {
	
	@Test
	public void testTimedAbort() {
		TimedAbortCondition timer500ms = new TimedAbortCondition(500);
		TimedAbortCondition timer100ms = new TimedAbortCondition(100);
		
		assertFalse(timer100ms.abort(null, 90, 0));
		assertFalse(timer100ms.abort(null, 100, 0));
		assertTrue(timer100ms.abort(null, 101, 0));
		assertTrue(timer100ms.abort(null, 1000, 0));
		
		assertFalse(timer500ms.abort(null, 490, 0));
		assertFalse(timer500ms.abort(null, 500, 0));
		assertTrue(timer500ms.abort(null, 501, 0));
		assertTrue(timer500ms.abort(null, 1000, 0));
	}
	
	@Test
	public void testInitialization() {
		new TimedAbortCondition(1);//positive time value -> no exception
		assertThrows(IllegalArgumentException.class, () -> new TimedAbortCondition(-1));
		assertThrows(IllegalArgumentException.class, () -> new TimedAbortCondition(0));
	}
	
	@Test
	public void testProgress() {
		TimedAbortCondition timer50ms = new TimedAbortCondition(50);
		TimedAbortCondition timer100ms = new TimedAbortCondition(100);
		
		double epsilon = 1e-5;
		assertEquals(0, timer50ms.getProgress(null, 0, 0), epsilon);
		assertEquals(0.5, timer50ms.getProgress(null, 25, 0), epsilon);
		assertEquals(1, timer50ms.getProgress(null, 50, 0), epsilon);
		
		assertEquals(0, timer100ms.getProgress(null, 0, 0), epsilon);
		assertEquals(0.25, timer100ms.getProgress(null, 25, 0), epsilon);
		assertEquals(0.5, timer100ms.getProgress(null, 50, 0), epsilon);
		assertEquals(0.7, timer100ms.getProgress(null, 70, 0), epsilon);
		assertEquals(0.98, timer100ms.getProgress(null, 98, 0), epsilon);
		assertEquals(1, timer100ms.getProgress(null, 100, 0), epsilon);
	}
}