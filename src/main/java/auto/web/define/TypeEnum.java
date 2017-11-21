package auto.web.define;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeEnum {
	NONE("none"), // 空动作
	COMMON("common"), // 公用模块
	URL("url"), //
	BYCSS("cssSelector"), //
	BYTID("id"), //
	READ("read"), //
	SAVE("save"), //
	TYPEEND("typeend");// 结束

	private final String typename;

	private TypeEnum(final String typename) {
		this.typename = typename;
	}

	public static TypeEnum fromTypeName(String typename) {
		for (TypeEnum type : TypeEnum.values()) {
			if (type.getTypename().equals(typename)) {
				return type;
			}
		}
		return NONE;
	}

	@JsonValue
	public String getTypename() {
		return this.typename;
	}
}
