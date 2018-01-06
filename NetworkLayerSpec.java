package csce6231.bored.net.layer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NetworkLayerSpec implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String type;
	private String functionName;
	private Map<String, String> properties = new HashMap<>();
	
	public NetworkLayerSpec() {}
	
	public NetworkLayerSpec(String type, String functionName) {
		this.type = type;
		this.functionName = functionName;
	}
	
	public String getType() {
		return type;
	}
	
	public NetworkLayerSpec setType(String type) {
		this.type = type;
		return this;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public NetworkLayerSpec setFunctionName(String functionName) {
		this.functionName = functionName;
		return this;
	}
	
	public String[] getProperties() {
		return properties.keySet().toArray(new String[properties.size()]);
	}
	
	public String getStringProperty(String name, String defaultValue) {
		return properties.getOrDefault(name, defaultValue);
	}
	
	public NetworkLayerSpec setStringProperty(String name, String value) {
		properties.put(name, value);
		return this;
	}
	
	public int getIntProperty(String name, int defaultValue) {
		try { return Integer.parseInt(properties.get(name)); }
		catch (Exception e) { return defaultValue; }
	}
	
	public NetworkLayerSpec setIntProperty(String name, int value) {
		properties.put(name, String.valueOf(value));
		return this;
	}
	
	public long getLongProperty(String name, long defaultValue) {
		try { return Long.parseLong(properties.get(name)); }
		catch (Exception e) { return defaultValue; }
	}
	
	public NetworkLayerSpec setLongProperty(String name, long value) {
		properties.put(name, String.valueOf(value));
		return this;
	}
	
	public double getDoubleProperty(String name, double defaultValue) {
		try { return Double.parseDouble(properties.get(name)); }
		catch (Exception e) { return defaultValue; }
	}
	
	public NetworkLayerSpec setDoubleProperty(String name, double value) {
		properties.put(name, String.valueOf(value));
		return this;
	}
}
