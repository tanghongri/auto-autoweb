package auto.web.define;

//基础命令类，执行一个操作
public class CommandInfo {
	// 序号
	public int index = 0;
	// 操作
	public String action = "";
	// 目标类型
	public String type = "";
	// 目标名称
	public String target = "";
	// 值
	public String value = "";
	// 正确执行
	public int right = 0;
	// 错误执行
	public int error = 0;
}
