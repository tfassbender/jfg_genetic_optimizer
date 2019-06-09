package net.jfabricationgames.genetic_optimizer.heredity;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class HeredityDoubleCross implements Heredity {
	
	@Override
	public DNA mixDNA(DNA father, DNA mother) {
		int n = father.getLength();
		DNA child = new DNA(n);
		
		int splitAt1 = (int) (getRandomNumber() * n);
		int splitAt2 = (int) (getRandomNumber() * n);
		
		if (splitAt1 > splitAt2) {
			int tmp = splitAt2;
			splitAt2 = splitAt1;
			splitAt1 = tmp;
		}
		
		DNA first;
		DNA second;
		
		if (getRandomNumber() < 0.5) {
			first = father;
			second = mother;
		}
		else {
			first = mother;
			second = father;
		}
		
		double[] childsDnaCode = child.getDnaCode();
		double[] firstDnaCode = first.getDnaCode();
		double[] secondDnaCode = second.getDnaCode();
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
	
	@Override
	public String toString() {
		return "HeredityDoubleCross []";
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
}