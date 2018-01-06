package csce6231.bored.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import csce6231.bored.net.layer.NetworkLayerConfig;
import csce6231.bored.net.layer.NetworkLayerSpec;

public class ConfigWriter implements Closeable {

	private PrintWriter writer;
	
	public ConfigWriter(String path) throws IOException {
		this(new File(path));
	}
	
	public ConfigWriter(File file) throws IOException {
		this(new FileOutputStream(file));
	}
	
	public ConfigWriter(OutputStream out) {
		this.writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
	
	public void writeString(String value) {
		writer.println(value);
	}
	
	public void writeInt(int value) {
		writer.println(value);
	}
	
	public void writeLong(long value) {
		writer.println(value);
	}
	
	public void writeDouble(double value) {
		writer.println(value);
	}

	public void writeMatrix(Matrix m) {
		if (m == null) {
			writeInt(0);
			writeInt(0);
			
		} else {
			writeInt(m.getRows());
			writeInt(m.getColumns());
			for (int r = 0; r < m.getRows(); r++) {
				for (int c = 0; c < m.getColumns(); c++) {
					writeDouble(m.get(r, c));
				}
			}
		}
	}
	
	public void writeNetworkLayerSpec(NetworkLayerSpec spec) {
		writeString(spec.getType());
		writeString(spec.getFunctionName());
		
		String[] properties = spec.getProperties();
		writeInt(properties.length);
		for (String prop : properties) {
			writeString(prop);
			writeString(spec.getStringProperty(prop, ""));
		}
	}
	
	public void writeNetworkLayerConfig(NetworkLayerConfig config) {
		writeNetworkLayerSpec(config.getSpec());
		writeMatrix(config.getWeights());
		writeMatrix(config.getBiases());
	}
	
	public void writeNetworkSpec(NetworkLayerSpec ... specs) {
		writeInt(specs.length);
		for (NetworkLayerSpec spec : specs) {
			writeNetworkLayerSpec(spec);
		}
	}
	
	public void writeNetworkConfig(NetworkLayerConfig ... configs) {
		writeInt(configs.length);
		for (NetworkLayerConfig config : configs) {
			writeNetworkLayerConfig(config);
		}
	}
}
