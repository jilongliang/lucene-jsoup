package com.lucene.jsoup;

import java.io.IOException;
import java.util.List;
import jxl.Cell;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.cyf.excel.util.CellStyle;
import org.cyf.excel.util.ExcelUtil;
import org.cyf.excel.util.Extension;
import org.cyf.excel.util.JDBCUtil;
import org.cyf.excel.util.Title;
import org.junit.Test;

public class Excel_demo {
	//将数据库表导出成EXCEL表形式
	@Test
	public void EXCELTest(){
		try {
				//-------------------------------EXCEL操作完成-----------------------------------
				String fileName = "e:\\chen"+Extension.EXCEL;
				WritableWorkbook workbook = ExcelUtil.newInstanceOfWritableWorkbook(fileName);
				WritableSheet sheet = ExcelUtil.newInstanceOfSheet(workbook, "单位表信息");
				Title title = new Title("单位编号","单位名称","上属单位编号","单位地址","单位联系电话","负责人");
				//需要导出哪一张数据库表，则写相关查询语名
				String sql = "select * from sys_unit";
				//通过sql查询语名，返回一个包含表信息的list集合
				List list = JDBCUtil.getResultSet(sql);
				//-----------------设置导出sheet工作表中单元格的格式------------------//
				//实例单元格字体样式类
				WritableFont font = new WritableFont(WritableFont.ARIAL,12,WritableFont.BOLD);
				//设置字体颜色(例如:粉色)
				font.setColour(Colour.PINK);
				//设置字体是否倾斜true倾斜false不倾斜
				font.setItalic(true);
				//将单元格字体对象font与格式化对象format绑定
				WritableCellFormat format=new WritableCellFormat(font);
				//水平居中对齐
				format.setAlignment(Alignment.CENTRE);
				//垂直居中对齐
				format.setVerticalAlignment(VerticalAlignment.CENTRE);
				//自动换行
				format.setWrap(true);
				//设置单元格边框样式Border.ALL代表单元格四条边都被设置
				format.setBorder(Border.ALL, BorderLineStyle.MEDIUM_DASHED);
				//通过CellStyle类,可以设置单元格高度，宽度，单元格格式对象format
				CellStyle cell = new CellStyle(500, 30,format);
				//最后一步，调用writeExcel()方法，将查询结果集，导出表的每个字段的标题，excel工作薄，excel工作表,CellStyle作为参数，即可成功实现EXCEL文件相关操作
				ExcelUtil.writeExcel(list, title, workbook, sheet, cell);
				//-------------------------------EXCEL操作完成-----------------------------------
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		
	}
	
	//读取EXCEL内容
	@Test
	public void readerEXCELTest(){
		Cell[][] cell = ExcelUtil.readerXLS("e:\\chen"+Extension.EXCEL);//不同版本可转为.xls读取
		for (int i = 0; i < cell.length; i++) {
			for (int j = 0; j < cell[i].length; j++) {
				System.out.print(cell[i][j].getContents()+"\t");
			}
			System.out.println();
		}
	}
	
}
