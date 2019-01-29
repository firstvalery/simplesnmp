package ru.smartsarov.simplesnmp;



public class ERDState implements Comparable<ERDState> {
	public long timestamp=0;
	public String quality="";
	public boolean ec = false;
	public boolean mp = false;
	public boolean rs = false;
	
	public ERDState(long ts, boolean ec, boolean mp, boolean rs){
		this.timestamp = ts;
		quality = "GOOD";
		this.ec = ec;
		this.mp = mp;
		this.rs = rs;
	}
	
	public ERDState(long ts){
		this.timestamp = ts;
		quality = "BAD";
		this.ec = false;
		this.mp = false;
		this.rs = false;
	}

	@Override
	public int compareTo(ERDState o) {
		if(ec==o.ec && mp==o.mp && rs == o.rs && quality==o.quality)
			return 0;
		return 1;
	}
}
