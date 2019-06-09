package net.jfabricationgames.genetic_optimizer.mutation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class MutationReptationTest {
	
	@Test
	public void testMoveSomeElementsFromFrontToBack() {
		DNA dna = getIndexInitializedDNA();
		
		MutationReptation mutation = mock(MutationReptation.class);
		when(mutation.getReptationLengthMax()).thenReturn(5);
		when(mutation.getReptationLengthMin()).thenReturn(3);
		
		//first is for mutation chance (which is 0 so the returned value has to be out of range); 
		//second is for reptation length (from minimum (0) to maximum (1));
		//third is for front to back probability (which is 0 so the returned value may has to be out of range)
		when(mutation.getRandomNumber()).thenReturn(-1d, 0d, -1d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {3, 4, 5, 6, 7, 8, 9, 0, 1, 2};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testMoveSomeElementsFromBackToFront() {
		DNA dna = getIndexInitializedDNA();
		
		MutationReptation mutation = mock(MutationReptation.class);
		when(mutation.getReptationLengthMax()).thenReturn(10);
		when(mutation.getReptationLengthMin()).thenReturn(3);
		
		//first is for mutation chance (which is 0 so the returned value has to be out of range); 
		//second is for reptation length (from minimum (0) to maximum (1));
		//third is for front to back probability (which is 0 so the returned value may has to be out of range)
		when(mutation.getRandomNumber()).thenReturn(-1d, 0d, 1d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {7, 8, 9, 0, 1, 2, 3, 4, 5, 6};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testMoveCompleteSequenceFromFrontToBack_shouldNotChangeTheDnaCode() {
		DNA dna = getIndexInitializedDNA();
		
		MutationReptation mutation = mock(MutationReptation.class);
		when(mutation.getReptationLengthMax()).thenReturn(10);
		when(mutation.getReptationLengthMin()).thenReturn(10);
		
		//first is for mutation chance (which is 0 so the returned value has to be out of range); 
		//second is for reptation length (from minimum (0) to maximum (1));
		//third is for front to back probability (which is 0 so the returned value may has to be out of range)
		when(mutation.getRandomNumber()).thenReturn(-1d, 1d, 1d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testNoMutationWhenRandomValueIsBiggerThanTheMutationRate() {
		DNA dna = new DNA(10);
		
		MutationReptation mutation = mock(MutationReptation.class);
		when(mutation.getReptationLengthMax()).thenReturn(5);
		when(mutation.getReptationLengthMin()).thenReturn(3);
		
		//first is for mutation chance (which is 0 so the returned value has to be out of range); 
		//second is for reptation length (from minimum (0) to maximum (1));
		//third is for front to back probability (which is 0 so the returned value may has to be out of range)
		when(mutation.getRandomNumber()).thenReturn(1d, 0d, 0d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[10];
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
		verify(mutation, times(1)).getRandomNumber();//getRandomNumber should be called only once to decide whether the DNA should be mutated
	}
	
	@Test
	public void testRangeBiggerThanDnaSize_shouldCauseAnArrayIndexOutOfBoundsException() {
		DNA dna = new DNA(10);
		
		MutationReptation mutation = mock(MutationReptation.class);
		when(mutation.getReptationLengthMax()).thenReturn(42);
		when(mutation.getReptationLengthMin()).thenReturn(5);
		
		//first is for mutation chance (which is 0 so the returned value has to be out of range); 
		//second is for reptation length (from minimum (0) to maximum (1));
		//third is for front to back probability (which is 0 so the returned value may has to be out of range)
		when(mutation.getRandomNumber()).thenReturn(-1d, 1d, -1d);//use length of 42 (second parameter)
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> mutation.mutate(dna));
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