package com.lucene.jsoup.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.lucene.jsoup.bean.MS;
import com.lucene.jsoup.bean.MusicInfo;
import com.lucene.jsoup.bean.WebInfo;

public class FetchUtil {

	private static final HttpClient client = new HttpClient();
	private static final StringBuffer bufone = new StringBuffer();
	
	public static String getWeather(String city) {
		   GetMethod getMethod = null;
		   String weatherContent = "【" + city +"】天气实况\n";
	       try {
	       	    city = city.replaceAll("市", "");
	       	    String url = "http://api.map.baidu.com/telematics/v3/weather?output=json&ak=140E6A73F89b353afe0df18433cf106f&location="+URLEncoder.encode(city,"UTF-8");
	      		getMethod = new GetMethod(url);
	      		System.out.println(getMethod.getURI().getHost());
	      		getMethod.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
	      		int statusCode = client.executeMethod(getMethod);
	      		if(statusCode == HttpStatus.SC_OK){
	      			InputStream inputStream = getMethod.getResponseBodyAsStream();
	      			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
	      			String str = "";
	      			while((str = br.readLine()) != null){
	      				bufone.append(str+System.getProperty("line.separator"));
	      			}
	      			str = null;
	      			br.close();
	      			inputStream.close();
	      		}
	      		JSONObject json = JSONObject.fromObject(bufone.toString());
	      		if(json != null && json.has("results")){
	      			JSONArray array = json.getJSONArray("results");
	      			if(array != null && array.size() > 0){
	      				JSONObject obj = array.getJSONObject(0);
	      				if(obj != null){
	      					JSONArray arr = obj.getJSONArray("weather_data");
	      					for (int i = 0; i < arr.size(); i++) {
	      						JSONObject o = (JSONObject) arr.get(i);
	      						if(o != null){
	      							if(i==0){
	      								weatherContent += o.get("date") + " 温度："+o.get("temperature") +  " 天气："+o.get("weather") + " 风速："+o.get("wind") + "\n" ;
	      							}else{
	      								weatherContent += o.get("date") + " "+o.get("temperature") + " "+o.get("weather") + " "+o.get("wind") + "\n" ;
	      							}
	      						}
	      					}
	      				}
	      			}
	      		}else{
	      			weatherContent = "暂无数据";
	      		}
	      		bufone.setLength(0);
	       }catch(Exception e){
	    	   System.out.println("抓取出错"+e);
	       }finally{
	    	if(getMethod!=null) getMethod.releaseConnection();
			   else getMethod = null;
	       }
	       return weatherContent;
	}
	
	public static void main(String[] args) throws IOException {
	}
	
	public static void getAuthor(String url){
		   GetMethod getMethod = null;
	       try {
	      		getMethod = new GetMethod(url);
	      		System.out.println(getMethod.getURI().getHost());
	      		getMethod.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
	      		int statusCode = client.executeMethod(getMethod);
	      		if(statusCode == HttpStatus.SC_OK){
	      			InputStream inputStream = getMethod.getResponseBodyAsStream();
	      			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"gbk"));//此处根据网页而定
	      			String str = "";
	      			while((str = br.readLine()) != null){
	      				bufone.append(str+System.getProperty("line.separator"));
	      			}
	      			str = null;
	      			br.close();
	      			inputStream.close();
	      		}
	      		System.out.println(bufone.toString());
	      		bufone.setLength(0);
	       }catch(Exception e){
	    	   System.out.println("抓取出错"+e);
	       }finally{
	    	if(getMethod!=null) getMethod.releaseConnection();
			   else getMethod = null;
	       }
	}
	
	/**
	 * 中国好声音分类抓取音乐
	 * @param url
	 * @throws IOException
	 */
	public static List<MusicInfo> getCHNVoice(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			 //取出歌名和链接
			 Elements imgs = doc.select("div[class=info]");
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").attr("title");
				 String href = tag.getElementsByTag("a").attr("href");
				 MusicInfo music = new MusicInfo();
				 music.setUrl(href);//链接
				 music.setTitle(title);//歌名
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("xiami");
				 list.add(music);
			 }
			 //取出作者和专辑
			 Elements song = doc.select("td").select("span");
			 int i = 0;
			 MusicInfo ms = null;
			 for (org.jsoup.nodes.Element tag : song) {
				 String cont = tag.getElementsByTag("a").attr("title");
				 if(i%2==0){
					 ms = list.get(i/2);
					 ms.setAuthor(cont);//作者
				 }else{
					 ms.setSpecial(cont);//专辑
				 }
				 i ++;
			 }
		 }
		 return list;
	}
	
	/**
	 * 音乐台分类抓取音乐（内地篇、港台篇、欧美篇、韩国篇、日本篇）
	 * @param web
	 * @throws IOException
	 */
	public List<MusicInfo> getYinYueTai(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			 Elements imgs = doc.select("ul[class=mv_info]");
			 int i = 0;
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("h3").text().toString();
				 String href = tag.getElementsByTag("a").attr("href");
				 String author = tag.getElementsByTag("h4").text().toString();
				 MusicInfo music = new MusicInfo();
				 music.setUrl(href);//链接
				 music.setTitle(title);//歌名
				 music.setAuthor(author);//歌手
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("yinyuetai");
				 list.add(music);
				 i ++;
			 }
		 }
		 return list;
	}
	
	/**
	 * 要想听歌分类抓取音乐（网络歌曲、流行歌曲、经典老歌、伤感歌曲、非主流歌曲、影视歌曲、英文歌曲、日韩歌曲、闽南歌曲、古风歌曲）
	 * @param web
	 * @throws IOException
	 */
	public List<MusicInfo> getWantListen(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			//取出歌名和链接
			 Elements imgs = doc.select("div[class=mb8] ul li");
			 int i = 0;
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).text().toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href");
				 String author = tag.getElementsByTag("a").get(1).text().toString();
				 MusicInfo music = new MusicInfo();
				 music.setUrl("http://www.31tg.com"+href);//链接
				 music.setTitle(title);//歌名
				 music.setAuthor(author);//歌手
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("31tg");
				 list.add(music);
				 i ++;
			 }
		 }
		 return list;
	}
	
	/**
	 * VV视频社区分类抓取音乐
	 * @throws IOException
	 */
	public List<MusicInfo> get51VV(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			//取出歌名和链接
			 Elements imgs = doc.select("div[class=songList] ul li");
			 int i = 0;
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).text().toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href").replace("../", "");
				 String author = tag.getElementsByTag("a").get(1).text().toString();
				 MusicInfo music = new MusicInfo();
				 music.setUrl("http://www.51vv.com/"+href);//链接
				 music.setTitle(title);//歌名
				 music.setAuthor(author);//歌手
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("51vv");
				 list.add(music);
				 i ++;
			 }
		 }
		 return list;
	}
	
	/**
	 * 手机铃声之家分类抓取音乐
	 * @throws IOException
	 */
	public List<MusicInfo> getCnwav(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			//取出歌名和链接
			 Elements imgs = doc.select("div[class=ringlist] ul li");
			 int i = 0;
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).text().toString();
				 String href = tag.getElementsByTag("a").get(1).attr("href").toString();
				 MusicInfo music = new MusicInfo();
				 music.setUrl("http://www.cnwav.com"+href);//链接
				 music.setTitle(title);//歌名
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("cnwav");
				 music.setSpecial(title.contains("-")?title.substring(title.lastIndexOf("-")+1,title.length()).trim():null);
				 list.add(music);
				 i ++;
			 }
		 }
		 return list;
	}
	
	/**
	 * A8音乐网分类抓取音乐
	 * @throws IOException
	 */
	public List<MusicInfo> getA8Music(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			//取出歌名和链接
			 Elements imgs = doc.select("ul[class=so_result_ul clearfix] li[class=list-music]");
			 int i = 0;
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).text().toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href").toString();
				 MusicInfo music = new MusicInfo();
				 music.setUrl(href);//链接
				 music.setTitle(title);//歌名
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("a8");
				 music.setSpecial(title.contains("-")?title.substring(title.lastIndexOf("-")+1,title.length()).trim():null);
				 list.add(music);
				 i ++;
			 }
		 }
		 return list;
	}
	
	/**
	 * DJ嗨嗨网分类抓取音乐
	 * @throws IOException
	 */
	public List<MusicInfo> getDJKK(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			//取出歌名和链接
			 Elements imgs = doc.select("div[id=phblist] li dl");
			 int i = 0;
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).text().toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href").toString();
				 MusicInfo music = new MusicInfo();
				 music.setUrl(getHost(web.getWebUrl())+href.replace("../", "/"));//链接
				 music.setTitle(title);//歌名
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("djkk");
				 music.setSpecial(title.contains("-")?title.substring(title.lastIndexOf("-")+1,title.length()).trim():null);
				 list.add(music);
				 i ++;
			 }
		 }
		 return list;
	}
	
	/**
	 * 360音乐网分类抓取音乐
	 * @throws IOException
	 */
	public List<MusicInfo> get360Music(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			 Elements imgs = doc.select("div[class=song gclearfix]");
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).text().toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href").toString();
				 String author = tag.getElementsByTag("a").get(1).text().toString();
				 String special = tag.getElementsByTag("a").get(2).text().toString();
				 MusicInfo music = new MusicInfo();
				 music.setUrl(getHost(web.getWebUrl())+href);//链接
				 music.setTitle(title);//歌名
				 music.setAuthor(author);
				 music.setSpecial(special);
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("360");
				 list.add(music);
			 }
		 }
		 return list;
	}
	
	/**
	 * 叮当音乐网分类抓取音乐
	 * @throws IOException
	 */
	public List<MusicInfo> getMTV123(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			 Elements imgs = doc.select("div[class=toplist_box] ul li");
			 int i = 0;
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("div").get(2).getElementsByTag("a").text().toString();
				 String author = tag.getElementsByTag("div").get(3).getElementsByTag("a").text().toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href").toString();
				 MusicInfo music = new MusicInfo();
				 music.setUrl(getHost(web.getWebUrl())+href.replace("../", "/"));//链接
				 music.setTitle(title);//歌名
				 music.setAuthor(author);
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("mtv123");
				 music.setSpecial(title.contains("-")?title.substring(title.lastIndexOf("-")+1,title.length()).trim():null);
				 list.add(music);
				 i ++;
			 }
		 }
		 return list;
	}

	/**
	 * 循环抓取
	 * @throws IOException
	 */
	@Test
	public void FetchLoop() throws IOException{
		List<WebInfo> list = CRUD.getWebInfoByFetch(3, "kuwo.cn");
		System.out.println("抓取链接数："+list.size());
		int i = 0;
		for(WebInfo web : list){
//			System.out.println(web.getWebUrl());
			if(web.getWebName().equals("戏剧")){
				List<MusicInfo> musicList = getKWVoice(web);
				for(MusicInfo music : musicList){
					System.out.println(music.toString());
//				 CRUD.saveMusicInfo(music);
					i ++;
				}
			}
		}
		System.out.println("数量："+i);
	}
	
	/**
	 * 一听声乐分类抓取音乐
	 * @param web
	 * @throws IOException 
	 */
	public List<MusicInfo> getYTVoice1(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			 MusicInfo music=null;
			 //取出歌名和链接
			 Elements imgs = doc.select("div[id=song-list] ul li");
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).text().toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href");
				 String author = tag.getElementsByTag("a").get(1).text().toString();
				 music = new MusicInfo();
				 music.setUrl("http://www.1ting.com"+href);//链接
				 music.setTitle(title);//歌名
				 music.setAuthor(author);//歌手名
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("1ting");
				 list.add(music);
			 }
		 }
		 return list;
	}
	
	/**
	 * 酷狗音乐分类抓取音乐 （获取不到播放链接）
	 * @param web
	 * @throws IOException 
	 */
	public List<MusicInfo> getKGVoice(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			 MusicInfo music=null;
			 //取出歌名和链接
			 Elements imgs = doc.select("ul[id=ul] li");
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).attr("title").toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href");
				 music = new MusicInfo();
				 music.setUrl(href);//链接
				 music.setTitle(title.split(" - ")[1]);//歌名
				 music.setAuthor(title.split(" - ")[0]);
//				 music.setSpecial(special);//专辑名
				 music.setMusicType(web.getWebName());
				 music.setUrlHost(web.getWebHost());
				 music.setWebsite("kugou");
				 list.add(music);
			 }
		 }
		 return list;
	}
	
	/**
	 * 酷我音乐分类抓取音乐  
	 * @param web
	 * @throws IOException 
	 */
	public List<MusicInfo> getKWVoice(WebInfo web) throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(web.getWebUrl()).get();
		 if(doc != null){
			 //取出分类链接
			 Elements categoryHref = doc.select("ul[class=singer_list clearfix] li");
			 int i = 0;
			 for (org.jsoup.nodes.Element tag : categoryHref) {
				 String href = "http://yinyue.kuwo.cn"+tag.getElementsByTag("a").get(0).attr("href");
				 doc = Jsoup.connect(href).timeout(5000).get();
				 if(doc != null){
					 Elements imgs = doc.select("ul[id=musicList] li[class=clearfix]");
					 for (org.jsoup.nodes.Element tags : imgs) {
						 String title = tags.select("p[class=m_name] a").attr("title");
						 href = tags.select("p[class=m_name] a").attr("href");
						 String author = tags.select("p[class=s_name] a").attr("title");
						 String special = tags.select("p[class=a_name] a").attr("title");
						 if(title.length()>0 && href.length()>0 && author.length()>0 && !title.contains("上传者")){
							 MusicInfo music = new MusicInfo();
							 music.setUrl(href);//链接
							 music.setTitle(title);//歌名
							 music.setAuthor(author);
							 music.setSpecial(special);//专辑名
							 music.setMusicType(web.getWebName());
							 music.setUrlHost(web.getWebHost());
							 music.setWebsite("kuwo");
							 list.add(music);
							 i ++;
						 }
					 }
				 }
			 }
		 }
		 return list;
	}
	
	/**
	 * 9酷音乐分类抓取音乐  
	 * @param url
	 * @throws IOException 
	 */
	@Test
	public void get9KVoice() throws IOException{
		String url = "http://www.9ku.com/music/t_new.htm";
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		 if(doc != null){
			 MusicInfo music=null;
			 //取出歌名和链接
			 Elements imgs = doc.select("div[class=songList clearfix] ol li");
			 for (org.jsoup.nodes.Element tag : imgs) {
				 String title = tag.getElementsByTag("a").get(0).text().toString();
				 String href = tag.getElementsByTag("a").get(0).attr("href");
				 String special="";
				 if(title.indexOf("(")!=-1&&title.indexOf("")!=-1)
				 special = title.substring(title.indexOf("(")+1,title.indexOf(")"));
				 music = new MusicInfo();
				 music.setUrl(href);//链接
				 music.setTitle(title);//歌名
				 music.setSpecial(special);//专辑名
				 list.add(music);
			 }
			 for (MusicInfo tmp : list) {
				System.out.println(tmp.toString());
			}
		 }
	}
		
		/**
		 * 百度音乐分类抓取音乐  
		 * @param url
		 * @throws IOException 
		 */
		@Test
		public void getBaiDuVoice() throws IOException{
			String url = "http://music.baidu.com/album/cn?order=time&style=all";
			List<MusicInfo> list = new ArrayList<MusicInfo>();
			org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
			 if(doc != null){
				 MusicInfo music=null;
				 //取出歌名和链接
				 Elements imgs = doc.select("div[class=songList clearfix] ol li");
				 for (org.jsoup.nodes.Element tag : imgs) {
					 String title = tag.getElementsByTag("a").get(0).text().toString();
					 String href = tag.getElementsByTag("a").get(0).attr("href");
					 String special="";
					 if(title.indexOf("(")!=-1&&title.indexOf("")!=-1)
					 special = title.substring(title.indexOf("(")+1,title.indexOf(")"));
					 music = new MusicInfo();
					 music.setUrl(href);//链接
					 music.setTitle(title);//歌名
					 music.setSpecial(special);//专辑名
					 list.add(music);
				 }
				 for (MusicInfo tmp : list) {
					System.out.println(tmp.toString());
				}
			 }
		}
	
	/**
	 * 抓取音乐
	 * @throws IOException
	 */
	@Test
	public void getMusicUtil() throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.parse(new File("C:\\Users\\Administrator\\Downloads\\data.html"), "gbk");
//		System.out.println(doc.toString());
		 if(doc != null){
			 MusicInfo music = null;
			 
			 Elements element = doc.select("div[class=ttc] span[class=txt]");
			 for (org.jsoup.nodes.Element tag : element) {
				 String title = tag.select("a").text().trim();
				 String href = tag.select("a").attr("href").trim();
				 String[] special = tag.select("div[class=text]").text().split("[|]");
				 if(title.length()>0 && href.length()>0){
					 music = new MusicInfo();
					 music.setUrl(href);//链接
					 music.setTitle(title);//歌名
					 music.setSpecial(special[5].trim().replace("&#124;", "|"));
					 music.setWebsite("163.com");
					 music.setMusicType("云音乐新歌榜");
					 music.setUrlHost("music.163.com");
					 list.add(music);
				 }
			 }
			 
			 Elements elm = doc.select("div[class=text]");
			 String aus = "";
			 for (org.jsoup.nodes.Element tag : elm) {
				 //<td><div class="text"><span title="张学友"><a class="s-fc3" href="/artist?id=6460">张学友</a></span></div></td>
				 String author = tag.select("a[class=s-fc3]").text().trim();
				 aus += author + "@";
			 }
			 System.out.println("抓取数量："+list.size());
			 int j = 0;
			 for(MusicInfo mus : list){
				 mus.setAuthor(aus.split("@")[j]);
				 System.out.println(mus.toString());
//				 CRUD.saveMusicInfo(music);
				 j ++;
			 }
		 }
	}
	 
	@Test
	public void getBaiDo() throws IOException{
		String url = "http://music.baidu.com/top/dayhot";
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
		 if(doc != null){
			 int i = 0;
			 Elements imgs = doc.select("div[id=songListWrapper] ul li");
			 System.out.println(imgs.size());
			 for (org.jsoup.nodes.Element tags : imgs) {
				   /* String html = tags.attr("data-songitem");
				    String regex = "sid\":(.*),\"author";
					final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
			  		final Matcher ma = pa.matcher(html);
			  		while(ma.find()){
			  			if(ma.groupCount() > 0){
			  				String ms = ma.group(1);//内容
			  				url = ms.replace("\"", "");
			  			}
			  		}*/
			  	 url = tags.select("span[style=width: 305px;] a").attr("href");
			  	 String title = tags.select("span[style=width: 305px;] a").attr("title");
			  	 if(title.length() >0){
			  		 String author = tags.select("span[class=author_list]").text();
			  		 MusicInfo music = new MusicInfo();
			  		 music.setUrl("http://music.baidu.com"+url);//链接
			  		 music.setTitle(title);//歌名
			  		 music.setAuthor(author);
			  		 music.setMusicType("dayhot");
			  		 music.setWebsite("baidu");
			  		 music.setUrlHost("music.baidu.com");
//				 music.setSpecial(special);//专辑名
			  		 list.add(music);
			  		 i ++;
			  	 }
			 }
			 System.out.println(i);
			 for (MusicInfo music : list) {
				 System.out.println(music.toString());
//				 CRUD.saveMusicInfo(music);
			 }
		 }
	}
	
	/**
	 * QQ音乐
	 * @throws IOException
	 */
	@Test
	public void getQQ() throws IOException{
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		org.jsoup.nodes.Document doc = Jsoup.parse(new File("C:\\Users\\Administrator\\Downloads\\data.html"), "gbk");
		 if(doc != null){
			 Elements element = doc.select("ol[id=divsonglist] li");
			 for (org.jsoup.nodes.Element tag : element) {
				 String title = tag.select("div[class=music_name] a").attr("title").trim();
				 String href = tag.select("div[class=music_name] a").attr("href").trim();
				 String author = tag.select("div[class=singer_name] a[class=album_name]").text().trim();
				 String[] special = tag.select("a[class=data]").text().split("[|]");
				 if(title.length()>0 && href.length()>0 && author.length()>0 && href.contains("00")){
					 MusicInfo music = new MusicInfo();
					 music.setUrl(href);//链接
					 music.setTitle(title);//歌名
					 music.setAuthor(author);
					 music.setSpecial(special[5].trim().replace("&#124;", "|"));
					 music.setWebsite("QQ");
					 music.setMusicType("网络新声排行榜");
					 music.setUrlHost("s.plcloud.music.qq.com");
					 list.add(music);
				 }
			 }
			 System.out.println("抓取数量："+list.size());
			 for(MusicInfo music : list){
				 System.out.println(music.toString());
			 }
		 }
	
	}
	
	@Test
	public void FetchHtml() throws IOException{
        List<MS> list = CRUD.getMS("ms_baidu",0);
        int i = 0;
        System.out.println(list.size());
        for(MS ms : list){
        	if(getStatusCode(ms)!=200){
        		System.out.println(ms.getId());
        		CRUD.updateMS_Url_sts(ms.getId());
        		continue;
        	}
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
        			music.setMusicType("11G");
        			music.setUrlHost("music.baidu.com");
        			System.out.println(music);
//        			CRUD.saveMusicInfo(music);
//        			CRUD.updateMS_Fethch_sts(ms.getId());
        			i ++;
        		}
        	}
        }
	}
	
	@Test
	public void FetchArtist(){
		try {
	        List<MS> list = CRUD.getMS("ms_artist",0);
	        int i = 0;
	        System.out.println(list.size());
	        for(MS ms : list){
	        	if(getStatusCode(ms)!=200){
	        		System.out.println(ms.getId());
	        		CRUD.updateMS_Url_sts(ms.getId());
	        		continue;
	        	}
	        	org.jsoup.nodes.Document doc = Jsoup.connect(ms.getUrl()).get();
	        	System.out.println("id: "+ms.getId());
	        	if(doc != null){
	        		String title = doc.select("h2[class=singer-name]").text().trim();
//	        		String author = doc.select("span[class=author_list]").attr("title").trim();
//	        		String special = doc.select("div[class=info-holder clearfix] ul li[class=clearfix] a").text().trim();
//		 			if(title.length()>0 && author.length()==0){
//		 				author = "未知";
//		 			}
	        		if(title.length()>0){
	        			System.out.println(ms.getId());
	        			MusicInfo music = new MusicInfo();
	        			music.setUrl(ms.getUrl());//链接
	        			music.setTitle(title);//歌名
	        			music.setWebsite("baidu");
	        			music.setMusicType("artist");
	        			music.setUrlHost("music.baidu.com");
	        			System.out.println(music);
	        			CRUD.saveMusicInfo(music);
	        			CRUD.updateMS_Fethch_sts(ms.getId());
	        			i ++;
	        		}
	        	}
	        }
		} catch (Exception e) {
			FetchArtist();
		}
	}
	
	@Test
	public void FetchAlum() throws IOException{
        List<MS> list = CRUD.getMS("ms_artist",0);
        int i = 0;
        System.out.println(list.size());
        for(MS ms : list){
        	if(getStatusCode(ms)!=200){
        		System.out.println(ms.getId());
        		CRUD.updateMS_Url_sts(ms.getId());
        		continue;
        	}
        	org.jsoup.nodes.Document doc = Jsoup.connect(ms.getUrl()).get();
        	if(doc != null){
        		String title = doc.select("h2[class=singer-name]").text().trim();
//        		String author = doc.select("span[class=author_list]").attr("title").trim();
//        		String special = doc.select("div[class=info-holder clearfix] ul li[class=clearfix] a").text().trim();
//	 			if(title.length()>0 && author.length()==0){
//	 				author = "未知";
//	 			}
        		if(title.length()>0){
        			System.out.println(ms.getId());
        			MusicInfo music = new MusicInfo();
        			music.setUrl(ms.getUrl());//链接
        			music.setTitle(title);//歌名
        			music.setWebsite("baidu");
        			music.setMusicType("artist");
        			music.setUrlHost("music.baidu.com");
        			System.out.println(music);
        			CRUD.saveMusicInfo(music);
        			CRUD.updateMS_Fethch_sts(ms.getId());
        			i ++;
        		}
        	}
        }
	}
	
	public static int getStatusCode(MS ms){
		GetMethod getMethod = null;
  		int statusCode = 0;
		try {
			getMethod = new GetMethod(ms.getUrl());
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
	 * 截取域名
	 * @param link
	 * @return
	 */
	public static String getHost(String link){
		if(link.contains(".com")){
			link = link.substring(0, link.indexOf("com")+3);
		}else if(link.contains(".net")){
			link = link.substring(0, link.indexOf("net")+3);
		}else if(link.contains(".cn")){
			link = link.substring(0, link.indexOf("cn")+2);
		}
		return link;
	}
}
