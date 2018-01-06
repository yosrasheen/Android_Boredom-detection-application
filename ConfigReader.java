package csce6231.bored.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import csce6231.bored.net.layer.NetworkLayerConfig;
import csce6231.bored.net.layer.NetworkLayerSpec;

public class ConfigReader implements Closeable {

	private BufferedReader reader;
	
	public ConfigReader(String path) throws IOException {
		this(new File(path));
	}
	
	public ConfigReader(File file) throws IOException {
		this(new FileInputStream(file));
	}
	
	public ConfigReader(InputStream in) {
		this.reader = new BufferedReader(new InputStreamReader(in));
	}
	
	@Override
	public void close() throws IOException {
		this.reader.close();
	}
	
	public String readString() {
		try { return reader.readLine(); }
		catch (Exception e) { return null; }
	}
	
	public int readInt() {
		return Integer.parseInt(readString());
	}
	
	public long readLong() {
		return Long.parseLong(readString());
	}
	
	public double readDouble() {
		return Double.parseDouble(readString());
	}
	
	public Matrix readMatrix() {
		int rows = readInt();
		int columns = readInt();
		
		if (rows == 0 || columns == 0) {
			return null;
			
		} else {
			Matrix m = Matrix.create(rows, columns);
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < columns; c++) {
					m.set(r, c, readDouble());
				}
			}
			return m;
		}
	}
	
	public NetworkLayerSpec readNetworkLayerSpec() {
		NetworkLayerSpec spec = new NetworkLayerSpec();
		spec.setType(readString());
		spec.setFunctionName(readString());
		
		int count = readInt();
		while (count-- > 0) {
			String name = readString();
			String value = readString();
			spec.setStringProperty(name, value);
		}
		
		return spec;
	}
	
	public NetworkLayerConfig readNetworkLayerConfig() {
		NetworkLayerConfig config = new NetworkLayerConfig();
		config.setSpec(readNetworkLayerSpec());
		config.setWeights(readMatrix());
		config.setBiases(readMatrix());
		return config;
	}
	
	public NetworkLayerSpec[] readNetworkSpec() {
		NetworkLayerSpec[] specs = new NetworkLayerSpec[readInt()];
		for (int i = 0; i < specs.length; i++) {
			specs[i] = readNetworkLayerSpec();
		}
		return specs;
	}
	
	public NetworkLayerConfig[] readNetworkConfig() {
		NetworkLayerConfig[] configs = new NetworkLayerConfig[readInt()];
		for (int i = 0; i < configs.length; i++) {
			configs[i] = readNetworkLayerConfig();
		}
		return configs;
	}
}
