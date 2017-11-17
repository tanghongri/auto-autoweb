package auto.web.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import auto.web.define.BrowserEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemConfig {
	public BrowserEnum browser = BrowserEnum.BROWER_FIREFOX;
	public int waittime = 30;
	public List<PropertyInfo> Property;
	public List<String> preload;
}
