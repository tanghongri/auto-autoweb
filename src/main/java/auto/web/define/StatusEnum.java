package auto.web.define;

//状态列表
//0默认无任何操作
//1-99 跳转到对应的命令
//100成功跳出
//100 
//-1 错误输出
public enum StatusEnum {
	ACTIONEMPTY(-1, "empty action"),
	TYPEEMPTY(-2, "empty type"),
	TARGETEMPTY(-3, "empty value"),	
	ACTIONUNKNOWN(-4, "unknown type"), 
	TYPEUNKNOWN(-5, "unknown type"), 
	COMMONUNKNOWN(-6, "unknown common"), 
	IDEMPTYM(-11, "empty module id"), 
	NAMEEMPTYM(-12, "empty module name"), 
	TYPEEMPTYM(-13, "empty module type"), 
	CMDEMPTYM(-14, "empty module cmdlist"), 
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
