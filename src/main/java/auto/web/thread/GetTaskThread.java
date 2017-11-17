package auto.web.thread;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import auto.web.App;
import auto.web.common.SystemConfig;
import auto.web.common.TaskInfo;

//获取任务线程
public class GetTaskThread implements Runnable {
	private final Logger LOG = LoggerFactory.getLogger(GetTaskThread.class);
	private PriorityBlockingQueue<TaskInfo> taskqueue;
	private boolean brun = true;
	//
	private SystemConfig systemconfig;

	public GetTaskThread(PriorityBlockingQueue<TaskInfo> taskqueue, SystemConfig systemconfig) {
		this.taskqueue = taskqueue;
		this.systemconfig = systemconfig;
	}

	// 停止任务
	public void stopTask(boolean brun) {
		this.brun = !brun;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		TaskInfo task;
		while (brun) {
			// 获取任务
			// 测试从本地文件获取
			// 读取测试文件
			String sTestFilePath = System.getProperty("user.dir").concat("\\task\\task.conf");
			File TaskFile = new File(sTestFilePath);
			// 加载任务文件json
			ObjectMapper mapper = new ObjectMapper();
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
