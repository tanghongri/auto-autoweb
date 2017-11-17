package auto.web.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import auto.web.define.BrowserEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemConfig {
	public BrowserEnum browser;
	public int waittime;
	public List<String> preload;
}
