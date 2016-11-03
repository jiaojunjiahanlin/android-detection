package com.netease.LDNetDiagnoService;


public interface LDNetDiagnoListener {
	
	/**
	 * 当结束之后返回日志
	 * @param log
	 */
	public void OnNetDiagnoFinished(String log);

	
	/**
	 * 跟踪过程中更新日志
	 * @param log
	 */
	public void OnNetDiagnoUpdated(String log);
}
