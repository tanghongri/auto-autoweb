package auto.web;

import java.util.List;

public class TaskInfo {
	public String TaskName="";
	public List<CommandInfo> cmdList;
	
	public String getTaskName() {
		return TaskName;
	}
	public void setTaskName(String taskName) {
		TaskName = taskName;
	}
}
