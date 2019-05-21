package com.lucene.jsoup.bean;

import java.io.Serializable;

public class Disease implements Serializable {

	private int count;
	private String disease;
	private String category;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDisease() {
		return disease;
	}
	public void setDisease(String disease) {
		this.disease = disease;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String toString() {
		return disease + "," + count + "," + category;
	}
}
