package auto.web.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import auto.web.define.CommandInfo;

//模块信息类
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleInfo {
	public String id = "";
	public String name = "";
	public String type = "";
	public int retime = 30;
	public int recount = 30;
	public List<CommandInfo> cmdlist;
}
