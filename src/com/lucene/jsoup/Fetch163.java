package com.lucene.jsoup;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.lucene.jsoup.bean.MS;
import com.lucene.jsoup.bean.MusicInfo;
import com.lucene.jsoup.bean.MusicUser;
import com.lucene.jsoup.common.CRUD;
import com.lucene.jsoup.common.FetchUtil;
import com.lucene.jsoup.common.Util;

public class Fetch163 {
	int id = 10869;//中断前抓取的id

	@Test
	public void getPlayList_163(){
		try {
	        List<MS> list = CRUD.getMS("163_playlist", id);
	        System.out.println(list.size());
	        for(MS ms : list){
	        	if(FetchUtil.getStatusCode(ms)!=200){
	        		System.out.println(ms.getId());
	        		continue;
	        	}
	        	id = ms.getId();
	        	String url = "http://music.163.com/playlist?id=" + Util.getParam(ms.getUrl(), "id=(\\d+)");
	        	org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
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
	    			music.setUid(Util.getParam(uid, "id=(\\d+)"));
	    			music.setRefUrl(ms.getUrl());
	    			music.setSinger(singer);
	    			music.setUrl("http://music.163.com"+href);
	    			music.setTitle(title);
	    			music.setSpecial(special);
	    			CRUD.saveMS_Us_163(music);
	    			System.out.println(ms.getId() + ": " + music);
	    		}
	    		if(h2.length()>0 && name.length()>0 && elmts.size()==0){
	    			music.setSongListName(h2);
	    			music.setUsername(name);
	    			CRUD.saveMS_Us_163(music);
	    			System.out.println(music);
	    		}
	        }
		} catch (Exception e) {
			getPlayList_163();
		}
	}
	
	public void getSingleSong_163(){
		try {
	        List<MS> list = CRUD.getMS("ms_track", id);
	        System.out.println(list.size());
	        for(MS ms : list){
	        	if(FetchUtil.getStatusCode(ms)!=200){
	        		System.out.println(ms.getId());
//	        		CRUD.updateMS_Url_sts("ms_163_api", ms.getId());
	        		continue;
	        	}
	        	id = ms.getId();
	        	System.out.println("id: "+ id);
	        	String url = "http://music.163.com/song/"+MusicMacth.getParam(ms.getUrl(),"trackId=(\\d+)");
	        	org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
	        	String content = doc.select("meta[name=keywords]").attr("content").trim();
	            if(content !=null && content.length()>0){
	            	String[] cont = content.replaceAll("，", ",").split("[,]");
	            	if(cont !=null && cont.length>2){
	            		String title = cont[0].trim();
	            		String author = cont[2].trim();
	            		String special = cont[1].trim();
	            		if(title.length()>0 && author.length()==0){
	    	 				author = "未知";
	    	 			}
	            		if(!title.contains("网易云音乐") && title.length()>0 && author.length()>0){
	            			MusicInfo music = new MusicInfo();
	            			music.setUrl(ms.getUrl());//链接
	            			music.setTitle(title);//歌名
	            			music.setAuthor(author);
	            			music.setSpecial(special);
	            			music.setWebsite("163");
	            			music.setUrlHost("163.com");
	            			System.out.println(music);
	            			CRUD.saveTrack(music);
//	            			CRUD.saveMusicInfo(music);
//	            			CRUD.updateMS_Fethch_sts("ms_track", ms.getId());
	            		}
	            	}
	            }
	        }
		} catch (Exception e) {
			getSingleSong_163();
		}
	
	}
	
		
}
