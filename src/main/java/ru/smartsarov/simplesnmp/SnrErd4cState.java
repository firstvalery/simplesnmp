package ru.smartsarov.simplesnmp;

import java.time.Instant;

public class SnrErd4cState implements Comparable<SnrErd4cState> {
	public boolean con1;
	public boolean con2;
	public boolean con3;
	public boolean con4;
	public boolean con5;
	public boolean con6;
	public String quality;
	public long ts=0;
	public SnrErd4cState() {
		quality = "bad";
		con1 = false;
		con2 = false;
		con3 = false;
		con4 = false;
		con5 = false;
		con6 = false;
		ts = Instant.now().toEpochMilli();
	}
	
	@Override
	public int compareTo(SnrErd4cState obj) {
		if (con1==obj.con1 &&con2==obj.con2 &&con3==obj.con3 
				&&con4==obj.con4 &&con5==obj.con5 &&con6==obj.con6 
						&& quality.equals(obj.quality))return 0;
		return 1;
	}
}
