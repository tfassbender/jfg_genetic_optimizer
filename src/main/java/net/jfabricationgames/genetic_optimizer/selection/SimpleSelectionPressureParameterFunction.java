package net.jfabricationgames.genetic_optimizer.selection;

public class SimpleSelectionPressureParameterFunction implements SelectionPressureParameterFunction {
	
	private double gradientGeneration;
	private double gradientTimeUsed;
	private double bias;
	
	/**
	 * DefaultConstructor for XML-Encoder. DO NOT USE.
	 */
	@Deprecated
	public SimpleSelectionPressureParameterFunction() {
		
	}
	public SimpleSelectionPressureParameterFunction(double gradientGeneration, double gradientTimeUsed, double bias) {
		this.gradientGeneration = gradientGeneration;
		this.gradientTimeUsed = gradientTimeUsed;
		this.bias = bias;
	}
	
	@Override
	public double getParameterValue(int generation, long timeUsed) {
		return gradientGeneration * generation + gradientTimeUsed * timeUsed + bias;
	}
	
	@Override
	public String toString() {
		return "SimpleSelectionPressureParameterFunction [gradientGeneration=" + gradientGeneration + ", gradientTimeUsed=" + gradientTimeUsed
				+ ", bias=" + bias + "]";
	}
	public double getGradientGeneration() {
		return gradientGeneration;
	}
	public void setGradientGeneration(double gradientGeneration) {
		this.gradientGeneration = gradientGeneration;
	}
	
	public double getGradientTimeUsed() {
		return gradientTimeUsed;
	}
	public void setGradientTimeUsed(double gradientTimeUsed) {
		this.gradientTimeUsed = gradientTimeUsed;
	}
	
	public double getBias() {
		return bias;
	}
	public void setBias(double bias) {
		this.bias = bias;
	}
}