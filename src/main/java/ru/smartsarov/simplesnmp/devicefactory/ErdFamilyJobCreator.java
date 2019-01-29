package ru.smartsarov.simplesnmp.devicefactory;
import ru.smartsarov.simplesnmp.job.JobTable;

public class ErdFamilyJobCreator extends JobCreator{
/**
 * Create customized object of DoJob interface
 * This is factory method of abstract JobCreator class
 */
	@Override
	public DoJob createJobber(JobTable job) {
		String str = job.getType();
		
		if(str==null) return new ERDEmpty();//The String in the switch expression is compared with the 
											//expressions associated with each case label as if the String.equals...
											//so it's necessary to check str for null 
		
		switch (str) {
		case "SNRERD3": 
			return new ERDSndRcv();	
		case "SNRERD4":
			return new SnrErd4c();
		default:
			return new ERDEmpty();
		}
	}
}
