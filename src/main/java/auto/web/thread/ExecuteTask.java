package auto.web.thread;

import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
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

//注意：参数检查在 CheckConfig.java中
//模块、任务加载前执行参数检查再添加队列
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

	private WebElement findTypeElement(CommandInfo cmd) {
		WebElement element = null;
		try {
			element = driver.findElement(GetCmdBy(cmd));
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			LOG.error("pattern:" + e.getMessage());
		}
		return element;
	}

	private By GetCmdBy(CommandInfo cmd) {
		By by = null;
		switch (cmd.type) {
		case BYCSS:
			by = By.cssSelector(cmd.target);
			break;
		case BYTID:
			by = By.id(cmd.target);
			break;
		default:
			break;
		}
		return by;
	}

	// 执行一个动作
	private StatusEnum ExeCmd(CommandInfo cmd) {
		StatusEnum status = StatusEnum.SUCESS;
		LOG.info("deal cmd: " + cmd);
		switch (cmd.action) {
		case MODULE:
			switch (cmd.type) {
			case COMMON:// 执行公共模板
				ExeModule(commonModule.get(cmd.target));
				break;
			default:
				break;
			}
			break;
		case GET:
			driver.get(cmd.target);
			break;
		case PREEL:
			try {
				element = null;
				By by = GetCmdBy(cmd);
				element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				LOG.error("wait:" + e.getMessage());
				status = StatusEnum.WAITTIMEOUTE;
			}
			break;
		case TREG:
			element = findTypeElement(cmd);
			if (element == null) {
				status = StatusEnum.ELEMENTEMPTY;
			} else {
				try {
					Pattern pattern = Pattern.compile(cmd.value);
					String sText = element.getText();
					Matcher matcher = pattern.matcher(sText);
					if (matcher.find()) {
						if (cmd.right != 0) {
							status = StatusEnum.fromSatusID(cmd.right);
						}
					} else {
						if (cmd.error != 0) {
							status = StatusEnum.fromSatusID(cmd.error);
						}
					}
				} catch (PatternSyntaxException e) {
					// TODO Auto-generated catch block
					LOG.error("pattern:" + e.getMessage());
					status = StatusEnum.ELEMENTEMPTY;
				}
			}
			break;
		case CLICK:
			element = findTypeElement(cmd);
			if (element == null) {
				status = StatusEnum.ELEMENTEMPTY;
			} else {
				element.click();
			}
			break;
		case TYPE:
			element = findTypeElement(cmd);
			if (element == null) {
				status = StatusEnum.ELEMENTEMPTY;
			} else {
				element.sendKeys(cmd.value);
			}
			break;
		default:
			LOG.error("unknown cmd: " + cmd);
			break;
		}
		return status;
	}

	// 执行一个模块
	private int ExeModule(ModuleInfo module) {
		StatusEnum status = StatusEnum.SUCESS;
		LOG.info("deal module: " + module);
		while (module.recount > 0) {
			module.recount--;
			for (CommandInfo cmd : module.cmdlist) {
				status = ExeCmd(cmd);
				if (status != StatusEnum.SUCESS) {
					LOG.error("CheckCmd：" + cmd + ", " + status);
					break;
				}
			}
			if (status.getStatus() >= StatusEnum.SUCESS.getStatus()) {
				break;
			}
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
				wait = new WebDriverWait(driver, 5);

				LOG.info("start task: " + task.taskname);
				for (ModuleInfo module : task.step) {
					// 处理每步任务列表
					ExeModule(module);
				}
				driver.quit();
				LOG.info("quit WebDriver: " + systemconfig.browser);
			}
		}
	}

}
