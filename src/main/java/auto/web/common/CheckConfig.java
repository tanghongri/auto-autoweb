package auto.web.common;

import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import auto.web.define.CommandInfo;
import auto.web.define.StatusEnum;

public class CheckConfig {
	private HashMap<String, ModuleInfo> commonModule;

	public CheckConfig(HashMap<String, ModuleInfo> commonModule) {
		// TODO Auto-generated constructor stub
		this.commonModule = commonModule;
	}

	public StatusEnum CheckCmd(CommandInfo cmd) {
		StatusEnum status = StatusEnum.NONE;
		switch (cmd.action) {
		case NONE:
			status = StatusEnum.ACTIONEMPTY;
			break;
		case MODULE:
			switch (cmd.type) {
			case NONE:// 执行公共模板
				break;
			case COMMON:// 执行公共模板
				if (cmd.value.isEmpty()) {
					status = StatusEnum.COMMONEMTPY;
				} else if (commonModule.get(cmd.value) == null) {
					status = StatusEnum.COMMONFIND;
				}
				break;
			default:
				break;
			}
			break;
		case GET:
			break;
		case PREEL:
			break;
		default:
			status = StatusEnum.ACTIONUNKNOWN;
			break;
		}
		return status;
	}
}
