package net.jfabricationgames.genetic_optimizer.mutation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class MutationInversionTest {
	
	@Test
	public void testInvertFirstHalf() {
		DNA dna = new DNA(10);
		
		MutationInversion mutation = mock(MutationInversion.class);
		when(mutation.getMaxValue()).thenReturn(1d);
		when(mutation.getRangeSize()).thenReturn(5);
		//first is for mutation chance (which is 0 so the returned value has to be out of range); second is for start position
		when(mutation.getRandomNumber()).thenReturn(-1d, 0d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {1, 1, 1, 1, 1, 0, 0, 0, 0, 0};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testInvertLastPart_withRangeBiggerThanLengthOfDna() {
		DNA dna = new DNA(10);
		
		double[] dnaCode = dna.getDnaCode();
		dnaCode[7] = 5;
		dnaCode[8] = 6;
		dnaCode[9] = 9;
		
		MutationInversion mutation = mock(MutationInversion.class);
		when(mutation.getMaxValue()).thenReturn(10d);
		when(mutation.getRangeSize()).thenReturn(42);
		//first is for mutation chance (which is 0 so the returned value has to be out of range); second is for start position
		when(mutation.getRandomNumber()).thenReturn(-1d, 0.5);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {0, 0, 0, 0, 0, 10, 10, 5, 4, 1};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testWrongValueInDnaCode_chromosomeValueBiggerThanMaxRange_problemShouldBeIgnoredAndWrongValueShouldBeAddedToDnaCode() {
		DNA dna = new DNA(1);
		
		double[] dnaCode = dna.getDnaCode();
		dnaCode[0] = 42;
		
		MutationInversion mutation = mock(MutationInversion.class);
		when(mutation.getMaxValue()).thenReturn(10d);//the value is 42 but the allowed max value is 10
		when(mutation.getRangeSize()).thenReturn(1);
		//first is for mutation chance (which is 0 so the returned value has to be out of range); second is for start position
		when(mutation.getRandomNumber()).thenReturn(-1d, 0d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[] {-32};
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testNoMutationWhenRandomValueIsBiggerThanTheMutationRate() {
		DNA dna = new DNA(10);
		
		MutationInversion mutation = mock(MutationInversion.class);
		when(mutation.getMaxValue()).thenReturn(10d);
		when(mutation.getRangeSize()).thenReturn(5);
		//first is for mutation chance (which is 0 so the mutation doesn't start); second is for start position
		when(mutation.getRandomNumber()).thenReturn(1d, 0.5);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		double[] expected = new double[10];//all entries are expected to stay zero
		
		mutation.mutate(dna);
		
		assertArrayEquals(expected, dna.getDnaCode(), 1e-8);
		verify(mutation, times(1)).getRandomNumber();//getRandomNumber should be called only once to decide whether the DNA should be mutated
	}
}