package net.jfabricationgames.genetic_optimizer.selection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SelectorTest {
	
	@Test
	public void testToSummedProbabilities() {
		double[] probabilities1 = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
		double[] probabilities2 = new double[] {0.2, 0.2, 0.2, 0.2, 0.2};
		double[] probabilities3 = new double[] {0.1, 0.2, 0.4, 0.3};
		
		double epsilon = 1e-8;
		assertArrayEquals(new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1}, Selector.toSummedProbabilities(probabilities1), epsilon);
		assertArrayEquals(new double[] {0.2, 0.4, 0.6, 0.8, 1}, Selector.toSummedProbabilities(probabilities2), epsilon);
		assertArrayEquals(new double[] {0.1, 0.3, 0.7, 1}, Selector.toSummedProbabilities(probabilities3), epsilon);
	}
	
	@Test
	public void testGetSelectedIndexByBisectionSearch() {
		double[] sumProbabilities = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
		
		assertEquals(3, Selector.getSelectedIndexByBisectionSearch(sumProbabilities, 0.35));
		assertEquals(5, Selector.getSelectedIndexByBisectionSearch(sumProbabilities, 0.51));
		assertEquals(4, Selector.getSelectedIndexByBisectionSearch(sumProbabilities, 0.5));
		assertEquals(0, Selector.getSelectedIndexByBisectionSearch(sumProbabilities, 0.05));
		assertEquals(9, Selector.getSelectedIndexByBisectionSearch(sumProbabilities, 0.95));
		assertEquals(1, Selector.getSelectedIndexByBisectionSearch(sumProbabilities, 0.2));
		assertEquals(9, Selector.getSelectedIndexByBisectionSearch(sumProbabilities, 1.5));
	}
	
	@Test
	public void testGetSelectedIndexByLinearSearch() {
		double[] sumProbabilities = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
		
		assertEquals(3, Selector.getSelectedIndexByLinearSearch(sumProbabilities, 0.35));
		assertEquals(5, Selector.getSelectedIndexByLinearSearch(sumProbabilities, 0.51));
		assertEquals(4, Selector.getSelectedIndexByLinearSearch(sumProbabilities, 0.5));
		assertEquals(0, Selector.getSelectedIndexByLinearSearch(sumProbabilities, 0.05));
		assertEquals(9, Selector.getSelectedIndexByLinearSearch(sumProbabilities, 0.95));
		assertEquals(1, Selector.getSelectedIndexByLinearSearch(sumProbabilities, 0.2));
		assertEquals(9, Selector.getSelectedIndexByLinearSearch(sumProbabilities, 1.5));
	}
}