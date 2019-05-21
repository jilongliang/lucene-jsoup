package com.lucene.jsoup;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.lucene.jsoup.bean.MS;
import com.lucene.jsoup.bean.MusicInfo;
import com.lucene.jsoup.common.CRUD;
import com.lucene.jsoup.common.FetchUtil;

public class FetchIFeng {
	int id = 0;//中断前抓取的id

	@Test
	public void getIFeng(){
		try {
	        List<MS> list = CRUD.getMS("ms_ifeng", id);
	        System.out.println(list.size());
	        for(MS ms : list){
	        	if(FetchUtil.getStatusCode(ms)!=200){
	        		System.out.println(ms.getId());
	        		CRUD.updateMS_Url_sts("ms_ifeng", ms.getId());
	        		continue;
	        	}
	        	id = ms.getId();
	        	org.jsoup.nodes.Document doc = Jsoup.connect(ms.getUrl()).get();
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
            			music.setUrl(ms.getUrl());//链接
            			music.setTitle(title);//歌名
            			music.setAuthor(singer);
            			music.setWebsite("163");
            			music.setUrlHost("163.com");
            			System.out.println(music);
            			CRUD.saveMusicInfo(music);
	    			}
	    		}
	        }
		} catch (Exception e) {
			getIFeng();
		}
	}
	
	//163单曲抓取 http://music.163.com/#/song?id=
}
