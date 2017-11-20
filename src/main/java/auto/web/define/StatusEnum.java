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
	TARGETEMPTY(-3, "empty target"), 
	ACTIONUNKNOWN(-4, "unknown type"), 
	TYPEUNKNOWN(-5, "unknown type"), 
	COMMONUNKNOWN(-6, "unknown common"), 
	VALUEEMPTY(-7, "empty value"), 
	IDEMPTYM(-11, "empty module id"), 
	NAMEEMPTYM(-12, "empty module name"), 
	TYPEEMPTYM(-13, "empty module type"), 
	CMDEMPTYM(-14, "empty module cmdlist"), 
	NAMEEMPTYT(-15, "empty task name"), 
	STEPEMPTYT(-16, "empty task step"), 
	WAITTIMEOUTE(-20, "wait element time out"), 
	ELEMENTEMPTY(-21, "empty element"), 
	TEXTPATTERN(-22, "text pattern "), 
	SUCESS(100, "sucess"), 
	RETSUCESS(101, "sucess ret"), 
	NONE(0, "none");
	private final int status;
	private final String desc;

	private StatusEnum(int status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	public static StatusEnum fromSatusID(int nstatus) {
		for (StatusEnum status : StatusEnum.values()) {
			if (status.getStatus() == nstatus) {
				return status;
			}
		}
		return NONE;
	}

	public int getStatus() {
		return this.status;
	}

	public String getDesc() {
		return this.desc;
	}

	public String toString() {
		return this.status + ": " + this.desc;
	}
}
