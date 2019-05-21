package com.lucene.jsoup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URLDecoder;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.lucene.jsoup.bean.MS;
import com.lucene.jsoup.bean.MusicInfo;
import com.lucene.jsoup.common.CRUD;
import com.lucene.jsoup.common.FetchUtil;
import com.lucene.jsoup.common.Util;

public class FetchBaidu {

	/**
	 * http://music.baidu.com/tag/%E7%BD%91%E7%BB%9C%E6%AD%8C%E6%9B%B2
	 */
	@Test
	public void getTagList(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("D:\\3gqq.txt"));
			String data = br.readLine().replace("?", "");//一次读入一行，直到读入null为文件结束
			int i = 1;
			while(data!=null){
				i ++;
				System.out.println(data);
				if(Util.getStatusCode(data)!=200){
					data = br.readLine().replace("?", ""); //接着读下一行
	        		continue;
	        	}
				org.jsoup.nodes.Document doc = Jsoup.connect(data).get();
				data = data.replace("tag/", "?tag=").replace("size=20", "").replace("fr=hao123", "")
						   .replace("_paresh=true", "").replace("/", "").replace("undefined", "");
				data = URLDecoder.decode(data, "utf-8");
	        	String tag = Util.getParam(data, "tag=([^\"|[\u4e00-\u9fa5]]+)");
	        	if(data.contains("start")){
	        		tag = data.substring(data.indexOf("tag")+4, data.indexOf("start"));
	        	}
	        	System.out.println("tag: " + tag);
	        	
	        	Elements elmt = doc.select("div[class=song-item clearfix]");
	        	for(org.jsoup.nodes.Element tags : elmt){
	        		String url = tags.select("span[class=song-title] a").attr("href").trim();
	        		String title = tags.select("span[class=song-title] a").text();
	        		String singer = tags.select("span[class=author_list]").text();
	        		String special = tags.select("span[class=album-titl] a").attr("href").trim();
	        		if(title.length()>0 && url.length()>0 && url.contains("song") && !url.contains("#")){
    					MusicInfo music = new MusicInfo();
    					music.setUrl("http://music.baidu.com"+url);//链接
    					music.setTitle(title);//歌名
    					music.setAuthor(singer);
    					music.setSpecial(special);
    					music.setWebsite("163");
    					music.setUrlHost("163.com");
    					System.out.println(music);
//        			CRUD.saveMusicInfo(music);
//        			CRUD.updateMS_Fethch_sts("ms_163_api",ms.getId());
    				
	        		} 
	        	}
			}
			System.out.println(i);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	/**抓取不了
	 * http://music.163.com/#/playlist?id=4739503
	 */
	@Test
	public void getPlayList_163(){
		try {
	        List<MS> list = CRUD.getMS("ms_163_api",0);
	        System.out.println(list.size());
	        for(MS ms : list){
	        	ms.setUrl("http://music.163.com/song/"+MusicMacth.getParam(ms.getUrl(),"id=(\\d+)"));
	        	if(FetchUtil.getStatusCode(ms)!=200){
	        		System.out.println(ms.getId());
//	        		CRUD.updateMS_Url_sts("ms_163_api", ms.getId());
	        		continue;
	        	}
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
	            			music.setUrlHost("163.com");
	            			System.out.println(music);
//	            			CRUD.saveMusicInfo(music);
//	            			CRUD.updateMS_Fethch_sts("ms_163_api",ms.getId());
	            		}
	            	}
	            }
	        }
		} catch (Exception e) {
		}
	}
}
