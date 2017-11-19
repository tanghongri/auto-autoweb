package auto.web.thread;

import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import auto.web.common.ModuleInfo;
import auto.web.common.SystemConfig;
import auto.web.common.TaskInfo;
import auto.web.define.CommandInfo;
import auto.web.define.StatusEnum;
import auto.web.define.TypeEnum;

public class ExecuteTask implements Runnable {
	private final Logger LOG = LoggerFactory.getLogger(ExecuteTask.class);

	private boolean brun = true;
	private SystemConfig systemconfig;
	private PriorityBlockingQueue<TaskInfo> taskqueue;
	private HashMap<String, ModuleInfo> commonModule;
	//
	WebDriver driver = null;
	WebElement element = null;
	WebDriverWait wait;

	public ExecuteTask(PriorityBlockingQueue<TaskInfo> taskqueue, SystemConfig systemconfig,
			HashMap<String, ModuleInfo> commonModule) {
		this.taskqueue = taskqueue;
		this.systemconfig = systemconfig;
		this.commonModule = commonModule;
	}

	// 停止任务
	public void stopTask(boolean brun) {
		this.brun = !brun;
	}

	private WebElement findTypeElement() {
		WebElement element = null;

		return element;
	}

	private StatusEnum GetCmdBy(CommandInfo cmd, By by) {
		if (cmd.target.isEmpty()) {
			LOG.error("cmd target isEmpty！");
			return StatusEnum.EMPTYTAR;
		}
		if (cmd.type == TypeEnum.NONE) {
			LOG.error("cmd type isEmpty！");
			return StatusEnum.EMPTYTYPE;
		}
		return StatusEnum.SUCESS;
	}

	// 执行一个动作
	private StatusEnum ExeCmd(CommandInfo cmd) {
		LOG.info("deal cmd: " + cmd.action + ", " + cmd.type + ", " + cmd.value);
		switch (cmd.action) {
		case MODULE:
			switch (cmd.type) {
			case COMMON:// 执行公共模板
				ModuleInfo module = commonModule.get(cmd.value);
				if (module == null) {
					LOG.error("can not find commonModule: " + cmd.value);
					return StatusEnum.FINDCOMMON;
				}
				ExeModule(module);
				break;
			default:
				break;
			}
			break;
		case GET:
			driver.get(cmd.value);
			break;
		case PREEL:
			By by = null;
			GetCmdBy(cmd, by);
			wait.until(ExpectedConditions.presenceOfElementLocated(by));

			break;
		default:
			LOG.error("unknown cmd: " + cmd.action + ", " + cmd.type + ", " + cmd.value);
			break;
		}

		return StatusEnum.SUCESS;
	}

	// 执行一个模块
	private int ExeModule(ModuleInfo module) {
		if (module == null) {
			LOG.error("ExeModule arg null");
			return -1;
		}
		LOG.info("deal module: " + module.id + ", " + module.name);
		for (CommandInfo cmd : module.cmdlist) {
			ExeCmd(cmd);
		}
		return 0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
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
				switch (systemconfig.browser) {
				case BROWER_FIREFOX:
					driver = new FirefoxDriver();
					break;
				default:
					break;
				}
				LOG.info("init WebDriver: " + systemconfig.browser);
				wait = new WebDriverWait(driver, 20);

				LOG.info("start task: " + task.taskname);
				for (ModuleInfo module : task.step) {
					// 处理每步任务列表
					ExeModule(module);
				}
				driver.quit();
				LOG.info("quit WebDriver: " + systemconfig.browser);
			}
		}

		try {
			element = driver.findElement(By.cssSelector(".link-login"));
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String stxt = element.getText();
		if (stxt.indexOf("请登陆") == -1) {// 需要登陆
			element.click();
			wait = new WebDriverWait(driver, 20);
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
