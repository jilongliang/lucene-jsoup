package com.lucene.jsoup;

import java.net.URL;
import java.net.URLDecoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/***
 *@Author:liangjilong
 *@Date:2016年1月16日下午6:19:46
 *@Email:jilongliang@sina.com
 *@Version:1.0
 *@CopyRight(c)liangjilong
 *@Description:
 */
public class Test1 {

	public static void main(String[] args)throws Exception {
		
		
		 String reqURL ="http://pic.sogou.com/pics?query=小清新&p=40230500&st=255&mode=255";
		// reqURL=URLEncoder.encode(reqURL, "utf-8");
		 reqURL = URLDecoder.decode(reqURL, "utf-8");
		 
		 System.out.println(reqURL);
		 
		 Document doc=Jsoup.parse(new URL(reqURL), 3000);
		 if(doc!=null){
			 //这句代码说的意思就是说获取网页的<script>这个标签并且第三个,把它的内容获取出来
			 Elements scripts = doc.select("script");
			 System.out.println(scripts.get(2));
			 for(Element s:scripts){
				// System.out.println(s);
			 }
		 }
	}
}
