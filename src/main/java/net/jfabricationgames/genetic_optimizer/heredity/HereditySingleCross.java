package net.jfabricationgames.genetic_optimizer.heredity;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class HereditySingleCross implements Heredity {
	
	@Override
	public DNA mixDNA(DNA father, DNA mother) {
		int n = mother.getLength();
		DNA child = new DNA(n);
		
		int splitAt = (int) (Math.random() * n);
		
		DNA first = father;
		DNA second = mother;
		if (Math.random() < 0.5) {
			first = mother;
			second = father;
		}
		
		double[] childsDnaCode = child.getDNACode();
		double[] firstDnaCode = first.getDNACode();
		double[] secondDnaCode = second.getDNACode();
		for (int i = 0; i < splitAt; i++) {
			childsDnaCode[i] = firstDnaCode[i];
		}
		for (int i = splitAt; i < n; i++) {
			childsDnaCode[i] = secondDnaCode[i];
		}
		
		return child;
	}
}