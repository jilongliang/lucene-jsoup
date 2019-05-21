package com.lucene.jsoup.example;

import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test2 {

	public static void main(String[] args) throws Exception {
		//定义一个URL地址
		String url 	 = "https://www.csdn.net/";
		//定义一个代理
		String agent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)"
				+ "  Chrome/56.0.2924.87 Safari/537.36";
		Document doc = Jsoup.connect(url).ignoreContentType(true).userAgent(agent)
				// ignoreHttpErrors
				// 这个很重要 否则会报HTTP error fetching URL. Status=404
				.ignoreHttpErrors(true) // 这个很重要
				.timeout(3000).get();
		
		// class=company_list > li > div#class=content
		// 找到样式为company_list的标签所有的li标签，再li下面的标签所有div样式为content的标签的信息

		Elements contents = doc.getElementsByClass("company_list").select("li").select("div[class=content]");
		for (Element node : contents) {
			//遍历所有p标签的文本内容
			Elements selects = node.select("p");
			selects.forEach(obj ->{
				String text = obj.text();
				System.out.println(text);
			});
		}
	}
}
