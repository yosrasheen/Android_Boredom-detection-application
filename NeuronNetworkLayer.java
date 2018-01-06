package csce6231.bored.net.layer;

import java.util.function.DoubleSupplier;

import csce6231.bored.net.func.ActivationFunction;
import csce6231.bored.util.Matrix;

public abstract class NeuronNetworkLayer extends NetworkLayer {
	
	protected Matrix weights;
	protected Matrix biases;
	protected ActivationFunction function;
	
	protected NeuronNetworkLayer(NetworkLayerSpec spec) {
		super(spec);
		this.function = ActivationFunction.get(spec.getFunctionName());
	}
	
	protected void initWeights(int inputSize, int outputSize) {
		final double maxInitialWeight = 1.0 / Math.sqrt(inputSize);
		final double minInitialWeight = -maxInitialWeight;

		Matrix.ValueProvider rnd = new Matrix.ValueProvider() {
			@Override
			public double getValue() {
				return Math.random() * (maxInitialWeight - minInitialWeight) + minInitialWeight;
			}
		};
		
		this.weights = Matrix.create(outputSize, inputSize, rnd);
		this.biases = Matrix.create(outputSize, 1, rnd);
	}

	public Matrix getWeights() {
		return weights;
	}
	
	public void setWeights(Matrix weights) {
		this.weights = weights;
	}
	
	public Matrix getBiases() {
		return biases;
	}
	
	public void setBiases(Matrix biases) {
		this.biases = biases;
	}
	
	@Override
	public NetworkLayerConfig getConfig() {
		return new NetworkLayerConfig(getSpec(), weights, biases);
	}
	
	@Override
	public void configure(NetworkLayerConfig config) {
		this.weights = config.getWeights();
		this.biases = config.getBiases();
	}
	
	@Override
	public ActivationFunction getFunction() {
		return function;
	}
}
