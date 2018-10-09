package ru.smartsarov.simplesnmp;

import com.google.gson.Gson;



public class ERDSndRcv {
	static final String TARGET_IP_ADDRESS = "192.168.10.20";
	static final String RESET_SMART_CONTACT_DO1_OID = "1.3.6.1.4.1.40418.2.2.2.1";
	static final String REMOTE_CONTROL_CONTACT_DO2_OID = "1.3.6.1.4.1.40418.2.2.2.3 ";
	static final String MONITOR_SENSOR1 = "1.3.6.1.4.1.40418.2.2.3.3";
	static final String MONITOR_SENSOR2 = "1.3.6.1.4.1.40418.2.2.3.4";
	static final String MONITOR_SENSOR3 = "1.3.6.1.4.1.40418.2.2.3.5";
	static final int RESET_SMART_CONTACT_DO1_VALUE = 1;
	static final int MAN_OFF_SMART_CONTACT_DO1_VALUE = 0;
	static final int MAN_ON_SMART_CONTACT_DO1_VALUE = 1;
	static final String COMMUNITY_NAME = "public"; 	
	static final int REPEAT_NUMBER = 2; 
	static final int TIMEOUT_MS = 3000; 	
	
	
	/*
	 * Static method for send PDU to reset contact DO1
	 * 
	 * */
	static void erdSendOn() {
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(TARGET_IP_ADDRESS, COMMUNITY_NAME, RESET_SMART_CONTACT_DO1_OID, RESET_SMART_CONTACT_DO1_VALUE);	
	}
	
	
	/*
	 * Static method for send to 2 PDU to reset contact DO2
	 * 
	 * */
	static void erdSendOff() {
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(TARGET_IP_ADDRESS, COMMUNITY_NAME, REMOTE_CONTROL_CONTACT_DO2_OID, MAN_ON_SMART_CONTACT_DO1_VALUE);	
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		st.snmpSetInt(TARGET_IP_ADDRESS, COMMUNITY_NAME, REMOTE_CONTROL_CONTACT_DO2_OID, MAN_OFF_SMART_CONTACT_DO1_VALUE);	
	}	
	
	/*
	 * Static method to Get Information about state of DO/DI contacts
	 * 
	 * */
	static String erdGetInfo() {
		SnmpTest st = new SnmpTest();
		Gson gs = new Gson();
		int ec = st.snmpGetInt(TARGET_IP_ADDRESS, COMMUNITY_NAME, MONITOR_SENSOR1);
		if (ec >= 0) { 
			int mp = st.snmpGetInt(TARGET_IP_ADDRESS, COMMUNITY_NAME, MONITOR_SENSOR2);
			if (mp >= 0) {
				int rs = st.snmpGetInt(TARGET_IP_ADDRESS, COMMUNITY_NAME, MONITOR_SENSOR3);
				if (rs >= 0) {
					ERDState erd = new ERDState("Time", ec==1, rs==1, mp==1);
					return gs.toJson(erd);
				}
			}
		}

		return gs.toJson(new ERDState("Time"));
	}
	
/*
 * Get timestamp
 * 
 * 
 * 
 * */	
	public String GetTimestamp() {
		
		
		
		return null;
	}
	
	
}
