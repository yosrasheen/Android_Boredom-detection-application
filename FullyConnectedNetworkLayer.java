package csce6231.bored.net.layer;

import csce6231.bored.net.func.ActivationFunction;
import csce6231.bored.util.Matrix;

public class FullyConnectedNetworkLayer extends NeuronNetworkLayer {

	public FullyConnectedNetworkLayer(NetworkLayerSpec spec) {
		super(spec);
		
		int inputSize = spec.getIntProperty("inputSize", -1);
		int outputSize = spec.getIntProperty("outputSize", -1);
		
		this.initWeights(inputSize, outputSize);
	}
	
	@Override
	public ActivationFunction getFunction() {
		return super.getFunction();
	}

	@Override
	protected Matrix calculateNets(Matrix input) {
		if (input.getRows() != weights.getColumns() || input.getColumns() != 1) {
			throw new IllegalArgumentException("Invalid size of input matrix. Expecting [" + weights.getColumns() + "x1], not [" + input.getRows() + "x" + input.getColumns() + "].");
		}
		
		return weights.multiply(input).subtract(biases);
	}
	
	@Override
	protected Matrix trainIteration(Matrix input, Matrix nets, Matrix delta, boolean inputLayer) {
		// Multiply deltas by function derivative
		Matrix m = delta.multiplyElements(applyFunctionDerivative(nets));
		
		// Calculate delta of previous layer
		Matrix previousLayerDelta = null;
		if (!inputLayer) {
			previousLayerDelta = Matrix.create(input.getRows(), 1);
			for (int i = 0; i < weights.getColumns(); i++) {
				double value = 0;
				for (int o = 0; o < weights.getRows(); o++) {
					value += weights.get(o, i) * m.get(o, 0);
				}
				previousLayerDelta.set(i, 0, value);
			}
		}
		
		// Calculate delta weights
		Matrix deltaWeights = Matrix.create(weights.getRows(), weights.getColumns());
		Matrix deltaBiases = Matrix.create(biases.getRows(), biases.getColumns());
		
		for (int o = 0; o < weights.getRows(); o++) {
			for (int i = 0; i < input.getRows(); i++) {
				deltaWeights.set(o, i, m.get(o, 0) * input.get(i, 0));
			}
			deltaBiases.set(o, 0, m.get(o, 0) * -1);
		}
		
		// Apply weights
		weights = weights.add(deltaWeights);
		biases = biases.add(deltaBiases);
		
		// Done
		return previousLayerDelta;
	}
}
