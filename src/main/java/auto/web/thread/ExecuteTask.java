package auto.web.thread;

import java.util.concurrent.PriorityBlockingQueue;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import auto.web.common.TaskInfo;

public class ExecuteTask implements Runnable {
	private PriorityBlockingQueue<TaskInfo> taskqueue;
	private boolean brun = true;

	public ExecuteTask(PriorityBlockingQueue<TaskInfo> taskqueue) {
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
				System.out.println(task + " 办理业务.");
			}

		}
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
