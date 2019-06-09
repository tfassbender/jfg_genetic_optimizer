package net.jfabricationgames.genetic_optimizer.mutation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class MutationScrambleTest {
	
	@Test
	public void testMutateScramblingStartValuesOnce() {
		DNA dna = getIndexInitializedDNA();
		
		MutationScramble mutation = mock(MutationScramble.class);
		when(mutation.getMaxSwaps()).thenReturn(1);
		
		//1: mutation chance (which is 0 so the returned value has to be out of range); 
		//2-3: scrambling part
		//4: number of swaps
		//5+: indices for swaps (in the scrambling part)
		when(mutation.getRandomNumber()).thenReturn(-1d, 0d, 0.1, 1d, 0d, 1d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {1, 0, 2, 3, 4, 5, 6, 7, 8, 9};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testMutateScramblingValuesInTheMiddleOfTheDna_swapOnce() {
		DNA dna = getIndexInitializedDNA();
		
		MutationScramble mutation = mock(MutationScramble.class);
		when(mutation.getMaxSwaps()).thenReturn(1);
		
		//1: mutation chance (which is 0 so the returned value has to be out of range); 
		//2-3: scrambling part
		//4: number of swaps
		//5+: indices for swaps (in the scrambling part)
		when(mutation.getRandomNumber()).thenReturn(-1d, 0.5, 0.6, 1d, 0d, 1d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {0, 1, 2, 3, 4, 6, 5, 7, 8, 9};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testK2BiggerThanK1() {
		DNA dna = getIndexInitializedDNA();
		
		MutationScramble mutation = mock(MutationScramble.class);
		when(mutation.getMaxSwaps()).thenReturn(1);
		
		//1: mutation chance (which is 0 so the returned value has to be out of range); 
		//2-3: scrambling part
		//4: number of swaps
		//5+: indices for swaps (in the scrambling part)
		when(mutation.getRandomNumber()).thenReturn(-1d, 0.6, 0.5, 1d, 0d, 1d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {0, 1, 2, 3, 4, 6, 5, 7, 8, 9};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testManySwapsButOnSameIndex() {
		DNA dna = getIndexInitializedDNA();
		
		MutationScramble mutation = mock(MutationScramble.class);
		when(mutation.getMaxSwaps()).thenReturn(42);
		
		//1: mutation chance (which is 0 so the returned value has to be out of range); 
		//2-3: scrambling part
		//4: number of swaps
		//5+: indices for swaps (in the scrambling part)
		when(mutation.getRandomNumber()).thenReturn(-1d, 0.5, 0.5, 1d, 0d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testNoMutationWhenRandomValueIsBiggerThanTheMutationRate() {
		DNA dna = getIndexInitializedDNA();
		
		MutationScramble mutation = mock(MutationScramble.class);
		when(mutation.getMaxSwaps()).thenReturn(42);
		
		//1: mutation chance (which is 0 so the returned value has to be out of range); 
		//2-3: scrambling part
		//4: number of swaps
		//5+: indices for swaps (in the scrambling part)
		when(mutation.getRandomNumber()).thenReturn(1d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
		verify(mutation, times(1)).getRandomNumber();
	}
	
	private DNA getIndexInitializedDNA() {
		DNA dna = new DNA(10);
		
		//initialize the DNA code with their indices
		double[] dnaCode = dna.getDnaCode();
		for (int i = 0; i < dnaCode.length; i++) {
			dnaCode[i] = i;
		}
		
		return dna;
	}
}