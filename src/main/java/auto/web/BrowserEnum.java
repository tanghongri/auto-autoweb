package auto.web;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BrowserEnum {
	BROWER_FIREFOX("Mozilla Firefox"), B("text2"), C("text3"), D("text4");

	private final String browsername;

	private BrowserEnum(final String browsername) {
		this.browsername = browsername;
	}

	public static BrowserEnum fromBrowserName(String BrowserName) {
		for (BrowserEnum browser : BrowserEnum.values()) {
			if (browser.getbrowsername().equals(BrowserName)) {
				return browser;
			}
		}
		return BROWER_FIREFOX;
	}

	@JsonValue
	public String getbrowsername() {
		return this.browsername;
	}
}
