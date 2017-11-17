package auto.web.common;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import auto.web.define.CommandInfo;

//任务信息类
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskInfo {
	// 任务名称
	public String taskname = "";
	// 优先级
	public int priority = 0;
	// 任务开始时间要求
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	public Date starttime = null;
	// 任务列表
	public List<List<CommandInfo>> step;
}
