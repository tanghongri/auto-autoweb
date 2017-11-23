package auto.web.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import auto.web.common.ModuleInfo;
import auto.web.common.SystemConfig;
import auto.web.common.TaskInfo;
import auto.web.define.CommandInfo;
import auto.web.define.StatusEnum;
import auto.web.define.TypeEnum;

//注意：参数检查在 CheckConfig.java中
//模块、任务加载前执行参数检查再添加队列
public class ExecuteTaskThread implements Runnable {
	private final Logger LOG = LoggerFactory.getLogger(ExecuteTaskThread.class);

	private boolean brun = true;
	private SystemConfig systemconfig;
	private PriorityBlockingQueue<TaskInfo> taskqueue;
	private HashMap<String, ModuleInfo> commonModule;
	private String cookiepath = "";
	//
	WebDriver driver = null;
	WebElement element = null;
	WebDriverWait wait;

	public ExecuteTaskThread(PriorityBlockingQueue<TaskInfo> taskqueue, SystemConfig systemconfig,
			HashMap<String, ModuleInfo> commonModule) {
		this.taskqueue = taskqueue;
		this.systemconfig = systemconfig;
		this.commonModule = commonModule;
		cookiepath = System.getProperty("user.dir").concat("\\conf\\cookie\\");
	}

	// 停止任务
	public void stopTask(boolean brun) {
		this.brun = !brun;
	}

	// 将value中的变量引用转换为实际参数,嵌套处理，存在多重变量引用
	public String getArgValue(String value) {
		if (value.length() < 8) {
			return value;
		}
		String sKey = value.substring(0, 7);
		String sValue = value.substring(7);
		if (sKey.equals("cookie:")) {// cookie返回文件路径
			return cookiepath.concat(getArgValue(sValue));
		} else if (sKey.equals("system:")) {// cookie返回文件路径
			return systemconfig.args.get(getArgValue(sValue));
		}
		return value;
	}

	private WebElement findTypeElement(CommandInfo cmd) {
		WebElement element = null;
		try {
			element = driver.findElement(getCmdBy(cmd));
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			LOG.error("pattern:" + e.getMessage());
		}
		return element;
	}

	private By getCmdBy(CommandInfo cmd) {
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

	private int loadCookieFromFile(String sFilePath) {
		File file = new File(sFilePath);
		if (!file.exists()) {
			return -1;
		}

		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				StringTokenizer str = new StringTokenizer(line, ";");
				while (str.hasMoreTokens()) {
					String name = str.nextToken();
					String value = str.nextToken();
					String domain = str.nextToken();
					String path = str.nextToken();
					Date expiry = null;
					String dt;
					if (!(dt = str.nextToken()).equals(null)) {
						// expiry=new Date(dt);
						System.out.println();
					}
					boolean isSecure = new Boolean(str.nextToken()).booleanValue();
					Cookie ck = new Cookie(name, value, domain, path, expiry, isSecure);
					driver.manage().addCookie(ck);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private int saveCookieToFile(String sFilePath) {
		File file = new File(sFilePath);
		try {
			FileWriter filewriter = new FileWriter(file, false);
			BufferedWriter bufwriter = new BufferedWriter(filewriter);
			ObjectMapper mapper = new ObjectMapper();
			String json = "";

			for (Cookie cookie : driver.manage().getCookies()) {
				try {
					json = mapper.writeValueAsString(cookie);
					bufwriter.write(json);
					bufwriter.newLine();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			bufwriter.flush();
			bufwriter.close();
			filewriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("cookie write to file");
		}
		return 0;
	}

	// 执行一个动作
	private StatusEnum execCmd(CommandInfo cmd) {
		StatusEnum status = StatusEnum.SUCESS;
		switch (cmd.action) {
		case MODULE:
			switch (cmd.type) {
			case COMMON:// 执行公共模板
				execModule(commonModule.get(cmd.target));
				break;
			default:
				break;
			}
			break;
		case GET:
			// 打开页面读取cookie
			loadCookieFromFile(getArgValue(cmd.value));
			driver.get(cmd.target);
			break;
		case PREEL:
			try {
				element = null;
				By by = getCmdBy(cmd);
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
				element.sendKeys(getArgValue(cmd.value));
			}
			break;
		case COOKIE:
			switch (cmd.type) {
			case SAVE:// 执行公共模板
				saveCookieToFile(getArgValue(cmd.target));
				break;
			default:
				break;
			}
			break;
		default:
			LOG.error("unknown cmd: " + cmd);
			break;
		}
		return status;
	}

	// 执行一个模块
	private int execModule(ModuleInfo module) {
		StatusEnum status = StatusEnum.SUCESS;
		while (module.recount > 0) {
			module.recount--;
			for (CommandInfo cmd : module.cmdlist) {
				LOG.info("ExeCmd " + module.cmdlist.indexOf(cmd) + " " + module.recount + ": " + cmd);
				status = execCmd(cmd);
				if (status != StatusEnum.SUCESS) {
					LOG.error("CheckCmd：" + cmd + ", " + status);
					break;
				}
			}
			if (status.getStatus() >= StatusEnum.SUCESS.getStatus()) {
				break;
			}
			int nTime = systemconfig.retime;
			if (module.retime > 5 && nTime > module.retime) {
				nTime = module.retime;
			}
			try {
				Thread.sleep(1000 * nTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		LOG.info("ExecuteTaskThread start");

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
				// 若驱动路径错误直接退出
				try {
					switch (systemconfig.browser) {
					case BROWER_FIREFOX:
						driver = new FirefoxDriver();
						break;
					default:
						break;
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOG.error("init WebDriver: " + e.getMessage());
					System.exit(1);
				}
				LOG.info("init WebDriver: " + systemconfig.browser);
				wait = new WebDriverWait(driver, 5);

				LOG.info("start task: " + task.taskname);

				for (ModuleInfo module : task.step) {
					// 处理每步任务列表
					LOG.info("ExeModule " + task.step.indexOf(module) + ": " + module);
					execModule(module);
				}
				driver.quit();
				LOG.info("quit WebDriver: " + systemconfig.browser);
			}
		}
	}

}
