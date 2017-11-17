package auto.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import auto.web.common.ModuleInfo;
import auto.web.common.SystemConfig;
import auto.web.common.TaskInfo;
import auto.web.common.TaskInfoComparator;
import auto.web.define.CommandInfo;
import auto.web.thread.ExecuteTask;
import auto.web.thread.GetTaskThread;

public class App {
	// 日志类
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	// 当前系统配置
	private static SystemConfig systemconfig;
	// 当前程序路径
	private static String sCurPath = "";
	// 公共模块map
	private static HashMap<String, ModuleInfo> commonModule = new HashMap<String, ModuleInfo>();

	public static void main(String[] args) {
		// 测试使用
		//TestJackson();

		LOG.info("Starting...");
		// 初始化系统配置
		if (LoadSystemConfig() != 0) {
			return;
		}
		// 加载通用模块
		LoadCommonModule();

		// 任务队列
		PriorityBlockingQueue<TaskInfo> taskqueue = new PriorityBlockingQueue<TaskInfo>(200, new TaskInfoComparator());

		Thread GetThread = new Thread(new GetTaskThread(taskqueue, systemconfig.waittime));
		Thread ExecThread = new Thread(new ExecuteTask(taskqueue));
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
		sCurPath = System.getProperty("user.dir");
		File ConfigFile = new File(sCurPath.concat("\\conf\\system.conf"));

		LOG.info("LoadSystemConfig:" + sCurPath.concat("\\conf\\system.conf"));
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
		return 0;
	}

	public static int LoadCommonModule() {
		ObjectMapper mapper = new ObjectMapper();
		ModuleInfo module = null;
		File TaskFile;
		for (String taskpath : systemconfig.preload) {
			TaskFile = new File(sCurPath.concat("\\conf\\").concat(taskpath));
			try {
				module = mapper.readValue(TaskFile, ModuleInfo.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadCommonTask JsonParseException:" + e.getMessage());
				return 1;
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadCommonTask JsonMappingException:" + e.getMessage());
				return 2;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadCommonTask IOException:" + e.getMessage());
				return 3;
			}
			commonModule.put(module.id, module);
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
		cmd.action = "open";

		List<CommandInfo> cmdlist = new ArrayList<CommandInfo>();
		cmdlist.add(cmd);
		cmdlist.add(cmd);

		List<List<CommandInfo>> testlist = new ArrayList<List<CommandInfo>>();
		testlist.add(cmdlist);
		testlist.add(cmdlist);

		task.step = testlist;

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
