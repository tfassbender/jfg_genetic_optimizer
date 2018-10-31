package net.jfabricationgames.genetic_optimizer.heredity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class HeredityDoubleCrossTest {
	
	@Test
	public void testOnlyFather() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		HeredityDoubleCross heredity = mock(HeredityDoubleCross.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		when(heredity.getRandomNumber()).thenReturn(1d, 1d, 0d);//first two are split positions, last is father first
		
		DNA child = heredity.mixDNA(father, mother);
		
		assertArrayEquals(father.getDNACode(), child.getDNACode(), 1e-8);
	}
	
	@Test
	public void testCombinationOfFatherAndMother() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		HeredityDoubleCross heredity = mock(HeredityDoubleCross.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		when(heredity.getRandomNumber()).thenReturn(0.4, 0.8, 0d);//first two are split positions, last is father first
		
		DNA child = heredity.mixDNA(father, mother);
		
		double[] childsFirstPartCode = Arrays.copyOfRange(child.getDNACode(), 0, 4);
		double[] childsSecondPartCode = Arrays.copyOfRange(child.getDNACode(), 4, 8);
		double[] childsLastPartCode = Arrays.copyOfRange(child.getDNACode(), 8, 10);
		
		double[] fathersFirstPartCode = Arrays.copyOfRange(father.getDNACode(), 0, 4);
		double[] fathersLastPartCode = Arrays.copyOfRange(father.getDNACode(), 8, 10);
		double[] mothersMiddlePartCode = Arrays.copyOfRange(mother.getDNACode(), 4, 8);
		
		assertArrayEquals(fathersFirstPartCode, childsFirstPartCode, 1e-8);
		assertArrayEquals(mothersMiddlePartCode, childsSecondPartCode, 1e-8);
		assertArrayEquals(fathersLastPartCode, childsLastPartCode, 1e-8);
	}
	
	@Test
	public void testMotherOnly_motherFirst() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		HeredityDoubleCross heredity = mock(HeredityDoubleCross.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		when(heredity.getRandomNumber()).thenReturn(0.5, 0.5, 1d);//first two are split positions, last is mother first
		
		DNA child = heredity.mixDNA(father, mother);
		
		//the child's DNA is only the mothers DNA because the two split positions are the same value
		assertArrayEquals(mother.getDNACode(), child.getDNACode(), 1e-8);
	}
	
	@Test
	public void testSwitchPositionOneBiggerThanTwo() {
		//same as testCombinationOfFatherAndMother() but with switched split positions (as generated in the random generator)
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		HeredityDoubleCross heredity = mock(HeredityDoubleCross.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		when(heredity.getRandomNumber()).thenReturn(0.8, 0.4, 0d);//first two are split positions, last is father first
		
		DNA child = heredity.mixDNA(father, mother);
		
		double[] childsFirstPartCode = Arrays.copyOfRange(child.getDNACode(), 0, 4);
		double[] childsSecondPartCode = Arrays.copyOfRange(child.getDNACode(), 4, 8);
		double[] childsLastPartCode = Arrays.copyOfRange(child.getDNACode(), 8, 10);
		
		double[] fathersFirstPartCode = Arrays.copyOfRange(father.getDNACode(), 0, 4);
		double[] fathersLastPartCode = Arrays.copyOfRange(father.getDNACode(), 8, 10);
		double[] mothersMiddlePartCode = Arrays.copyOfRange(mother.getDNACode(), 4, 8);
		
		assertArrayEquals(fathersFirstPartCode, childsFirstPartCode, 1e-8);
		assertArrayEquals(mothersMiddlePartCode, childsSecondPartCode, 1e-8);
		assertArrayEquals(fathersLastPartCode, childsLastPartCode, 1e-8);
	}
}