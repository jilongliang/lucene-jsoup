package com.lucene.jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *@DEMO:napp
 *@Author:jilongliang
 *@Date:2013-6-5
 */
public class JsoupUtli {

	private static final String toUrl = "d:\\mytest\\";
	private static final String loginUrl = "http://localhost:8081/jsoup/index.jsp";
	private static final String cssPath = "d:\\mytest\\css.txt";
	private static Date d=new Date();

	/**
	 * 写文件
	 * 
	 * @param method
	 * @param random
	 * @return
	 */
	public static String writeFile(HttpMethod method, int random) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		String filePath = toUrl
				+ format.format(d )+ "_" + random + ".html";
		File newFile = new File(filePath);
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new BufferedInputStream(method.getResponseBodyAsStream()),
					"UTF-8"));
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(newFile), "GBK");
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			String content = in.readLine();
			while (content != null) {
				bufferedWriter.write(content + "");
				content = in.readLine();
			}
			System.out.println("\n写入文件成功!");

			bufferedWriter.close();
			in.close();
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * 重定向
	 * 
	 * @param location
	 * @param lastURI
	 * @return
	 */
	private static String toRedirectURL(String location, URI lastURI) {
		try {
			if (location == null || location.trim().length() == 0) {
				location = "/";
			}
			String temp = location.toLowerCase();// 转为小写
			if (!temp.startsWith("http://") && !temp.startsWith("https://")) {
				if (lastURI == null) {
					System.err.println("lastUrl is null!");
				}
				return new URI(lastURI, location, false).toString();
			}
		} catch (URIException e) {
			e.printStackTrace();
		}
		return location;
	}

	/**
	 * 抓取页面地址
	 * 
	 * @param url
	 * @param random
	 * @return
	 */
	public static String createHtml(String url, int random) {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(loginUrl);
		String returnUrl = null;
		try {
			client.executeMethod(method);

			String redirectURL = toRedirectURL(url, method.getURI());
			System.out.println(redirectURL);
			GetMethod redirect = new GetMethod(redirectURL);
			client.executeMethod(redirect);//重定向执行

			String filePath = writeFile(redirect, random);
			returnUrl = JsoupFile(filePath, random);
			System.out.println(filePath);
			redirect.releaseConnection();
		} catch (URIException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnUrl;
	}

	/**
	 * 
	 * @param filePath
	 * @param random
	 * @return
	 */
	private static String JsoupFile(String filePath, int random) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		File infile = new File(filePath);
		String url = toUrl
				+ format.format(d )+ "_new_" + random + ".html";
		try {
			File outFile = new File(url);
			Document doc = Jsoup.parse(infile, "GBK");
			StringBuffer sb = new StringBuffer(
					"<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
			sb.append("<html>");
			Elements head = doc.getElementsByTag("head");
			sb.append(head.toString());
			File cssFile = new File(cssPath);
			BufferedReader in = new BufferedReader(new FileReader(cssFile));
			BufferedWriter out=new BufferedWriter(new java.io.FileWriter(outFile));
			String content=in.readLine();
			while(content!=null){
				//System.out.println(content);
				sb.append(content);
				content=in.readLine();
			}
			in.close();
			sb.append("<body>");
			Elements form=doc.getElementsByTag("form");
			form.select("img").remove();
			//form.select("td").removeClass("td");
			sb.append(form.toString());
			sb.append("</html>");
			out.write(sb.toString());
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return url;
	}
	
	public static void main(String[] args) {
		String url="/jsoup/test1.jsp";
		createHtml(url, 1);
	}
	
	
}