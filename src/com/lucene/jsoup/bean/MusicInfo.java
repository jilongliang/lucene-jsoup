package com.lucene.jsoup.bean;

import java.io.Serializable;

public class MusicInfo  implements Serializable{

	private String url;
	private String title;
	private String author;
	private String special;//专辑
	private String urlHost;//域名
	private String website;//站名
	private String musicType;//分类
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSpecial() {
		return special;
	}
	public void setSpecial(String special) {
		this.special = special;
	}
	public String getUrlHost() {
		return urlHost;
	}
	public void setUrlHost(String urlHost) {
		this.urlHost = urlHost;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getMusicType() {
		return musicType;
	}
	public void setMusicType(String musicType) {
		this.musicType = musicType;
	}
	
	public String toString() {
		return title + "," + url + "," + author + "," + special;
	}
	
}
