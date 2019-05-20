package net.jfabricationgames.genetic_optimizer.selection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

class StochasticallyDistributedSelectorTest {
	
	@Test
	public void testShuffle_noShuffling() {
		StochasticallyDistributedSelector selector = mock(StochasticallyDistributedSelector.class);
		doCallRealMethod().when(selector).shuffle(ArgumentMatchers.<int[]> any());
		when(selector.getRandomNumber()).thenReturn(1 - 1e-8);//always return maximum -> no shuffling
		
		int[] shuffle = new int[] {1, 2, 3, 4, 5};
		selector.shuffle(shuffle);
		
		assertArrayEquals(new int[] {1, 2, 3, 4, 5}, shuffle);
	}
	
	@Test
	public void testShuffle() {
		StochasticallyDistributedSelector selector = mock(StochasticallyDistributedSelector.class);
		doCallRealMethod().when(selector).shuffle(ArgumentMatchers.<int[]> any());
		when(selector.getRandomNumber()).thenReturn(0d, 0.3, 0.1, 0.99);//always return maximum -> no shuffling
		
		int[] shuffle = new int[] {1, 2, 3, 4, 5};
		selector.shuffle(shuffle);
		
		assertArrayEquals(new int[] {3, 4, 5, 2, 1}, shuffle);
	}
	
	@Test
	public void testSelect() {
		StochasticallyDistributedSelector selector = mock(StochasticallyDistributedSelector.class);
		when(selector.select(ArgumentMatchers.<double[]> any(), anyInt())).thenCallRealMethod();
		when(selector.getRandomNumber()).thenReturn(0.1);
		//no shuffling
		
		double[] probabilities1 = new double[] {0.25, 0.25, 0.25, 0.25};
		double[] probabilities2 = new double[] {0.5, 0.25, 0.25, 0.0};
		double[] probabilities3 = new double[] {1, 0.0, 0.0, 0.0};
		double[] probabilities4 = new double[] {0.3, 0.2, 0.3, 0.2};
		double[] probabilities5 = new double[] {0.3, 0.3, 0.2, 0.2};
		
		int[] selected1 = selector.select(probabilities1, 2);
		int[] selected2 = selector.select(probabilities2, 2);
		int[] selected3 = selector.select(probabilities3, 5);
		int[] selected4 = selector.select(probabilities4, 1);
		int[] selected5 = selector.select(probabilities5, 1);
		
		assertArrayEquals(new int[] {0, 1, 2, 3}, selected1);
		assertArrayEquals(new int[] {0, 0, 1, 2}, selected2);
		assertArrayEquals(new int[10], selected3);
		assertArrayEquals(new int[] {0, 2}, selected4);
		assertArrayEquals(new int[] {0, 1}, selected5);
	}
}