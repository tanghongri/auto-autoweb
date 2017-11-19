package auto.web.common;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import auto.web.define.BrowserEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemConfig {
	public BrowserEnum browser = BrowserEnum.BROWER_FIREFOX;
	public int waittime = 30;
	public Map<String,String> property;
	public Map<String,String> args;
	public List<String> preload;
}
