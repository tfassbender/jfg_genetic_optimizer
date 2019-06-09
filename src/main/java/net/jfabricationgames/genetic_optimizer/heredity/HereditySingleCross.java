package net.jfabricationgames.genetic_optimizer.heredity;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

import net.jfabricationgames.genetic_optimizer.optimizer.DNA;

public class HereditySingleCross implements Heredity {
	
	@Override
	public DNA mixDNA(DNA father, DNA mother) {
		int n = mother.getLength();
		DNA child = new DNA(n);
		
		int splitAt = (int) (getRandomNumber() * n);
		
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
		for (int i = 0; i < splitAt; i++) {
			childsDnaCode[i] = firstDnaCode[i];
		}
		for (int i = splitAt; i < n; i++) {
			childsDnaCode[i] = secondDnaCode[i];
		}
		
		return child;
	}
	
	@Override
	public String toString() {
		return "HereditySingleCross []";
	}
	
	@VisibleForTesting
	/*private*/ double getRandomNumber() {
		return ThreadLocalRandom.current().nextDouble();
	}
}