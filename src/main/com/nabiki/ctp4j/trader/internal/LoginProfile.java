package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.trader.ThostTeResumeType;

import java.util.LinkedList;
import java.util.List;

public class LoginProfile {
	public String FlowPath = "flow";
	public List<String> FrontAddresses = new LinkedList<>();
	public int PublicTopicType = ThostTeResumeType.THOST_TERT_RESUME;
	public int PrivateTopicType = ThostTeResumeType.THOST_TERT_RESUME;

	public LoginProfile() {
	}
}
