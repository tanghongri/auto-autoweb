package auto.web;

import java.io.File;
import java.io.IOException;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
	private static String sCurPath = "";
	private static BrowserEnum BrowserType;

	public static void main(String[] args) {
		// 初始化系统配置
		if (LoadSystemConfig() != 0) {
			return;
		}
		Logger logger = LoggerFactory.getLogger(App.class);
		logger.info("logback 成功了");
        logger.error("logback 成功了");
        
		int test = 1;
		if (test == 1) {
			LoadTask mLoadTask = new LoadTask();
			mLoadTask.LoadFileTask(sCurPath.concat("\\conf\\jd_login.conf"));
			
		} else {
			System.setProperty("webdriver.firefox.bin", "C:\\Program Files\\Mozilla Firefox\\firefox.exe");
			System.setProperty("webdriver.gecko.driver", "C:\\auto\\geckodriver.exe");
			// Create a new instance of the Firefox driver
			// Notice that the remainder of the code relies on the interface,
			// not the implementation.
			WebDriver driver = new FirefoxDriver();

			// And now use this to visit Google
			driver.get("https://www.jd.com");
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

			// Check the title of the page
			System.out.println("Page title is: " + driver.getTitle());

			// Google's search is rendered dynamically with JavaScript.
			// Wait for the page to load, timeout after 10 seconds
			(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.getTitle().toLowerCase().startsWith("cheese!");
				}
			});

			// Should see: "cheese! - Google Search"
			System.out.println("Page title is: " + driver.getTitle());

			// Close the browser
			driver.quit();
		}
	}

	public static int LoadSystemConfig() {
		// 获取配置文件路径，判断文件是否存在
		sCurPath = System.getProperty("user.dir");
		File ConfigFile = new File(sCurPath.concat("\\conf\\system.conf"));
		if (ConfigFile.exists()) {
			if (ConfigFile.isDirectory()) {
				System.out.println("system.conf is dir：." + sCurPath.concat("\\conf\\system.conf"));
				return 1;
			}
		} else {
			System.out.println("system.conf not exists：." + sCurPath.concat("\\conf\\system.conf"));
			return 2;
		}
		// 加载配置文件json
		ObjectMapper objectMapper = null;
		JsonNode node = null;
		String sValue = "";
		try {
			objectMapper = new ObjectMapper();
			node = objectMapper.readTree(ConfigFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}
		if (node.has("browser")) {
			sValue = node.get("browser").toString();
			switch (sValue) {
			case "Mozilla Firefox":
				BrowserType = BrowserEnum.BROWER_FIREFOX;
				break;
			default:
				return 0;
			}
		} else {

		}
		return 0;
	}
}
