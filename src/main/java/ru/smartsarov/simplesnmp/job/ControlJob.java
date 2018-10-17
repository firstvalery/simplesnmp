package ru.smartsarov.simplesnmp.job;



import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ControlJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

			JobsTableAgregator.getCurrentJob(60);
	}

}
