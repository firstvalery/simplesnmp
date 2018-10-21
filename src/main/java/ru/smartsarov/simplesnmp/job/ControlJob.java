package ru.smartsarov.simplesnmp.job;



import java.sql.SQLException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ControlJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

				try {
					JobsTableAgregator.getCurrentJob(60);
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
	}

}
