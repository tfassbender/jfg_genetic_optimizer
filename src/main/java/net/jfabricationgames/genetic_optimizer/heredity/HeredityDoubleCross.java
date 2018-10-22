package net.jfabricationgames.genetic_optimizer.heredity;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class HeredityDoubleCross implements Heredity {
	
	@Override
	public DNA mixDNA(DNA father, DNA mother) {
		int n = father.getLength();
		DNA child = new DNA(n);
		
		int splitAt1 = (int) (Math.random() * n);
		int splitAt2 = (int) (Math.random() * n);
		
		DNA first = father;
		DNA second = mother;
		
		if (splitAt1 > splitAt2) {
			int tmp = splitAt2;
			splitAt2 = splitAt1;
			splitAt1 = tmp;
		}
		
		if (Math.random() < 0.5) {
			first = mother;
			second = father;
		}
		
		double[] childsDnaCode = child.getDNACode();
		double[] firstDnaCode = first.getDNACode();
		double[] secondDnaCode = second.getDNACode();
		for (int i = 0; i < splitAt1; i++) {
			childsDnaCode[i] = firstDnaCode[i];
		}
		for (int i = splitAt1; i < splitAt2; i++) {
			childsDnaCode[i] = secondDnaCode[i];
		}
		for (int i = splitAt2; i < n; i++) {
			childsDnaCode[i] = firstDnaCode[i];
		}
		
		return child;
	}
}