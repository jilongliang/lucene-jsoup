package com.lucene.jsoup.bean;

import java.io.Serializable;

public class MusicUser  implements Serializable{

	private String listUrl;
	private String refUrl;
	private String uid;
	private String username;
	private String songListName;//用户歌单名
	private String url;
	private String title;
	private String singer;
	private String special;//专辑
	
	public String getListUrl() {
		return listUrl;
	}
	public void setListUrl(String listUrl) {
		this.listUrl = listUrl;
	}
	public String getRefUrl() {
		return refUrl;
	}
	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSongListName() {
		return songListName;
	}
	public void setSongListName(String songListName) {
		this.songListName = songListName;
	}
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
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getSpecial() {
		return special;
	}
	public void setSpecial(String special) {
		this.special = special;
	}

	public String toString() {
		return title + "," + url + "," + singer + "," + special;
	}
	
}
