package ru.smartsarov.simplesnmp;

import java.time.Instant;

import com.google.gson.Gson;



public class ERDSndRcv {
	static final String RESET_SMART_CONTACT_DO1_OID = "1.3.6.1.4.1.40418.2.2.2.1";
	static final String REMOTE_CONTROL_CONTACT_DO2_OID = "1.3.6.1.4.1.40418.2.2.2.3 ";
	static final String MONITOR_SENSOR1 = "1.3.6.1.4.1.40418.2.2.3.3";
	static final String MONITOR_SENSOR2 = "1.3.6.1.4.1.40418.2.2.3.4";
	static final String MONITOR_SENSOR3 = "1.3.6.1.4.1.40418.2.2.3.5";
	static final int RESET_SMART_CONTACT_DO1_VALUE = 1;
	static final int MAN_OFF_SMART_CONTACT_DO1_VALUE = 0;
	static final int MAN_ON_SMART_CONTACT_DO1_VALUE = 1;	
	static final int REPEAT_NUMBER = 2; 
	
	
	/*
	 * Static method for send PDU to reset contact DO1
	 * 
	 * */
	static String erdSendOn() {
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(Props.get().getProperty("simplesnmp.ip", "127.0.0.1"), Props.get().getProperty("simplesnmp.write_community", "public"), RESET_SMART_CONTACT_DO1_OID, RESET_SMART_CONTACT_DO1_VALUE);	
	
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return erdGetInfo();
	}
	
	/*
	 * Static method for send PDU to reset contact DO1
	 * 
	 * */
	public static void erdSendOn(String ip, String community) {
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(ip, community, RESET_SMART_CONTACT_DO1_OID, RESET_SMART_CONTACT_DO1_VALUE);	
	}
	
	
	/*
	 * Static method for send to 2 PDU to reset contact DO2
	 * 
	 * */
	static String erdSendOff() {
		SnmpTest st = new SnmpTest();
		String host = Props.get().getProperty("simplesnmp.ip", "127.0.0.1");
		String community = Props.get().getProperty("simplesnmp.write_community", "public");
		st.snmpSetInt(host, community, REMOTE_CONTROL_CONTACT_DO2_OID, MAN_ON_SMART_CONTACT_DO1_VALUE);	
		int time_out=0;
		try {
			time_out = Integer.parseInt(Props.get().getProperty("simplesnmp.cmd_off_timer", "3000"));
		}catch(NumberFormatException e){
			time_out=3000;
		}
		try {
			Thread.sleep(time_out);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		st.snmpSetInt(host, community, REMOTE_CONTROL_CONTACT_DO2_OID, MAN_OFF_SMART_CONTACT_DO1_VALUE);	
		return erdGetInfo();
	}	
	
	/*
	 * Static method for send to 2 PDU to reset contact DO2
	 * 
	 * */
	public static void erdSendOff(String ip, String community) {
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(ip, community, REMOTE_CONTROL_CONTACT_DO2_OID, MAN_ON_SMART_CONTACT_DO1_VALUE);	
		int time_out=0;
		try {
			time_out = Integer.parseInt(Props.get().getProperty("simplesnmp.cmd_off_timer", "3000"));
		}catch(NumberFormatException e){
			time_out=3000;
		}
		try {
			Thread.sleep(time_out);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		st.snmpSetInt(ip, community, REMOTE_CONTROL_CONTACT_DO2_OID, MAN_OFF_SMART_CONTACT_DO1_VALUE);	
	}
	
	/*
	 * Static method to Get Information about state of DO/DI contacts
	 * 
	 * */
	static String erdGetInfo() {
		SnmpTest st = new SnmpTest();
		String host = Props.get().getProperty("simplesnmp.ip", "127.0.0.1");
		String community = Props.get().getProperty("simplesnmp.read_community", "public");
		Gson gs = new Gson();
		int ec = st.snmpGetInt(host, community, MONITOR_SENSOR1);
		if (ec >= 0) { 
			int mp = st.snmpGetInt(host, community, MONITOR_SENSOR2);
			if (mp >= 0) {
				int rs = st.snmpGetInt(host, community, MONITOR_SENSOR3);
				if (rs >= 0) {
					ERDState erd = new ERDState(Instant.now().toEpochMilli(), ec==1, mp==1, rs==1);
					return gs.toJson(erd);
				}
			}
		}
		return gs.toJson(new ERDState(Instant.now().toEpochMilli()));
	}
}
