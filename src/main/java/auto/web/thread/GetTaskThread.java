package auto.web.thread;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import auto.web.common.CheckConfig;
import auto.web.common.ModuleInfo;
import auto.web.common.SystemConfig;
import auto.web.common.TaskInfo;
import auto.web.define.StatusEnum;

//获取任务线程
public class GetTaskThread implements Runnable {
	private final Logger LOG = LoggerFactory.getLogger(GetTaskThread.class);

	private boolean brun = true;
	private PriorityBlockingQueue<TaskInfo> taskqueue;
	private SystemConfig systemconfig;
	private HashMap<String, ModuleInfo> commonModule;

	public GetTaskThread(PriorityBlockingQueue<TaskInfo> taskqueue, SystemConfig systemconfig,
			HashMap<String, ModuleInfo> commonModule) {
		this.taskqueue = taskqueue;
		this.systemconfig = systemconfig;
		this.commonModule = commonModule;
	}

	// 停止任务
	public void stopTask(boolean brun) {
		this.brun = !brun;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		LOG.info("GetTaskThread start");

		TaskInfo task;
		StatusEnum status = StatusEnum.SUCESS;
		ObjectMapper mapper = new ObjectMapper();
		CheckConfig checkconfig = new CheckConfig(systemconfig, commonModule);
		while (brun) {
			// 获取任务
			// 测试从本地文件获取
			// 读取测试文件
			String sTestFilePath = System.getProperty("user.dir").concat("\\task\\task.conf");
			File TaskFile = new File(sTestFilePath);
			// 加载任务文件json
			try {
				task = mapper.readValue(TaskFile, TaskInfo.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadTask JsonParseException:" + e.getMessage());
				continue;
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadTask JsonMappingException:" + e.getMessage());
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("LoadTask IOException:" + e.getMessage());
				continue;
			}
			// 检查任务
			status = checkconfig.CheckTask(task);
			if (status != StatusEnum.SUCESS) {
				LOG.warn("CheckTask error: " + status + ", " + task);
				continue;
			}
			// 任务队列添加
			LOG.info("add task" + task.taskname);
			taskqueue.put(task);
			// 休眠等待
			try {
				Thread.sleep(1000 * systemconfig.waittime);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 测试跳出
			break;
		}
	}
}
