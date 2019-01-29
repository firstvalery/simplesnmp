package ru.smartsarov.simplesnmp.devicefactory;

import com.google.gson.Gson;

import ru.smartsarov.simplesnmp.SnmpTest;
import ru.smartsarov.simplesnmp.SnrErd4cState;
import ru.smartsarov.simplesnmp.job.JobTable;
import ru.smartsarov.simplesnmp.job.JobsTableAgregator;

public class SnrErd4c implements DoJob{
	static final String ERD_DIGITAL_OUTPUT_OID = "1.3.6.1.4.1.40418.2.6.2.2.1.3.";
	static final int SET_OFF_VALUE = 0;
	static final int SET_ON_VALUE = 1;
	static final int SET_RELOAD_VALUE = 2;
	
	
	/**
	 * Turn On DO number "contactNum". 
	 */
	public static void erdSendOn(int contactNum, String ipAddr, String community) {
		if ((contactNum<1)&&(contactNum>6)) { 
			SnmpTest st = new SnmpTest();
			st.snmpSetInt(ipAddr, community, ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_ON_VALUE);
		}
	}
	
	/**
	 * Turn On DO number "contactNum". Returns JSON formatted State message
	 */
	public static String erdSendOnAndCheck(int contactNum, String ipAddr, String community) {
		if ((contactNum<1)&&(contactNum>6)) 
			return JobsTableAgregator.getJsonMessage("contactNum must be int and from {1,2,3,4,5,6}");	
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(ipAddr, community, ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_ON_VALUE);
	return getContactState( ipAddr,  community);
	}
	
	
	
	/**
	 * Turn Off DO number "contactNum".
	 * 
	 */
	public static void erdSendOff(int contactNum, String ipAddr, String community) {
		if ((contactNum<1)&&(contactNum>6)) {
			SnmpTest st = new SnmpTest();
			st.snmpSetInt(ipAddr, community, ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_OFF_VALUE);
		}
	}
	/**
	 * Turn Off DO number "contactNum". Returns JSON formatted message
	 * 
	 */
	public static String erdSendOffAndCheck(int contactNum, String ipAddr, String community) {
		if ((contactNum<1)&&(contactNum>6)) 
			return JobsTableAgregator.getJsonMessage("contactNum must be int and from {1,2,3,4,5,6}");
		
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(ipAddr, community, ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_OFF_VALUE);
		return getContactState( ipAddr,  community);
	}
	
	/**
	 * Reload DO number "contactNum". 
	 */
	public static void erdSendReload(int contactNum,String ipAddr, String community) {
		if ((contactNum<1)&&(contactNum>6)) {	
			SnmpTest st = new SnmpTest();
			st.snmpSetInt(ipAddr, community, ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_RELOAD_VALUE);	
		}
	}
	
	/**
	 * Reload DO number "contactNum". Returns JSON formatted message
	 */
	public static String erdSendReloadAndCheck(int contactNum,String ipAddr, String community) {
		if ((contactNum<1)&&(contactNum>6)) 
			return JobsTableAgregator.getJsonMessage("contactNum must be int and from {1,2,3,4,5,6}");
		
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(ipAddr, community, ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_RELOAD_VALUE);	
		return getContactState( ipAddr,  community);
	}
	
	
	
	/**
	 * Returns JSON formatted state of digital outputs 
	 */
	public static String getContactState(String ipAddr, String community) {
		SnmpTest st = new SnmpTest();
		SnrErd4cState js = new SnrErd4cState();
		int ec = st.snmpGetInt(ipAddr,  community, ERD_DIGITAL_OUTPUT_OID.concat("1"));
		if (ec>=0) {
			js.con1 = ec==1;
			ec = st.snmpGetInt(ipAddr,  community, ERD_DIGITAL_OUTPUT_OID.concat("2"));
			if (ec>=0) {
				js.con2 = ec==1;
				ec = st.snmpGetInt(ipAddr,  community, ERD_DIGITAL_OUTPUT_OID.concat("3"));	
				if (ec>=0) {
					js.con3 = ec==1;
					ec = st.snmpGetInt(ipAddr,  community, ERD_DIGITAL_OUTPUT_OID.concat("4"));
					if (ec>=0) {
						js.con4 = ec==1;
						ec = st.snmpGetInt(ipAddr,  community, ERD_DIGITAL_OUTPUT_OID.concat("5"));	
						if (ec>=0) {
							js.con5 = ec==1;
							ec = st.snmpGetInt(ipAddr,  community, ERD_DIGITAL_OUTPUT_OID.concat("6"));	
							if (ec>=0) {
								js.con6 = ec==1;
								js.quality = "good";
								return new Gson().toJson(js); 
							}
						}
					}
				}
			}
		} 
		return new Gson().toJson(new SnrErd4cState());
	}

/**
 * DoJob interface Implementation
 */
	@Override
	public void doCmd(JobTable j) {
		if(j.getCommand()%2==1)
			erdSendOn(j.getCommand()/2+1, j.getIp(), j.getCommunity());
		else 
			erdSendOff(j.getCommand()/2, j.getIp(), j.getCommunity());
	}
}
