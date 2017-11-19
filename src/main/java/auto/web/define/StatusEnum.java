package auto.web.define;

//状态列表
//0默认无任何操作
//1-99 跳转到对应的命令
//100成功跳出
//100 
//-1 错误输出
public enum StatusEnum {
	ACTIONUNKNOWN(-1, "unknown action"),
	ACTIONEMPTY(-2, "empty action"),
	
	COMMONEMTPY(-11, ""), 
	COMMONFIND(-12, "can not find common mudule"), 
	EMPTYTAR(-3, "target is empty"), 
	EMPTYTYPE(-4, "type is empty"), 
	SUCESS(100, "sucess"), 
	NONE(0, "none");
	private final int status;
	private final String desc;

	private StatusEnum(int status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	public int getStatus() {
		return this.status;
	}
	
	public String getDesc() {
		return this.desc;
	}
}
