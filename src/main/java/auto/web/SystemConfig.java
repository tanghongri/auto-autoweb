package auto.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemConfig {
	public BrowserEnum browser;
	public List<String> preload;
}
