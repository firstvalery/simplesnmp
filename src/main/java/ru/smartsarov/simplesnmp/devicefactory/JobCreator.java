package ru.smartsarov.simplesnmp.devicefactory;
import ru.smartsarov.simplesnmp.job.JobTable;

public abstract class JobCreator {
	
	/**
	 * Predefined method for creating DoJob object and do CMD
	 */
	public String doJob(JobTable job) {
		DoJob dj = createJobber(job); 
		dj.doCmd(job);	
		return String.valueOf(job.getId());	
	}
	
	/**
	 * abstract factory method for creating DoJob object
	 */
	public abstract DoJob createJobber(JobTable job);
}
