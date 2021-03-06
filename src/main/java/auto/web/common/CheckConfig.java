package auto.web.common;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import auto.web.define.ActionEnum;
import auto.web.define.CommandInfo;
import auto.web.define.StatusEnum;
import auto.web.define.TypeEnum;

public class CheckConfig {
	private final Logger LOG = LoggerFactory.getLogger(CheckConfig.class);
	private HashMap<String, ModuleInfo> commonModule = null;
	private SystemConfig systemconfig = null;
	// 0 不检查变量，1检查变量
	private int checktype = 0;

	// 加载公共类使用
	public CheckConfig(SystemConfig systemconfig) {
		this.systemconfig = systemconfig;
		this.checktype = 0;
	}

	// 加载任务使用
	public CheckConfig(SystemConfig systemconfig, HashMap<String, ModuleInfo> commonModule) {
		this.systemconfig = systemconfig;
		this.commonModule = commonModule;
		this.checktype = 1;
	}

	public StatusEnum CheckCmd(CommandInfo cmd) {
		StatusEnum status = StatusEnum.SUCESS;

		if (cmd.action == ActionEnum.NONE) {
			status = StatusEnum.ACTIONEMPTY;
		} else if (cmd.type == TypeEnum.NONE) {
			status = StatusEnum.TYPEEMPTY;
		} else if (cmd.target.isEmpty()) {
			status = StatusEnum.TARGETEMPTY;
		} else {
			switch (cmd.action) {
			case MODULE:
				switch (cmd.type) {
				case COMMON:
					if (commonModule.get(cmd.target) == null) {
						status = StatusEnum.COMMONUNKNOWN;
					}
					break;
				default:
					status = StatusEnum.TYPEUNKNOWN;
					break;
				}
				break;
			case GET:
				switch (cmd.type) {
				case URL://
					break;
				default:
					status = StatusEnum.TYPEUNKNOWN;
					break;
				}
				break;
			case PREEL:
			case CLICK:
				switch (cmd.type) {
				case BYCSS:
				case BYTID:
					break;
				default:
					status = StatusEnum.TYPEUNKNOWN;
					break;
				}
				break;
			case TREG:
			case TYPE:
				switch (cmd.type) {
				case BYCSS:
				case BYTID:
					if (cmd.value.isEmpty()) {
						status = StatusEnum.VALUEEMPTY;
					}
					break;
				default:
					status = StatusEnum.TYPEUNKNOWN;
					break;
				}
				break;
			case COOKIE:
				switch (cmd.type) {
				case READ:
				case SAVE:
					break;
				default:
					status = StatusEnum.TYPEUNKNOWN;
					break;
				}
				break;
			default:
				status = StatusEnum.ACTIONUNKNOWN;
				break;
			}
		}
		return status;
	}

	// 仅输出下级错误信息，本级错误由上级输出
	public StatusEnum CheckModule(ModuleInfo module) {
		StatusEnum retstatus = StatusEnum.SUCESS;
		StatusEnum status = StatusEnum.SUCESS;
		if (module.id.isEmpty()) {
			retstatus = StatusEnum.IDEMPTYM;
		} else if (module.name.isEmpty()) {
			retstatus = StatusEnum.NAMEEMPTYM;
		} else if (module.type.isEmpty()) {
			retstatus = StatusEnum.TYPEEMPTYM;
		} else if (module.cmdlist == null) {
			retstatus = StatusEnum.CMDEMPTYM;
		} else {
			for (CommandInfo cmd : module.cmdlist) {
				status = CheckCmd(cmd);
				if (status != StatusEnum.SUCESS) {
					retstatus = status;
					LOG.error("CheckCmd error：" + cmd);
				}
			}
		}
		return retstatus;
	}

	// 仅输出下级错误信息，本级错误由上级输出
	public StatusEnum CheckTask(TaskInfo task) {
		StatusEnum retstatus = StatusEnum.SUCESS;
		StatusEnum status = StatusEnum.SUCESS;
		if (task.taskname.isEmpty()) {
			retstatus = StatusEnum.NAMEEMPTYT;
		} else if (task.step == null) {
			retstatus = StatusEnum.STEPEMPTYT;
		} else {
			for (ModuleInfo module : task.step) {
				status = CheckModule(module);
				if (status != StatusEnum.SUCESS) {
					retstatus = status;
					LOG.error("CheckModule error: " + module);
				}
			}
		}
		return retstatus;
	}
}
