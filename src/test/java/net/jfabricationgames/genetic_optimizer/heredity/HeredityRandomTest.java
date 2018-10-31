package net.jfabricationgames.genetic_optimizer.heredity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class HeredityRandomTest {
	
	@Test
	public void testOnlyFather() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		//create a HeredityRandom where the chance for the fathers chromosomes is 100%
		Heredity heredityFatherOnly = new HeredityRandom(1);
		
		DNA child = heredityFatherOnly.mixDNA(father, mother);
		
		assertArrayEquals(father.getDNACode(), child.getDNACode(), 1e-8);
	}
	
	@Test
	public void testOnlyMother() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		//create a HeredityRandom where the chance for the fathers chromosomes is 100%
		Heredity heredityMotherOnly = new HeredityRandom(0);
		
		DNA child = heredityMotherOnly.mixDNA(father, mother);
		
		assertArrayEquals(mother.getDNACode(), child.getDNACode(), 1e-8);
	}
	
	@Test
	public void testHalfFatherHalfMother() {
		DNA father = DNA.generateRandomDNA(10, 1);
		DNA mother = DNA.generateRandomDNA(10, 1);
		
		//probability father is 0 here but because the mocked random generator returns exactly 0 thats no problem here
		HeredityRandom heredity = mock(HeredityRandom.class);
		when(heredity.mixDNA(any(DNA.class), any(DNA.class))).thenCallRealMethod();
		//return 0 for 5 times to use the fathers chromosome in the first 5 cases; then return 1 for mothers chromosome
		when(heredity.getRandomNumber()).thenReturn(0d, 0d, 0d, 0d, 0d, 1d);
		
		DNA child = heredity.mixDNA(father, mother);
		
		double[] childsFirstHalfCode = Arrays.copyOfRange(child.getDNACode(), 0, 5);
		double[] childsLastHalfCode = Arrays.copyOfRange(child.getDNACode(), 5, 10);
		
		double[] fathersFirstHalfCode = Arrays.copyOfRange(father.getDNACode(), 0, 5);
		double[] mothersLastHalfCode = Arrays.copyOfRange(mother.getDNACode(), 5, 10);
		
		assertArrayEquals(childsFirstHalfCode, fathersFirstHalfCode, 1e-8);
		assertArrayEquals(childsLastHalfCode, mothersLastHalfCode, 1e-8);
	}
}