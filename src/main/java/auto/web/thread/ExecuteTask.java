package auto.web.thread;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import auto.web.common.PropertyInfo;
import auto.web.common.SystemConfig;
import auto.web.common.TaskInfo;
import auto.web.define.BrowserEnum;
import auto.web.define.CommandInfo;

public class ExecuteTask implements Runnable {
	private final Logger LOG = LoggerFactory.getLogger(ExecuteTask.class);
	private PriorityBlockingQueue<TaskInfo> taskqueue;
	private boolean brun = true;
	private SystemConfig systemconfig;

	public ExecuteTask(PriorityBlockingQueue<TaskInfo> taskqueue, SystemConfig systemconfig) {
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
		WebDriver driver = null;
		switch (systemconfig.browser) {
		case BROWER_FIREFOX:
			driver = new FirefoxDriver();
			break;
		default:
			break;
		}
		LOG.info("init execute: " + systemconfig.browser);

		while (brun) {
			TaskInfo task = null;
			try {
				task = taskqueue.take();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (task == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				LOG.info("start task: " + task.taskname);
				for (List<CommandInfo> cmdlist : task.step) {
					// 处理每步任务列表
					for (CommandInfo cmd : cmdlist) {
						// 处理具体任务
						switch (cmd.action) {
						case MODULE:

							break;
						case GET:
							driver.get(cmd.value);
							break;
						default:
							break;

						}
					}
				}
			}

		}
		driver.quit();
		// Create a new instance of the Firefox driver
		// Notice that the remainder of the code relies on the interface,
		// not the implementation.

		// And now use this to visit Google
		// driver.get("https://www.jd.com");
		// Alternatively the same thing can be done like this
		// driver.navigate().to("http://www.google.com");

		// Find the text input element by its name

		WebElement element = null;
		try {
			element = driver.findElement(By.cssSelector(".link-login"));
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String stxt = element.getText();
		if (stxt.indexOf("请登陆") == -1) {// 需要登陆
			element.click();
			WebDriverWait wait = new WebDriverWait(driver, 20);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.login-tab.login-tab-r")));
			driver.findElement(By.cssSelector("div.login-tab.login-tab-r")).click();
			driver.findElement(By.id("loginname")).sendKeys("rige001");
			driver.findElement(By.id("nloginpwd")).sendKeys("110120");
			driver.findElement(By.id("loginsubmit")).click();
		} else {
			System.out.println("log: " + stxt);
		}
		System.out.println("log: " + stxt);
		// Enter something to search for
		element.sendKeys("Cheese!");

		// Now submit the form. WebDriver will find the form for us from the element
		element.submit();

	}

}
