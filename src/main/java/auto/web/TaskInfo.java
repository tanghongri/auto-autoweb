package auto.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskInfo {
	public String id = "";
	public String name = "";
	public String type = "";
	public int retime = 30;
	public int recount = 30;
	public List<CommandInfo> cmdlist;
}
