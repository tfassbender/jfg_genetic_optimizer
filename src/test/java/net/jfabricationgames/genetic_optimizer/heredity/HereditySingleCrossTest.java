package net.jfabricationgames.genetic_optimizer.heredity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class HereditySingleCrossTest {
	
	@Test
	public void testOnlyFather() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		HereditySingleCross heredity = mock(HereditySingleCross.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		when(heredity.getRandomNumber()).thenReturn(1d, 0d);//first is split position, second is father first
		
		DNA child = heredity.mixDNA(father, mother);
		
		assertArrayEquals(father.getDnaCode(), child.getDnaCode(), 1e-8);
	}
	
	@Test
	public void testHalfFatherHalfMother() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		HereditySingleCross heredity = mock(HereditySingleCross.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		when(heredity.getRandomNumber()).thenReturn(0.5, 0d);//first is split position, second is father first
		
		DNA child = heredity.mixDNA(father, mother);
		
		double[] childsFirstHalfCode = Arrays.copyOfRange(child.getDnaCode(), 0, 5);
		double[] childsLastHalfCode = Arrays.copyOfRange(child.getDnaCode(), 5, 10);
		
		double[] fathersFirstHalfCode = Arrays.copyOfRange(father.getDnaCode(), 0, 5);
		double[] mothersLastHalfCode = Arrays.copyOfRange(mother.getDnaCode(), 5, 10);
		
		assertArrayEquals(fathersFirstHalfCode, childsFirstHalfCode, 1e-8);
		assertArrayEquals(mothersLastHalfCode, childsLastHalfCode, 1e-8);
	}
	
	@Test
	public void testHalfFatherHalfMother_motherFirst() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		HereditySingleCross heredity = mock(HereditySingleCross.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		when(heredity.getRandomNumber()).thenReturn(0.5, 1d);//first is split position, second is mother first
		
		DNA child = heredity.mixDNA(father, mother);
		
		double[] childsFirstHalfCode = Arrays.copyOfRange(child.getDnaCode(), 0, 5);
		double[] childsLastHalfCode = Arrays.copyOfRange(child.getDnaCode(), 5, 10);
		
		double[] mothersFirstHalfCode = Arrays.copyOfRange(mother.getDnaCode(), 0, 5);
		double[] fathersLastHalfCode = Arrays.copyOfRange(father.getDnaCode(), 5, 10);
		
		assertArrayEquals(childsFirstHalfCode, mothersFirstHalfCode, 1e-8);
		assertArrayEquals(childsLastHalfCode, fathersLastHalfCode, 1e-8);
	}
}