package auto.web;

public enum BrowserEnum {
	BROWER_DEFAULT("Mozilla Firefox"), BROWER_FIREFOX("Mozilla Firefox"), B("text2"), C("text3"), D("text4");

	private final String BrowserName;

	private BrowserEnum(final String BrowserName) {
		this.BrowserName = BrowserName;
	}

	public static BrowserEnum fromBrowserName(String BrowserName) {
		for (BrowserEnum browser : BrowserEnum.values()) {
			if (browser.getBrowserName().equals(BrowserName)) {
				return browser;
			}
		}
		return BROWER_DEFAULT;
	}

	public String getBrowserName() {
		return this.BrowserName;
	}
}
