package com.lucene.jsoup.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.lucene.jsoup.bean.MusicInfo;
import com.lucene.jsoup.bean.MusicUser;

public class Util {

	private static final HttpClient client = new HttpClient();
	
	/**
	 * HttpURLConnection抓取网页内容
	 * @throws IOException
	 */
	@Test
	public void fetchTest() throws IOException{
		String url = "http://music.163.com/playlist?id=5130510";
		String html = getHtml(url);
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		String h2 = doc.select("h2[class=f-ff2 f-brk]").text().trim();//歌单名
		String uid = doc.select("span[class=name] a[class=s-fc7]").attr("href").trim();//用户id
		String name = doc.select("span[class=name] a[class=s-fc7]").text().trim();//用户名
		String title = "", singer = "", href = "", special = "";
		MusicUser music = new MusicUser();
		Elements elmts = doc.select("tbody[id=m-song-list-module] tr");
		for(Element tag : elmts){
			title = tag.select("div[class=ttc ttc-1] span[class=txt] a").text().trim();
			href = tag.select("div[class=ttc ttc-1] span[class=txt] a").attr("href").trim();
			singer = tag.select("td div[class=text] span").attr("title").trim();
			special = tag.select("td div[class=text] a[class=s-fc3]").attr("title").trim();
			music.setSongListName(h2);
			music.setUsername(name);
			music.setUid(getParam(uid, "id=(\\d+)"));
			music.setRefUrl(url);
			music.setSinger(singer);
			music.setUrl("http://music.163.com"+href);
			music.setTitle(title);
			music.setSpecial(special);
			CRUD.saveMS_Us_163(music);
			System.out.println(music);
		}
		if(h2.length()>0 && name.length()>0 && elmts.size()==0){
			music.setSongListName(h2);
			music.setUsername(name);
			CRUD.saveMS_Us_163(music);
			System.out.println(music);
		}
	}
	
	/**
	 * Jsoup抓取网页内容
	 * @throws IOException
	 */
	@Test
	public void fetchJsoup() throws IOException{
		String url = "http://i.ifeng.com/musci/music_wap?vt=5&mid=5kLA4D";
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
    	Elements elmts = doc.select("ul li");
		for(Element tag : elmts){
			String title = tag.select("a").text().trim();
			String str = tag.text().trim();
			String href = tag.select("a").attr("href").trim();
			if(title.length()>0 && href.contains("MDSF")){
				String regex = "(（.*）)";
				final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
		  		final Matcher ma = pa.matcher(str);
		  		String singer = "";
		  		while(ma.find()){
		  			if(ma.groupCount() > 0){
		  				singer = ma.group(1).replace("（", "").replace("）", "");
		  			}
		  		}
    			MusicInfo music = new MusicInfo();
    			music.setUrl(href);//链接
    			music.setTitle(title);//歌名
    			music.setAuthor(singer);
    			music.setWebsite(url);
    			music.setUrlHost("i.ifeng.com");
    			System.out.println(music);
    			CRUD.saveIfeng(music);
			}
		}
	}
	
	/**
	 * 抓取网页内容
	 * @param requestUrl
	 * @return
	 */
	public static String getHtml(String requestUrl){
		StringBuffer buffer = new StringBuffer();
		try {
			String type = "utf-8";//163 utf-8 QQ gbk
//			if(requestUrl.contains("qq.com")){
//				type = "gbk";
//				requestUrl = "http://s.plcloud.music.qq.com/fcgi-bin/fcg_yqq_song_detail_info.fcg?songid=" + getParam(requestUrl);
//			}
			HttpURLConnection httpUrlConn = (HttpURLConnection) new URL(requestUrl).openConnection();
	        httpUrlConn.setDoOutput(true);
	        httpUrlConn.setRequestMethod("GET");
			InputStream inputStream = httpUrlConn.getInputStream();
        	InputStreamReader inputStreamReader = new InputStreamReader(inputStream, type);
        	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	/**
	 * 获取网页状态码
	 * @param url
	 * @return
	 */
	public static int getStatusCode(String url){
		GetMethod getMethod = null;
  		int statusCode = 0;
		try {
			getMethod = new GetMethod(url);
			getMethod.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
			statusCode = client.executeMethod(getMethod);
			if(statusCode == HttpStatus.SC_OK){
				return statusCode;
			}
		} catch (Exception e) {
			statusCode = 500;
//			e.printStackTrace();
		}
  		return statusCode;
	}
	
	/**
	 * 获取参数
	 * @param url
	 */
	public static String getParam(String url,String regex){
		final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
  		final Matcher ma = pa.matcher(url);
  		while(ma.find()){
  			if(ma.groupCount() > 0){
  				String songid = ma.group(1);//内容
  				return songid;
  			}
  		}
  		return null;
	}
	
	/**
	 * 获取内容
	 * @param content
	 * @return
	 * @throws IOException
	 */
	@Test
	public void getContent() throws IOException {
		URL url = new URL("http://s.plcloud.music.qq.com/fcgi-bin/fcg_musiclist_getinfo.fcg?");
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
		String content = "uin=519918611&dirid=201&new=0&dirinfo=1&miniportal=1&fromDir2Diss=1&mobile=1&user=qqmusic&from=0&to=20";
		out.write(URLEncoder.encode(content, "UTF-8")); // 向页面传递数据。post的关键所在
		// remember to clean up
		out.flush();
		out.close();
		// 一旦发送成功，用以下方法就可以得到服务器的回应：
		String sCurrentLine = "";
		String sTotalString = "";
		InputStream l_urlStream = connection.getInputStream();
		
		// 传说中的三层包装阿！
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
		while ((sCurrentLine = l_reader.readLine()) != null) {
			sTotalString += sCurrentLine;
		}
		System.out.println(sTotalString);
	}
	
	@Test
	public void captureJavascript() throws Exception {
		String strURL = "http://s.plcloud.music.qq.com/fcgi-bin/fcg_musiclist_getinfo.fcg?uin=519918611&dirid=201&new=0&dirinfo=1&miniportal=1&fromDir2Diss=1&mobile=1&user=qqmusic&from=0&to=20";
		URL url = new URL(strURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		InputStreamReader input = new InputStreamReader(httpConn
				.getInputStream(), "utf-8");
		BufferedReader bufReader = new BufferedReader(input);
		String line = "";
		StringBuilder contentBuf = new StringBuilder();
		while ((line = bufReader.readLine()) != null) {
			contentBuf.append(line);
		}
		System.out.println("captureJavascript()的结果：\n" + contentBuf.toString());
	}
	
	
}
