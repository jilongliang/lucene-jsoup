package com.lucene.jsoup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.junit.Test;

import com.lucene.jsoup.bean.MS;
import com.lucene.jsoup.bean.MusicInfo;
import com.lucene.jsoup.common.CRUD;
import com.lucene.jsoup.common.FetchUtil;

public class MusicMacth {

	/**
	 * 循环抓取
	 * @throws IOException
	 */
	@Test
	public void FetchLoop(){
		try {
	        List<MS> list = CRUD.getMS("ms_qq",0);
	        int i = 0;
	        System.out.println(list.size());
	        for(MS ms : list){
	        	if(FetchUtil.getStatusCode(ms)!=200){
	        		System.out.println(ms.getId());
	        		CRUD.updateMS_Url_sts(ms.getId());
	        		continue;
	        	}
	        	if(ms.getUrl()!=null && ms.getUrl().length()>0){
	        		System.out.println("------------"+ms.getId());
//	        		getMS_Baidu(ms);
	        		getMS_QQ(ms);
	        	}
	        }
		} catch (Exception e) {
			FetchLoop();
		}
	}
	
	/**
	 * 获取参数
	 * @param url
	 */
	public static String getParam(String url){
		String regex = "id=(\\d+)";
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
	 * 抓取QQ音乐
	 * @param html
	 * @param url
	 * @throws IOException 
	 */
	public static void getMS_QQ(MS ms) throws IOException{
		if(getParam(ms.getUrl())==null)return;
		String url = "http://s.plcloud.music.qq.com/fcgi-bin/fcg_yqq_song_detail_info.fcg?songid=" + getParam(ms.getUrl());
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        String content = doc.select("div[id=top_song_data]").text().trim();
        if(content !=null && content.length()>0){
        	String[] cont = content.split("[|]");
        	if(cont !=null && cont.length>5){
        		String title = cont[1];
        		String author = cont[3];
        		String special = cont[5];
        		if(title.length()>0 && author.length()==0){
	 				author = "未知";
	 			}
        		if(title.length()>0 && author.length()>0){
        			MusicInfo music = new MusicInfo();
        			music.setUrl(ms.getUrl());//链接
        			music.setTitle(title);//歌名
        			music.setAuthor(author);
        			music.setSpecial(special);
        			music.setWebsite("yqq");
        			music.setMusicType("11G");
        			music.setUrlHost("y.qq.com");
        			System.out.println(music);
        			CRUD.saveMusicInfo(music);
        			CRUD.updateMS_Fethch_sts(ms.getId());
        		}
        	}
        }
	}
	
	@Test
	public void test163() throws IOException{
		String url = "http://y.qq.com/w/sign.html?uin=519918611";
		url = "http://y.qq.com/#type=taoge&id=965621190";//
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		System.out.println(doc);
	
		String html = getHtml(url);
//		System.out.println(html);
//		org.jsoup.nodes.Document doc = Jsoup.parse(html);
	}
	/**
	 * 抓取163音乐
	 * @param url
	 * @throws IOException 
	 */
	public static void getMS_163(MS ms) throws IOException{
		org.jsoup.nodes.Document doc = Jsoup.connect(ms.getUrl()).get();
        String content = doc.select("meta[name=keywords]").attr("content").trim();
        if(content !=null && content.length()>0){
        	String[] cont = content.replaceAll("，", ",").split("[,]");
        	if(cont !=null && cont.length>2){
        		String title = cont[0];
        		String author = cont[2];
        		String special = cont[1];
        		if(title.length()>0 && author.length()==0){
	 				author = "未知";
	 			}
        		if(title.length()>0 && author.length()>0){
        			MusicInfo music = new MusicInfo();
        			music.setUrl(ms.getUrl());//链接
        			music.setTitle(title);//歌名
        			music.setAuthor(author);
        			music.setSpecial(special);
        			music.setWebsite("163");
        			music.setMusicType("11G");
        			music.setUrlHost("163.com");
        			System.out.println(music);
//        			CRUD.saveMusicInfo(music);
//        			CRUD.updateMS_Fethch_sts(ms.getId());
//        			CRUD.save100USER(music);
//        			CRUD.update100_Fethch_sts(ms.getId());
        		}
        	}
        }
	}
	
	/**
	 * 抓取百度音乐
	 * @param url
	 * @throws IOException 
	 */
	public static void getMS_Baidu(MS ms) throws IOException{
		org.jsoup.nodes.Document doc = Jsoup.connect(ms.getUrl()).get();
    	if(doc != null){
    		String title = doc.select("h2[class=songpage-title clearfix] span[class=name]").text().trim();
    		String author = doc.select("div[class=info-holder clearfix] ul li span[class=author_list]").attr("title").trim();
    		String special = doc.select("div[class=info-holder clearfix] ul li[class=clearfix] a").text().trim();
 			if(title.length()>0 && author.length()==0){
 				author = "未知";
 			}
    		if(title.length()>0 && author.length()>0){
    			MusicInfo music = new MusicInfo();
    			music.setUrl(ms.getUrl());//链接
    			music.setTitle(title);//歌名
    			music.setAuthor(author);
    			music.setSpecial(special);
    			music.setWebsite("baidu");
    			music.setMusicType(String.valueOf(ms.getId()));
    			music.setUrlHost("music.baidu.com");
    			System.out.println(music);
    			CRUD.saveMusicInfo(music);
    			CRUD.updateMS_Fethch_sts(ms.getId());
    		}
    	}
	}
	
	@Test
	public void myUpdate163() throws IOException{
		List<MusicInfo> list = CRUD.slt163();
		for(MusicInfo ms : list){
			String url = "";
			url = "http://music.163.com/song/" + getParam(ms.getUrl());
			org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
	        String content = doc.select("meta[name=keywords]").attr("content").trim();
	        if(content !=null && content.length()>0){
	        	String[] cont = content.replaceAll("，", ",").split("[,]");
	        	if(cont !=null && cont.length>2){
	        		String title = cont[0];
	        		String author = cont[2];
	        		String special = cont[1];
	        		if(title.length()>0 && author.length()==0){
		 				author = "未知";
		 			}
	        		if(title.length()>0 && author.length()>0 && !title.contains("网易云音")){
	        			String info = title + "@" + author + "@" + special;
	        			System.out.println(info);
	        			CRUD.update163(info, Integer.parseInt(ms.getTitle()));
	        		}
	        	}
	        }
		}
	}
	@Test
	public void txt() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("D:\\3gqq.txt"));
		String data = br.readLine();//一次读入一行，直到读入null为文件结束
		int i = 1;
		while(data!=null){
//			MusicInfo music = new MusicInfo();
//			music.setUrl(data);//链接
//			music.setTitle(getParam(data,"tag=(\\S+)").replace("_paresh=true", ""));
//			System.out.println(music);
//			CRUD.saveMusicInfo(music);
		
//			String html = getHtml(data);
//			System.out.println("李正，第" + i + "个链接：" + "=>" + data);
//			org.jsoup.nodes.Document doc = Jsoup.parse(html);
			data = data.replace("tag/", "tag=");
			data = URLDecoder.decode(data, "utf-8");
			String tag = getParam(data,"tag=(\\S+.)")==null?"":getParam(data,"tag=(\\S+.)").replace("_paresh=true", "");
			tag = tag.replace("/", "").replace("?", "");
			System.out.println(tag);
			data = br.readLine(); //接着读下一行
			i ++;
		}
}
	
	@Test
	public void myTest() throws IOException{
		//http://3g.music.qq.com/fcgi-bin/getsession http://3g.music.qq.com/fcgi-bin/fcg_unite_update http://3g.music.qq.com/fcgi-bin/getsession
		String url = "http://music.baidu.com/playmv/62379401?_paresh=true";
		url = "tag/%E7%BD%91%E7%BB%9C%E6%AD%8C%E6%9B%B2".replace("tag/", "tag=");
		url = URLDecoder.decode(url, "utf-8");
		System.out.println(getParam(url,"tag=([^\"|[\u4e00-\u9fa5]]+)"));
		//title: doc.select("ul[class=c6 info-item clearfix] li").get(0).select("a").text()
		//singer: doc.select("ul[class=c6 info-item clearfix] li").get(1).select("a").text()
//		System.out.println(Jsoup.connect(url).get().select("ul[class=c6 info-item clearfix] li").get(0).select("a").text());
//		String html = getHtml("http://music.163.com/#/playlist?id=8781907");
//		org.jsoup.nodes.Document doc = Jsoup.parse(html);
//		System.out.println(doc);
		/*List<MusicInfo> list = CRUD.slt163();
		for(MusicInfo ms : list){
			 String html = getHtml(ms.getUrl());
			 org.jsoup.nodes.Document doc = Jsoup.parse(html);
			 String content = doc.select("div[class=data]").text().trim();
			 if(content !=null && content.length()>0){
		        	String[] cont = content.split("[|]");
		        	if(cont !=null && cont.length>5){
		        		String title = cont[1];
		        		String author = cont[3];
		        		String special = cont[5];
		        		if(title.length()>0 && author.length()==0){
			 				author = "未知";
			 			}
		        		if(title.length()>0 && author.length()>0){
		        			MusicInfo music = new MusicInfo();
		        			music.setTitle(title);//歌名
		        			music.setAuthor(author);
		        			music.setSpecial(special);
		        			music.setUrlHost(ms.getTitle());
		        			System.out.println(music);
		        		}
		        	}
			 }
		}*/
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
}
