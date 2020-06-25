package com.nabiki.ctp4j.trader.internal;

import com.nabiki.ctp4j.jni.flag.ThostTeResumeType;

import java.util.LinkedList;
import java.util.List;

public class TraderLoginProfile {
	public String FlowPath = "flow";
	public List<String> FrontAddresses = new LinkedList<>();
	public int PublicTopicType = ThostTeResumeType.THOST_TERT_RESUME;
	public int PrivateTopicType = ThostTeResumeType.THOST_TERT_RESUME;

	public TraderLoginProfile() {
	}
}
