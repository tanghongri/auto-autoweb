package auto.web;
//状态列表
//0默认无任何操作
//1-99 跳转到对应的命令
//100成功跳出
//100 
//-1 错误输出
public enum StatusEnum {
	SUCESS(100);
	private final int  Status;
	private StatusEnum(int  Status){
        this.Status=Status;
    }
	public int getStatus() {
		return this.Status;
	}
}
