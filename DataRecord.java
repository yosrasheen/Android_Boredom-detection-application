package csce6231.bored;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import csce6231.bored.util.Matrix;

public class DataRecord {

	private static final int GENDER_UNSPECIFIED = 0;
	private static final int GENDER_MALE = 1;
	private static final int GENDER_FEMALE = 2;
	
	private static final int AGE_UNSPECIFIED = 0;
	
	private int minutesSinceLastIncomingCall;
	private int minutesSinceLastOutgoingCall;
	private int minutesSinceLastIncomingMessage;
	private int minutesSinceLastOutgoingMessage;
	private int minutesSinceLastLock;
	private int age;
	private int gender;
	private int boredLevel;
	
	private DataRecord() {}
	
	public DataRecord(String dataStr) {
		for (String line : dataStr.trim().split("\n")) { parseLine(line); }
	}

	public int getMinutesSinceLastIncomingCall() {
		return minutesSinceLastIncomingCall;
	}

	public void setMinutesSinceLastIncomingCall(int minutesSinceLastIncomingCall) {
		this.minutesSinceLastIncomingCall = minutesSinceLastIncomingCall;
	}

	public int getMinutesSinceLastOutgoingCall() {
		return minutesSinceLastOutgoingCall;
	}

	public void setMinutesSinceLastOutgoingCall(int minutesSinceLastOutgoingCall) {
		this.minutesSinceLastOutgoingCall = minutesSinceLastOutgoingCall;
	}

	public int getMinutesSinceLastIncomingMessage() {
		return minutesSinceLastIncomingMessage;
	}

	public void setMinutesSinceLastIncomingMessage(int minutesSinceLastIncomingMessage) {
		this.minutesSinceLastIncomingMessage = minutesSinceLastIncomingMessage;
	}

	public int getMinutesSinceLastOutgoingMessage() {
		return minutesSinceLastOutgoingMessage;
	}

	public void setMinutesSinceLastOutgoingMessage(int minutesSinceLastOutgoingMessage) {
		this.minutesSinceLastOutgoingMessage = minutesSinceLastOutgoingMessage;
	}

	public int getMinutesSinceLastLock() {
		return minutesSinceLastLock;
	}

	public void setMinutesSinceLastLock(int minutesSinceLastLock) {
		this.minutesSinceLastLock = minutesSinceLastLock;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getBoredLevel() {
		return boredLevel;
	}

	public void setBoredLevel(int boredLevel) {
		this.boredLevel = boredLevel;
	}

	private void parseLine(String line) {
		line = line.trim();
		if (line.isEmpty()) { return; }
		
		String[] elements = line.split(":");
		String key = elements[0].trim();
		String value = elements[1].trim();
		
		switch (key) {
		case "Time since last Incoming Call":
			this.setMinutesSinceLastIncomingCall(Integer.parseInt(value));
			break;
			
		case "Time since last Outgoing Call":
			this.setMinutesSinceLastOutgoingCall(Integer.parseInt(value));
			break;
			
		case "Time since last Incoming message is":
			this.setMinutesSinceLastIncomingMessage(Integer.parseInt(value));
			break;
			
		case "Time since last Outgoing message is":
			this.setMinutesSinceLastOutgoingMessage(Integer.parseInt(value));
			break;
			
		case "Time since last Lock":
			this.setMinutesSinceLastLock(Integer.parseInt(value));
			break;
			
		case "Age":
			if (value.equals("null")) {
				this.setAge(AGE_UNSPECIFIED);
				
			} else {
				this.setAge(Integer.parseInt(value.substring("Between ".length(), "Between ".length() + 2)));
			}
			break;
			
		case "Gender":
			switch (value) {
			case "Female":
				this.setGender(GENDER_FEMALE);
				break;

			case "Make":
				this.setGender(GENDER_MALE);
				break;
				
			default:
				this.setGender(GENDER_UNSPECIFIED);
				break;
			}
			break;
			
		case "Bored level":
			this.setBoredLevel(Integer.parseInt(value));
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public String toString() {
		return "DataRecord [minutesSinceLastIncomingCall=" + minutesSinceLastIncomingCall
				+ ", minutesSinceLastOutgoingCall=" + minutesSinceLastOutgoingCall
				+ ", minutesSinceLastIncomingMessage=" + minutesSinceLastIncomingMessage
				+ ", minutesSinceLastOutgoingMessage=" + minutesSinceLastOutgoingMessage + ", minutesSinceLastLock="
				+ minutesSinceLastLock + ", age=" + age + ", gender=" + gender + ", boredLevel=" + boredLevel + "]";
	}
	
	public Matrix toInputMatrix() {
		Matrix m = Matrix.create(8, 1);
		m.set(0, 0, normalizeMinutes(this.getMinutesSinceLastIncomingCall()));
		m.set(1, 0, normalizeMinutes(this.getMinutesSinceLastOutgoingCall()));
		m.set(2, 0, normalizeMinutes(this.getMinutesSinceLastIncomingCall()));
		m.set(3, 0, normalizeMinutes(this.getMinutesSinceLastIncomingMessage()));
		m.set(4, 0, normalizeMinutes(this.getMinutesSinceLastOutgoingMessage()));
		m.set(5, 0, normalizeMinutes(this.getMinutesSinceLastLock()));
		m.set(6, 0, normalizeAge(this.getAge()));
		m.set(7, 0, normalizeGender(this.getGender()));
		return m;
	}
	
	public Matrix toTargetMatrix() {
		Matrix m = Matrix.create(1, 1);
		m.set(0, 0, normalizeBoredLevel(this.getBoredLevel()));
		return m;
	}
	
	private static double normalizeMinutes(int minutes) {
		return (minutes - 150000.0) / 150000.0;
	}
	
	private static double normalizeAge(int age) {
		return (age - 30.0) / 30.0;
	}
	
	private static double normalizeGender(int gender) {
		return (gender - 1.0);
	}
	
	private static double normalizeBoredLevel(int boredLevel) {
		return (boredLevel > 2.0) ? 1.0 : -1.0;
	}

	public static List<DataRecord> parse(String path) throws Exception {
		return parse(new File(path));
	}
	
	public static List<DataRecord> parse(File file) throws Exception {
		try (InputStream in = new FileInputStream(file)) {
			return parse(in);
		}
	}
	
	public static List<DataRecord> parse(InputStream in) throws Exception {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			List<DataRecord> records = new ArrayList<>();
			DataRecord currentRecord = null;
			
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					if (currentRecord != null) {
						records.add(currentRecord);
						currentRecord = null;
					}
					
				} else {
					if (currentRecord == null) {
						currentRecord = new DataRecord();
					}
					
					currentRecord.parseLine(line);
				}
			}
			
			if (currentRecord != null) {
				records.add(currentRecord);
			}
			
			return records;
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<DataRecord> records = parse("bored-all.txt");
		
		int max = Integer.MIN_VALUE;
		
		for (DataRecord r : records) {
			max = Integer.max(max, r.getMinutesSinceLastIncomingCall());
			max = Integer.max(max, r.getMinutesSinceLastOutgoingCall());
			max = Integer.max(max, r.getMinutesSinceLastIncomingMessage());
			max = Integer.max(max, r.getMinutesSinceLastOutgoingCall());
			max = Integer.max(max, r.getMinutesSinceLastLock());
		}
		
		System.out.println(max);
	}
}
