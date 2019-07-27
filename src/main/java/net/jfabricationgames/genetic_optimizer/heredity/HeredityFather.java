package net.jfabricationgames.genetic_optimizer.heredity;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class HeredityFather implements Heredity {
	
	/**
	 * Generate a new child DNA that is just a copy of the father's DNA code.
	 * 
	 * This heredity type can be used to make the genetic optimizer algorithm an evolutional optimizer algorithm.
	 */
	@Override
	public DNA mixDNA(DNA father, DNA mother) {
		DNA child = new DNA(father.getLength());
		father.copyTo(child);
		return child;
	}
	
	@Override
	public String toString() {
		return "HeredityFather []";
	}
}