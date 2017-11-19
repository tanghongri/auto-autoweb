package auto.web.define;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActionEnum {
	NONE("none"), // 空动作
	MODULE("module"), // 执行模块
	GET("get"), // 打开一个url
	NAVIGATE("navigate"), // 访问一个页面
	CLICK("click"), // 点击
	TYPE("type"), // 输入内容
	SUBMIT("submit"), // 提交
	PREEL("presenceOfElementLocated"), // 检查元素是否出现
	TREG("textreg"), // 对象字符串正则匹配
	ACTIONEND("actionend");// 结束

	private final String actionname;

	private ActionEnum(final String actionname) {
		this.actionname = actionname;
	}

	public static ActionEnum fromActionName(String actionname) {
		for (ActionEnum action : ActionEnum.values()) {
			if (action.getActionname().equals(actionname)) {
				return action;
			}
		}
		return NONE;
	}

	@JsonValue
	public String getActionname() {
		return this.actionname;
	}
}
