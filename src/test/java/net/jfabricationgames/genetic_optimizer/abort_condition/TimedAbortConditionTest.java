package net.jfabricationgames.genetic_optimizer.abort_condition;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimedAbortConditionTest {
	
	@Test
	public void testTimedAbort() {
		TimedAbortCondition timer500ms = new TimedAbortCondition(500);
		TimedAbortCondition timer100ms = new TimedAbortCondition(100);
		
		assertFalse(timer100ms.abort(null, 90));
		assertFalse(timer100ms.abort(null, 100));
		assertTrue(timer100ms.abort(null, 101));
		assertTrue(timer100ms.abort(null, 1000));
		
		assertFalse(timer500ms.abort(null, 490));
		assertFalse(timer500ms.abort(null, 500));
		assertTrue(timer500ms.abort(null, 501));
		assertTrue(timer500ms.abort(null, 1000));
	}
}