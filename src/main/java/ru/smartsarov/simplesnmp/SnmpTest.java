package ru.smartsarov.simplesnmp;


import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpTest implements ResponseListener {
private String sendResult = null;
	static final int REPEAT_NUMBER = 2; 
	static final int TIMEOUT_MS = 3000; 
/*
 * Use asynchronously request to set data 
 * 
 * */
public void snmpSetInt(String host, String community, String strOID, int Value) {
  try {
	Snmp snmp;
	//Creates a UDP transport with an arbitrary local port on all local interfaces.
    TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
    //create target
    CommunityTarget target = createTarget(host, community, REPEAT_NUMBER, TIMEOUT_MS);
    //  create PDU
    PDU pdu = new PDU();
    //Depending on the MIB attribute type, appropriate casting can be done here
    //Configuring PDU for SetRequest by OID strOID, Value
    pdu.add(new VariableBinding(new OID(strOID), new Integer32(Value))); 
    pdu.setType(PDU.SET);
    snmp = new Snmp(transport);
    try {	
    	transport.listen();
    	//send PDU to target and define listener instance to process incoming response PDU 
    	snmp.send(pdu, target, null, null);// this - our class is listener
    }
    finally {
    //Close snmp
    snmp.close();
    }
  } catch (Exception e) {
    e.printStackTrace();
  }
}
 
/**
 * Create Target with required parameters
 * 
 */
private static CommunityTarget createTarget(String host, String community, int retries, int timeout) {
	
    host= host+"/"+	SnmpConstants.DEFAULT_COMMAND_RESPONDER_PORT;//xxx.xxx.xxx.xxx/161 - 
    
 // Target configuration
    CommunityTarget target = new CommunityTarget();
    target.setCommunity(new OctetString(community));
    target.setAddress(GenericAddress.parse(host));
    target.setRetries(retries);
    target.setTimeout(timeout);
    target.setVersion(SnmpConstants.version1); //Set the correct SNMP version here
    
	return target;
}

public String getSendResult() {
	return sendResult;
}

public void setSendResult(String sendResult) {
	this.sendResult = sendResult;
}

@Override
public void onResponse(ResponseEvent event) {
	// TODO Auto-generated method stub
	((Snmp)event.getSource()).cancel(event.getRequest(), this);
	setSendResult("Send success");
}


/*
 * Use synchronously Get Data by SNMP request
 * */

public int snmpGetInt(String host, String community, String strOID) {
	  try {
		Snmp snmp;
		//Creates a UDP transport with an arbitrary local port on all local interfaces.
	    TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();

	    //create target
	    CommunityTarget target = createTarget(host, community, REPEAT_NUMBER, TIMEOUT_MS);
	    
	    //  create and prepare PDU
	    PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(strOID)));
	    pdu.setType(PDU.GET);
	    
	    snmp = new Snmp(transport);
	    try {
	    	transport.listen();     
	    	//Synchronously send PDU to target
	    	ResponseEvent rs = snmp.get(pdu, target);
	    	if(rs!=null && (rs.getResponse().getErrorStatus()==0)) {
	    		return rs.getResponse().getVariableBindings().firstElement().getVariable().toInt();
	    	}	   
	    }finally {
	    //Close snmp
	    	snmp.close();
	    }
	  } catch (Exception e) {
		return -1;
	  }
	return -1;
	}
}