package csce6231.bored.net.layer;

import csce6231.bored.net.func.ErrorFunction;
import csce6231.bored.net.func.Function;
import csce6231.bored.util.Matrix;

public abstract class NetworkLayer {

	public static final String FULLY_CONNECTED = "fc";
	public static final String RECURRENT = "recurrent";
	public static final String CONVOLUTION = "convolution";
	public static final String POOL = "pool";
	
	public static NetworkLayer create(NetworkLayerConfig config) {
		NetworkLayer nl = create(config.getSpec());
		nl.configure(config);
		return nl;
	}
	
	public static NetworkLayer create(NetworkLayerSpec spec) {
		switch (spec.getType()) {
			case FULLY_CONNECTED: return new FullyConnectedNetworkLayer(spec);
//			case RECURRENT: return new RecurrentNetworkLayer(spec);
//			case CONVOLUTION: return new ConvolutionNetworkLayer(spec);
//			case POOL: return new PoolNetworkLayer(spec);
			default: throw new IllegalArgumentException("Invalid network layer type: " + spec.getType());
		}
	}

	protected final NetworkLayerSpec spec;
	protected NetworkLayer nextLayer;
	
	protected NetworkLayer(NetworkLayerSpec spec) {
		this.spec = spec;
	}

	public NetworkLayerSpec getSpec() {
		return spec;
	}
	
	public abstract NetworkLayerConfig getConfig();
	
	public NetworkLayer getNextLayer() {
		return nextLayer;
	}
	
	public void setNextLayer(NetworkLayer nextLayer) {
		this.nextLayer = nextLayer;
	}
	
	public abstract void configure(NetworkLayerConfig config);
	
	public Matrix apply(Matrix input) {
		Matrix output = adjustSize(applyFunction(calculateNets(input)));
		if (nextLayer != null) { return nextLayer.apply(output); }
		else { return output; }
	}
	
	protected abstract Matrix calculateNets(Matrix input);
	
	protected abstract Function getFunction();
	
	protected Matrix applyFunction(Matrix nets) {
		return getFunction().apply(nets);
	}
	
	protected Matrix applyFunctionDerivative(Matrix nets) {
		return getFunction().applyDerivative(nets);
	}
	
	protected Matrix adjustSize(Matrix functionOutput) { return functionOutput; }
	protected Matrix reverseAdjustSize(Matrix output) { return output; }
	
	public void reset() {
		if (nextLayer != null) { nextLayer.reset(); }
	}
	
	protected abstract Matrix trainIteration(Matrix input, Matrix nets, Matrix delta, boolean inputLayer);
	
	public Matrix trainIteration(Matrix input, Matrix target, ErrorFunction errorFunction, final double learningRate, boolean inputLayer) {
		// Calculate output
		Matrix nets = calculateNets(input);
		Matrix functionOutput = applyFunction(nets);
		Matrix output = adjustSize(functionOutput);
		
		// Calculate delta of next layer
		Matrix delta;
		
		// If this is the output layer, use error function
		if (nextLayer == null) {
			delta = reverseAdjustSize(errorFunction.applyDerivative(output, target))
					.transform(new Matrix.ValueTransformer() {
						@Override
						public double apply(double value) {
							return -learningRate*value;
						}
					});
		}
		
		// If this is a hidden layer, train next layer and get weighted deltas
		else {
			delta = reverseAdjustSize(nextLayer.trainIteration(output, target, errorFunction, learningRate, false));
		}
		
		// Train and return weighted deltas for previous layer
		return trainIteration(input, nets, delta, inputLayer);
	}
}
