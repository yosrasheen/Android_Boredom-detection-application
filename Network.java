package csce6231.bored.net.layer;

import java.util.ArrayList;
import java.util.List;

import csce6231.bored.net.func.ErrorFunction;
import csce6231.bored.util.Matrix;

public class Network {
	
	private NetworkLayer first;
	
	public Network(NetworkLayerSpec ... specs) {
		this.first = NetworkLayer.create(specs[0]);
		
		NetworkLayer last = this.first;
		for (int i = 1; i < specs.length; i++) {
			NetworkLayer n = NetworkLayer.create(specs[i]);
			last.setNextLayer(n);
			last = n;
		}
	}
	
	public Network(NetworkLayerConfig ... configs) {
		this.first = NetworkLayer.create(configs[0]);
		
		NetworkLayer last = this.first;
		for (int i = 1; i < configs.length; i++) {
			NetworkLayer n = NetworkLayer.create(configs[i]);
			last.setNextLayer(n);
			last = n;
		}
	}
	
	public NetworkLayerSpec[] getSpec() {
		List<NetworkLayerSpec> specs = new ArrayList<>();
		
		NetworkLayer n = this.first;
		while (n != null) {
			specs.add(n.getSpec());
			n = n.getNextLayer();
		}
		
		return specs.toArray(new NetworkLayerSpec[specs.size()]);
	}
	
	public NetworkLayerConfig[] getConfig() {
		List<NetworkLayerConfig> configs = new ArrayList<>();
		
		NetworkLayer n = this.first;
		while (n != null) {
			configs.add(n.getConfig());
			n = n.getNextLayer();
		}
		
		return configs.toArray(new NetworkLayerConfig[configs.size()]);
	}
	
	public Matrix apply(Matrix input) {
		return first.apply(input);
	}
	
	public void reset() {
		first.reset();
	}
	
	public void trainIteration(Matrix input, Matrix target, ErrorFunction errorFunction, double learningRate) {
		first.trainIteration(input, target, errorFunction, learningRate, true);
	}
}
