package auto.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

//@JsonInclude(JsonInclude.Include.NON_NULL) 
public class SystemConfig {
	public BrowserEnum browser;
	public List<String> preload;

	
	public BrowserEnum getBrowser() {
		return browser;
	}

	public void setBrowser(BrowserEnum browser) {
		this.browser = browser;
	}

	public List<String> getPreload() {
		return preload;
	}

	public void setPreload(List<String> preload) {
		this.preload = preload;
	}
}
