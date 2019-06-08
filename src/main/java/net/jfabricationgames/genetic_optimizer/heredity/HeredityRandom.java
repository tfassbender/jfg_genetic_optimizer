package net.jfabricationgames.genetic_optimizer.heredity;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class HeredityRandom implements Heredity {
	
	private double probabilityFather;
	
	/**
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public HeredityRandom() {
		
	}
	
	/**
	 * Generate a new Heredity that is based on a random choosing of father or mother chromosoms.
	 * 
	 * @param proabilityFather
	 *        The probability to choose the fathers chromosom.
	 */
	public HeredityRandom(double probabilityFather) {
		this.probabilityFather = probabilityFather;
	}
	
	@Override
	public DNA mixDNA(DNA father, DNA mother) {
		DNA child = new DNA(father.getLength());
		double[] dnaCode = child.getDNACode();
		double[] fathersCode = father.getDNACode();
		double[] mothersCode = mother.getDNACode();
		
		for (int i = 0; i < father.getLength(); i++) {
			if (getRandomNumber() > probabilityFather) {
				dnaCode[i] = mothersCode[i];
			}
			else {
				dnaCode[i] = fathersCode[i];
			}
		}
		
		return child;
	}
	
	@Override
	public String toString() {
		return "HeredityRandom [probabilityFather=" + probabilityFather + "]";
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	public double getProbabilityFather() {
		return probabilityFather;
	}
}