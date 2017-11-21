package auto.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import auto.web.common.CheckConfig;
import auto.web.common.ModuleInfo;
import auto.web.common.SystemConfig;
import auto.web.common.TaskInfo;
import auto.web.common.TaskInfoComparator;
import auto.web.define.CommandInfo;
import auto.web.define.StatusEnum;
import auto.web.thread.ExecuteTaskThread;
import auto.web.thread.GetTaskThread;

public class App {
	// 日志类
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	// 当前系统配置
	public static SystemConfig systemconfig;
	// 公共模块map
	public static HashMap<String, ModuleInfo> commonModule = new HashMap<String, ModuleInfo>();

	public static void main(String[] args) {
		// 测试使用
		// TestJackson();
		LOG.info("Starting...");
		// 初始化系统配置
		if (LoadSystemConfig() != 0) {
			return;
		}
		// 加载通用模块
		LoadCommonModule();
		// 任务队列
		PriorityBlockingQueue<TaskInfo> taskqueue = new PriorityBlockingQueue<TaskInfo>(200, new TaskInfoComparator());
		Thread GetThread = new Thread(new GetTaskThread(taskqueue, systemconfig, commonModule));
		Thread ExecThread = new Thread(new ExecuteTaskThread(taskqueue, systemconfig, commonModule));
		GetThread.start();
		ExecThread.start();
		try {
			GetThread.join();
			ExecThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("thread.join:" + e.getMessage());
		}
		LOG.info("Exit...");
	}

	public static int LoadSystemConfig() {
		// 获取配置文件路径，判断文件是否存在
		String sFilePath = System.getProperty("user.dir").concat("\\conf\\system.conf");
		File ConfigFile = new File(sFilePath);
		LOG.info("loading:" + sFilePath);
		// 加载配置文件json
		ObjectMapper mapper = new ObjectMapper();
		try {
			systemconfig = mapper.readValue(ConfigFile, SystemConfig.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("LoadSystemConfig JsonParseException:" + e.getMessage());
			return 1;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("LoadSystemConfig JsonMappingException:" + e.getMessage());
			return 2;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("LoadSystemConfig IOException:" + e.getMessage());
			return 3;
		}
		// 设置系统环境变量
		for (Entry<String, String> entry : systemconfig.property.entrySet()) {
			if (entry.getKey().isEmpty() == false) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
		}
		LOG.info("LoadSystemConfig:" + sFilePath);
		return 0;
	}

	public static int LoadCommonModule() {
		CheckConfig checkconfig = new CheckConfig(systemconfig);
		ObjectMapper mapper = new ObjectMapper();
		ModuleInfo module = null;
		StatusEnum status = StatusEnum.SUCESS;
		File TaskFile;
		String sCurPath = System.getProperty("user.dir");
		String sFilePath = "";
		for (String taskpath : systemconfig.preload) {
			sFilePath = sCurPath.concat("\\conf\\").concat(taskpath);
			TaskFile = new File(sFilePath);
			LOG.info("loading:" + sFilePath);
			try {
				module = mapper.readValue(TaskFile, ModuleInfo.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadCommonTask JsonParseException:" + e.getMessage());
				continue;
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadCommonTask JsonMappingException:" + e.getMessage());
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadCommonTask IOException:" + e.getMessage());
				continue;
			}
			status = checkconfig.CheckModule(module);
			if (status != StatusEnum.SUCESS) {
				LOG.error("CheckModule: " + status + ", " + sFilePath);
				continue;
			}
			if (commonModule.get(module.id) != null) {
				LOG.warn("module id exist:" + module.id + ", " + sFilePath);
			}
			commonModule.put(module.id, module);
			LOG.info("LoadCommonModule:" + module.id + ", " + sFilePath);
		}
		return 0;
	}

	public static void TestJackson() {
		TaskInfo task = new TaskInfo();
		task.taskname = "test";
		task.priority = 3;
		task.starttime = new Date();

		CommandInfo cmd = new CommandInfo();
		cmd.index = 1;

		List<CommandInfo> cmdlist = new ArrayList<CommandInfo>();
		cmdlist.add(cmd);
		cmdlist.add(cmd);

		ModuleInfo module = new ModuleInfo();
		module.cmdlist = cmdlist;

		task.step = new ArrayList<ModuleInfo>();
		task.step.add(module);

		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(task);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(json);
	}
}
