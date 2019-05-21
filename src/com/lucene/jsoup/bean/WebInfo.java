package com.lucene.jsoup.bean;

import java.io.Serializable;

public class WebInfo implements Serializable{

	private String webUrl;
	private String webName;
	private String webHost;
	private String webType;
	private int typeLevel;
	
	public String getWebUrl() {
		return webUrl;
	}
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	public String getWebName() {
		return webName;
	}
	public void setWebName(String webName) {
		this.webName = webName;
	}
	public String getWebHost() {
		return webHost;
	}
	public void setWebHost(String webHost) {
		this.webHost = webHost;
	}
	public String getWebType() {
		return webType;
	}
	public void setWebType(String webType) {
		this.webType = webType;
	}
	public int getTypeLevel() {
		return typeLevel;
	}
	public void setTypeLevel(int typeLevel) {
		this.typeLevel = typeLevel;
	}
	
	
	public String toString() {
		return webName + "," + webUrl + "," + webHost + "," + webType;
	}
	
}
