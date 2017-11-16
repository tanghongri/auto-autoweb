package auto.web.thread;

import java.util.concurrent.PriorityBlockingQueue;

import auto.web.common.TaskInfo;

//获取任务线程
public class GetTaskThread implements Runnable {
	private PriorityBlockingQueue<TaskInfo> taskqueue;
	private boolean brun = true;

	public GetTaskThread(PriorityBlockingQueue<TaskInfo> taskqueue) {
		this.taskqueue = taskqueue;
	}

	// 停止任务
	public void stopTask(boolean brun) {
		this.brun = !brun;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (brun) {
			TaskInfo task = new TaskInfo();
			taskqueue.put(task);
			System.out.println(task + " 开始排队...");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
