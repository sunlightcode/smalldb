package com.emeralddb.base;

public class Snapshot {
	private int insertTimes;
	private int delTimes;
	private int queryTimes;
	private int serverRunTime;
	public void setInsertTimes(int insertTimes) {
		this.insertTimes = insertTimes;
	}
	public int getInsertTimes() {
		return insertTimes;
	}
	public void setDelTimes(int delTimes) {
		this.delTimes = delTimes;
	}
	public int getDelTimes() {
		return delTimes;
	}
	public void setQueryTimes(int queryTimes) {
		this.queryTimes = queryTimes;
	}
	public int getQueryTimes() {
		return queryTimes;
	}
	public void setServerRunTime(int serverRunTime) {
		this.serverRunTime = serverRunTime;
	}
	public int getServerRunTime() {
		return serverRunTime;
	}
	public String toString() {
		return String.format("insertTimes:%d; delTimes:%d; queryTimes:%d", 
				insertTimes,delTimes,queryTimes);
	}
	
}
