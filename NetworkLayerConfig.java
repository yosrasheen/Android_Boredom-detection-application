package csce6231.bored.net.layer;

import java.io.Serializable;

import csce6231.bored.util.Matrix;

public class NetworkLayerConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	private NetworkLayerSpec spec;
	private Matrix weights;
	private Matrix biases;
	
	public NetworkLayerConfig() {}
	
	public NetworkLayerConfig(NetworkLayerSpec spec, Matrix weights, Matrix biases) {
		this.spec = spec;
		this.weights = weights;
		this.biases = biases;
	}

	public NetworkLayerSpec getSpec() {
		return spec;
	}
	
	public void setSpec(NetworkLayerSpec spec) {
		this.spec = spec;
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
}
