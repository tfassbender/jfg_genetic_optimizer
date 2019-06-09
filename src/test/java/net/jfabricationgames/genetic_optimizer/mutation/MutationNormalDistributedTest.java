package net.jfabricationgames.genetic_optimizer.mutation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class MutationNormalDistributedTest {
	
	private static final double EPSILON = 1e-5;
	
	@Test
	public void testAddOne() {
		DNA dna = new DNA(5);
		
		MutationNormalDistributed mutation = mock(MutationNormalDistributed.class);
		when(mutation.getExpectedValue()).thenReturn(1d);
		when(mutation.getStandardDeviation()).thenReturn(0d);
		
		when(mutation.getMaxMutatedFieldsPropotion()).thenReturn(0.21d);//mutate only one field
		when(mutation.getMaxValue()).thenReturn(2d);
		when(mutation.getMinValue()).thenReturn(0d);
		
		//random answers to mutationRate, mutatedFields, mutatedFieldIndex
		when(mutation.getRandomNumber()).thenReturn(-1d, 1d, 0d);
		when(mutation.getGaussianRandomNumber()).thenCallRealMethod();
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		mutation.mutate(dna);
		
		assertArrayEquals(new double[] {1, 0, 0, 0, 0}, dna.getDnaCode(), EPSILON);
	}
	
	@Test
	public void testAddDeviation() {
		DNA dna = new DNA(3);
		
		MutationNormalDistributed mutation = mock(MutationNormalDistributed.class);
		when(mutation.getExpectedValue()).thenReturn(1d);
		when(mutation.getStandardDeviation()).thenReturn(1d);
		
		when(mutation.getMaxMutatedFieldsPropotion()).thenReturn(1d);//mutate all fields
		when(mutation.getMaxValue()).thenReturn(2d);
		when(mutation.getMinValue()).thenReturn(0d);
		
		//random answers to mutationRate, mutatedFields, 3 x mutatedFieldIndices
		when(mutation.getRandomNumber()).thenReturn(-1d, 1d, 0d, 0.35, 0.7);
		when(mutation.getGaussianRandomNumber()).thenReturn(0.5, -0.5, 0d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		mutation.mutate(dna);
		
		assertArrayEquals(new double[] {1.5, 0.5, 1}, dna.getDnaCode(), EPSILON);
	}
	
	@Test
	public void testLimits() {
		DNA dna = new DNA(3);
		
		MutationNormalDistributed mutation = mock(MutationNormalDistributed.class);
		when(mutation.getExpectedValue()).thenReturn(1d);
		when(mutation.getStandardDeviation()).thenReturn(1d);
		
		when(mutation.getMaxMutatedFieldsPropotion()).thenReturn(1d);//mutate all fields
		when(mutation.getMaxValue()).thenReturn(2d);//upper limit 2
		when(mutation.getMinValue()).thenReturn(0d);//lower limit 0
		
		//random answers to mutationRate, mutatedFields, 3 x mutatedFieldIndices
		when(mutation.getRandomNumber()).thenReturn(-1d, 1d, 0d, 0.35, 0.7);
		when(mutation.getGaussianRandomNumber()).thenReturn(1.5, -1.5, 0d);
		doCallRealMethod().when(mutation).mutate(any(DNA.class));
		
		mutation.mutate(dna);
		
		assertArrayEquals(new double[] {2, 0, 1}, dna.getDnaCode(), EPSILON);
	}
}