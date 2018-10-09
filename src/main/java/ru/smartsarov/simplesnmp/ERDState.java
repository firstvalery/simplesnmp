package ru.smartsarov.simplesnmp;



public class ERDState {
	public long timestamp=0;
	public String quallity="";
	public boolean ec = false;
	public boolean mp = false;
	public boolean rs = false;
	
	ERDState(long ts, boolean ec, boolean mp, boolean rs){
		this.timestamp = ts;
		quallity = "GOOD";
		this.ec = ec;
		this.mp = mp;
		this.rs = rs;
	}
	
	ERDState(long ts){
		this.timestamp = ts;
		quallity = "BAD";
		this.ec = false;
		this.mp = false;
		this.rs = false;
	}
}
