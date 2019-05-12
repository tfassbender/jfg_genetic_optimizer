package net.jfabricationgames.genetic_optimizer.heredity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class HeredityShuffleCrossTest {
	
	private HeredityShuffleCross getMockedHeredity() {
		HeredityShuffleCross heredity = mock(HeredityShuffleCross.class);
		when(heredity.generateShuffleIndices(anyInt())).thenCallRealMethod();
		doCallRealMethod().when(heredity).shuffleArray(ArgumentMatchers.<double[]> any(), ArgumentMatchers.<int[]> any());
		doCallRealMethod().when(heredity).unshuffleArray(ArgumentMatchers.<double[]> any(), ArgumentMatchers.<int[]> any());
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		
		return heredity;
	}
	
	@Test
	public void testGenerateShuffleIndices() {
		HeredityShuffleCross heredity = mock(HeredityShuffleCross.class);
		
		when(heredity.getRandomNumber()).thenReturn(1d - 1e-5);//always return the maximum to test the bounds
		when(heredity.generateShuffleIndices(anyInt())).thenCallRealMethod();
		
		int len = 5;
		int[] shuffleIndices = heredity.generateShuffleIndices(len);
		
		assertArrayEquals(new int[] {4, 3, 2, 1}, shuffleIndices);
	}
	
	@Test
	public void testShuffle() {
		HeredityShuffleCross heredity = new HeredityShuffleCross();
		
		int len = 10_000;//big array size to make it nearly impossible that the shuffled array equals the array before it was shuffled
		double[] shuffleArray = new double[len];
		double[] compareArray = new double[len];
		
		//generate a random initialized array
		for (int i = 0; i < shuffleArray.length; i++) {
			shuffleArray[i] = Math.random() * 42;
		}
		//copy the array to compare the result
		System.arraycopy(shuffleArray, 0, compareArray, 0, len);
		
		//create the shuffle indices
		int[] indices = heredity.generateShuffleIndices(len);
		
		//assert the arrays are equal before shuffling
		assertTrue(Arrays.equals(shuffleArray, compareArray));
		
		//shuffle the array
		heredity.shuffleArray(shuffleArray, indices);
		
		//assert the arrays are not equal
		assertFalse(Arrays.equals(shuffleArray, compareArray));
	}
	
	@Test
	public void testUnshuffle() {
		HeredityShuffleCross heredity = new HeredityShuffleCross();
		
		int len = 10_000;//big array size to make it nearly impossible that the shuffled array equals the array before it was shuffled
		double[] shuffleArray = new double[len];
		double[] compareArray = new double[len];
		
		//generate a random initialized array
		for (int i = 0; i < shuffleArray.length; i++) {
			shuffleArray[i] = Math.random() * 42;
		}
		//copy the array to compare the result
		System.arraycopy(shuffleArray, 0, compareArray, 0, len);
		
		//create the shuffle indices
		int[] indices = heredity.generateShuffleIndices(len);
		
		//assert the arrays are equal before shuffling
		assertTrue(Arrays.equals(shuffleArray, compareArray));
		
		//shuffle the array
		heredity.shuffleArray(shuffleArray, indices);
		
		//un-shuffle the array back to the initial array
		heredity.unshuffleArray(shuffleArray, indices);
		
		//assert the arrays are equal again
		assertArrayEquals(compareArray, shuffleArray, 1e-8);
	}
	
	@Test
	public void testMixDna_fatherEqualsMother() {
		HeredityShuffleCross heredity = getMockedHeredity();
		when(heredity.getRandomNumber()).thenCallRealMethod();
		
		DNA father = new DNA(new double[] {1, 2, 3, 4, 5});
		DNA mother = new DNA(new double[] {1, 2, 3, 4, 5});
		
		DNA child = heredity.mixDNA(father, mother);
		
		assertArrayEquals(father.getDNACode(), child.getDNACode());
	}
	
	@Test
	public void testMixDNA_differentParents() {
		HeredityShuffleCross heredity = getMockedHeredity();
		
		//the first two are shuffle indices (shuffle to index 0 -> [2, 3, 1]); the last one is the split position (between index 1 and 2; -> [2, 3, 0])
		when(heredity.getRandomNumber()).thenReturn(0d, 0d, 0.7);
		
		DNA father = new DNA(new double[] {1, 2, 3});
		DNA mother = new DNA(new double[] {0, 0, 0});
		
		DNA child = heredity.mixDNA(father, mother);
		
		assertArrayEquals(new double[] {0, 2, 3}, child.getDNACode(), 1e-5);
	}
}