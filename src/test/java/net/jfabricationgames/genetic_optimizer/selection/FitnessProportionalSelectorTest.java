package net.jfabricationgames.genetic_optimizer.selection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

class FitnessProportionalSelectorTest {
	
	@Test
	public void testSelect_withMockedRandom() {
		FitnessProportionalSelector selector = mock(FitnessProportionalSelector.class);
		when(selector.select(ArgumentMatchers.<double[]> any(), anyInt())).thenCallRealMethod();
		when(selector.getRandomNumber()).thenReturn(0d, 0.2, 0.4, 0.6, 0.8, 0.99, 0d, 0.2, 0.4, 0.6, 0.8, 0.99);
		
		double[] probabilities1 = new double[] {0.11, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.09};
		double[] probabilities2 = new double[] {0.09, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.11};
		
		assertArrayEquals(new int[] {0, 1, 3, 5, 7, 9}, selector.select(probabilities1, 3));
		assertArrayEquals(new int[] {0, 2, 4, 6, 8, 9}, selector.select(probabilities2, 3));
	}
	
	@Test
	public void testSelect_withRandom() {
		FitnessProportionalSelector selector = new FitnessProportionalSelector();
		
		double[] probabilities = new double[] {0, 0, 0, 1 - 1e-8, 0};
		
		assertArrayEquals(new int[] {3, 3, 3, 3, 3, 3}, selector.select(probabilities, 3));
	}
}