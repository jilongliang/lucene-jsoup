package com.lucene.jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.lucene.jsoup.bean.Disease;
import com.lucene.jsoup.bean.Medicine;
import com.lucene.jsoup.common.CRUD;

/**
 * 演示分词
 * @author liangjilong
 */
public class AnalyzerTest {
	int idx = 0;
	static List<String> ciku = new ArrayList<String>();//病理词库
	Map<String, Integer> dataMap = new HashMap<String, Integer>();//病理词频
	
	/*
	 * 初始化库词
	 */
	static {
		try {
			String path="D:\\test\\ciku.txt";
			File f=new File(path);
			if(!f.exists()){
				f.mkdirs();
			}
			BufferedReader br = new BufferedReader(new FileReader(path));
			String text = br.readLine();//一次读入一行，直到读入null为文件结束
			while(text != null){
				if(text !=null && text.length()>0){
					ciku.add(text.trim());
				}
				text = br.readLine(); //接着读下一行
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void t_test(){
		Map<String, Integer> map = CRUD.getDataMap();
		System.out.println(map.size());
		ciku.add("好好学习");
		ciku.add("你要学习");
		System.out.println(ciku.contains("学习"));
	}
	
	@Test
	public void fetchCont(){
		try {
			//neike内科36774 waike外科2029 nanke男科1897 fuke妇科2958 erke儿科7160 pfxbk性病科873 pfk皮肤科1964
			//wgk五官科2150 sjk神经科108 zyk中医保健3316 yp药品保健176 fck孕产保健4269 zxmr整形美容338 guke骨科673 ganbing肝病科449
			String url = "http://ask.99.com.cn/pfxbk/new/idx/";
			for(int i = idx; idx < 873; i ++){
				idx ++;
				System.out.println("idx: " + idx);
				org.jsoup.nodes.Document doc = Jsoup.connect(url.replace("idx", String.valueOf(idx))).get();
				if(doc != null){
					Elements elements = doc.select("div[class=quest_wt] dl");
					for(Element tag : elements){
						String href = tag.select("h3 a").attr("href").trim();
						String category = tag.select("h3 a span").text().trim().replace("[", "").replace("]", "");
						System.out.println(category);
						String question = tag.select("h3 a").attr("title").trim();
						String answer = tag.select("p").text().trim();
						if(category.length() > 0 && question.length() > 0 && answer.length() > 0){
							Medicine medicine = new Medicine();
							medicine.setUrl("http://ask.99.com.cn" + href);
							medicine.setQuestion(question);
							medicine.setAnswer(answer);
							medicine.setCategory(category);
							fenci(medicine);
						}
					}
				}
			}
			//保存词频记录
			save();
		} catch (Exception e) {
			fetchCont();
		}
	}
	
	@Test
	public void testFenci() throws Exception{
		Analyzer analyzer = new IKAnalyzer(true);// true:智能切分，false:细粒度切分
		// 对content进行分词，得到的结果是分词流
		TokenStream ts = analyzer.tokenStream("text", "肚子经常涨气，放屁也多，整天想排便，但又排不出来，又全身伐力，类似感冒头晕的症状");
		ts.reset();

		CharTermAttribute attr = null;
		// 遍历分词流
		while (ts.incrementToken()) {
			attr = ts.getAttribute(CharTermAttribute.class);
			System.out.println(attr.toString());
		}
	}
	
	/**
	 * 分词
	 * @param medicine
	 * @throws Exception
	 */
	public void fenci(Medicine medicine) throws Exception{
		fenciByQues(medicine);//按问题分词
		fenciByAns(medicine); //按答案分词
	}
	
	/**
	 * 按问题分词
	 * @param medicine
	 * @throws Exception
	 */
	public void fenciByQues(Medicine medicine) throws Exception{
		List<String> list = new ArrayList<String>();//存放不重复的病理词
		Analyzer analyzer = new IKAnalyzer(true);// true:智能切分，false:细粒度切分
		// 对content进行分词，得到的结果是分词流
		String content = contentFilter(medicine.getQuestion());
		TokenStream ts = analyzer.tokenStream("text", content);
		ts.reset();

		CharTermAttribute attr = null;
		// 遍历分词流
		while (ts.incrementToken()) {
			attr = ts.getAttribute(CharTermAttribute.class);
			String ciyu = attr.toString().trim();
//			System.out.println(ciyu);
			if(ciyu.length()>1 && ciku.contains(ciyu)){
				if(!list.contains(ciyu)){
					//记录词频
					if(dataMap.containsKey(ciyu)){
						dataMap.put(ciyu, dataMap.get(ciyu) + 1);
					}else{
						dataMap.put(ciyu, 1);
					}
					//保存病理词信息
					list.add(ciyu);
					medicine.setType("ques");
					medicine.setDisease(ciyu);
//					CRUD.saveMedicine(medicine);
//					System.out.println(medicine);
//					System.out.println("出现的病理词： " + ciyu);
				}
			}
		}
	}
	
	/**
	 * 按答案分词
	 * @param medicine
	 * @throws Exception
	 */
	public void fenciByAns(Medicine medicine) throws Exception{
		List<String> list = new ArrayList<String>();//存放不重复的病理词
		Analyzer analyzer = new IKAnalyzer(true);// true:智能切分，false:细粒度切分
		// 对content进行分词，得到的结果是分词流
		String content = contentFilter(medicine.getAnswer());
		TokenStream ts = analyzer.tokenStream("text", content);
		ts.reset();

		CharTermAttribute attr = null;
		// 遍历分词流
		while (ts.incrementToken()) {
			attr = ts.getAttribute(CharTermAttribute.class);
			String ciyu = attr.toString().trim();
//			System.out.println(ciyu);
			if(ciyu.length()>1 && ciku.contains(ciyu)){
				if(!list.contains(ciyu)){
					//记录词频
					if(dataMap.containsKey(ciyu)){
						dataMap.put(ciyu, dataMap.get(ciyu) + 1);
					}else{
						dataMap.put(ciyu, 1);
					}
					//保存病理词信息
					list.add(ciyu);
					medicine.setType("ans");
					medicine.setDisease(ciyu);
//					CRUD.saveMedicine(medicine);
//					System.out.println(medicine);
//					System.out.println("出现的病理词： " + ciyu);
				}
			}
		}
	}
	
	/**
	 * 保存词频记录
	 */
	public void save(){
		for(String dis : dataMap.keySet()){
			Disease disease = new Disease();
			disease.setCategory("性病科");
			disease.setCount(dataMap.get(dis));
			disease.setDisease(dis);
//			CRUD.saveDisease(disease);
			System.out.println(disease);
		}
	}
	
	/**
	 * 过滤文本
	 * @param content
	 * @return
	 */
	public String contentFilter(String content){
		content = content.replace("你", "").replace("的", "").replace("你好", "").replace("根据你描述的情况分析", "").replace("去医院", "")
						 .replace("这种情况", "").replace("一般", "").replace("建议", "").replace("检查", "").replace("治疗", "")
						 .replace("需要", "").replace("不", "").replace("是", "").replace("考虑", "").replace("由于", "")
						 .replace("病情描述", "");
		return content;
	}
	
	
}