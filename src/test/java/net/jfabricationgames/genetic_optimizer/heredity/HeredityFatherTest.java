package net.jfabricationgames.genetic_optimizer.heredity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

class HeredityFatherTest {
	
	private DNA getRandomDNA() {
		double[] dnaCode = new double[10];
		for (int i = 0; i < dnaCode.length; i++) {
			dnaCode[i] = Math.random();
		}
		return new DNA(dnaCode);
	}
	
	@Test
	public void testMixDna() {
		DNA father = getRandomDNA();
		DNA mother = new DNA(0);
		Heredity heredityFather = new HeredityFather();
		
		DNA child = heredityFather.mixDNA(father, mother);
		
		final double epsilon = 1e-8;
		assertArrayEquals(father.getDnaCode(), child.getDnaCode(), epsilon);
	}
}