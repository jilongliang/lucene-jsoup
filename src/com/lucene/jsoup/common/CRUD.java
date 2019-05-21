package com.lucene.jsoup.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lucene.jsoup.bean.Disease;
import com.lucene.jsoup.bean.MS;
import com.lucene.jsoup.bean.Medicine;
import com.lucene.jsoup.bean.MusicInfo;
import com.lucene.jsoup.bean.MusicUser;
import com.lucene.jsoup.bean.WebInfo;

/**
 * CRUD操作类
 * @author liangjilong
 *
 */
public class CRUD {
	private static ResultSet rs = null;
	private static Connection conn = null;
    private static PreparedStatement ps = null;
    
    /**
     * 保存音乐信息
     * @param music
     * @return
     */
	public static void saveMusicInfo(MusicInfo music){
		String sql = "insert into music_info(url,title,singer,special,url_host,website,music_type) values(?,?,?,?,?,?,?)";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, music.getUrl());
			ps.setString(2, music.getTitle());
			ps.setString(3, music.getAuthor());
			ps.setString(4, music.getSpecial());
			ps.setString(5, FetchUtil.getHost(music.getUrl()));
			ps.setString(6, music.getWebsite());//网站名称
			ps.setString(7, music.getMusicType());//音乐分类
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static void saveIfeng(MusicInfo music){
		String sql = "insert into music_ifeng(url,title,singer,special,url_host,website,music_type) values(?,?,?,?,?,?,?)";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, music.getUrl());
			ps.setString(2, music.getTitle());
			ps.setString(3, music.getAuthor());
			ps.setString(4, music.getSpecial());
			ps.setString(5, FetchUtil.getHost(music.getUrl()));
			ps.setString(6, music.getWebsite());//网站名称
			ps.setString(7, music.getMusicType());//音乐分类
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static void saveTrack(MusicInfo music){
		String sql = "insert into track_info(url,title,singer,special,url_host,website,music_type) values(?,?,?,?,?,?,?)";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, music.getUrl());
			ps.setString(2, music.getTitle());
			ps.setString(3, music.getAuthor());
			ps.setString(4, music.getSpecial());
			ps.setString(5, FetchUtil.getHost(music.getUrl()));
			ps.setString(6, music.getWebsite());//网站名称
			ps.setString(7, music.getMusicType());//音乐分类
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	/**
     * 保存网站信息
     * @param web
     * @return
     */
	public static void saveWebInfo(WebInfo web){
		String sql = "insert into web_info(web_url,web_name,web_host,web_type,type_level) values(?,?,?,?,?)";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, web.getWebUrl());
			ps.setString(2, web.getWebName());
			ps.setString(3, web.getWebHost());
			ps.setString(4, web.getWebType());
			ps.setInt(5, web.getTypeLevel());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	/**
	 * 查询要抓取的链接集合
	 * @param level
	 * @param host
	 * @return
	 */
	public static List<WebInfo> getWebInfoByFetch(int level, String host){
		List<WebInfo> list = new ArrayList<WebInfo>();
		String sql = "select * from web_info where type_level = ? and web_host like '%str%'".replace("str", host);
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, level);
			rs = ps.executeQuery();
			while(rs.next()){
				WebInfo web = new WebInfo();
				web.setTypeLevel(level);
				web.setWebUrl(rs.getString("web_url"));
				web.setWebHost(rs.getString("web_host"));
				web.setWebName(rs.getString("web_name"));
				web.setWebType(rs.getString("web_type"));
				list.add(web);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
		return list;
	}
	
	public static List<MusicInfo> getMusicByHost(String host){
		List<MusicInfo> list = new ArrayList<MusicInfo>();
		String sql = "select * from music_info where website like = '%" + host + "%'";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				MusicInfo music = new MusicInfo();
				music.setAuthor(rs.getString("singer"));
				music.setMusicType(rs.getString("music_type"));
				music.setSpecial(rs.getString("singer"));
				music.setTitle(rs.getString("title"));
				music.setUrl(rs.getString("url"));
				music.setWebsite(rs.getString("website"));
				list.add(music);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
		return list;
	}
	
	public static List<MS> getMS(String table,int id){
		List<MS> list = new ArrayList<MS>();
		String sql = "select * from " + table + " where id>? order by id";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while(rs.next()){
				MS ms = new MS();
				ms.setId(rs.getInt("id"));
				ms.setUrl(rs.getString("url"));
				list.add(ms);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
		return list;
	}
	
	/**
	 * 修改网址状态，0表示不能打开的无效链接
	 * @param id
	 */
	public static void updateMS_Url_sts(int id){
		String sql = "update ms_qq set url_status = 0 where id = ?";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	/**
	 * 修改网址抓取状态，1表示已抓取
	 * @param id
	 */
	public static void updateMS_Fethch_sts(int id){
		String sql = "update ms_qq set fetch_status = 1 where id = ?";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	/**
	 * 修改网址状态，0表示不能打开的无效链接
	 * @param table
	 * @param id
	 */
	public static void updateMS_Url_sts(String table,int id){
		String sql = "update table set url_status = 0 where id = ?".replace("table", table);
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	/**
	 * 修改网址抓取状态，1表示已抓取
	 * @param table
	 * @param id
	 */
	public static void updateMS_Fethch_sts(String table,int id){
		String sql = "update table set fetch_status = 1 where id = ?".replace("table", table);
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	//-----------------------------100 USER------------------------------
	/**
	 * 修改网址状态，0表示不能打开的无效链接
	 * @param id
	 */
	public static void update100_URL_sts(int id){
		String sql = "update 100_user set url_status = 0 where ID = ?";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	/**
	 * 修改网址抓取状态，1表示已抓取
	 * @param id
	 */
	public static void update100_Fethch_sts(int id){
		String sql = "update 100_user set fetch_status = 1 where ID = ?";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static void save100USER(MusicInfo music){
		String sql = "insert into 100_u(url,title,singer,special,ref_id) values(?,?,?,?,?)";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, music.getUrl());
			ps.setString(2, music.getTitle());
			ps.setString(3, music.getAuthor());
			ps.setString(4, music.getSpecial());
			ps.setInt(5, Integer.parseInt(music.getMusicType()));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static List<MS> get100USER(){
		List<MS> list = new ArrayList<MS>();
		String sql = "select * from 100_user where url like '%y.qq.com%' and url like '%songid%' order by id";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				MS ms = new MS();
				ms.setId(rs.getInt("id"));
				ms.setUrl(rs.getString("url"));
				list.add(ms);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
		return list;
	}
	
	/**
	 * 修改新加字段SONG_INFO
	 * @return
	 */
	public static List<MusicInfo> slt163(){
		List<MusicInfo> list = new ArrayList<MusicInfo>();
//		String sql = "SELECT DISTINCT net_address as url, id FROM tbl_100_user WHERE net_address LIKE '%music.163.com%' AND net_address LIKE '%song%' AND net_address LIKE '%id=%'";
		String sql = "select * from ms_3gqq where url like '%songlistCallback%' order by id";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				MusicInfo ms  = new MusicInfo();
				ms.setUrl(rs.getString("url"));
				ms.setTitle(rs.getString("id"));
				list.add(ms);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
		return list;
	}
	
	public static void update163(String info,int id){
		String sql = "update tbl_100_user set SONG_INFO = ? where id = ?";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, info);
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static void update3GQQ(MusicInfo ms){
		String sql = "update ms_3gqq set title = ?, singer = ?, special = ? where id = ?";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, ms.getTitle());
			ps.setString(2, ms.getAuthor());
			ps.setString(3, ms.getSpecial());
			ps.setInt(4, Integer.parseInt(ms.getUrlHost()));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static void saveMS_Us_163(MusicUser music){
		String sql = "insert into music_user(url,title,singer,special,ref_url,uid,username,song_list_name) values(?,?,?,?,?,?,?,?)";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, music.getUrl());
			ps.setString(2, music.getTitle());
			ps.setString(3, music.getSinger());
			ps.setString(4, music.getSpecial());
			ps.setString(5, music.getRefUrl());
			ps.setString(6, music.getUid());
			ps.setString(7, music.getUsername());
			ps.setString(8, music.getSongListName());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static void saveMedicine(Medicine medicine){
		String sql = "insert into medicine(url,question,answer,disease,category,type) values(?,?,?,?,?,?)";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setString(1, medicine.getUrl());
			ps.setString(2, medicine.getQuestion());
			ps.setString(3, medicine.getAnswer());
			ps.setString(4, medicine.getDisease());
			ps.setString(5, medicine.getCategory());
			ps.setString(6, medicine.getType());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static void saveDisease(Disease disease){
		String sql = "insert into disease(count,disease,category) values(?,?,?)";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, disease.getCount());
			ps.setString(2, disease.getDisease());
			ps.setString(3, disease.getCategory());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
	}
	
	public static Map<String, Integer> getDataMap(){
		Map<String, Integer> map = new HashMap<String, Integer>();
		String sql = "select * from disease";
		try {
			conn = JDBC.getDBConn();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				map.put(rs.getString("disease"), rs.getInt("count"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			JDBC.closeDB(rs, ps, conn);
		}
		return map;
	}
	
}
