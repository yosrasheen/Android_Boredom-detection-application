package csce6231.bored;

import java.io.ByteArrayInputStream;

import csce6231.bored.net.layer.Network;
import csce6231.bored.util.ConfigReader;

public class Classifier {

	private static final String NETWORK_CONFIG = "2\n" +
			"fc\n" +
			"tanh\n" +
			"2\n" +
			"outputSize\n" +
			"4\n" +
			"inputSize\n" +
			"8\n" +
			"4\n" +
			"8\n" +
			"-0.1447550554686645\n" +
			"0.3425322932210329\n" +
			"0.29401074133607175\n" +
			"0.22006013270903105\n" +
			"-0.1865309528913737\n" +
			"0.19131290185856792\n" +
			"0.03002497399388641\n" +
			"0.2468523203623779\n" +
			"-0.1938672891109681\n" +
			"-0.11562060414239943\n" +
			"-0.21929729637118814\n" +
			"0.3581328852277262\n" +
			"-0.23166394865360726\n" +
			"0.1336200838168313\n" +
			"-0.06974952424104129\n" +
			"0.244742852828717\n" +
			"0.0945145830472058\n" +
			"0.18188768132186262\n" +
			"0.19777689956140027\n" +
			"-0.21398408423988471\n" +
			"0.2694153530053233\n" +
			"-0.22719002163072907\n" +
			"-0.031810974554719894\n" +
			"0.2667703432761191\n" +
			"0.15613655734991708\n" +
			"0.1509956104502311\n" +
			"-0.336514415313325\n" +
			"0.1971762707277553\n" +
			"-0.13587984998081928\n" +
			"0.13838203569985483\n" +
			"0.2434191158191722\n" +
			"0.5461149576006267\n" +
			"4\n" +
			"1\n" +
			"0.08236341652533519\n" +
			"-0.2855768208044374\n" +
			"-0.05644793498735258\n" +
			"0.038296149277508476\n" +
			"fc\n" +
			"tanh\n" +
			"2\n" +
			"outputSize\n" +
			"1\n" +
			"inputSize\n" +
			"4\n" +
			"1\n" +
			"4\n" +
			"-0.19257175874942475\n" +
			"0.23401277749301244\n" +
			"-0.2668370248303831\n" +
			"0.4847488096047231\n" +
			"1\n" +
			"1\n" +
			"-0.10790179672058285\n";

	private Network net;
	private final double threshold;
	
	public Classifier() {
		this(0.077);
	}
	
	public Classifier(double threshold) {
		this.threshold = threshold;

		ConfigReader cr = new ConfigReader(new ByteArrayInputStream(NETWORK_CONFIG.getBytes()));
		try { this.net = new Network(cr.readNetworkConfig()); }
		finally {
			try { cr.close(); }
			catch (Exception e) {}
		}
	}
	
	public boolean isBored(String dataStr) {
		return isBored(new DataRecord(dataStr));
	}
	
	public boolean isBored(DataRecord data) {
		return net.apply(data.toInputMatrix()).get(0, 0) > threshold;
	}
}
