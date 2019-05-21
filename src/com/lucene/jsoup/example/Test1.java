package com.lucene.jsoup.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Test1 {

	public static void main(String[] args) {
		String html = "<html><head><title>First parse</title></head>"
				+ "<body><p>Parsed HTML into a doc.</p></body></html>";
		Document doc = Jsoup.parse(html);
		//把p标签的內容打印出来
		System.out.println(doc.select("p").text());
	}
}
