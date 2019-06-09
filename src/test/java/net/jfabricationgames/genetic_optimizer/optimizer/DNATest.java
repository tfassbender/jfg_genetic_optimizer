package net.jfabricationgames.genetic_optimizer.optimizer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class DNATest {
	
	@Test
	void testGenerateRandomDNA() {
		DNA dna = DNA.generateRandomDNA(1000, 10);
		double[] dnaCode = dna.getDnaCode();
		int sum = 0;
		for (int i = 0; i < dnaCode.length; i++) {
			sum += dnaCode[i];
			assertTrue(dnaCode[i] < 10);
			assertTrue(dnaCode[i] >= 0);
		}
		double expectedMean = 1000 * 10 * 0.5;
		//assert a small difference from the expected mean
		assertTrue(sum > expectedMean * 0.7);
		assertTrue(sum < expectedMean * 1.3);
	}
	
	@Test
	void testCopyTo() {
		DNA dna = DNA.generateRandomDNA(100, 10);
		DNA dna2 = new DNA(100);
		dna.copyTo(dna2);
		assertArrayEquals(dna.getDnaCode(), dna2.getDnaCode(), 1e-8);
	}
}