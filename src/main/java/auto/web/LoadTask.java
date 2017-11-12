package auto.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//加载json操作配置
public class LoadTask {
	public String sConfigPath = "";
	public HashMap<String, TaskInfo> commonTask;

	public LoadTask() {
		super();
		// TODO Auto-generated constructor stub
	}

	// 从文件读取命令
	public int LoadFileTask(String Path) {
		File ConfigFile = new File(Path);
		if (ConfigFile.exists()) {
			if (ConfigFile.isDirectory()) {
				System.out.println("system.conf is dir：." + Path);
				return 1;
			}
		} else {
			System.out.println("system.conf not exists：." + Path);
			return 2;
		}
		// 加载配置文件json
		ObjectMapper objectMapper = null;
		JsonNode node = null;
		String sValue = "";
		TaskInfo task=new TaskInfo();
		try {
			objectMapper = new ObjectMapper();
			node = objectMapper.readTree(ConfigFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}

		return 0;
	}
}
